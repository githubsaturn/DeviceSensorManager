package com.bigdeli.kasra.devicesensormanager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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

    int w, h;
    SensorDataHolder sensor;

    public void setSensor(SensorDataHolder sensor){
        this.sensor =sensor;
    }


    private void resizeGraphics(int w) {

    }

    private void init() {

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
        w = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        h = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
//        Log.d("test", "2 ___ widthMeasureSpec= " + w + "    heightMeasureSpec=" + h);
//        Log.d("test", "3 ___ getSuggestedMinimumWidth= " + getSuggestedMinimumWidth() + "    getSuggestedMinimumHeight=" + getSuggestedMinimumHeight());
//        Log.d("test", "4 ___ widthMeasureSpec= " + MeasureSpec.getSize(widthMeasureSpec) + "    heightMeasureSpec=" + MeasureSpec.getSize(heightMeasureSpec));

        h = (int) (w * 1.0 / 1.6);
        resizeGraphics(w);

        setMeasuredDimension(w, h);
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

       // canvas.drawColor(Color.argb(30,0,0,0));

    }
}
