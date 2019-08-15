package com.sunmi.ipc.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author yinhui
 * @date 2019-08-15
 */
public class FaceGroupListResp {
    @SerializedName("group_list")
    private List<Group> groupList;

    public List<Group> getGroupList() {
        return groupList;
    }

    public static class Group {
        /**
         * company_id : 6759
         * shop_id : 8699
         * group_name : stranger
         * mark : stranger
         * target_group_id : 483
         * group_id : 484
         * type : 1
         * threshold : 5
         * period : 86400
         * capacity : 10000
         * count : 0
         * last_modified_time : 1561656491
         * alarm_notified : 0
         */

        @SerializedName("company_id")
        private int companyId;
        @SerializedName("shop_id")
        private int shopId;
        @SerializedName("group_name")
        private String groupName;
        @SerializedName("mark")
        private String mark;
        @SerializedName("target_group_id")
        private int targetGroupId;
        @SerializedName("group_id")
        private int groupId;
        @SerializedName("type")
        private int type;
        @SerializedName("threshold")
        private int threshold;
        @SerializedName("period")
        private int period;
        @SerializedName("capacity")
        private int capacity;
        @SerializedName("count")
        private int count;
        @SerializedName("last_modified_time")
        private int lastModifiedTime;
        @SerializedName("alarm_notified")
        private int alarmNotified;

        public int getCompanyId() {
            return companyId;
        }

        public int getShopId() {
            return shopId;
        }

        public String getGroupName() {
            return groupName;
        }

        public String getMark() {
            return mark;
        }

        public int getTargetGroupId() {
            return targetGroupId;
        }

        public int getGroupId() {
            return groupId;
        }

        public int getType() {
            return type;
        }

        public int getThreshold() {
            return threshold;
        }

        public int getPeriod() {
            return period;
        }

        public int getCapacity() {
            return capacity;
        }

        public int getCount() {
            return count;
        }

        public int getLastModifiedTime() {
            return lastModifiedTime;
        }

        public int getAlarmNotified() {
            return alarmNotified;
        }
    }
}
