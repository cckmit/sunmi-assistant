package com.sunmi.ipc.cash.model;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * @author yinhui
 * @date 2019-12-20
 */
public class CashBox {

    private int startTime;
    private int endTime;
    private int key;
    private float[] rect;

    public CashBox(int startTime, int endTime, float[] rect) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.rect = rect;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public int getKey() {
        return key;
    }

    public float[] getRect() {
        return rect;
    }

    @NotNull
    @Override
    public String toString() {
        return "CashBox{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", key=" + key +
                ", rect=" + Arrays.toString(rect) +
                '}';
    }
}
