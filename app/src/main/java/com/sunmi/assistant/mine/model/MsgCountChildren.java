package com.sunmi.assistant.mine.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-16.
 */
public class MsgCountChildren implements Parcelable, Comparable<MsgCountChildren> {
    /**
     * model_id : 111
     * model_name : 商品库对接完成（tag）
     * total_count : 18
     * unread_count : 18
     * remind_unread_count : 18
     * last_receive_time : 1563246183
     */

    @SerializedName("model_id")
    private int modelId;
    @SerializedName("model_name")
    private String modelName;
    @SerializedName("total_count")
    private int totalCount;
    @SerializedName("unread_count")
    private int unreadCount;
    @SerializedName("remind_unread_count")
    private int remindUnreadCount;
    @SerializedName("last_receive_time")
    private int lastReceiveTime;
    @SerializedName("children")
    private List<MsgCountChildren> children;

    protected MsgCountChildren(Parcel in) {
        modelId = in.readInt();
        modelName = in.readString();
        totalCount = in.readInt();
        unreadCount = in.readInt();
        remindUnreadCount = in.readInt();
        lastReceiveTime = in.readInt();
        children = in.createTypedArrayList(MsgCountChildren.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(modelId);
        dest.writeString(modelName);
        dest.writeInt(totalCount);
        dest.writeInt(unreadCount);
        dest.writeInt(remindUnreadCount);
        dest.writeInt(lastReceiveTime);
        dest.writeTypedList(children);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MsgCountChildren> CREATOR = new Creator<MsgCountChildren>() {
        @Override
        public MsgCountChildren createFromParcel(Parcel in) {
            return new MsgCountChildren(in);
        }

        @Override
        public MsgCountChildren[] newArray(int size) {
            return new MsgCountChildren[size];
        }
    };

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public int getRemindUnreadCount() {
        return remindUnreadCount;
    }

    public void setRemindUnreadCount(int remindUnreadCount) {
        this.remindUnreadCount = remindUnreadCount;
    }

    public int getLastReceiveTime() {
        return lastReceiveTime;
    }

    public void setLastReceiveTime(int lastReceiveTime) {
        this.lastReceiveTime = lastReceiveTime;
    }

    public List<MsgCountChildren> getChildren() {
        return children;
    }

    public void setChildren(List<MsgCountChildren> children) {
        this.children = children;
    }

    @Override
    public int compareTo(MsgCountChildren o) {
        return o.lastReceiveTime - lastReceiveTime;
    }
}
