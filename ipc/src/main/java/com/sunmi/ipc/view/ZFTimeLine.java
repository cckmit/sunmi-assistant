package com.sunmi.ipc.view;

import android.content.Context;
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

    private final int INTERVAL_SECONDS = 60 * 10;   //小刻度代表的秒数
    private int intervalValue;                      //小刻度宽度
    private long currentInterval;                   //中间刻度对应的秒数

    private Paint pWhite, pOrange, pCenterLine;     //三种不同颜色的画笔
    private float moveStartX = 0;                   //用于记录单点触摸点位置,用于计算拖距离

    private boolean onLock;                         //用于屏蔽时间轴拖动,为true时无法拖动

    private SimpleDateFormat formatterScale;        //日期格式化,用于秒数和时间字符的转换
    private SimpleDateFormat formatterProject;      //日期格式化,用于秒数和时间字符的转换

    private OnZFTimeLineListener listener;          //时间轴拖动监听,这个只在拖动完成时返回数据

    List<VideoTimeSlotBean> videoData;              //已录制视频数据信息
    //刻度尺移动定时器
    private ScheduledExecutorService executorService;

    public ZFTimeLine(Context context) {
        super(context);
        init();
    }

    public ZFTimeLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZFTimeLine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //数据数据初始化
    private void init() {
        intervalValue = 0;
        timeNow();
        formatterScale = new SimpleDateFormat("HH:mm", Locale.getDefault());
        formatterProject = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());

        pWhite = new Paint();
        pWhite.setColor(Color.WHITE);
        pWhite.setTextSize(getIntervalValue());
        pWhite.setAntiAlias(true);
        pWhite.setTextAlign(Paint.Align.CENTER);
        pWhite.setStrokeWidth(dip2px(1));

        pOrange = new Paint();
        pOrange.setColor(ContextCompat.getColor(getContext(), R.color.common_orange_20a));

        pCenterLine = new Paint();
        pCenterLine.setStrokeWidth(dip2px(1));
        pCenterLine.setColor(ContextCompat.getColor(getContext(), R.color.common_orange));
    }

    //设置监听
    public void setListener(OnZFTimeLineListener listener) {
        this.listener = listener;
    }

    //把当前秒数设置我中间刻度对应的秒数
    private void timeNow() {
        currentInterval = System.currentTimeMillis() / 1000;
    }

    //宽度1所代表的秒数
    private long secondsOfIntervalValue() {
        return (long) (INTERVAL_SECONDS / intervalValue);
    }

    private float dip2px(float dipValue) {
        return dipValue * (getResources().getDisplayMetrics().densityDpi / 160);
    }

    private int getIntervalValue() {
        return (int) (dip2px((float) 10) + 0.5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //初始化小刻度的间隔,在init里densityDpi的数据为0,所以放到这里了
        if (intervalValue == 0) {
            intervalValue = getIntervalValue();
        }

        //中间线的x值
        long centerX = getWidth() / 2;
        //左边界线代表的秒数
        long leftInterval = currentInterval - centerX * secondsOfIntervalValue();
        //右边界线秒数
        long rightInterval = currentInterval + centerX * secondsOfIntervalValue();

        //下面计算需要绘制的第一个刻度线的位置和所代表的秒数
        long first = leftInterval / INTERVAL_SECONDS;
        //记录所绘制刻度线代表的秒数
        long interval = ((first + 1) * INTERVAL_SECONDS);
        //记录绘制刻度线的位置
        long x = (interval - leftInterval) / secondsOfIntervalValue();

        //这里是这个项目特有的需求,根据视频数据绘制绿色和红色区域,分别代表该位置有已录制的普通视频和紧急视频
        float displayTop = getHeight() / 2 - dip2px(15);
        float displayBottom = getHeight() / 2 + dip2px(1);
        //渲染回放视频区域
        if (videoData != null) {
            for (int i = 0; i < videoData.size(); i++) {
                VideoTimeSlotBean info = videoData.get(i);
                //获取视频文件的开始秒数和结束秒数
                long startInterval = info.getStartTime();
                long endInterval = info.getEndTime();
                //判断是否需要绘制
                if ((startInterval > leftInterval && startInterval < rightInterval)
                        || (endInterval > leftInterval && endInterval < rightInterval)
                        || (startInterval < leftInterval && endInterval > rightInterval)) {
                    canvas.drawRect((startInterval - leftInterval) / secondsOfIntervalValue(),
                            displayTop, (endInterval - leftInterval) / secondsOfIntervalValue(),
                            displayBottom, pOrange);
                }
            }
        }
        //画刻度线
        while (x >= 0 && x <= getWidth()) {
            int a = INTERVAL_SECONDS;//长刻度线间隔所代表的时间长度,用于计算,单位是秒
            long rem = interval % (a * 6);
            //根据秒数值对大刻度间隔是否整除判断画长刻度或者短刻度
            if (rem != 0) {//小刻度
                canvas.drawLine(x, getHeight() / 2 - dip2px(3),
                        x, displayBottom, pWhite);
            } else {//大刻度
                canvas.drawLine(x, getHeight() / 2 - dip2px(7),
                        x, displayBottom, pWhite);
                //大刻度绘制时间文字
                String time = formatterScale.format(interval * 1000);
                canvas.drawText(time, x, getHeight() / 2 + dip2px(14), pWhite);
            }
            //下一个刻度
            x = x + intervalValue;
            interval = interval + a;
        }
        //画中间线
        canvas.drawLine(centerX, displayTop, centerX, displayBottom, pCenterLine);
    }

    //通过onTouchEvent来实现拖动和缩放
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                moveStartX = event.getX();
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                currentInterval = currentInterval - secondsOfIntervalValue()
                        * ((long) (event.getX() - moveStartX));
                if (listener != null) {
                    listener.moveTo(DateTimeUtils.secondToDate(currentInterval, "yyyy-MM-dd HH:mm:ss"),
                            (moveStartX - event.getX()) < 0, currentInterval);
                }
                moveStartX = event.getX();
            }
            break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                //拖动结束  这里应该有Bug没有区分移动可缩放状态 不过影响不大
                if (listener != null) {
                    listener.didMoveToDate(formatterProject.format(currentInterval * 1000),
                            currentInterval);
                }
            }
            break;
        }
        //重新绘制
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
                listener.didMoveToDate(formatterProject.format(currentInterval * 1000), currentInterval);
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
        if (timeInterval == 0) return;
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

    //拖动时间轴监听
    public interface OnZFTimeLineListener {

        void didMoveToDate(String date, long timeStamp);

        void moveTo(String data, boolean isLeftScroll, long timeStamp);
    }

}