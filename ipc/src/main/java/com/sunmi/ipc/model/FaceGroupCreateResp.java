package com.sunmi.ipc.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author yinhui
 * @date 2019-08-15
 */
public class FaceGroupCreateResp {
    @SerializedName("group_list")
    private List<CreateResult> groupList;

    public List<CreateResult> getGroupList() {
        return groupList;
    }

    public static class CreateResult {
        /**
         * name : test
         * errcode : 1
         */

        @SerializedName("name")
        private String name;
        @SerializedName("errcode")
        private int code;

        public String getName() {
            return name;
        }

        public int getCode() {
            return code;
        }

    }
}
