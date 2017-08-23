package com.example.aaa.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/23.
 */

public class LineView extends View {

    private int width;
    private Paint mPaint;
    private int mTolerance = 15;//容差，点击直线偏差距离
    private ArrayList<float[]> mPointList = new ArrayList<>();
    private final int mPointRadius = 10;
    private Canvas mCanvas;
    private boolean drawPoint = true;

    public LineView(Context context) {
        this(context, null);
    }

    public LineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();

        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#ffffff"));
        mPaint.setStrokeWidth(4);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCanvas.drawPoint(100,100,mPaint);
            }
        });
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        setMeasuredDimension(width, height);
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCanvas = canvas;
        canvas.drawLine(0, getMeasuredHeight() / 2, width, getMeasuredHeight() / 2, mPaint);
        //init point list
        mPointList.add(new float[]{0, getMeasuredHeight() / 2});
        mPointList.add(new float[]{width, getMeasuredHeight() / 2});
        mPaint.setStrokeWidth(20);
        canvas.drawPoint(mPointList.get(0)[0], mPointList.get(0)[1], mPaint);
        canvas.drawPoint(mPointList.get(1)[0], mPointList.get(1)[1], mPaint);
    }

    private int lastX, mLastY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float rawX = event.getRawX();
        float rawY = event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPoint = true;
                System.out.println("ACTION_DOWN 点击坐标: " + event.getY());
                System.out.println("ACTION_DOWN 直线位置: " + getMeasuredHeight() / 2);
                if (Math.abs(event.getY() - getMeasuredHeight() / 2) > mTolerance) return false;
                break;
            case MotionEvent.ACTION_MOVE:
                drawPoint = false;
                float dealtY = rawY - mLastY;
                System.out.println("移动：" + dealtY);
//                moveLineViewTo(0, dealtY);
                setTranslationY(getTranslationY() + dealtY);

                break;
            case MotionEvent.ACTION_UP:
                //draw point
//                if (drawPoint)
//                    addPoint(event.getRawX(), event.getRawY());
                break;
        }
        mLastY = (int) rawY;
        return true;
    }

    private void addPoint(float dx, float dy) {
        if (Math.abs(dy - mLastY) <= mTolerance) {
            mCanvas.drawPoint(dx, mLastY, mPaint);
        }
    }
}
