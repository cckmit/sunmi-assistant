package com.sunmi.assistant.pos;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * @author yangShiJie
 * @date 2019-11-14
 */
public class PosArcView extends View {

    //根据数据显示的圆弧Paint
    private Paint mArcPaint;
    //文字描述的paint
    private Paint mTextPaint;
    //圆弧开始的角度
    private float startAngle = 150;
    //圆弧结束的角度
    private float endAngle = 60;
    //圆弧背景的开始和结束间的夹角大小
    private float mAngle = 240;
    //当前进度夹角大小
    private float mIncludedAngle = 0;
    //圆弧的画笔的宽度
    private float mStrokeWith = 5;
    //中心的文字描述
    private String mDes = "";
    //动画效果的数据及最大/小值
    private int mAnimatorValue, mMinValue, mMaxValue;
    //中心点的XY坐标
    private float centerX, centerY;


    //根据数据显示的圆弧Paint
    private Paint mArcBigPaint;
    private float mBigStrokeWith = 10;
    //动画时间
    private int animatorDuration = 1000;

    //设置进度颜色
    private int[] colorArray;
//    private int[] colorDefault = new int[]{Color.parseColor("#00000000"), Color.parseColor("#00000000"), Color.parseColor("#00000000")};
//    private int[] colorOrange = new int[]{Color.parseColor("#FF7040"), Color.parseColor("#FF3838"), Color.parseColor("#FF7040")};
//    private int[] colorDeepBlue = new int[]{Color.parseColor("#3399FF"), Color.parseColor("#3355FF"), Color.parseColor("#3399FF")};
//    private int[] colorLightBlue = new int[]{Color.parseColor("#46EBEB"), Color.parseColor("#00AAFF"), Color.parseColor("#46EBEB")};

    public PosArcView(Context context) {
        this(context, null);
        initPaint();
    }

    public PosArcView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        initPaint();
    }

    public PosArcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        //圆弧的paint
        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //抗锯齿
        mArcPaint.setAntiAlias(true);
        mArcPaint.setColor(Color.parseColor("#666666"));
        //设置透明度（数值为0-255）
        mArcPaint.setAlpha(100);
        //设置画笔的画出的形状
        mArcPaint.setStrokeJoin(Paint.Join.ROUND);
        //两边是否radius
//        mArcPaint.setStrokeCap(Paint.Cap.ROUND);
        //设置画笔类型
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(dp2px(mStrokeWith));

        //中心文字的paint
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.parseColor("#FF4A40"));
        //设置文本的对齐方式
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(dp2px(25));

    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        //绘制弧度
        drawArc(canvas);
        //绘制文本
