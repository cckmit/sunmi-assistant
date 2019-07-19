package com.sunmi.ipc.setting.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author yinhui
 * @date 2019-07-19
 */
public class IpcDetectionConfig implements Parcelable {

    public static final int DETECTION_ALL_TIME = 0x80;
    public static final String INTENT_EXTRA_DETECTION_CONFIG = "config";

    @SerializedName("audio_level")
    public int soundDetection;
    @SerializedName("motion_level")
    public int activeDetection;
    @SerializedName("weekday")
    public int detectionDays;
    @SerializedName("start_time")
    public int detectionTimeStart;
    @SerializedName("stop_time")
    public int detectionTimeEnd;

    protected IpcDetectionConfig(Parcel in) {
        soundDetection = in.readInt();
        activeDetection = in.readInt();
        detectionDays = in.readInt();
        detectionTimeStart = in.readInt();
        detectionTimeEnd = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(soundDetection);
        dest.writeInt(activeDetection);
        dest.writeInt(detectionDays);
        dest.writeInt(detectionTimeStart);
        dest.writeInt(detectionTimeEnd);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<IpcDetectionConfig> CREATOR = new Creator<IpcDetectionConfig>() {
        @Override
        public IpcDetectionConfig createFromParcel(Parcel in) {
            return new IpcDetectionConfig(in);
        }

        @Override
        public IpcDetectionConfig[] newArray(int size) {
            return new IpcDetectionConfig[size];
        }
    };

}
