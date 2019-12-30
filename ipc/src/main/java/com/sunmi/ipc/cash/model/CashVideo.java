package com.sunmi.ipc.cash.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author yinhui
 * @date 2019-12-30
 */
public class CashVideo implements Parcelable {

    /**
     * video_id : 124
     * video_url : http://test.cdn.sunmi.com/VIDEO/abcdefgh.flv
     * snapshot_url : http://test.cdn.sunmi.com/VIDEO/abcdefgh.flv?*********
     * device_id : 356
     * device_sn : SS101D8BS09178
     * description : ************************
     * video_type : 1
     * video_tag : [1,2]
     * event_id : 123
     * order_no : B12019060414421630291
     * purchase_time : 1565235765
     * amount : 30.12
     * start_time : 1565235765
     * end_time : 1565235777
     */

    @SerializedName("video_id")
    private long videoId;
    @SerializedName("video_url")
    private String videoUrl;
    @SerializedName("snapshot_url")
    private String snapshotUrl;
    @SerializedName("device_id")
    private int deviceId;
    @SerializedName("device_sn")
    private String deviceSn;
    @SerializedName("description")
    private String description;
    @SerializedName("video_type")
    private int videoType;
    @SerializedName("event_id")
    private int eventId;
    @SerializedName("order_no")
    private String orderNo;
    @SerializedName("purchase_time")
    private long purchaseTime;
    @SerializedName("amount")
    private double amount;
    @SerializedName("start_time")
    private long startTime;
    @SerializedName("end_time")
    private long endTime;
    @SerializedName("video_tag")
    private List<Integer> videoTag;

    private String deviceName;
    private boolean hasCashLossPrevent;

    public long getVideoId() {
        return videoId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getSnapshotUrl() {
        return snapshotUrl;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public String getDeviceSn() {
        return deviceSn;
    }

    public String getDescription() {
        return description;
    }

    public int getVideoType() {
        return videoType;
    }

    public int getEventId() {
        return eventId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public long getPurchaseTime() {
        return purchaseTime;
    }

    public double getAmount() {
        return amount;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public List<Integer> getVideoTag() {
        return videoTag;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public boolean isHasCashLossPrevent() {
        return hasCashLossPrevent;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVideoType(int videoType) {
        this.videoType = videoType;
    }

    public void setVideoTag(List<Integer> videoTag) {
        this.videoTag = videoTag;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setHasCashLossPrevent(boolean hasCashLossPrevent) {
        this.hasCashLossPrevent = hasCashLossPrevent;
    }

    protected CashVideo(Parcel in) {
        videoId = in.readLong();
        videoUrl = in.readString();
        snapshotUrl = in.readString();
        deviceId = in.readInt();
        deviceSn = in.readString();
        description = in.readString();
        videoType = in.readInt();
        eventId = in.readInt();
        orderNo = in.readString();
        purchaseTime = in.readLong();
        amount = in.readDouble();
        startTime = in.readLong();
        endTime = in.readLong();
        deviceName = in.readString();
        hasCashLossPrevent = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(videoId);
        dest.writeString(videoUrl);
        dest.writeString(snapshotUrl);
        dest.writeInt(deviceId);
        dest.writeString(deviceSn);
        dest.writeString(description);
        dest.writeInt(videoType);
        dest.writeInt(eventId);
        dest.writeString(orderNo);
        dest.writeLong(purchaseTime);
        dest.writeDouble(amount);
        dest.writeLong(startTime);
        dest.writeLong(endTime);
        dest.writeString(deviceName);
        dest.writeByte((byte) (hasCashLossPrevent ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CashVideo> CREATOR = new Creator<CashVideo>() {
        @Override
        public CashVideo createFromParcel(Parcel in) {
            return new CashVideo(in);
        }

        @Override
        public CashVideo[] newArray(int size) {
            return new CashVideo[size];
        }
    };

}
