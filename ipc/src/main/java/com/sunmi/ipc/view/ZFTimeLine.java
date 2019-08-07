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
import com.sunmi.ipc.model.VideoListResp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import sunmi.common.utils.log.LogCat;

/**
 * Description:
 * Created by bruce on 2019/8/6.
 */
public class ZFTimeLine extends View {

    private final int SCALE_TYPE_BIG = 1;           //大刻度
//    private final int SCALE_TYPE_SMALL = 2;         //小刻度

    private int intervalValue;                      //小刻度宽度
    //    private int scaleType;
    private long currentInterval;                   //中间刻度对应的时间戳

    private SimpleDateFormat formatterScale;        //日期格式化,用于时间戳和时间字符的转换
    private SimpleDateFormat formatterProject;      //日期格式化,用于时间戳和时间字符的转换

    private Paint pWhite, pOrange, pCenterLine;     //三种不同颜色的画笔
    private int point = 0;                          //用于当前触控点数量
    private float moveStartX = 0;                   //用于记录单点触摸点位置,用于计算拖距离
//    private float scaleValue = 0;                   //用于记录两个触摸点间距,用于时间轴缩放计算

    private boolean onLock;                         //用于屏蔽时间轴拖动,为true时无法拖动

    private OnZFTimeLineListener listener;          //时间轴拖动监听,这个只在拖动完成时返回数据

    List<VideoListResp.VideoBean> videoData;        //已录制视频数据信息

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
//        scaleType = SCALE_TYPE_BIG;
        intervalValue = 0;
        timeNow();
        onLock = false;
        formatterScale = new SimpleDateFormat("HH:mm");
        formatterProject = new SimpleDateFormat("yyyyMMddHHmmss");

        pWhite = new Paint();
        pWhite.setColor(Color.WHITE);
        pWhite.setTextSize(intDip2px(10));
        pWhite.setAntiAlias(true);
        pWhite.setTextAlign(Paint.Align.CENTER);
        pWhite.setStrokeWidth(dip2px(1));

        pOrange = new Paint();
        pOrange.setColor(ContextCompat.getColor(getContext(), R.color.colorOrangeLight));

