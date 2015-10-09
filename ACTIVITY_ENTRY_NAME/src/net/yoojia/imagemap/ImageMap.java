package net.yoojia.imagemap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.Toast;

import net.yoojia.imagemap.core.Bubble;
import net.yoojia.imagemap.core.Shape;
import net.yoojia.imagemap.core.ShapeExtension;
import net.yoojia.imagemap.support.TranslateAnimation;

import static android.os.SystemClock.sleep;


/**
 * author :  chenyoca@gmail.com
 * date   :  2013-5-19
 * An HTML map like widget in an Android view controller
 */
public class ImageMap extends FrameLayout implements ShapeExtension,ShapeExtension.OnShapeActionListener,
															 TranslateAnimation.OnAnimationListener
{

    public HighlightImageView highlightImageView;
    private Bubble[] bubble;
    private int bubble_count;
	private View viewForAnimation;

    public ImageMap(Context context) {
        this(context,null);
    }

    public ImageMap(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialImageView(context);
    }

    public ImageMap(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialImageView(context);
    }

    private void initialImageView(Context context){

        highlightImageView = new HighlightImageView(context);
        highlightImageView.setOnShapeClickListener(this);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(highlightImageView, params);
        viewForAnimation= new View(context);
		addView(viewForAnimation,0,0);
        //test
        bubble  = new Bubble[100];
        bubble_count = 0;
    }

    /**
     * Set a bubble view controller and it's renderDelegate interface.
     * @param bubbleView A view controller object for display on image map.
     * @param renderDelegate The display interface for bubble view controller render.
     */
    public void setBubbleView(View bubbleView,Bubble.RenderDelegate renderDelegate){
        if(bubbleView == null){
            throw new IllegalArgumentException("View for bubble cannot be null !");
        }
        bubble[bubble_count] = new Bubble(bubbleView);
        bubble[bubble_count].setRenderDelegate(renderDelegate);
        addView(bubble[bubble_count]);
        bubble[bubble_count].view.setVisibility(View.INVISIBLE);
        bubble_count =( bubble_count + 1) % 100;
    }

	/**
	 * 添加Shape，并关联到Bubble的位置
	 * - Add a shape and set reference to the bubble.
	 * @param shape Shape
	 */
    public void addShapeAndRefToBubble(final Shape shape){
        addShape(shape);
        if(bubble_count != 0){
            shape.createBubbleRelation(bubble[(bubble_count-1)%100]);
            bubble[(bubble_count-1)%100].showAtShape(shape);
        }
    }

	@Override
	public void onTranslate (float deltaX, float deltaY) {
		highlightImageView.moveBy(deltaX, deltaY);
	}

    @Override
    public void addShape(Shape shape) {

		float scale = highlightImageView.getScale();
		shape.onScale(scale);

		// 将图像中心移动到目标形状的中心坐标上
		// Move the center point of the image to the target shape center.

//		PointF from = highlightImageView.getAbsoluteCenter();
//		PointF to = shape.getCenterPoint();
//		TranslateAnimation movingAnimation = new TranslateAnimation(from.x,to.x,from.y,to.y);
//		movingAnimation.setOnAnimationListener(this);
//		movingAnimation.setInterpolator(new DecelerateInterpolator());
//		movingAnimation.setDuration(500);
//		movingAnimation.setFillAfter(true);
//		viewForAnimation.startAnimation(movingAnimation);

		PointF offset = highlightImageView.getAbsoluteOffset();
		shape.onTranslate(offset.x , offset.y);
		highlightImageView.addShape(shape);


    }

    @Override
    public void removeShape(Object tag) {
        highlightImageView.removeShape(tag);
    }

    @Override
    public void clearShapes() {
        int i;
        for(Shape item : highlightImageView.getShapes()){
            item.cleanBubbleRelation();
        }
        highlightImageView.clearShapes();
		if (bubble_count != 0){
			for (i=0;i<bubble_count;i++) {
                bubble[i].view.setVisibility(View.GONE);
            }
		}
        bubble_count = 0;
    }

    @Override
    public final void onShapeClick(Shape shape, float xOnImage, float yOnImage) {
//        int i;
//        for(Shape item : highlightImageView.getShapes()){
//            item.cleanBubbleRelation();
//        }
//        if(bubble_count != 0){
//            for (i=0;i<bubble_count;i++){
//                bubble[i].showAtShape(shape);
//            }
//
//        }

        Toast.makeText(this.getContext(), "已找到好友！",
                Toast.LENGTH_SHORT).show();
    }
    /**
     * set a bitmap for image map.
     * @param bitmap image
     */
    public void setMapBitmap(Bitmap bitmap){
        highlightImageView.setImageBitmap(bitmap);
    }


}
