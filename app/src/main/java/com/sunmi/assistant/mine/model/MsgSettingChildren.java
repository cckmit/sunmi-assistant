package com.sunmi.assistant.mine.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-22.
 */
public class MsgSettingChildren implements Parcelable {
    /**
     * id : 11
     * name : 网络摄像机（tag）
     * pid : 1
     * status : 1
     * children : [{"id":111,"name":"IPC上下线（tag）","pid":"1","status":1,"children":[]}]
     */

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("pid")
    private String pid;
    @SerializedName("status")
    private int status;
    @SerializedName("children")
    private List<MsgSettingChildren> children;

    protected MsgSettingChildren(Parcel in) {
        id = in.readInt();
        name = in.readString();
        pid = in.readString();
        status = in.readInt();
        children = in.createTypedArrayList(MsgSettingChildren.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(pid);
        dest.writeInt(status);
        dest.writeTypedList(children);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MsgSettingChildren> CREATOR = new Creator<MsgSettingChildren>() {
        @Override
        public MsgSettingChildren createFromParcel(Parcel in) {
            return new MsgSettingChildren(in);
        }

        @Override
        public MsgSettingChildren[] newArray(int size) {
            return new MsgSettingChildren[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<MsgSettingChildren> getChildren() {
        return children;
    }

    public void setChildren(List<MsgSettingChildren> children) {
        this.children = children;
    }
}
