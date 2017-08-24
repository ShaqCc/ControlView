package com.example.aaa.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.aaa.myapplication.demo.Cubic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/23.
 */

public class LineView extends View {

    private int width;
    private int height;
    private Paint mPaint;
    private int mTolerance = 15;//容差，点击直线偏差距离
    private int widthUnit;//单元格宽度
    private final int LINE_WIDTH = 3;
    private ArrayList<float[]> mPointList = new ArrayList<>();
    private boolean drawPoint = true;
    private boolean initPoint = false;
    private int currentPointIndex;//当前选中的点索引

    private Path linePath;
    private Path curvePath;
    List<Integer> points_x;
    List<Integer> points_y;

    boolean drawLineFlag;
    boolean drawCurveFlag;

    private static int STEPS = 12;//曲线精度


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
        widthUnit = width / 12;
        initObj();
    }

    /**
     * 初始化.
     */
    private void initObj() {
        mPaint = new Paint();
        linePath = new Path();
        curvePath = new Path();
        points_x = new LinkedList<Integer>();
        points_y = new LinkedList<Integer>();
        drawLineFlag = true;
        drawCurveFlag = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        height = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!initPoint) {
            //init point list
            mPointList.add(new float[]{0, getMeasuredHeight() / 2});
            mPointList.add(new float[]{width, getMeasuredHeight() / 2});
            initPoint = true;
        }
        //draw point
        drawPoints(canvas);

        linePath.reset();
        curvePath.reset();

        //draw lines
        drawLines(canvas);

        //draw curve
        if (mPointList.size() > 2) {
            drawCurve(canvas);
        }
    }

    /**
     * 画曲线
     *
     * @param canvas
     */
    private void drawCurve(Canvas canvas) {
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(LINE_WIDTH);
        points_x.clear();
        points_y.clear();
        for (int i = 0; i < mPointList.size(); i++) {
            points_x.add((int) mPointList.get(i)[0]);
            points_y.add((int) mPointList.get(i)[1]);
        }

        List<Cubic> calculate_x = calculate(points_x);
        List<Cubic> calculate_y = calculate(points_y);
        curvePath.moveTo(calculate_x.get(0).eval(0), calculate_y.get(0).eval(0));

        for (int i = 0; i < calculate_x.size(); i++) {
            for (int j = 1; j <= STEPS; j++) {
                float u = j / (float) STEPS;
                curvePath.lineTo(calculate_x.get(i).eval(u), calculate_y.get(i)
                        .eval(u));
            }
        }
        canvas.drawPath(curvePath, mPaint);
    }

    private List<Cubic> calculate(List<Integer> x) {
        int n = x.size() - 1;
        float[] gamma = new float[n + 1];
        float[] delta = new float[n + 1];
        float[] D = new float[n + 1];
        int i;
        gamma[0] = 1.0f / 2.0f;
        for (i = 1; i < n; i++) {
            gamma[i] = 1 / (4 - gamma[i - 1]);
        }
        gamma[n] = 1 / (2 - gamma[n - 1]);

        delta[0] = 3 * (x.get(1) - x.get(0)) * gamma[0];
        for (i = 1; i < n; i++) {
            delta[i] = (3 * (x.get(i + 1) - x.get(i - 1)) - delta[i - 1])
                    * gamma[i];
        }
        delta[n] = (3 * (x.get(n) - x.get(n - 1)) - delta[n - 1]) * gamma[n];

        D[n] = delta[n];
        for (i = n - 1; i >= 0; i--) {
            D[i] = delta[i] - gamma[i] * D[i + 1];
        }

        List<Cubic> cubics = new LinkedList<Cubic>();
        for (i = 0; i < n; i++) {
            Cubic c = new Cubic(x.get(i), D[i], 3 * (x.get(i + 1) - x.get(i))
                    - 2 * D[i] - D[i + 1], 2 * (x.get(i) - x.get(i + 1)) + D[i]
                    + D[i + 1]);
            cubics.add(c);
        }
        return cubics;
    }


    /**
     * 划线
     *
     * @param canvas
     */
    private void drawLines(Canvas canvas) {
        if (mPointList.size() == 2) {
            mPaint.setColor(Color.WHITE);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(LINE_WIDTH);
            linePath.moveTo(mPointList.get(0)[0], mPointList.get(0)[1]);
            for (int i = 1; i < mPointList.size(); i++) {
                linePath.lineTo(mPointList.get(i)[0], mPointList.get(i)[1]);
            }
            canvas.drawPath(linePath, mPaint);
        }
    }

    /**
     * 画点
     *
     * @param canvas
     */
    private void drawPoints(Canvas canvas) {
        mPaint.setStrokeWidth(28);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        for (int i = 1; i < mPointList.size() - 1; i++) {
            canvas.drawPoint(mPointList.get(i)[0], mPointList.get(i)[1], mPaint);
        }
    }

    private int lastX, mLastY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPoint = true;
                float relativeY = event.getY();