//        drawText(canvas);
        initBigPaint(canvas);
    }

    /**
     * 绘制文本
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        Rect mRect = new Rect();
        String mValue = String.valueOf(mAnimatorValue);
        //绘制中心的数值
        mTextPaint.getTextBounds(mValue, 0, mValue.length(), mRect);
        canvas.drawText(String.valueOf(mAnimatorValue), centerX, centerY + mRect.height() - 20, mTextPaint);

        //绘制中心文字描述
        mTextPaint.setColor(Color.parseColor("#999999"));
        mTextPaint.setTextSize(dp2px(12));
        mTextPaint.getTextBounds(mDes, 0, mDes.length(), mRect);
        canvas.drawText(mDes, centerX, centerY + 2 * mRect.height() + dp2px(10), mTextPaint);
    }

    /**
     * 绘制当前的圆弧
     *
     * @param canvas
     */
    private void drawArc(Canvas canvas) {
        //绘制圆弧背景
        RectF mRectF = new RectF(mStrokeWith + dp2px(5), mStrokeWith + dp2px(5), getWidth() - mStrokeWith - dp2px(5), getHeight() - mStrokeWith);
        canvas.drawArc(mRectF, startAngle, mAngle, false, mArcPaint);
        //绘制当前数值对应的圆弧
        mArcPaint.setColor(Color.parseColor("#00000000"));//透明
        //根据当前数据绘制对应的圆弧
        canvas.drawArc(mRectF, startAngle, mIncludedAngle, false, mArcPaint);
    }

    private void initBigPaint(Canvas canvas) {
        if (colorArray == null) {
            return;
        }
        //圆弧的paint
        mArcBigPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //抗锯齿
        mArcBigPaint.setAntiAlias(true);
        mArcBigPaint.setColor(Color.parseColor("#666666"));//灰色
        //设置透明度（数值为0-255）
        mArcBigPaint.setAlpha(100);
        //设置画笔的画出的形状
        mArcBigPaint.setStrokeJoin(Paint.Join.ROUND);
        //两边是否radius
//        mArcBigPaint.setStrokeCap(Paint.Cap.ROUND);
        //设置画笔类型
        mArcBigPaint.setStyle(Paint.Style.STROKE);
        mArcBigPaint.setStrokeWidth(dp2px(mBigStrokeWith));
        //绘制圆弧背景
        RectF mRectF = new RectF(mBigStrokeWith + dp2px(5), mBigStrokeWith + dp2px(1.8f),
                getWidth() - mBigStrokeWith - dp2px(5), getHeight() - mStrokeWith);
//        canvas.drawArc(mRectF,startAngle,mAngle,false,mArcBigPaint);//绘制背景
        //绘制当前数值对应的圆弧
        mArcBigPaint.setColor(Color.parseColor("#3355FF"));
        //渐变色
        float[] positionArray = new float[]{0.2f, 0.5f, 0.9f};
        mArcBigPaint.setShader(new SweepGradient(mRectF.centerX(), mRectF.centerY(), colorArray, positionArray));
        //根据当前数据绘制对应的圆弧
        canvas.drawArc(mRectF, startAngle, mIncludedAngle, false, mArcBigPaint);
    }

    /**
     * 为绘制弧度及数据设置动画
     *
     * @param startAngle   开始的弧度
     * @param currentAngle 需要绘制的弧度
     * @param currentValue 需要绘制的数据
     * @param time         动画执行的时长
     */
    private void setAnimation(float startAngle, float currentAngle, int currentValue, int time) {
        //绘制当前数据对应的圆弧的动画效果
        ValueAnimator progressAnimator = ValueAnimator.ofFloat(startAngle, currentAngle);
        progressAnimator.setDuration(time);
        progressAnimator.setTarget(mIncludedAngle);
        progressAnimator.addUpdateListener(animation -> {
            mIncludedAngle = (float) animation.getAnimatedValue();
            //重新绘制，不然不会出现效果
            postInvalidate();
        });
        //开始执行动画
        progressAnimator.start();

        //中心数据的动画效果
        ValueAnimator valueAnimator = ValueAnimator.ofInt(mAnimatorValue, currentValue);
        valueAnimator.setDuration(animatorDuration);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(valueAnimator1 -> {
            mAnimatorValue = (int) valueAnimator1.getAnimatedValue();
            postInvalidate();
        });
        valueAnimator.start();
    }

    /**
     * 设置数据
     *
     * @param minValue     最小值
     * @param maxValue     最大值
     * @param currentValue 当前绘制的值
     * @param des          描述信息
     */
    public void setValues(int[] mColorArray, int minValue, int maxValue, int currentValue, String des) {
        mDes = des;
        mMaxValue = maxValue;
        mMinValue = minValue;
        colorArray = mColorArray;
        //完全覆盖
        if (currentValue > maxValue) {
            currentValue = maxValue;
        }
        //计算弧度比重
        float scale = (float) currentValue / maxValue;
        //计算弧度
        float currentAngle = scale * mAngle;
        //开始执行动画
        setAnimation(0, currentAngle, currentValue, animatorDuration);
    }

    /**
     * 设置数据
     *
     * @param minValue     最小值
     * @param maxValue     最大值
     * @param currentValue 当前绘制的值
     */
    public void setValues(int[] mColorArray, int minValue, int maxValue, int currentValue) {
        mMaxValue = maxValue;
        mMinValue = minValue;
        colorArray = mColorArray;
        //完全覆盖
        if (currentValue > maxValue) {
            currentValue = maxValue;
        }
        //计算弧度比重
        float scale = (float) currentValue / maxValue;
        //计算弧度
        float currentAngle = scale * mAngle;
        //开始执行动画
        setAnimation(0, currentAngle, currentValue, animatorDuration);
    }

    /**
     * 设置数据
     * 百分制
     *
     * @param currentValue 当前绘制的值
     */
    public void setValues(int[] mColorArray, int currentValue) {
        mMaxValue = 100;
        mMinValue = 0;
        colorArray = mColorArray;
        //完全覆盖
        if (currentValue > mMaxValue) {
            currentValue = mMaxValue;
        }
        //计算弧度比重
        float scale = (float) currentValue / mMaxValue;
        //计算弧度
        float currentAngle = scale * mAngle;
        //开始执行动画
        setAnimation(0, currentAngle, currentValue, animatorDuration);
    }


    public float dp2px(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return dp * metrics.density;
    }
}
