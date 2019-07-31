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
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
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

    public static final int STATE_INIT = 0;
    public static final int STATE_START = 1;
    public static final int STATE_END = 2;

    private final GestureDetector mDetector;
    private final Paint mPaint;
    private final Path mPath;

    private final Drawable mSmallPoint;
    private final Drawable mBigPoint;

    private final float[] mLineStart = new float[2];
    private final float[] mLineEnd = new float[2];

    private final View mTipView;
    private final Rect mTipRect = new Rect();
    private final int[] mTipSize = new int[2];

    private OnStateChangeListener mListener;
    private int mState = STATE_INIT;

    private int mSmallRadius;
    private int mBigRadius;
    private int mGap;

    public DoorLineView(Context context) {
        this(context, null);
    }

    public DoorLineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DoorLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDetector = new GestureDetector(context, new ClickEvent());
        mTipView = LayoutInflater.from(context).inflate(R.layout.ipc_setting_recognition_line_cancel, this, false);
        mTipView.measure(0, 0);
        mTipSize[0] = mTipView.getMeasuredWidth();
        mTipSize[1] = mTipView.getMeasuredHeight();
        mTipView.setVisibility(GONE);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(getResources().getDimension(R.dimen.dp_1));
        mPaint.setPathEffect(new DashPathEffect(new float[]{15, 10}, 0));
        mPath = new Path();
        mSmallPoint = ContextCompat.getDrawable(context, R.mipmap.setting_recognition_line_point_small);
        if (mSmallPoint != null) {
            mSmallRadius = mSmallPoint.getIntrinsicWidth() >> 1;
            mSmallPoint.setBounds(-mSmallRadius, -mSmallRadius, mSmallRadius, mSmallRadius);
        }
        mBigPoint = ContextCompat.getDrawable(context, R.mipmap.setting_recognition_line_point_big);
        if (mBigPoint != null) {
            mBigRadius = mBigPoint.getIntrinsicWidth() >> 1;
            mBigPoint.setBounds(-mBigRadius, -mBigRadius, mBigRadius, mBigRadius);
        }
        mGap = (int) getResources().getDimension(R.dimen.dp_4);
        addView(mTipView);
        setWillNotDraw(false);
    }

    public void init() {
        mState = STATE_INIT;
        mTipView.setVisibility(GONE);
        if (mListener != null) {
            mListener.onStateChanged(mState, mLineStart, mLineEnd);
        }
    }

    public void setStateChangeListener(OnStateChangeListener l) {
        mListener = l;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mState != STATE_END) {
            mTipView.setVisibility(GONE);
            return;
        }
        mTipView.setVisibility(VISIBLE);
        int left = Math.max(0, (int) (mLineEnd[0] - (mTipSize[0] >> 1)));
        int top = Math.max(0, (int) (mLineEnd[1] - mTipSize[1] - mGap - mBigRadius));
        mTipView.layout(left, top, left + mTipSize[0], top + mTipSize[1]);
        mTipRect.set(left, top, left + mTipSize[0], top + mTipSize[1]);
        LogCat.d(TAG, "Layout cancel tip: " + mTipRect);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        LogCat.d(TAG, "Draw: " + mSmallPoint.getBounds() + "; " + mBigPoint.getBounds());
        switch (mState) {
            case STATE_START:
                canvas.save();
                canvas.translate(mLineStart[0], mLineStart[1]);
                mBigPoint.draw(canvas);
                canvas.restore();
                break;
            case STATE_END:
                float dx = mLineEnd[0] - mLineStart[0];
                float dy = mLineEnd[1] - mLineStart[1];
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                float startRatio = (mSmallRadius + mGap) / distance;
                float endRatio = (mBigRadius + mGap) / distance;
                // Draw start point
                canvas.save();
                canvas.translate(mLineStart[0], mLineStart[1]);
                mSmallPoint.draw(canvas);
                // Draw end point
                canvas.translate(mLineEnd[0] - mLineStart[0], mLineEnd[1] - mLineStart[1]);
                mBigPoint.draw(canvas);
                canvas.restore();
                // Draw line
                mPath.reset();
                mPath.moveTo(dx * startRatio + mLineStart[0], dy * startRatio + mLineStart[1]);
                mPath.lineTo(mLineEnd[0] - dx * endRatio, mLineEnd[1] - dy * endRatio);
                canvas.drawPath(mPath, mPaint);
                break;
            default:
        }
    }

    private class ClickEvent extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();
            int left = mTipSize[0] >> 1;
            int right = getWidth() - left;
            int top = mTipSize[1];
            int bottom = getHeight();
            boolean isInvalidPoint = x < left || x > right || y < top;
            boolean isInTip = x >= mTipRect.left && x <= mTipRect.right && y >= mTipRect.top && y <= mTipRect.bottom;
            switch (mState) {
                case STATE_INIT:
                    if (isInvalidPoint) {
                        String msg = String.format(Locale.getDefault(), "Click: out of area." +
                                        " x(%f) must in [%d, %d], y(%f) must in [%d, %d]",
                                x, left, right, y, top, getHeight());
                        LogCat.e(TAG, msg);
                        return false;
                    }
                    mLineStart[0] = e.getX();
                    mLineStart[1] = e.getY();
                    mState = STATE_START;
                    if (mListener != null) {
                        mListener.onStateChanged(mState, mLineStart, mLineEnd);
                    }
                    invalidate();
                    return true;
                case STATE_START:
                    if (isInvalidPoint) {
                        String msg = String.format(Locale.getDefault(), "Click: out of area." +
                                        " x(%f) must in [%d, %d], y(%f) must in [%d, %d]",
                                x, left, right, y, top, getHeight());
                        LogCat.e(TAG, msg);
                        return false;
                    }
                    mLineEnd[0] = e.getX();
                    mLineEnd[1] = e.getY();
                    mState = STATE_END;
                    if (mListener != null) {
                        mListener.onStateChanged(mState, mLineStart, mLineEnd);
                    }
                    requestLayout();
                    return true;
                case STATE_END:
                    if (isInTip) {
                        mState = STATE_INIT;
                        if (mListener != null) {
                            mListener.onStateChanged(mState, mLineStart, mLineEnd);
                        }
                        requestLayout();
                        return true;
                    } else if (isInvalidPoint) {
                        String msg = String.format(Locale.getDefault(), "Click: out of area." +
                                        " x(%f) must in [%d, %d], y(%f) must in [%d, %d]",
                                x, left, right, y, top, getHeight());
                        LogCat.e(TAG, msg);
                        return false;
                    } else {
                        mLineEnd[0] = e.getX();
                        mLineEnd[1] = e.getY();
                        if (mListener != null) {
                            mListener.onStateChanged(mState, mLineStart, mLineEnd);
                        }
                        requestLayout();
                        invalidate();
                        return true;
                    }
                default:
            }
            return false;
        }

    }

    public interface OnStateChangeListener {

        /**
         * 划线状态切换时调用，表明正在处于的划线步骤
         *
         * @param state     状态
         * @param lineStart 划线起点坐标
         * @param lineEnd   划线终点坐标
         */
        void onStateChanged(int state, float[] lineStart, float[] lineEnd);
    }
}
