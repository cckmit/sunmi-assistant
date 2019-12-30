package com.sunmi.ipc.cash.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.sunmi.ipc.cash.model.CashBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author yinhui
 * @date 2019-12-20
 */
public class CashBoxView extends View {

    private int mWidth;
    private int mHeight;
    private int mCurrentTime;
    private int mInterval = 1;
    private boolean mStart = false;

    private Paint mBoxPaint;
    RectF mRect = new RectF();

    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, List<CashBox>> mData = new HashMap<>();

    public CashBoxView(Context context) {
        this(context, null);
    }

    public CashBoxView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CashBoxView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CashBoxView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mBoxPaint = new Paint();
        mBoxPaint.setAntiAlias(true);
        mBoxPaint.setColor(0xffff0000);
        mBoxPaint.setStrokeWidth(4.0f);
        mBoxPaint.setStyle(Paint.Style.STROKE);
    }

    public void setData(List<CashBox> data) {
        mData.clear();
        // 使用筛法缓存数据，用空间换时间，避免onDraw中遍历List来判断时间
        for (CashBox model : data) {
            int startTime = model.getStartTime() / 1000 / mInterval;
            int endTime = model.getEndTime() / 1000 / mInterval;
            for (int i = startTime; i <= endTime; i++) {
                List<CashBox> list = mData.get(i);
                if (list == null) {
                    list = new ArrayList<>();
                    mData.put(i, list);
                }
                list.add(model);
            }
        }

    }

    public void setCurrent(int currentTime) {
        this.mStart = true;
        this.mCurrentTime = currentTime;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.mWidth = w;
        this.mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!mStart) {
            return;
        }
        // 从Map中取出这一秒可能需要绘制的框数据
        List<CashBox> list = mData.get(mCurrentTime / 1000 / mInterval);
        if (list == null || list.isEmpty()) {
            return;
        }
        for (CashBox model : list) {
            // 精确判断当前时刻需要绘制的框数据
            if (mCurrentTime < model.getStartTime() || mCurrentTime > model.getEndTime()) {
                continue;
            }
            float[] rect = model.getRect();
            mRect.set((rect[0] * mWidth),
                    (rect[1] * mHeight),
                    (rect[2] * mWidth),
                    (rect[3] * mHeight));
            canvas.drawRoundRect(mRect, 10, 10, mBoxPaint);
        }
    }
}
