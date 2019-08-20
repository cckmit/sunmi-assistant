package com.sunmi.ipc.face.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

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

    public static final int SECONDS_PER_DAY = 86400;
    public static final int MAX_CAPACITY_ALL_GROUP = 10000;
    public static final int MAX_THRESHOLD = 100;
    public static final int MAX_LENGTH_NAME = 20;
    public static final int MAX_LENGTH_MARK = 100;

    /**
     * company_id : 6759
     * shop_id : 8699
     * group_name : stranger
     * mark : stranger
     * target_group_id : 483
     * group_id : 484
     * type : 1
     * threshold : 5
     * period : 86400
     * capacity : 10000
     * count : 0
     * last_modified_time : 1561656491
     * alarm_notified : 0
     */

    @SerializedName("company_id")
    private int companyId;
    @SerializedName("shop_id")
    private int shopId;
    @SerializedName("group_name")
    private String groupName;
    @SerializedName("mark")
    private String mark;
    @SerializedName("target_group_id")
    private int targetGroupId;
    @SerializedName("group_id")
    private int groupId;
    @SerializedName("type")
    private int type;
    @SerializedName("threshold")
    private int threshold;
    @SerializedName("period")
    private int period;
    @SerializedName("capacity")
    private int capacity;
    @SerializedName("count")
    private int count;
    @SerializedName("last_modified_time")
    private int lastModifiedTime;
    @SerializedName("alarm_notified")
    private int alarmNotified;

    public boolean isSystemType() {
        return type >= FACE_GROUP_TYPE_NEW && type <= FACE_GROUP_TYPE_BLACK;
    }

    public boolean isCustomType() {
        return type == FACE_GROUP_TYPE_CUSTOM;
    }

    public int getCompanyId() {
        return companyId;
    }

    public int getShopId() {
        return shopId;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getMark() {
        return mark;
    }

    public int getTargetGroupId() {
        return targetGroupId;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getType() {
        return type;
    }

    public int getThreshold() {
        return threshold;
    }

    public int getPeriod() {
        return period;
    }

    public int getPeriodDays() {
        return period / SECONDS_PER_DAY;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCount() {
        return count;
    }

    public int getLastModifiedTime() {
        return lastModifiedTime;
    }

    public int getAlarmNotified() {
        return alarmNotified;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public void setThreshold(int times, int days) {
        this.threshold = times;
        this.period = days * SECONDS_PER_DAY;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setAlarmNotified(int alarmNotified) {
        this.alarmNotified = alarmNotified;
    }

    protected FaceGroup(Parcel in) {
        companyId = in.readInt();
        shopId = in.readInt();
        groupName = in.readString();
        mark = in.readString();
        targetGroupId = in.readInt();
        groupId = in.readInt();
        type = in.readInt();
        threshold = in.readInt();
        period = in.readInt();
        capacity = in.readInt();
        count = in.readInt();
        lastModifiedTime = in.readInt();
        alarmNotified = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(companyId);
        dest.writeInt(shopId);
        dest.writeString(groupName);
        dest.writeString(mark);
        dest.writeInt(targetGroupId);
        dest.writeInt(groupId);
        dest.writeInt(type);
        dest.writeInt(threshold);
        dest.writeInt(period);
        dest.writeInt(capacity);
        dest.writeInt(count);
        dest.writeInt(lastModifiedTime);
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
