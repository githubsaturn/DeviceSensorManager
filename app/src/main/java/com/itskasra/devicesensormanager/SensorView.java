package com.itskasra.devicesensormanager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by kbigdeli on 1/7/2015.
 */
public class SensorView extends View {

    private int w, h;
    private SensorDataHolder sensor;
    private Paint p = new Paint();
    private float[] hsv = {0, 1, 0.68f};
    private Paint paintBlack = new Paint();

    public void setSensor(SensorDataHolder sensor) {
        this.sensor = sensor;
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


        paintBlack.setColor(Color.rgb(40, 40, 40));
        paintBlack.setTextSize(h / 9);

        setMeasuredDimension(w, h);
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // canvas.drawColor(Color.argb(30,0,0,0));

        int x0 = w / 2;
        int range = (int) (w * 0.4);

        if (!sensor.getIsBidirectional()) {
            x0 = (int) (w * 0.1);
            range = (int) (w * 0.8);
        }


        int textX = x0 + w / 40;

        double xRatio = sensor.getLastX() / sensor.getMaxValue();
        double yRatio = sensor.getLastY() / sensor.getMaxValue();
        double zRatio = sensor.getLastZ() / sensor.getMaxValue();
        int hRow = h / 7;
        int gap = h / 5;

        int startingY = (int) (h * 0.60);
        hsv[0] = (int) (90 + 268 * Math.abs(xRatio));
        p.setColor(Color.HSVToColor(175, hsv));

        if (xRatio >= 0)
            canvas.drawRect(x0, startingY, x0 + (int) (range * xRatio), startingY + hRow, p);
        else
            canvas.drawRect(x0 + (int) (range * xRatio), startingY, x0, startingY + hRow, p);

        canvas.drawText(String.format("%.2f", sensor.getLastX()), textX, startingY + (int) (hRow * 0.75), paintBlack);


        startingY -= gap;
        hsv[0] = (int) (90 + 268 * Math.abs(yRatio));
        p.setColor(Color.HSVToColor(175, hsv));

        if (yRatio >= 0)
            canvas.drawRect(x0, startingY, x0 + (int) (range * yRatio), startingY + hRow, p);
        else
            canvas.drawRect(x0 + (int) (range * yRatio), startingY, x0, startingY + hRow, p);

        canvas.drawText(String.format("%.2f", sensor.getLastY()), textX, startingY + (int) (hRow * 0.75), paintBlack);


        startingY -= gap;
        hsv[0] = (int) (90 + 268 * Math.abs(zRatio));
        p.setColor(Color.HSVToColor(175, hsv));

        if (zRatio >= 0)
            canvas.drawRect(x0, startingY, x0 + (int) (range * zRatio), startingY + hRow, p);
        else
            canvas.drawRect(x0 + (int) (range * zRatio), startingY, x0, startingY + hRow, p);

        canvas.drawText(String.format("%.2f", sensor.getLastZ()), textX, startingY + (int) (hRow * 0.75), paintBlack);


        invalidate();

    }
}
