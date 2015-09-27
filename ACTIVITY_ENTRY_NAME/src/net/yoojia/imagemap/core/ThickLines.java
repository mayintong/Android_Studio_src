package net.yoojia.imagemap.core;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;

import net.yoojia.imagemap.support.ScaleUtility;

/**
 * Created by Lyy on 2015/9/27.
 */
public class ThickLines extends Shape {

    private PointF start;
    private PointF end;
    private float wid;
    private Path path;
    public ThickLines(Object tag, int coverColor) {
        super(tag, coverColor);
    }

    /**
     * Set Center,radius
     * @param coords centerX,CenterY,radius
     */
    @Override
    public void setValues(float...coords){

        final float startX = coords[0];
        final float startY = coords[1];
        final float endX = coords[2];
        final float endY = coords[3];

        this.start = new PointF(startX, startY);
        this.end = new PointF(endX, endY);
        if (coords.length > 4)
            this.wid = coords[4];
    }

    public void setRadius(float radius) {

    }

    @Override
    public PointF getCenterPoint(){
        return new PointF((start.x + end.x) / 2, ( start.y + end.y ) / 2 );
    }

    @Override
    public void draw(Canvas canvas) {
        drawPaint.setAlpha(100);
        double x1,x2,y1,y2,k;
        if (start.y != end.y){
            if (start.x != end.x) {
                k = (start.x - end.x) / (end.y - start.y);
                x1 = start.x - (1 / Math.sqrt(k * k + 1)) * wid;
                x2 = end.x + (1 / Math.sqrt(k * k + 1)) * wid;
                y1 = start.y + (k / Math.sqrt(k * k + 1) * wid);
                y2 = end.y - (k / Math.sqrt(k * k + 1) * wid);
            }
            else{
                x1 = start.x - wid;
                x2 = end.x + wid;
                y1 = start.y;
                y2 = end.y;
            }

        }
        else {
            if(start.x !=end.x) {
                x1 = start.x;
                x2 = end.x;
                y1 = start.y - wid;
                y2 = end.y + wid;
            }
            else {
                return;
            }

        }
        canvas.drawRect((float)x1,(float) y1, (float)x2, (float)y2, drawPaint);
    }

    @Override
    public void scaleBy (float scale, float centerX, float centerY) {
        PointF newStart = ScaleUtility.scaleByPoint(start.x, start.y, centerX, centerY, scale);
        PointF newEnd = ScaleUtility.scaleByPoint(end.x, end.y, centerX, centerY, scale);
        start.set(newStart.x, newStart.y);
        end.set(newEnd.x, newEnd.y);
        wid *= scale;
    }

    @Override
    public void onScale(float scale){
//		scaleBy = (float)Math.sqrt(scaleBy);
        start.set( start.x *= scale , start.y *= scale );
        end.set(end.x *= scale, end.y *= scale);
        wid *= scale;
    }

    @Override
    public void translate(float deltaX, float deltaY) {
        start.x += deltaX;
        start.y += deltaY;
        end.x += deltaX;
        end.y += deltaY;
    }

    @Override
    public boolean inArea(float x, float y) {
        boolean ret = false;
//        float dx = center.x - Math.abs(x);
//        float dy = center.y - Math.abs(y);
//        float d = (float)Math.sqrt((dx*dx)+(dy*dy));
//        if (d<radius) {
//            ret = true;
//        }
        return ret;
    }

}