        pCenterLine = new Paint();
        pCenterLine.setStrokeWidth(dip2px(1));
        pCenterLine.setColor(ContextCompat.getColor(getContext(), R.color.common_orange));
    }

    //设置监听
    public void setListener(OnZFTimeLineListener listener) {
        this.listener = listener;
    }

    //把当前时间戳设置我中间刻度对应的时间戳
    private void timeNow() {
        currentInterval = System.currentTimeMillis();
    }

    //宽度1所代表的毫秒数
    private long millisecondsOfIntervalValue() {
//        if (scaleType == SCALE_TYPE_BIG) {
        return (long) (6 * 60000.0 / intervalValue);
//        } else {
//            return (long) (60000.0 / intervalValue);
//        }
    }

    private float dip2px(float dipValue) {
        return dipValue * (getResources().getDisplayMetrics().densityDpi / 160);
    }

    private int intDip2px(float dipValue) {
        return (int) (dip2px(dipValue) + 0.5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //初始化小刻度的间隔,在init里densityDpi的数据为0,所以放到这里了
        if (intervalValue == 0) intervalValue = intDip2px(10);

        //中间线的x值
        long centerX = getWidth() / 2;
        //左边界线代表的时间戳
        long leftInterval = currentInterval - centerX * millisecondsOfIntervalValue();
        //右边界线时间戳
        long rightInterval = currentInterval + centerX * millisecondsOfIntervalValue();

        long x;             //记录绘制刻度线的位置
        long interval;      //记录所绘制刻度线代表的时间戳

        //下面计算需要绘制的第一个刻度线的位置和所代表的时间戳
//        if (scaleType == SCALE_TYPE_BIG) {
        long first = leftInterval / (60 * 6 * 1000);
        interval = ((first + 1) * (60 * 6 * 1000));
        x = (interval - leftInterval) / millisecondsOfIntervalValue();
//        } else {
//            long a = leftInterval / (60 * 1000);
//            interval = ((a + 1) * (60 * 1000));
//            x = (interval - leftInterval) / millisecondsOfIntervalValue();
//        }

        //这里是这个项目特有的需求,根据视频数据绘制绿色和红色区域,分别代表该位置有已录制的普通视频和紧急视频(行车记录仪)
        if (videoData != null) {
            for (int i = 0; i < videoData.size(); i++) {
                VideoListResp.VideoBean info = videoData.get(i);
                //获取视频文件的开始时间戳和结束时间戳
                long startInterval = (long) info.getStart_time();
                long endInterval = (long) info.getEnd_time();
                //判断是否需要绘制
                if ((startInterval > leftInterval && startInterval < rightInterval)
                        || (endInterval > leftInterval && endInterval < rightInterval)
                        || (startInterval < leftInterval && endInterval > rightInterval)) {
                    //将开始和结束时间戳转化为对应的x的位置
                    long startX = (startInterval - leftInterval) / millisecondsOfIntervalValue();
                    long endX = (endInterval - leftInterval) / millisecondsOfIntervalValue();
//                    if (info.().contains("SOS")){
//                        //紧急视频 为红色区域色块
//                        canvas.drawRect(startX,0,endX,getHeight()-dip2px(24),paintRed);
//                    }else {
//                        //普通的为绿色
                    canvas.drawRect(startX, 0, endX, getHeight() - dip2px(24), pOrange);
//                    }
                }

//                LogCat.e("====>", "" + info.getStartTime().getTimeInMillis());
            }
        }
        //画刻度线
        while (x >= 0 && x <= getWidth()) {
            int a;//长刻度线间隔所代表的时间长度,用于计算,单位是毫秒
//            if (scaleType == SCALE_TYPE_BIG) {
            a = 60000 * 6;
//            } else {
//                a = 60000;
//            }
            long rem = interval % (a * 5);
            //根据时间戳值对大刻度间隔是否整除判断画长刻度或者短刻度
            if (rem != 0) {//小刻度
                canvas.drawLine(x, getHeight() / 2 - dip2px(3),
                        x, getHeight() / 2 + dip2px(1), pWhite);
            } else {//大刻度
                canvas.drawLine(x, getHeight() / 2 - dip2px(7),
                        x, getHeight() / 2 + dip2px(1), pWhite);
                //大刻度绘制时间文字
                String time = formatterScale.format(interval);
                canvas.drawText(time, x, getHeight() / 2 + dip2px(14), pWhite);
            }
            //下一个刻度
            x = x + intervalValue;
            interval = interval + a;
        }
        //画中间线
        canvas.drawLine(centerX, getHeight() / 2 - dip2px(15),
                centerX, getHeight() / 2 + dip2px(1), pCenterLine);
    }

    //通过onTouchEvent来实现拖动和缩放
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                LogCat.e("touch", "ACTION_DOWN" + event.getX());
                point = 1;
                moveStartX = event.getX();
            }
            break;
            case MotionEvent.ACTION_POINTER_DOWN: {
//                LogCat.e("touch","ACTION_POINTER_DOWN" + event.getX(0) + "-----" + event.getX(1));
                point = point + 1;
//                if (point == 2) {
//                    scaleValue = Math.abs(event.getX(1) - event.getX(0));
//                }
            }
            break;
            case MotionEvent.ACTION_MOVE: {
//                LogCat.e("touch","ACTION_MOVE");
                if (point == 1) {
                    //拖动
                    currentInterval = currentInterval - millisecondsOfIntervalValue()
                            * ((long) (event.getX() - moveStartX));
                    moveStartX = event.getX();
                }
//                else if (point == 2) {
//                    float value = Math.abs(event.getX(1) - event.getX(0));
//                    if (scaleType == SCALE_TYPE_BIG) {
//                        if (scaleValue - value < 0) {//变大
//                            intervalValue = intervalValue + ((int) ((value - scaleValue) / dip2px(100)));
//                            if (intervalValue >= intDip2px(15)) {
//                                scaleType = SCALE_TYPE_SMALL;
//                                intervalValue = intDip2px(10);
//                            }
//                        } else {//变小
//                            intervalValue = intervalValue + ((int) ((value - scaleValue) / dip2px(100)));
//                            if (intervalValue < intDip2px(10)) {
//                                intervalValue = intDip2px(10);
//                            }
//                        }
//                    } else {
//                        if (scaleValue - value < 0) {//变大
//                            intervalValue = intervalValue + ((int) ((value - scaleValue) / dip2px(100)));
//                            if (intervalValue >= intDip2px(15)) {
//                                intervalValue = intDip2px(15);
//                            }
//                        } else {//变小
//                            intervalValue = intervalValue + ((int) ((value - scaleValue) / dip2px(100)));
//                            if (intervalValue < intDip2px(10)) {
//                                scaleType = SCALE_TYPE_BIG;
//                                intervalValue = intDip2px(10);
//                            }
//                        }
//                    }
//                }
                else {
                    return true;
                }
            }
            break;
            case MotionEvent.ACTION_POINTER_UP: {
//                LogCat.e("touch","ACTION_POINTER_UP");
                point = point - 1;
            }
            break;
            case MotionEvent.ACTION_UP: {
//                LogCat.e("touch","ACTION_UP");
                point = 0;
                //拖动结束  这里应该有Bug没有区分移动可缩放状态 不过影响不大
                if (listener != null) {
                    listener.didMoveToDate(formatterProject.format(currentInterval));
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
        if (onLock || point != 0) {
            return;
        }
        timeNow();
        refresh();
    }

    //移动到某时间  传入参数格式举例 20170918120000
    public void moveToTime(String timeStr) {
        if (onLock || point != 0) {
            return;
        }
        try {
            currentInterval = formatterProject.parse(timeStr).getTime();
            invalidate();
            if (listener != null) {
                listener.didMoveToDate(formatterProject.format(currentInterval));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    //移动到某时间 传入时间戳
    public void moveToTime(long timeInterval) {
        if (onLock || point != 0) {
            return;
        }
        if (timeInterval == 0) return;
        currentInterval = timeInterval;
        invalidate();
    }

    //获取当前时间轴指向的时间 返回参数格式举例 20170918120000
    public String currentTimeStr() {
        return formatterProject.format(currentInterval);
    }

    //锁定,不可拖动和缩放
    public void lockMove() {
        onLock = true;
    }

    //解锁,可以拖动和缩放
    public void unLockMove() {
        onLock = false;
    }

    //获取当前时间轴指向的时间的时间戳
    public long getCurrentInterval() {
        return currentInterval;
    }

    //把时间数据转化为时间戳
    public long timeIntervalFromStr(String str) {
        try {
            return formatterProject.parse(str).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //把时间戳转化为时间字符串
    public String timeStrFromInterval(long interval) {
        return formatterProject.format(interval);
    }

    //写入视频数据
    public void setVideoData(List<VideoListResp.VideoBean> videos) {
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
        void didMoveToDate(String date);
    }

}