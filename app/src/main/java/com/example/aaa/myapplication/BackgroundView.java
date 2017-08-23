package com.example.aaa.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by Administrator on 2017/8/23.
 */

public class BackgroundView extends View{

    private Context mContext;
    private int width;
    private Paint mPaint;
    private int height;
    private final String[] arrVaule = {"30","50","100","200","500","1k","2k","4k","6k","10k(Hz)"};

    public BackgroundView(Context context) {
        this(context,null);
    }

    public BackgroundView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BackgroundView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        width = display.getWidth();
        height = width * 2 /3;

        //init paint
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setARGB(88,255,255,255);
        mPaint.setStrokeWidth(5);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(width, height);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //背景
        canvas.drawColor(Color.parseColor("#408998"));

        //网格 height=1/3 的width
        int netHeight= height/3;
        int netWidth = width/24;//单位宽度

        mPaint.setARGB(30,255,255,255);
        mPaint.setStrokeWidth(3);

        canvas.drawLine(0,height/2 - netHeight,width,height/2 - netHeight,mPaint);
        canvas.drawLine(0,height/2 + netHeight,width,height/2 + netHeight,mPaint);
        //base line
        canvas.drawLine(0,height/2,width,height/2,mPaint);

        for (int i = 0; i < 24; i++) {
            canvas.drawLine(netWidth*i,0,netWidth*i,height,mPaint);
        }

        //draw text
        mPaint.setColor(Color.parseColor("#ffffff"));
        mPaint.setTextSize(40);
        mPaint.setTextAlign(Paint.Align.CENTER);
        for (int i = 0; i < arrVaule.length; i++) {
            if (i == arrVaule.length-1) mPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(arrVaule[i],2*netWidth*(i+1),height-30,mPaint);
        }

    }

    public int getBackgroundHeight(){
        return height;
    }
}
