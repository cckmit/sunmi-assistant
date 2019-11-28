package com.sunmi.ipc.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.sunmi.ipc.R;
import com.sunmi.ipc.model.VideoTimeSlotBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ScheduledExecutorService;

import sunmi.common.utils.DateTimeUtils;

/**
 * Description:
 * Created by bruce on 2019/8/6.
 */
public class ZFTimeLine extends View {

    public static final int STYLE_NORMAL = 0;
    public static final int STYLE_BIG = 1;

    // 内容区域顶部坐标
    float displayTop;
    // 内容区域底部坐标
    float displayBottom;
    // 中心线顶部坐标
    float lineCenterTop;
    // 中心线底部坐标
    float lineCenterBottom;
    // 中心线宽度
    float lineCenterWidth;
    // 短线刻度顶部坐标
    float lineSmallTop;
    // 短线刻度底部坐标
    float lineSmallBottom;
    // 长线刻度顶部坐标
    float lineBigTop;
    // 长线刻度底部坐标
    float lineBigBottom;
    // 文字开始位置偏移
    float textStartOffset;
    // 文字基线位置坐标
    float textBaseline;

    // 每小刻度时间（秒），默认10分钟
    private int intervalSeconds = 60 * 10;
    // 每大刻度包含小刻度的个数，默认6个，即1小时
    private int intervalCount = 6;
    private int intervalValue;                    //小刻度宽度
    private long currentInterval;                 //中间刻度对应的秒数

    private int scaleLineColor = Color.WHITE;     //刻度的颜色
    private Paint pWhite, pOrange, pCenterLine;   //三种不同颜色的画笔
    private float moveStartX = 0;                 //用于记录单点触摸点位置,用于计算拖距离

    private long leftBound = 0;
    private long rightBound = Long.MAX_VALUE;
    private long leftMoveBound = 0;
    private long rightMoveBound = Long.MAX_VALUE;
    private boolean onLock;                       //用于屏蔽时间轴拖动,为true时无法拖动
    private int style;

    private SimpleDateFormat formatterScale;      //日期格式化,用于秒数和时间字符的转换
    private SimpleDateFormat formatterProject;    //日期格式化,用于秒数和时间字符的转换

    private OnZFTimeLineListener listener;        //时间轴拖动监听,这个只在拖动完成时返回数据

    List<VideoTimeSlotBean> videoData;            //已录制视频数据信息

    //刻度尺移动定时器
    private ScheduledExecutorService executorService;

    public ZFTimeLine(Context context) {
        this(context, null);
        init();
    }

    public ZFTimeLine(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public ZFTimeLine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributes(attrs);
        init();
    }

    private void initAttributes(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TimeLine);

        if (a.hasValue(R.styleable.TimeLine_scaleLineColor)) {
            scaleLineColor = a.getColor(R.styleable.TimeLine_scaleLineColor, -1);
        }
        style = a.getInteger(R.styleable.TimeLine_tl_style, STYLE_NORMAL);

