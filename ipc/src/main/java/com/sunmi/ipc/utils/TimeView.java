package com.sunmi.ipc.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.List;

/**
 * Created by YangShiJie on 2019/5/16
 * 自定义绘制时间view
 */
public class TimeView extends View {
    private List<String> list;
    private int x;//距离第一个长竖线的时间偏移量
    private int mX;//临时绘制的间距值

    // 定义画笔
    private Paint mPaint;
    // 用于获取文字的宽和高
    private Rect mBounds;
    // 文本宽高
    private float textWidth, textHeight;
    private int hourOffset = dip2px(10 * 6);//一个小时6格，一个小格10dp 转换px

    private int dip2px(float dipValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public TimeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 初始化画笔、Rect
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBounds = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制背景的填充色
        mPaint.setColor(Color.TRANSPARENT);
        // 绘制一个填充色的矩形
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);

        //绘制文本颜色
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(30);
        // 初始化第一个小时的偏移量
        if (list == null || list.size() == 0) return;
        x = -hourOffset + x;
        //Log.i("TAG", "x=" + x + "  hourOffset=" + hourOffset + " ， list =" + list.size());
        if (x > 0) {
            x = mX;
        } else if (x < 0) {
            mX = x;//当绘制小于0的时候赋值
        }
        for (int i = 0; i < list.size(); i++) {
            // 获取文本
            String timeText = list.get(i);
            // 获取文字的宽和高
            mPaint.getTextBounds(list.get(i), 0, timeText.length(), mBounds);
            textWidth = mBounds.width();
            textHeight = mBounds.height();
            // 绘制时间
            x += hourOffset;
            canvas.drawText(timeText,
                    x - dip2px(10), //30
                    getHeight() / 2 + textHeight / 2,
                    mPaint);
        }
    }

    /**
     * @param list 时间列表
     * @param x    初始化第一个偏移量
     */
    public void refresh(List<String> list, int x) {
        this.list = list;
        this.x = x;
        invalidate();//重绘
    }
}