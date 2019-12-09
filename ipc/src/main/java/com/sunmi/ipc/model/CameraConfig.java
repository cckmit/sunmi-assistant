package com.sunmi.ipc.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author yinhui
 * @date 2019-07-31
 */
public class CameraConfig implements Parcelable {

    @SerializedName("max_zoom")
    private int maxZoom;
    @SerializedName("max_focus")
    private int maxFocus;
    @SerializedName("zoom")
    private int currentZoom;
    @SerializedName("focus")
    private int currentFocus;
    @SerializedName("irmode")
    private int nightMode;
    @SerializedName("auto_focus_start")
    private int isAutoFocusing;
    @SerializedName("focused_x")
    private int autoFocusPointX;
    @SerializedName("focused_y")
    private int autoFocusPointY;

    public int getMaxZoom() {
        return maxZoom;
    }

    public int getMaxFocus() {
        return maxFocus;
    }

    public int getCurrentZoom() {
        return currentZoom;
    }

    public int getCurrentFocus() {
        return currentFocus;
    }

    public int getNightMode() {
        return nightMode;
    }

    public int getIsAutoFocusing() {
        return isAutoFocusing;
    }

    public int getAutoFocusPointX() {
        return autoFocusPointX;
    }

    public int getAutoFocusPointY() {
        return autoFocusPointY;
    }

    protected CameraConfig(Parcel in) {
        maxZoom = in.readInt();
        maxFocus = in.readInt();
        currentZoom = in.readInt();
        currentFocus = in.readInt();
        nightMode = in.readInt();
        isAutoFocusing = in.readInt();
        autoFocusPointX = in.readInt();
        autoFocusPointY = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(maxZoom);
        dest.writeInt(maxFocus);
        dest.writeInt(currentZoom);
        dest.writeInt(currentFocus);
        dest.writeInt(nightMode);
        dest.writeInt(isAutoFocusing);
        dest.writeInt(autoFocusPointX);
        dest.writeInt(autoFocusPointY);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CameraConfig> CREATOR = new Creator<CameraConfig>() {
        @Override
        public CameraConfig createFromParcel(Parcel in) {
            return new CameraConfig(in);
        }

        @Override
        public CameraConfig[] newArray(int size) {
            return new CameraConfig[size];
        }
    };

}