//                if (Math.abs(relativeY - getMeasuredHeight() / 2) > mTolerance) {
//                    //点击线以外了
//                    TAG_CURRENT = TAG_ELSE;
//                    return false;
//                } else {
                //点击线上,判断点到点还是线
                switchPointType(event.getX(), event.getY());
//                }
                break;
            case MotionEvent.ACTION_MOVE:
                drawPoint = false;
                int deltaY = y - mLastY;
                switch (TAG_CURRENT) {
                    case TAG_LINE:
                        if (!(getCurrentY() <= 50 || getCurrentY() >= height - 50))
                            setTranslationY(getTranslationY() + deltaY);
                        break;
                    case TAG_POINT:
                        if (event.getY() + getTranslationY() <= 50 || event.getY() + getTranslationY() >= height - 50)//判断点的移动范围
                        {
                            mPointList.remove(currentPointIndex);
                            TAG_CURRENT = TAG_ELSE;
                        } else {
                            mPointList.get(currentPointIndex)[0] = event.getX();
                            mPointList.get(currentPointIndex)[1] = event.getY();
                        }
                        invalidate();
                        break;
                }

                break;
            case MotionEvent.ACTION_UP:
                //draw point
                if (drawPoint) {
                    System.out.println("点击原始位置getRawY()=" + event.getRawX() + "      相对位置getY()=" + event.getY());
                    addPoint(event.getRawX(), event.getY());
                }
                System.out.println("直线当前位置：" + getCurrentY());
                break;
        }
        mLastY = y;
        return true;
    }

    private final int TAG_POINT = 1;//触点在点上
    private final int TAG_LINE = 2;//触点在线上
    private final int TAG_ELSE = 3;//无用触点

    private int TAG_CURRENT = TAG_ELSE;//初始化

    public void addPoint(float dx, float dy) {
        float positionY = dy + getTranslationY();
        System.out.println("添加点，Y=" + positionY);
//        if (Math.abs(positionY - getCurrentY()) < mTolerance) {
        //判断是否添加点
        if (canAddPoint(dx)) {
            mPointList.add(new float[]{dx, dy});
            //排序
            Collections.sort(mPointList, new SortPoint());
            invalidate();
        } else {
            Toast.makeText(getContext(), "空间太小了", Toast.LENGTH_SHORT).show();
        }
//        }
    }

    /**
     * @return 当前直线 Y轴 纵坐标
     */
    public int getCurrentY() {
        return (int) (getTranslationY() + height / 2);
    }

    private boolean canAddPoint(float x) {
        for (int i = 0; i < mPointList.size(); i++) {
            if (Math.abs(mPointList.get(i)[0] - x) <= 30) {
                //点到point上了
                Toast.makeText(getContext(), "这是第" + i + "个点", Toast.LENGTH_SHORT).show();
                return false;
            } else if (Math.abs(mPointList.get(i)[0] - x) < widthUnit) return false;
        }
        return true;
    }

    /**
     * 判断点击到哪里了
     *
     * @param x
     */
    private void switchPointType(float x, float y) {
        for (int i = 0; i < mPointList.size(); i++) {
            if (i == 0 || i == mPointList.size() - 1) {
                TAG_CURRENT = TAG_LINE;
                continue;
            }
            if (Math.abs(mPointList.get(i)[0] - x) <= 30 && Math.abs(mPointList.get(i)[1] - y) <= 30) {
                //点到point上了
                TAG_CURRENT = TAG_POINT;
                currentPointIndex = i;
                System.out.println("Type=点");
                break;
            } else {
                TAG_CURRENT = TAG_LINE;
                System.out.println("Type=线");
            }
        }
    }


    public void reset() {
        for (int i = 1; i < mPointList.size() - 1; i++) {
            mPointList.remove(i);
        }
        setTranslationY(0);
        invalidate();
    }

    class SortPoint implements Comparator<float[]> {
        @Override
        public int compare(float[] o1, float[] o2) {
            return o1[0] > o2[0] ? 1 : -1;
        }
    }
}
