package com.sunmi.ipc.face.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author yinhui
 * @date 2019-08-20
 */
public class Face implements Parcelable {
    /**
     * face_id : 1
     * name : Mike
     * gender : 1
     * age_range_code : 2
     * group_id : 3
     * arrival_count : 3
     * create_time : 1561375084
     * last_arrival_time : 1561375084
     * img_url : https://cdn.sunmi.com/****
     */

    @SerializedName("face_id")
    private int faceId;
    @SerializedName("name")
    private String name;
    @SerializedName("gender")
    private int gender;
    @SerializedName("age_range_code")
    private int ageRangeCode;
    @SerializedName("group_id")
    private int groupId;
    @SerializedName("arrival_count")
    private int arrivalCount;
    @SerializedName("create_time")
    private int createTime;
    @SerializedName("last_arrival_time")
    private int lastArrivalTime;
    @SerializedName("img_url")
    private String imgUrl;

    public int getFaceId() {
        return faceId;
    }

    public String getName() {
        return name;
    }

    public int getGender() {
        return gender;
    }

    public int getAgeRangeCode() {
        return ageRangeCode;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getArrivalCount() {
        return arrivalCount;
    }

    public int getCreateTime() {
        return createTime;
    }

    public int getLastArrivalTime() {
        return lastArrivalTime;
    }

    public String getImgUrl() {
        return imgUrl;
    }


    protected Face(Parcel in) {
        faceId = in.readInt();
        name = in.readString();
        gender = in.readInt();
        ageRangeCode = in.readInt();
        groupId = in.readInt();
        arrivalCount = in.readInt();
        createTime = in.readInt();
        lastArrivalTime = in.readInt();
        imgUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(faceId);
        dest.writeString(name);
        dest.writeInt(gender);
        dest.writeInt(ageRangeCode);
        dest.writeInt(groupId);
        dest.writeInt(arrivalCount);
        dest.writeInt(createTime);
        dest.writeInt(lastArrivalTime);
        dest.writeString(imgUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Face> CREATOR = new Creator<Face>() {
        @Override
        public Face createFromParcel(Parcel in) {
            return new Face(in);
        }

        @Override
        public Face[] newArray(int size) {
            return new Face[size];
        }
    };

}
