package com.example.aaa.myapplication;

import android.content.Context;
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

/**
 * Created by Administrator on 2017/8/23.
 */

public class MyViewGroup extends FrameLayout{

    private BackgroundView backgroundView;
    private LineView lineView;
    private Button mBtReset;

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

        addView(backgroundView);
        LayoutParams layoutParams = new LayoutParams(width, backgroundView.getBackgroundHeight(), Gravity.CENTER);
        addView(lineView, layoutParams);
    }

    public void reset(){
        lineView.reset();
    }
}
