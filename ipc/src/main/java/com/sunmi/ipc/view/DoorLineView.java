package com.sunmi.ipc.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.sunmi.ipc.R;

import java.util.Locale;

import sunmi.common.utils.log.LogCat;

/**
 * @author yinhui
 * @date 2019-07-29
 */
public class DoorLineView extends ViewGroup {

    private static final String TAG = DoorLineView.class.getSimpleName();

    private static final String LOG_MSG_RANGE = "Click: out of boundary. click(%d, %d) must be inside of [%d, %d, %d, %d]";
    private static final String LOG_MSG_OVERLAP = "Click: point overlapped. click(%d, %d) overlap with existing point[%d, %d].";

    public static final int STATE_INIT = 0;
    public static final int STATE_START = 1;
    public static final int STATE_END = 2;

    public static final int DRAG_STATE_INIT = 0;
    public static final int DRAG_STATE_START = 1;
    public static final int DRAG_STATE_END = 2;
    public static final int DRAG_STATE_LINE = 3;

    private final Paint mPaint;
    private final Path mPath;

    private final InternalPoint mStart;
    private final InternalPoint mEnd;

    /**
     * Cache the touch slop from the context that created the view.
     */
    private int mTouchSlop;
    private boolean mIsPressed;
    private float[] mDown = new float[]{-1, -1};

    private int mDragState = DRAG_STATE_INIT;
    private float[] mDragDown = new float[]{-1, -1};

    private OnStateChangeListener mListener;
    private int mState;
    private Rect mBoundary = new Rect();

    private int mSmallRadius;
    private int mBigRadius;
    private int mLineRadius;
    private int mGap;

    public DoorLineView(Context context) {
        this(context, null);
    }

