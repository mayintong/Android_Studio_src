package net.yoojia.imagemap.core;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import net.yoojia.imagemap.support.ScaleUtility;

/**
 * Created by Lyy on 2015/9/27.
 */
public class Lines extends Shape {

    private PointF start;
    private PointF end;
    private float wid;

    public Lines(Object tag, int coverColor) {
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
        drawPaint.setAlpha(255);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(wid);
        canvas.drawLine(start.x, start.y, end.x, end.y, drawPaint);
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
        wid *= scale;
        end.set(end.x *= scale, end.y *= scale);
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
