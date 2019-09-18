package com.sunmi.ipc.utils;

import java.util.Timer;
import java.util.TimerTask;

import sunmi.common.constant.CommonNotifications;
import sunmi.common.notification.BaseNotification;

/**
 * @author yangShiJie
 * @date 2019/8/6
 */
public class TimeoutTimer extends Timer {
    private static final int TIMEOUT = 10;
    private TimeoutTimer timer = null;
    private TimerTask timerTask = null;
    private int countDown;

    private static final TimeoutTimer INSTANCE = new TimeoutTimer();

    public static TimeoutTimer getInstance() {
        return INSTANCE;
    }

    private TimeoutTimer() {
    }

    public void start() {
        if (timer != null) {
            return;
        }
        timer = new TimeoutTimer();
        timer.schedule(timerTask = new TimerTask() {
            @Override
            public void run() {
                if (countDown++ == TIMEOUT) {
                    stop();
                    BaseNotification.newInstance().postNotificationName(CommonNotifications.timeout);
                }
            }
        }, 0, 1000);
    }

    public void stop() {
        countDown = 0;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }
}
