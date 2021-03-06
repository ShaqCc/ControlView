package com.example.aaa.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

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
    private ArrayList<float[]> mEdleList = new ArrayList<>();
    private boolean drawPoint = true;
    private boolean initPoint = false;
    private int currentPointIndex;//当前选中的点索引
    private int currentSignlePtIndex;//当前选中的孤立点索引

    private Path linePath;
    private Path curvePath;
    List<Integer> points_x;
    List<Integer> points_y;

    boolean drawLineFlag;
    boolean drawCurveFlag;

    private static int STEPS = 12;//曲线精度

    private OnPointMoveListener moveListener;
    private int mGlobal_padding = 10;

    private final static int LEFT_DIRECTION = -1;//点 向左移动
    private final static int RIGHT_DIRECTION = 1;//点 向右移动
    private final static int VERTICAL = 0;//点 停止状态 | 竖直移动
    private int HORIZONTAL_TAG = VERTICAL;//点 当前移动方向


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
        drawEdlePoints(canvas);

        linePath.reset();
        curvePath.reset();

        //draw lines
//        drawLines(canvas);

        //draw curve
        if (mPointList.size() > 1) {
            drawCurve(canvas);
        }
    }

    /**
     * 画单独的点
     *
     * @param canvas
     */
    private void drawEdlePoints(Canvas canvas) {
        if (mEdleList.size() > 0) {
            mPaint.setStrokeWidth(28);
            mPaint.setColor(Color.WHITE);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            for (int i = 0; i < mEdleList.size(); i++) {
                canvas.drawPoint(mEdleList.get(i)[0], mEdleList.get(i)[1], mPaint);
            }
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
                curvePath.lineTo(calculate_x.get(i).eval(u), calculate_y.get(i).eval(u));
            }
        }
        mPaint.setColor(Color.WHITE);
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
        /* a + b*u + c*u^2 +d*u^3 */
        for (i = 0; i < n; i++) {
            Cubic c = new Cubic(x.get(i),
                    D[i],
                    3 * (x.get(i + 1) - x.get(i)) - 2 * D[i] - D[i + 1],
                    2 * (x.get(i) - x.get(i + 1)) + D[i] + D[i + 1]);
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
        int x = (int) event.getRawX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPoint = true;
                //点击线上,判断点到点还是线
                switchPointType(event.getX(), event.getY());
                //是否
                boolean onPath = isOnPath((int) event.getX(), (int) event.getY());
                System.out.println("在曲线上：" + onPath);
                break;
            case MotionEvent.ACTION_MOVE:

                int deltaY = y - mLastY;
                int deltaX = x - lastX;
                //判断点的移动方向
                if (deltaX > 0) HORIZONTAL_TAG = RIGHT_DIRECTION;
                else if (deltaX < 0) HORIZONTAL_TAG = LEFT_DIRECTION;
                else HORIZONTAL_TAG = VERTICAL;

                //如果移动的距离超过10，则不能添加点
                if (Math.abs(deltaY) > 5) drawPoint = false;

                //根据当前选中的是点还是线，更新UI
                switch (TAG_CURRENT) {
                    case TAG_SIGNLE:
                        if (currentSignlePtIndex >= 0) {
                            mEdleList.get(currentSignlePtIndex)[0] = event.getX();
                            mEdleList.get(currentSignlePtIndex)[1] = event.getY();
                            invalidate();
                        }
                        break;
                    case TAG_LINE:
                        updatePointPosition(0, deltaY);
                        if (moveListener != null && mPointList.size() > 2) {
                            //移动监听
                            if (currentPointIndex > 0 && currentPointIndex != mPointList.size() - 1) {
                                moveListener.onPointMove((int) mPointList.get(currentPointIndex)[0],
                                        (int) mPointList.get(currentPointIndex)[1]);
                            } else moveListener.onPointMove(0, 0);
                        }

                        break;
                    case TAG_POINT:
                        if (event.getY() + getTranslationY() <= mGlobal_padding || event.getY() + getTranslationY() >= height - mGlobal_padding)//判断点的移动范围
                        {
                            //超出了view范围，移除点
                            mPointList.remove(currentPointIndex);
                            //当前无选中点
                            currentPointIndex = -1;
                            TAG_CURRENT = TAG_ELSE;
                            if (moveListener != null) moveListener.onPointMove(0, 0);

                        } else {
                            //在view范围内，移动点的位置
                            if (event.getX() > width - mGlobal_padding || event.getX() < mGlobal_padding) {
                                TAG_CURRENT = TAG_ELSE;
                            } else {
                                mPointList.get(currentPointIndex)[0] = event.getX();
                                mPointList.get(currentPointIndex)[1] = event.getY();
                                if (moveListener != null)
                                    moveListener.onPointMove((int) (event.getX()), (int) event.getY());

                                //要判断X坐标，与左右两点比较
                                if (currentPointIndex > 0 && currentPointIndex < mPointList.size() - 1) {
                                    //小于左边的点
                                    //或大于右边的点
                                    if (mPointList.get(currentPointIndex)[0] <= mPointList.get(currentPointIndex - 1)[0]
                                            || mPointList.get(currentPointIndex)[0] >= mPointList.get(currentPointIndex + 1)[0]) {

                                        //更改相邻点的Y轴值
                                        updateBesidePoint();
                                    }
                                }
                            }
                        }
                        invalidate();
                        break;
                }

                break;
            case MotionEvent.ACTION_UP:
                //draw point
                if (drawPoint) {
                    System.out.println("点击原始位置getRawY()=" + event.getRawY() + "      相对位置getY()=" + event.getY());
                    addPoint(event.getRawX(), event.getY());
                    if (moveListener != null && currentPointIndex > 0)
                        moveListener.onPointMove((int) mPointList.get(currentPointIndex)[0], (int) mPointList.get(currentPointIndex)[1]);
                }
                break;
        }
        mLastY = y;
        lastX = x;
        return true;
    }

    /**
     * 更新曲线
     */
    private void updateBesidePoint() {
        if (HORIZONTAL_TAG == RIGHT_DIRECTION) {
            //处理右边的点
            mEdleList.add(mPointList.get(currentPointIndex + 1));
            removePoint(currentPointIndex + 1);
        } else if (HORIZONTAL_TAG == LEFT_DIRECTION) {
            //处理左边的点
            mEdleList.add(mPointList.get(currentPointIndex - 1));
            removePoint(currentPointIndex - 1);
        }
        //更新文字指示
//        if (moveListener != null) moveListener.onPointMove(0, 0);
        TAG_CURRENT = TAG_ELSE;
    }


    private final int TAG_POINT = 1;//触点在点上
    private final int TAG_LINE = 2;//触点在线上
    private final int TAG_ELSE = 3;//空白区域
    private final int TAG_SIGNLE = -1;//孤立的点


    private int TAG_CURRENT = TAG_ELSE;//初始化

    public void addPoint(float dx, float dy) {
        //判断是否添加点
        if (canAddPoint(dx)) {
            float[] newPoint = {dx, dy};
            mPointList.add(newPoint);
            //排序
            Collections.sort(mPointList, new SortPoint());
            currentPointIndex = mPointList.indexOf(newPoint);
            invalidate();
        }
    }

    /**
     * 判断是否可以添加点
     *
     * @param x
     * @return
     */
    private boolean canAddPoint(float x) {
        for (int i = 0; i < mPointList.size(); i++) {
            if (Math.abs(mPointList.get(i)[0] - x) <= 30) {
                //点到point上了
                currentPointIndex = i;
                return false;
            } else if (Math.abs(mPointList.get(i)[0] - x) < widthUnit)//小于点之间的最小间距
                return false;
            else if (mPointList.get(i)[1] <= 0 || mPointList.get(i)[1] >= height)
                return false;
        }
        return true;
    }

    /**
     * 判断点击到哪里了
     *
     * @param x
     */
    private void switchPointType(float x, float y) {
        //判断是不是孤立的点
        if (mEdleList.size() > 0) {
            for (int i = 0; i < mEdleList.size(); i++) {
                if (Math.abs(mEdleList.get(i)[0] - x) <= 30 && Math.abs(mEdleList.get(i)[1] - y) <= 30) {
                    TAG_CURRENT = TAG_SIGNLE;
                    currentSignlePtIndex = i;
                    System.out.println("Type=孤立点");
                    return;
                }
            }
        }
        for (int i = 0; i < mPointList.size(); i++) {
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


    /**
     * 初始化状态
     */
    public void reset() {
        mPointList.clear();
        mEdleList.clear();
        mPointList.add(new float[]{0, getMeasuredHeight() / 2});
        mPointList.add(new float[]{width, getMeasuredHeight() / 2});
        currentPointIndex = -1;
        invalidate();
    }

    /**
     * 按照点的X值升序排列
     */
    class SortPoint implements Comparator<float[]> {
        @Override
        public int compare(float[] o1, float[] o2) {
            return o1[0] > o2[0] ? 1 : -1;
        }
    }

    /**
     * 点移动接口
     */
    interface OnPointMoveListener {
        void onPointMove(int valueX, int valueY);
    }

    public void setOnPointMoveListener(OnPointMoveListener listener) {
        this.moveListener = listener;
    }

    /**
     * 删除当前的点
     */
    public void removeSelectPoint() {
        if (mPointList != null && mPointList.size() > 2
                && currentPointIndex != mPointList.size() - 1 && currentPointIndex > 0) {
            mPointList.remove(currentPointIndex);
            currentPointIndex = -1;
            invalidate();
        }
    }

    /**
     * 上下移动时，更新点的位置
     *
     * @param dx
     * @param dy
     */
    private void updatePointPosition(float dx, float dy) {
        if (mPointList != null && mPointList.size() > 1) {
            for (int i = 0; i < mPointList.size(); i++) {
                mPointList.get(i)[0] += dx;
                mPointList.get(i)[1] += dy;
            }
        }

        for (int i = 0; i < mEdleList.size(); i++) {
            mEdleList.get(i)[0] += dx;
            mEdleList.get(i)[1] += dy;
        }
        invalidate();
    }

    /**
     * 移除指定的点
     *
     * @param index
     */
    private void removePoint(int index) {
        if (index > 0)
            mPointList.remove(index);
        currentPointIndex = -1;
    }


    /**
     * 判断点击位置是否在path上
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isOnPath(int x, int y) {
        RectF bounds = new RectF();
        curvePath.computeBounds(bounds, true);
        Region region = new Region();
        region.setPath(curvePath, new Region((int) bounds.left, (int) bounds.top,
                (int) bounds.right, (int) bounds.bottom));
        return region.contains(x, y);
    }
}
