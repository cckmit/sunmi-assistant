package com.sunmi.ipc.cash.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.sunmi.ipc.R;
import com.sunmi.ipc.cash.model.CashBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sunmi.common.model.Interval;
import sunmi.common.utils.log.LogCat;

/**
 * @author yinhui
 * @date 2019-12-24
 */
public class CashProgressMark extends View {

    private static final String TAG = CashProgressMark.class.getSimpleName();

    private int mWidth;
    private int mHeight;
    private int mBarWidth;
    private int mBarHeight;
    private int mBarRadius;

    private Paint mMarkPaint;
    private Rect mBarRect;

    private int mDuration;
    private List<Interval> mData = new ArrayList<>();

    public CashProgressMark(Context context) {
        this(context, null);
    }

    public CashProgressMark(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CashProgressMark(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CashProgressMark(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mMarkPaint = new Paint();
        mMarkPaint.setAntiAlias(true);
        mMarkPaint.setColor(ContextCompat.getColor(context, R.color.common_orange));
        mMarkPaint.setStyle(Paint.Style.FILL);
    }

    public void setSize(int width, int height, int radius) {
        if (mWidth - getPaddingLeft() - getPaddingRight() >= width) {
            mBarWidth = width;
        } else {
            LogCat.e(TAG, "Progress bar width MUST be within the available limits of the view");
        }
        if (mHeight - getPaddingTop() - getPaddingBottom() >= height) {
            mBarHeight = height;
        } else {
            LogCat.e(TAG, "Progress bar height MUST be within the available limits of the view");
        }
        mBarRadius = radius;
        calcSize();
    }

    public void setData(List<CashBox> data) {
        mData.clear();
        // 将时间区间进行合并，避免onDraw中重复绘制
        Collections.sort(data, (o1, o2) -> o1.getStartTime() - o2.getStartTime());
        for (CashBox model : data) {
            if (mData.isEmpty() || mData.get(mData.size() - 1).end < model.getStartTime()) {
                mData.add(new Interval(model.getStartTime(), model.getEndTime()));
            } else {
                Interval last = mData.get(mData.size() - 1);
                last.end = Math.max(last.end, model.getEndTime());
            }
        }
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.mWidth = w;
        this.mHeight = h;
        calcSize();
    }

    private void calcSize() {
        if (this.mWidth == 0 || this.mHeight == 0) {
            mBarRect = null;
            return;
        }
        int l, t, r, b;
        if (mBarWidth <= 0) {
            mBarWidth = mWidth - getPaddingLeft() - getPaddingRight();
        }
        if (mBarHeight <= 0) {
            mBarHeight = mHeight - getPaddingTop() - getPaddingBottom();
        }
        l = getPaddingLeft() + ((mWidth - mBarWidth - getPaddingLeft() - getPaddingRight()) >> 1);
        r = l + mBarWidth;
        t = getPaddingTop() + ((mHeight - mBarHeight - getPaddingTop() - getPaddingBottom()) >> 1);
        b = t + mBarHeight;
        if (mBarRect == null) {
            mBarRect = new Rect(l, t, r, b);
        } else {
            mBarRect.set(l, t, r, b);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mDuration == 0) {
            return;
        }
        for (Interval interval : mData) {
            float startPx = (float) interval.start * mBarWidth / mDuration + mBarRect.left;
            float endPx = Math.min((float) interval.end * mBarWidth / mDuration + mBarRect.left, mBarRect.right);
            canvas.drawRect(startPx, mBarRect.top, endPx, mBarRect.bottom, mMarkPaint);
        }
    }
}
