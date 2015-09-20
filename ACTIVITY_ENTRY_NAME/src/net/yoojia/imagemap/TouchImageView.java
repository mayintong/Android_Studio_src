package net.yoojia.imagemap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

/**
 * TouchImageView璁捐涓轰竴涓叿鏈夌嫭绔嬪畬鏁村姛鑳界殑View銆傚彲缂╂斁锛屾嫋鍔ㄥ浘鐗囥�
 * TouchImageView - A full View with Scale/Drag support.
 */
public class TouchImageView extends ImageView {

	private final Matrix imageUsingMatrix = new Matrix();
	private final Matrix imageSavedMatrix = new Matrix();
	private final Matrix overLayerMatrix = new Matrix();

	/* 鎯�绯绘暟 */
    private static final float FRICTION = 0.9f;

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;

	private int mode = NONE;

	private float redundantXSpace;
	private float redundantYSpace;

	private float right, bottom, origWidth, origHeight, bmWidth, bmHeight;

	/* View鐨勫搴﹀拰楂樺害 */
    private float viewWidth;
	private float viewHeight;

	private PointF last = new PointF();
	private PointF mid = new PointF();
	private  PointF start = new PointF();

	private float[] matrixValues;

	/* 缁濆鍋忕Щ閲忥細View鍘熺偣涓庡浘鐗囧師鐐圭殑鍋忕Щ閲忥紝鏃犺缂╂斁鎴栬�骞崇Щ锛岃繖瀵瑰�閮借〃杈惧畠浠殑瀹為檯璺濈銆�	 * 娉細浠iew鍜屽浘鐗囩殑宸︿笂瑙掍负鍘熺偣锛屽悜涓嬩负Y杞存鍚戯紝鍚戝彸涓篨杞存鍚戙�
	 * */
	private float absoluteOffsetX;
	private float absoluteOffsetY;

	private float saveScale = 1f;
	private float minScale = 1f;
	private float maxScale = 5f;
	private float oldDist = 1f;

	private PointF lastDelta = new PointF(0, 0);
	private float velocity = 0;
	private long lastDragTime = 0;
    
    private Context mContext;
    private ScaleGestureDetector mScaleDetector;

    public boolean onLeftSide = false, onTopSide = false, onRightSide = false, onBottomSide = false;

    public TouchImageView(Context context) {
        this(context,null);
    }
    
