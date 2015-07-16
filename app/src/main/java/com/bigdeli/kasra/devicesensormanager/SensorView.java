package com.bigdeli.kasra.devicesensormanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by kbigdeli on 1/7/2015.
 */
public class SensorView extends View {

//    public interface onJoyStickClickedListener {
//        void onItemSelected(int idx);
//    }
//
//    private onJoyStickClickedListener mClickListener;


    private void resizeGraphics(int w) {

    }

    private void init(){

    }

    public SensorView(Context context) {
        super(context, null);
        init();
    }

    public SensorView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init();
    }

    public SensorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        Log.d("test", "1 ___ widthMeasureSpec= " + widthMeasureSpec + "    heightMeasureSpec=" + heightMeasureSpec);
        int w = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int h = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
//        Log.d("test", "2 ___ widthMeasureSpec= " + w + "    heightMeasureSpec=" + h);
//        Log.d("test", "3 ___ getSuggestedMinimumWidth= " + getSuggestedMinimumWidth() + "    getSuggestedMinimumHeight=" + getSuggestedMinimumHeight());
//        Log.d("test", "4 ___ widthMeasureSpec= " + MeasureSpec.getSize(widthMeasureSpec) + "    heightMeasureSpec=" + MeasureSpec.getSize(heightMeasureSpec));

        if (w > h)
            w = h;

        resizeGraphics(w);


        setMeasuredDimension(w, h);
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        boolean consumedTouch = true;
        if ((event.getAction() != MotionEvent.ACTION_DOWN) && (event.getActionMasked() != MotionEvent.ACTION_POINTER_DOWN))
            return consumedTouch;
        int pointerIndex = event.getActionIndex();
        float x0 = event.getX(pointerIndex);
        float y0 = event.getY(pointerIndex);
//        Log.i("test", "____ x=" + event.toString() + "    y=" + y);

        this.postInvalidate();
        return consumedTouch;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.RED);

    }
}
