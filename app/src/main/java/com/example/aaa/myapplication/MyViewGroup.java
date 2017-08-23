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
import android.widget.FrameLayout;

/**
 * Created by Administrator on 2017/8/23.
 */

public class MyViewGroup extends FrameLayout {

    private BackgroundView backgroundView;
    private LineView lineView;

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


    private void moveLineViewTo(float dx, float dy) {
        int top = lineView.getTop();
        lineView.setTranslationY(top + dy);
    }

    public void testMove() {
        lineView.setTranslationY(100);
    }

//    private int lastX,lastY;
//    @Override
//    public boolean onTouch(View view, MotionEvent motionEvent) {
//
//        switch (motionEvent.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                lastY = (int) motionEvent.getRawY();
//                break;
//            case MotionEvent.ACTION_MOVE:
//
//                float dealtY = motionEvent.getRawY() - lastY;
//                System.out.println("移动：" + dealtY);
////                moveLineViewTo(0, dealtY);
//                view.setTranslationY(view.getTop()+dealtY);
//                lastY = (int) motionEvent.getRawY();
//                break;
//        }
//        return true;
//    }
}
