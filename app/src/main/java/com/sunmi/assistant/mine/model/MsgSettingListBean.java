package com.sunmi.assistant.mine.model;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-22.
 */
public class MsgSettingListBean {

    @SerializedName("reminder_setting_list")
    private List<ReminderSettingListBean> reminderSettingList;

    public List<ReminderSettingListBean> getReminderSettingList() {
        return reminderSettingList;
    }

    public void setReminderSettingList(List<ReminderSettingListBean> reminderSettingList) {
        this.reminderSettingList = reminderSettingList;
    }

    public static class ReminderSettingListBean {
        /**
         * id : 1
         * name : 设备设置（tag）
         * pid : 0
         * status : 1
         * children : [{"id":11,"name":"网络摄像机（tag）","pid":"1","status":1,"children":[{"id":111,"name":"IPC上下线（tag）","pid":"1","status":1,"children":[]}]}]
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

}