    public TouchImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        super.setClickable(true);
        this.mContext = context;
        initialized();
    }
    
    private OnTouchListener touchListener = new OnTouchListener() {

    	final static float MAX_VELOCITY = 1.2f;
    	
    	private long dragTime ;
    	private float dragVelocity;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
            mScaleDetector.onTouchEvent(event);

            fillAbsoluteOffset();
            PointF curr = new PointF(event.getX(), event.getY());
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    mode = DRAG;
                    float xOnView =  event.getX(0);
                    float yOnView = event.getY(0);
                    onViewClick(xOnView, yOnView);
                    imageSavedMatrix.set(imageUsingMatrix);
                    last.set(event.getX(), event.getY());
                    start.set(last);

                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = spacing(event);
                    if (oldDist > 10f) {
                        imageSavedMatrix.set(imageUsingMatrix);
                        midPoint(mid, event);
                        mode = ZOOM;
                    }
                    break;

                case MotionEvent.ACTION_UP:
                	if(mode == DRAG){
                		velocity = dragVelocity;
                	}
                    mode = NONE;
                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    mode = NONE;
                    velocity = 0;
                    imageSavedMatrix.set(imageUsingMatrix);
                    oldDist = spacing(event);
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (mode == DRAG) {
                    	dragTime = System.currentTimeMillis();
                    	dragVelocity = (float)distanceBetween(curr, last) / (dragTime - lastDragTime) * FRICTION;
                    	dragVelocity = Math.min(MAX_VELOCITY,dragVelocity);
                        lastDragTime = dragTime;
                        float deltaX = curr.x - last.x;
                        float deltaY = curr.y - last.y;
                        checkAndSetTranslate(deltaX, deltaY);
                        lastDelta.set(deltaX, deltaY);
                        last.set(curr.x, curr.y);
                    }
                    break;
                }

            setImageMatrix(imageUsingMatrix);
            invalidate();
            return false;
		}
    };
    
	protected void initialized () {
        matrixValues = new float[9];
        setScaleType(ScaleType.MATRIX);
        setImageMatrix(imageUsingMatrix);
        mScaleDetector = new ScaleGestureDetector(mContext, new ScaleListener());
        setOnTouchListener(touchListener);

		float scale = saveScale = 1.0f;
		imageUsingMatrix.setScale(scale, scale);
		overLayerMatrix.setScale(scale, scale);
    }

	/**
	 * View琚偣鍑�	 * @param xOnView View涓婄殑X鍧愭爣
	 * @param yOnView View涓婄殑Y鍧愭爣
	 */
    protected void onViewClick (float xOnView, float yOnView){}
	
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        scrolling();
    }

	public PointF getAbsoluteCenter (){
		fillAbsoluteOffset();
		return new PointF( Math.abs(absoluteOffsetX) + viewWidth/2,Math.abs(absoluteOffsetY) + viewHeight/2 );
	}

	public void moveBy (float deltaX, float deltaY){
		checkAndSetTranslate(deltaX, deltaY);
		setImageMatrix(imageUsingMatrix);
	}

	/**
	 * 鎯�婊氬姩
	 */
	private void scrolling (){
		final float deltaX = lastDelta.x * velocity;
		final float deltaY = lastDelta.y * velocity;
		if (deltaX > viewWidth || deltaY > viewHeight) return;
		velocity *= FRICTION;
		if (Math.abs(deltaX) < 0.1 && Math.abs(deltaY) < 0.1) {
			return;
		}
		moveBy(deltaX, deltaY);
	}

	/**
	 * 鎻愪氦骞崇Щ鍙樻崲
	 * @param deltaX 骞崇Щ璺濈X
	 * @param deltaY 骞崇Щ璺濈Y
	 */
    protected void postTranslate(float deltaX, float deltaY){
    	imageUsingMatrix.postTranslate(deltaX, deltaY);
    	overLayerMatrix.postTranslate(deltaX, deltaY);
		fillAbsoluteOffset();
    }

	/**
	 * 鎻愪氦缂╂斁
	 * @param scaleFactor 缂╂斁姣斾緥
	 * @param scaleCenterX 缂╂斁涓績X
	 * @param scaleCenterY 缂╂斁涓績Y
	 */
    protected void postScale(float scaleFactor, float scaleCenterX, float scaleCenterY){
    	imageUsingMatrix.postScale(scaleFactor, scaleFactor, scaleCenterX, scaleCenterY);
        overLayerMatrix.postScale(scaleFactor, scaleFactor, scaleCenterX, scaleCenterY);
		fillAbsoluteOffset();
    }

	/**
	 * 妫�祴骞崇Щ杈圭晫骞惰缃钩绉�	 * @param deltaX 骞崇Щ璺濈X
	 * @param deltaY 骞崇Щ璺濈Y
	 */
    private void checkAndSetTranslate(float deltaX, float deltaY)
    {
        float scaleWidth = Math.round(origWidth * saveScale);
        float scaleHeight = Math.round(origHeight * saveScale);
        fillAbsoluteOffset();
		final float x = absoluteOffsetX;
		final float y = absoluteOffsetY;
        if (scaleWidth < viewWidth) {
            deltaX = 0;
            if (y + deltaY > 0)
                deltaY = -y;
            else if (y + deltaY < -bottom)
                deltaY = -(y + bottom);
        } else if (scaleHeight < viewHeight) {
            deltaY = 0;
            if (x + deltaX > 0)
                deltaX = -x;
            else if (x + deltaX < -right)
                deltaX = -(x + right);
        }
        else {
            if (x + deltaX > 0)
                deltaX = -x;
            else if (x + deltaX < -right)
                deltaX = -(x + right);

            if (y + deltaY > 0)
                deltaY = -absoluteOffsetY;
            else if (y + deltaY < -bottom)
                deltaY = -(y + bottom);
        }
        postTranslate(deltaX, deltaY);
        checkSiding();
    }

	/**
	 * 鍙栧緱骞崇Щ閲�	 * @return 骞崇Щ閲�	 */
	public PointF getAbsoluteOffset (){
		fillAbsoluteOffset();
		return new PointF(absoluteOffsetX, absoluteOffsetY);
	}

	public float getScale(){
		return saveScale;
	}

    private void checkSiding() {
        fillAbsoluteOffset();
		final float x = absoluteOffsetX;
		final float y = absoluteOffsetY;
        float scaleWidth = Math.round(origWidth * saveScale);
        float scaleHeight = Math.round(origHeight * saveScale);
        onLeftSide = onRightSide = onTopSide = onBottomSide = false;
        if (-x < 10.0f ) onLeftSide = true;
        if ((scaleWidth >= viewWidth && (x + scaleWidth - viewWidth) < 10) ||
            (scaleWidth <= viewWidth && -x + scaleWidth <= viewWidth)) onRightSide = true;
        if (-y < 10.0f) onTopSide = true;
        if (Math.abs(-y + viewHeight - scaleHeight) < 10.0f) onBottomSide = true;
    }
    
    private void calcPadding(){
        right = viewWidth * saveScale - viewWidth - (2 * redundantXSpace * saveScale);
        bottom = viewHeight * saveScale - viewHeight - (2 * redundantYSpace * saveScale);
    }
    
    private void fillAbsoluteOffset (){
        imageUsingMatrix.getValues(matrixValues);
        absoluteOffsetX = matrixValues[Matrix.MTRANS_X];
        absoluteOffsetY = matrixValues[Matrix.MTRANS_Y];
//		System.out.println(String.format("::: 鍥惧儚鐨勭粷瀵瑰亸绉诲潗鏍�(%f,%f) ::::",absoluteOffsetX,absoluteOffsetY));
	}
    
    @Override
    public void setImageBitmap(Bitmap bm) {
        bmWidth = bm.getWidth();
        bmHeight = bm.getHeight();
        super.setImageBitmap(bm);
    }


    
    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		float newWidth = MeasureSpec.getSize(widthMeasureSpec);
		float newHeight= MeasureSpec.getSize(heightMeasureSpec);

		viewWidth = newWidth;
        viewHeight = newHeight;

		initSize();
		calcPadding();
    }

	private void initSize(){
		// 鍒濆鏃讹紝鍥剧墖涓嶸iew杈圭紭涔嬮棿鐨勮窛绂�		
		redundantYSpace = viewHeight - (saveScale * bmHeight) ;
		redundantXSpace = viewWidth - (saveScale * bmWidth);

		redundantYSpace /= (float)2;
		redundantXSpace /= (float)2;

		origWidth = viewWidth - 2 * redundantXSpace;
		origHeight = viewHeight - 2 * redundantYSpace;
	}

	private double distanceBetween(PointF left, PointF right){
        return Math.sqrt(Math.pow(left.x - right.x, 2) + Math.pow(left.y - right.y, 2));
    }

	/**
	 * 璁＄畻涓や釜鎵嬫寚涔嬮棿鐨勮窛绂�	 * @param event 瑙︽懜鏁寸殑
	 * @return 璺濈
	 */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

	public void postScaleToImage(float scaleFactor,float scaleFocusX,float scaleFocusY){
		float origScale = saveScale;
		saveScale *= scaleFactor;
		if (saveScale > maxScale) {
			saveScale = maxScale;
			scaleFactor = maxScale / origScale;
		} else if (saveScale < minScale) {
			saveScale = minScale;
			scaleFactor = minScale / origScale;
		}
		right = viewWidth * saveScale - viewWidth - (2 * redundantXSpace * saveScale);
		bottom = viewHeight * saveScale - viewHeight - (2 * redundantYSpace * saveScale);

		// 鏄剧ず鐨勫浘鐗囨瘮婧愬浘鐗囪灏忔椂杩涜缂╂斁
		if (origWidth * saveScale <= viewWidth || origHeight * saveScale <= viewHeight) {
			final float scaleCenterX = viewWidth / 2;
			final float scaleCenterY = viewHeight / 2;
			postScale(scaleFactor, scaleCenterX, scaleCenterY);

			//鍦ㄨ竟缂樻椂缂╁皬鍥剧墖鐨勫钩绉讳慨姝�			
			if (scaleFactor < 1) {
				fillAbsoluteOffset();
				final float x = absoluteOffsetX;
				final float y = absoluteOffsetY;
				if (scaleFactor < 1) {
					if (Math.round(origWidth * saveScale) < viewWidth) {
						float deltaX = 0,deltaY = 0;
						if (y < -bottom){
							deltaY = -(y + bottom);
							postTranslate(deltaX, deltaY);
						}
						else if (y > 0){
							deltaY = -y;
							postTranslate(deltaX, deltaY);
						}
					} else {
						float deltaX = 0,deltaY = 0;
						if (x < -right){
							deltaX = -(x + right);
							postTranslate(deltaX, deltaY);
						}
						else if (x > 0){
							deltaX = -x;
							postTranslate(deltaX, deltaY);
						}
					}
				}
			}
		} else {
			// 鍥剧墖宸茬粡琚斁澶э紝浠ヨЕ鎽哥偣涓轰腑蹇冿紝缂╂斁鍥剧墖銆�			
			postScale(scaleFactor, scaleFocusX, scaleFocusY);
			fillAbsoluteOffset();
			final float x = absoluteOffsetX;
			final float y = absoluteOffsetY;

			// 缂╁皬鍥剧墖鏃讹紝濡傛灉鍦ㄥ浘鐗囪竟缂樼缉灏忥紝涓洪伩鍏嶈竟缂樺嚭鐜扮┖鐧斤紝瀵瑰浘鐗囪繘琛屽钩绉诲鐞�			
			if (scaleFactor < 1) {
				float deltaX = 0,deltaY = 0;
				if (x < -right){
					deltaX = -(x + right);
					deltaY = 0;
					postTranslate(deltaX, deltaY);
				}
				else if (x > 0){
					deltaX = -x;
					deltaY = 0;
					postTranslate(deltaX, deltaY);
				}
				if (y < -bottom){
					deltaX = 0;
					deltaY = -(y + bottom);
					postTranslate(deltaX, deltaY);
				}
				else if (y > 0){
					deltaX = 0;
					deltaY = -y;
					postTranslate(deltaX, deltaY);
				}
			}
		}
		postInvalidate();
	}
    
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mode = ZOOM;
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            postScaleToImage(scaleFactor,detector.getFocusX(),detector.getFocusY());
            return true;
        }
    }
}