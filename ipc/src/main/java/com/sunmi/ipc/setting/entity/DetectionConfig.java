package com.sunmi.ipc.setting.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author yinhui
 * @date 2019-07-19
 */
public class DetectionConfig implements Parcelable {

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

    protected DetectionConfig(Parcel in) {
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

    public static final Creator<DetectionConfig> CREATOR = new Creator<DetectionConfig>() {
        @Override
        public DetectionConfig createFromParcel(Parcel in) {
            return new DetectionConfig(in);
        }

        @Override
        public DetectionConfig[] newArray(int size) {
            return new DetectionConfig[size];
        }
    };

}