        a.recycle();
    }

    //数据数据初始化
    private void init() {
        intervalValue = 0;
        timeNow();
        formatterScale = new SimpleDateFormat("HH:mm", Locale.getDefault());
        formatterProject = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());

        pWhite = new Paint();
        pWhite.setColor(scaleLineColor);
        pWhite.setTextSize(getIntervalValue());
        pWhite.setAntiAlias(true);
        if (style == STYLE_BIG) {
            pWhite.setTextAlign(Paint.Align.LEFT);
        } else {
            pWhite.setTextAlign(Paint.Align.CENTER);
        }
        pWhite.setStrokeWidth(dip2px(1));

        pOrange = new Paint();
        pOrange.setColor(ContextCompat.getColor(getContext(), R.color.common_orange_20a));

        pCenterLine = new Paint();
        pCenterLine.setStrokeWidth(dip2px(1));
        pCenterLine.setColor(ContextCompat.getColor(getContext(), R.color.common_orange));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        // 中央点位坐标
        int centerX = width / 2;
        int centerY = height / 2;
        if (style == STYLE_BIG) {
            displayTop = 0;
            displayBottom = height;
            lineCenterTop = 0;
            lineCenterBottom = height;
            lineCenterWidth = dip2px(2);
            lineSmallTop = centerY - dip2px(8);
            lineSmallBottom = centerY + dip2px(8);
            lineBigTop = centerY - dip2px(23);
            lineBigBottom = centerY + dip2px(23);
            textStartOffset = dip2px(2);
            textBaseline = height - dip2px(6);
        } else {
            displayTop = centerY - dip2px(15);
            displayBottom = centerY + dip2px(1);
            lineCenterTop = centerY - dip2px(15);
            lineCenterBottom = centerY + dip2px(1);
            lineCenterWidth = 1;
            lineSmallTop = centerY - dip2px(3);
            lineSmallBottom = centerY + dip2px(1);
            lineBigTop = centerY - dip2px(7);
            lineBigBottom = centerY + dip2px(1);
            textStartOffset = 0;
            textBaseline = centerY + dip2px(14);
        }
        intervalValue = getIntervalValue();
    }

    //设置监听
    public void setListener(OnZFTimeLineListener listener) {
        this.listener = listener;
    }

    /**
     * 设定自定义的小刻度时间间隔以及每个大刻度包含小刻度的个数
     *
     * @param second 每个小刻度代表的时间段（秒）
     * @param count  每个大刻度包含小刻度的个数
     */
    public void setInterval(int second, int count) {
        intervalSeconds = second;
        intervalCount = count;
    }

    //把当前秒数设置我中间刻度对应的秒数
    private void timeNow() {
        currentInterval = System.currentTimeMillis() / 1000;
    }

    private float dip2px(float dipValue) {
        return dipValue * ((float) getResources().getDisplayMetrics().densityDpi / 160);
    }

    private int getIntervalValue() {
        return (int) (dip2px((float) 10) + 0.5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (currentInterval < leftBound) {
            currentInterval = leftBound;
        } else if (currentInterval > rightBound) {
            currentInterval = rightBound;
        }

        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        double secondsPerPixel = (double) intervalSeconds / intervalValue;

        // 左边第一个刻度代表的秒数
        long leftInterval = (long) Math.max(currentInterval - centerX * secondsPerPixel, leftBound);
        // 右边界线秒数
        long rightInterval = (long) Math.min(currentInterval + centerX * secondsPerPixel, rightBound);

        // 第一个刻度线所代表的秒数
        long interval = (long) Math.ceil(leftInterval / intervalSeconds) * intervalSeconds;

        //渲染回放视频区域
        if (videoData != null) {
            for (int i = 0; i < videoData.size(); i++) {
                VideoTimeSlotBean info = videoData.get(i);
                //获取视频文件的开始秒数和结束秒数
                long startInterval = info.getStartTime();
                long endInterval = info.getEndTime();
                //判断是否需要绘制
                if (startInterval > rightInterval || endInterval < leftInterval) {
                    continue;
                }
                // 根据中心线计算区域的左右边界位置
                float start = (float) Math.max((startInterval - currentInterval) / secondsPerPixel + centerX, 0f);
                float end = (float) Math.min((endInterval - currentInterval) / secondsPerPixel + centerX, width);
                canvas.drawRect(start, displayTop, end, displayBottom, pOrange);
            }
        }
        // 画刻度线
        while (interval <= rightInterval) {
            // 计算刻度线的位置
            float x = (float) ((interval - currentInterval) / secondsPerPixel + centerX);
            x = Math.max(0, Math.min(x, width));
            // 计算是否为大刻度
            long rem = interval % (intervalSeconds * intervalCount);
            // 根据秒数值对大刻度间隔是否整除判断画长刻度或者短刻度
            if (rem != 0) {
                // 不可整除，小刻度
                canvas.drawLine(x, lineSmallTop, x, lineSmallBottom, pWhite);
            } else {
                // 整除，大刻度
                canvas.drawLine(x, lineBigTop, x, lineBigBottom, pWhite);
                //大刻度绘制时间文字
                String time = formatterScale.format(interval * 1000);
                canvas.drawText(time, x + textStartOffset, textBaseline, pWhite);
            }
            interval = interval + intervalSeconds;
        }
        //画中间线
        canvas.drawRoundRect(centerX - lineCenterWidth / 2, lineCenterTop,
                centerX + lineCenterWidth / 2, lineCenterBottom,
                lineCenterWidth, lineCenterWidth, pCenterLine);
    }

    //通过onTouchEvent来实现拖动和缩放
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                //滑动开始
                moveStartX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                double secondsPerPixel = (double) intervalSeconds / intervalValue;
                currentInterval = (long) (currentInterval - secondsPerPixel * (event.getX() - moveStartX));
                if (currentInterval < leftMoveBound) {
                    currentInterval = leftMoveBound;
                } else if (currentInterval > rightMoveBound) {
                    currentInterval = rightMoveBound;
                }
                if (listener != null && currentInterval > leftMoveBound && currentInterval < rightMoveBound) {
                    listener.moveTo(DateTimeUtils.secondToDate(currentInterval, "yyyy-MM-dd HH:mm:ss"),
                            (moveStartX - event.getX()) < 0, currentInterval);
                }
                moveStartX = event.getX();
                break;
            case MotionEvent.ACTION_CANCEL:
                // fall through
            case MotionEvent.ACTION_UP:
                //滑动结束
                if (listener != null) {
                    listener.didMoveToTime(currentInterval < leftMoveBound ? leftMoveBound
                            : currentInterval > rightMoveBound ? rightMoveBound : currentInterval);
                }

                break;
            default:
        }

        invalidate();
        return true;
    }

    //所有暴露的刷新方法使用不当会引起崩溃(在时间轴创建之后但是没有显示的时候调用),解决办法是使用handel来调用该方法
    //刷新,重新绘制
    public void refresh() {
        invalidate();
    }

    //刷新到当前时间
    public void refreshNow() {
        if (onLock) {
            return;
        }
        timeNow();
        refresh();
    }

    //移动到某时间  传入参数格式举例 20170918120000
    public void moveToTime(String timeStr) {
        if (onLock) {
            return;
        }
        try {
            currentInterval = formatterProject.parse(timeStr).getTime() / 1000;
            invalidate();
            if (listener != null) {
                listener.didMoveToTime(currentInterval);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    //移动到某时间 传入秒数
    public void moveToTime(long timeInterval) {
        if (onLock) {
            return;
        }
        if (timeInterval == 0) {
            return;
        }
        currentInterval = timeInterval;
        invalidate();
    }

    //移动到某时间 传入秒数
    public void autoMove() {
        currentInterval += 60;
        invalidate();
    }

    //获取当前时间轴指向的时间 返回参数格式举例 20170918120000
    public String currentTimeStr() {
        return formatterProject.format(currentInterval * 1000);
    }

    //锁定,不可拖动和缩放
    public void lockMove() {
        onLock = true;
    }

    //解锁,可以拖动和缩放
    public void unLockMove() {
        onLock = false;
    }

    //获取当前时间轴指向的时间的秒数
    public long getCurrentInterval() {
        return currentInterval;
    }

    //把时间数据转化为秒数
    public long timeIntervalFromStr(String str) {
        try {
            return formatterProject.parse(str).getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //把秒数转化为时间字符串
    public String timeStrFromInterval(long interval) {
        return formatterProject.format(interval * 1000);
    }

    //写入视频数据
    public void setVideoData(List<VideoTimeSlotBean> videos) {
        this.videoData = videos;
        refresh();
    }

    //清除视频信息
    public void clearData() {
        this.videoData = null;
        refresh();
    }

    public void setBound(long leftBound, long rightBound) {
        this.leftBound = leftBound;
        this.rightBound = rightBound;
        this.leftMoveBound = leftBound;
        this.rightMoveBound = rightBound;
    }

    public void setBound(long leftBound, long rightBound, long leftMoveBound, long rightMoveBound) {
        this.leftBound = leftBound;
        this.rightBound = rightBound;
        this.leftMoveBound = Math.max(leftBound, leftMoveBound);
        this.rightMoveBound = Math.min(rightBound, rightMoveBound);
    }

    //拖动时间轴监听
    public interface OnZFTimeLineListener {

        void didMoveToTime(long timeStamp);

        void moveTo(String data, boolean isLeftScroll, long timeStamp);
    }

}