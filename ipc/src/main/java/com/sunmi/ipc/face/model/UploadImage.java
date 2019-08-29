package com.sunmi.ipc.face.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

import sunmi.common.mediapicker.data.model.Image;

/**
 * @author yinhui
 * @date 2019-08-27
 */
public class UploadImage implements Parcelable {

    public static final int STATE_INIT = 0;
    public static final int STATE_UPLOADING = 1;
    public static final int STATE_SUCCESS = 2;
    public static final int STATE_FAILED = 3;
    public static final int STATE_FAILED_NET = 4;
    public static final int STATE_ADD = 10;

    private int state = STATE_INIT;
    private String file;
    private String compressed;
    private String cloudName;

    public UploadImage() {
        this.state = STATE_ADD;
    }

    public UploadImage(Image image) {
        this.file = image.getPath();
    }

    public int getState() {
        return state;
    }

    public String getFile() {
        return file;
    }

    public String getCompressed() {
        return compressed;
    }

    public String getCloudName() {
        return cloudName;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setCompressed(String compressed) {
        this.compressed = compressed;
    }

    public void setCloudName(String cloudName) {
        this.cloudName = cloudName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UploadImage that = (UploadImage) o;
        return file.equals(that.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file);
    }

    protected UploadImage(Parcel in) {
        state = in.readInt();
        file = in.readString();
        compressed = in.readString();
        cloudName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(state);
        dest.writeString(file);
        dest.writeString(compressed);
        dest.writeString(cloudName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UploadImage> CREATOR = new Creator<UploadImage>() {
        @Override
        public UploadImage createFromParcel(Parcel in) {
            return new UploadImage(in);
        }

        @Override
        public UploadImage[] newArray(int size) {
            return new UploadImage[size];
        }
    };

}
