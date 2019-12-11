package com.sunmi.ipc.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author yinhui
 * @date 2019-12-09
 */
public class MotionVideo implements Parcelable {

    /**
     * id : 49
     * device_name : 绑定测试1
     * sn : yyyyyyyyyyyyy
     * source : 1
     * detect_time : 1554202517
     * cdn_address : http://test.cdn.sunmi.com/VIDEO/IPC/41dd3f089ee0746efa4f9c0500719e71d1cf474b111f2a72e832906e619bc6f8
     * snapshot_address : http://test.cdn.sunmi.com/VIDEO/IPC/41dd3f089ee0746efa4f9c0500719e71d1cf474b111f2a72e832906e619bc6f8?x-oss-process=video/snapshot,t_2300,f_jpg,m_fast
     */

    @SerializedName("id")
    private int id;
    @SerializedName("device_name")
    private String deviceName;
    @SerializedName("sn")
    private String sn;
    @SerializedName("source")
    private int source;
    @SerializedName("detect_time")
    private int detectTime;
    @SerializedName("cdn_address")
    private String cdnAddress;
    @SerializedName("snapshot_address")
    private String snapshotAddress;

    public int getId() {
        return id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getSn() {
        return sn;
    }

    public int getSource() {
        return source;
    }

    public int getDetectTime() {
        return detectTime;
    }

    public String getCdnAddress() {
        return cdnAddress;
    }

    public String getSnapshotAddress() {
        return snapshotAddress;
    }

    protected MotionVideo(Parcel in) {
        id = in.readInt();
        deviceName = in.readString();
        sn = in.readString();
        source = in.readInt();
        detectTime = in.readInt();
        cdnAddress = in.readString();
        snapshotAddress = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(deviceName);
        dest.writeString(sn);
        dest.writeInt(source);
        dest.writeInt(detectTime);
        dest.writeString(cdnAddress);
        dest.writeString(snapshotAddress);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MotionVideo> CREATOR = new Creator<MotionVideo>() {
        @Override
        public MotionVideo createFromParcel(Parcel in) {
            return new MotionVideo(in);
        }

        @Override
        public MotionVideo[] newArray(int size) {
            return new MotionVideo[size];
        }
    };

}
