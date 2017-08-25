package com.example.aaa.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.aaa.myapplication.utis.MathUtils;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/8/23.
 */

public class MyViewGroup extends FrameLayout {

    private BackgroundView backgroundView;
    private LineView lineView;
    private Button mBtReset;
    private TextView textView;
    private String formatStr = "%sHz  |  %sdB  |  频宽%s";
    private final int[] markArr = {0, 30, 50, 100, 200, 500, 1000, 2000, 4000, 6000, 10000, 16000};
    private static int mWidthUnit = 0;//网格宽度单位

    public MyViewGroup(@NonNull Context context) {
        this(context, null);
    }

    public MyViewGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyViewGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        backgroundView = new BackgroundView(context);
        lineView = new LineView(context);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        mWidthUnit = width / 12;

        addView(backgroundView);
        LayoutParams layoutParams = new LayoutParams(width, backgroundView.getBackgroundHeight(), Gravity.CENTER);
        addView(lineView, layoutParams);

        //init text
        textView = new TextView(context);
        textView.setTextSize(12);
        textView.setTextColor(Color.WHITE);
        textView.setText(String.format(formatStr, "0", "0", "0.6"));
        textView.setPadding(12, 12, 0, 0);
        addView(textView);

        lineView.setOnPointMoveListener(new LineView.OnPointMoveListener() {
            @Override
            public void onPointMove(int valueX, int valueY) {
                textView.setText(String.format(formatStr, caculateValueHz(valueX), caculateValuedB(valueY), "0.6"));
            }
        });
    }

    private String caculateValuedB(int valueY) {
        int height = backgroundView.getBackgroundHeight();

        if (valueY > 0) {
            if (valueY > height / 2) {
                //负值
                int i = valueY - height / 2;
                return String.valueOf(-i * 30 / height);
            } else if (valueY < height / 2) {
                //正值
                int i = height / 2 - valueY;
                return String.valueOf(i * 30 / height);
            } else return "0";
        }
        return "0";
    }

    private String caculateValueHz(int valueX) {
        System.out.println("valueX=" + valueX + "单位值：" + mWidthUnit);
        int unit = 0;
        int min = markArr[0];
        int max = markArr[markArr.length - 1];
        int rowIndex = valueX / mWidthUnit;//点击位置在水平位置区块索引
        switch (rowIndex) {
            case 0:
                return String.valueOf(min);
            case 11:
                return String.valueOf(max);
            default:
                min = markArr[rowIndex];
                max = markArr[rowIndex + 1];
                unit = max - min;
                System.out.println("-----------------start------------------");
                System.out.println("索引值：" + rowIndex + "最小值：" + min + "最大值：" + max + "unit=" + unit);


                double percent = MathUtils.div(valueX - mWidthUnit * rowIndex, mWidthUnit);
                System.out.println("百分比："+percent);
                double i = new BigDecimal(percent * unit).setScale(0,BigDecimal.ROUND_HALF_UP).doubleValue();
                System.out.println("结果：" + i);
                System.out.println("------------------end-----------------");
                return String.valueOf(min + i);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        textView.measure(widthMeasureSpec, heightMeasureSpec);
    }

    public void reset() {
        lineView.reset();
        //reset text
        textView.setText(String.format(formatStr, "0", "0", "1"));
    }

    /**
     * 移除当前点
     */
    public void removeCurrentPoint(){
        lineView.removeSelectPoint();
    }
}
