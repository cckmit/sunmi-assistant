package com.sunmi.ipc.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author yinhui
 * @date 2019-12-30
 */
public class CashVideoAbnormalEventResp {

    /**
     * event_id : 2
     * event_type : 101
     * start_time : 1557805280
     * end_time : 1557805280
     * risk_score : 82.2
     * resolution : [1440,1440]
     * key_objects : [{"timestamp":[1.55780528522E9,1.55780529022E9],"key_object":1,"bbox":[0.82,0.4,0.85,0.42]}]
     */

    @SerializedName("event_id")
    private int eventId;
    @SerializedName("event_type")
    private int eventType;
    @SerializedName("start_time")
    private long startTime;
    @SerializedName("end_time")
    private long endTime;
    @SerializedName("risk_score")
    private double riskScore;
    @SerializedName("resolution")
    private int[] resolution;
    @SerializedName("key_objects")
    private List<Box> keyObjects;

    public int getEventId() {
        return eventId;
    }

    public int getEventType() {
        return eventType;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public int[] getResolution() {
        return resolution;
    }

    public List<Box> getKeyObjects() {
        return keyObjects;
    }

    public static class Box {

        /**
         * timestamp : [1.55780528522E9,1.55780529022E9]
         * key_object : 1
         * bbox : [0.82,0.4,0.85,0.42]
         */

        @SerializedName("key_object")
        private int keyObject;
        @SerializedName("timestamp")
        private double[] timestamp;
        @SerializedName("bbox")
        private float[] box;

        public int getKeyObject() {
            return keyObject;
        }

        public double[] getTimestamp() {
            return timestamp;
        }

        public float[] getBox() {
            return box;
        }
    }
}
