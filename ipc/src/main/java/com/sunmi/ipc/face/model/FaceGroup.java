package com.sunmi.ipc.face.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.sunmi.ipc.model.FaceGroupListResp;

/**
 * @author yinhui
 * @date 2019-08-16
 */
public class FaceGroup implements Parcelable {

    public static final int FACE_GROUP_TYPE_NEW = 1;
    public static final int FACE_GROUP_TYPE_OLD = 2;
    public static final int FACE_GROUP_TYPE_STAFF = 3;
    public static final int FACE_GROUP_TYPE_BLACK = 4;
    public static final int FACE_GROUP_TYPE_CUSTOM = 5;

    public static final int MAX_CAPACITY_ALL_GROUP = 10000;

    private String name;
    private String mark;
    private int groupId;
    private int type;
    private int threshold;
    private int period;
    private int capacity;
    private int count;
    private int alarmNotified;

    public FaceGroup(FaceGroupListResp.Group group) {
        this.name = group.getGroupName();
        this.mark = group.getMark();
        this.groupId = group.getGroupId();
        this.type = group.getType();
        this.threshold = group.getThreshold();
        this.period = group.getPeriod();
        this.capacity = group.getCapacity();
        this.count = group.getCount();
        this.alarmNotified = group.getAlarmNotified();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getAlarmNotified() {
        return alarmNotified;
    }

    public void setAlarmNotified(int alarmNotified) {
        this.alarmNotified = alarmNotified;
    }

    protected FaceGroup(Parcel in) {
        name = in.readString();
        mark = in.readString();
        groupId = in.readInt();
        type = in.readInt();
        threshold = in.readInt();
        period = in.readInt();
        capacity = in.readInt();
        count = in.readInt();
        alarmNotified = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(mark);
        dest.writeInt(groupId);
        dest.writeInt(type);
        dest.writeInt(threshold);
        dest.writeInt(period);
        dest.writeInt(capacity);
        dest.writeInt(count);
        dest.writeInt(alarmNotified);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FaceGroup> CREATOR = new Creator<FaceGroup>() {
        @Override
        public FaceGroup createFromParcel(Parcel in) {
            return new FaceGroup(in);
        }

        @Override
        public FaceGroup[] newArray(int size) {
            return new FaceGroup[size];
        }
    };
}