    public DoorLineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DoorLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(getResources().getDimension(R.dimen.dp_1));
        mPaint.setPathEffect(new DashPathEffect(new float[]{15, 10}, 0));
        mPath = new Path();
        Drawable small = ContextCompat.getDrawable(context, R.mipmap.adjust_line_point_small);
        if (small != null) {
            mSmallRadius = small.getIntrinsicWidth() >> 1;
            small.setBounds(-mSmallRadius, -mSmallRadius, mSmallRadius, mSmallRadius);
        }
        Drawable big = ContextCompat.getDrawable(context, R.mipmap.adjust_line_point_big);
        if (big != null) {
            mBigRadius = big.getIntrinsicWidth() >> 1;
            big.setBounds(-mBigRadius, -mBigRadius, mBigRadius, mBigRadius);
        }
        mLineRadius = (int) getResources().getDimension(R.dimen.dp_16);
        int radius = (int) getResources().getDimension(R.dimen.dp_16);
        mStart = new InternalPoint(small, big, radius);
        mEnd = new InternalPoint(small, big, radius);
        mGap = (int) getResources().getDimension(R.dimen.dp_4);
        setWillNotDraw(false);
    }

    public void init(Rect boundary) {
        mState = STATE_INIT;
        mBoundary.set(boundary.left + mBigRadius, boundary.top + mBigRadius,
                boundary.right - mBigRadius, boundary.bottom - mBigRadius);
        LogCat.d(TAG, "Boundary: " + mBoundary);
        if (mListener != null) {
            mListener.onStateChanged(mState);
        }
    }

    public void setStateChangeListener(OnStateChangeListener l) {
        mListener = l;
    }

    public int getState() {
        return mState;
    }

    public Pair<Point, Point> getPoints() {
        return new Pair<>(mStart.copy(), mEnd.copy());
    }

    private void setPressed(boolean pressed, float x, float y) {
        if (pressed) {
            mDown[0] = x;
            mDown[1] = y;
        } else {
            mDown[0] = -1;
            mDown[1] = -1;
        }
        mIsPressed = pressed;
        setPressed(pressed);
    }

    private boolean pointMove(float x, float y, float slop) {
        return Math.abs(x - mDown[0]) > slop || Math.abs(y - mDown[1]) > slop;
    }

    private boolean atLine(float x, float y, float radius) {
        if (mState != STATE_END) {
            return false;
        }
        boolean atRegion;
        atRegion = (mStart.x < x && x < mEnd.x) || (mStart.x > x && x > mEnd.x);
        atRegion = ((mStart.y < y && y < mEnd.y) || (mStart.y > y && y > mEnd.y)) && atRegion;
        if (!atRegion) {
            return false;
        }
        float a = mEnd.y - mStart.y;
        float b = mStart.x - mEnd.x;
        float c = mEnd.x * mStart.y - mStart.x * mEnd.y;
        float distance = (float) (Math.abs(a * x + b * y + c) / Math.sqrt(a * a + b * b));
        return distance <= radius;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();
        final int action = event.getAction();

        switch (action) {

            case MotionEvent.ACTION_DOWN:

                if (x < mBoundary.left || x > mBoundary.right
                        || y < mBoundary.top || y > mBoundary.bottom) {
                    return false;
                }
                // Not inside a scrolling container, so show the feedback right away
                setPressed(true, x, y);
                break;

            case MotionEvent.ACTION_MOVE:
                drawableHotspotChanged(x, y);

                // Be lenient about moving outside of buttons
                if (pointMove(x, y, mTouchSlop)) {
                    // Outside button
                    if (mIsPressed) {
                        setPressed(false, x, y);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:

                if (mIsPressed) {
                    setPressed(false, x, y);
                    return click(x, y);
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                setPressed(false, x, y);
                return true;

            default:
                return false;
        }

        return drag(event);
    }

    private boolean drag(MotionEvent e) {
        if (mState != STATE_END) {
            return true;
        }
        float x = e.getX();
        float y = e.getY();
        int action = e.getAction();
        boolean result = true;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mStart.atPoint(x, y)) {
                    mDragState = DRAG_STATE_START;
                    mStart.setPressed(true);
                    mEnd.setPressed(false);
                } else if (mEnd.atPoint(x, y)) {
                    mDragState = DRAG_STATE_END;
                    mStart.setPressed(false);
                    mEnd.setPressed(true);
                } else if (atLine(x, y, mLineRadius)) {
                    mStart.setPressed(true);
                    mEnd.setPressed(true);
                    mDragState = DRAG_STATE_LINE;
                } else {
                    mDragState = DRAG_STATE_INIT;
                    result = false;
                }
                mDragDown[0] = x;
                mDragDown[1] = y;
                break;

            case MotionEvent.ACTION_MOVE:
                x = x < mBoundary.left ? mBoundary.left : x;
                x = x > mBoundary.right ? mBoundary.right : x;
                y = y < mBoundary.top ? mBoundary.top : y;
                y = y > mBoundary.bottom ? mBoundary.bottom : y;

                if (mDragState == DRAG_STATE_START) {
                    mStart.setCoordinate(x, y);
                } else if (mDragState == DRAG_STATE_END) {
                    mEnd.setCoordinate(x, y);
                } else if (mDragState == DRAG_STATE_LINE) {
                    float dx = x - mDragDown[0];
                    float dy = y - mDragDown[1];

                    dx = mStart.x + dx < mBoundary.left ? mBoundary.left - mStart.x : dx;
                    dx = mStart.x + dx > mBoundary.right ? mBoundary.right - mStart.x : dx;
                    dx = mEnd.x + dx < mBoundary.left ? mBoundary.left - mEnd.x : dx;
                    dx = mEnd.x + dx > mBoundary.right ? mBoundary.right - mEnd.x : dx;

                    dy = mStart.y + dy < mBoundary.top ? mBoundary.top - mStart.y : dy;
                    dy = mStart.y + dy > mBoundary.bottom ? mBoundary.bottom - mStart.y : dy;
                    dy = mEnd.y + dy < mBoundary.top ? mBoundary.top - mEnd.y : dy;
                    dy = mEnd.y + dy > mBoundary.bottom ? mBoundary.bottom - mEnd.y : dy;

                    mStart.x += dx;
                    mStart.y += dy;
                    mEnd.x += dx;
                    mEnd.y += dy;
                    mDragDown[0] = x;
                    mDragDown[1] = y;
                } else {
                    result = false;
                }
                break;

            case MotionEvent.ACTION_UP:
                mDragState = DRAG_STATE_INIT;
                mDragDown[0] = -1;
                mDragDown[1] = -1;
                mStart.setPressed(false);
                mEnd.setPressed(false);
                if (mListener != null) {
                    mListener.isLineInvalid(mStart.copy(), mEnd.copy());
                }
                break;

            default:
                return false;
        }
        invalidate();
        return result;
    }

    private boolean click(float x, float y) {
        LogCat.d(TAG, "Click Point: x=" + x + "; y=" + y);
        boolean outOfBoundary = x < mBoundary.left || x > mBoundary.right
                || y < mBoundary.top || y > mBoundary.bottom;
        boolean pointOverlap = mState == STATE_START
                && Math.abs(x - mStart.x) < mBigRadius * 2
                && Math.abs(y - mStart.y) < mBigRadius * 2;
        if (outOfBoundary) {
            String msg = String.format(Locale.getDefault(), LOG_MSG_RANGE,
                    (int) x, mBoundary.left, mBoundary.right,
                    (int) y, mBoundary.top, mBoundary.bottom);
            LogCat.e(TAG, msg);
            return false;
        }
        if (pointOverlap) {
            String msg = String.format(Locale.getDefault(), LOG_MSG_OVERLAP,
                    (int) x, (int) y, (int) mStart.x, (int) mStart.y);
            LogCat.e(TAG, msg);
            return false;
        }
        switch (mState) {
            case STATE_INIT:
                mState = STATE_START;
                mStart.setCoordinate(x, y);
                mStart.setPressed(true);
                if (mListener != null) {
                    mListener.onStateChanged(mState);
                }
                break;

            case STATE_START:
                if (mListener != null && mListener.isLineInvalid(mStart.copy(), new InternalPoint(x, y))) {
                    return false;
                }
                mState = STATE_END;
                mStart.setPressed(false);
                mEnd.setPressed(true);
                mEnd.setCoordinate(x, y);
                if (mListener != null) {
                    mListener.onStateChanged(mState);
                }
                break;

            case STATE_END:
                mStart.setPressed(false);
                mEnd.setPressed(false);
                break;

            default:
                return false;
        }
        invalidate();
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        switch (mState) {
            case STATE_START:
                canvas.save();
                canvas.translate(mStart.x, mStart.y);
                mStart.draw(canvas);
                canvas.restore();
                break;
            case STATE_END:
                float dx = mEnd.x - mStart.x;
                float dy = mEnd.y - mStart.y;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                float startRatio = (mStart.isPressed ? mBigRadius : mSmallRadius + mGap) / distance;
                float endRatio = (mEnd.isPressed ? mBigRadius : mSmallRadius + mGap) / distance;
                // Draw start point
                canvas.save();
                canvas.translate(mStart.x, mStart.y);
                mStart.draw(canvas);
                // Draw end point
                canvas.translate(dx, dy);
                mEnd.draw(canvas);
                canvas.restore();
                // Draw line
                mPath.reset();
                mPath.moveTo(dx * startRatio + mStart.x, dy * startRatio + mStart.y);
                mPath.lineTo(mEnd.x - dx * endRatio, mEnd.y - dy * endRatio);
                canvas.drawPath(mPath, mPaint);
                break;
            default:
        }
    }

    private static class InternalPoint implements Point {

        private Drawable small;
        private Drawable big;

        private boolean isPressed;
        private float x;
        private float y;
        private float radius;

        private InternalPoint(Drawable small, Drawable big, float radius) {
            this.small = small;
            this.big = big;
            this.radius = radius;
        }

        private InternalPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public float getX() {
            return x;
        }

        @Override
        public float getY() {
            return y;
        }

        private void setPressed(boolean pressed) {
            isPressed = pressed;
        }

        private void setCoordinate(float x, float y) {
            this.x = x;
            this.y = y;
        }

        private float[] getCoordinate() {
            return new float[]{x, y};
        }

        private boolean atPoint(float x, float y) {
            float dx = x - this.x;
            float dy = y - this.y;
            return Math.sqrt(dx * dx + dy * dy) <= this.radius;
        }

        private void draw(Canvas canvas) {
            if (big == null || small == null) {
                return;
            }
            if (isPressed) {
                big.draw(canvas);
            } else {
                small.draw(canvas);
            }
        }

        private Point copy() {
            return new InternalPoint(x, y);
        }

    }

    public interface Point {

        /**
         * 获取X坐标值
         *
         * @return X坐标值
         */
        float getX();

        /**
         * 获取Y坐标值
         *
         * @return Y坐标值
         */
        float getY();
    }

    public interface OnStateChangeListener {

        /**
         * 划线状态切换时调用，表明正在处于的划线步骤
         *
         * @param state 状态
         */
        void onStateChanged(int state);

        /**
         * 判断线段坐标是否有效
         *
         * @param start 划线起点坐标
         * @param end   划线终点坐标
         * @return 是否有效
         */
        boolean isLineInvalid(Point start, Point end);
    }
}
