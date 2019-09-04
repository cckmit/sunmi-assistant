package com.sunmi.ipc.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author yinhui
 * @date 2019-08-15
 */
public class FaceSaveResp {

    @SerializedName("success_list")
    private List<FaceUrl> successList;
    @SerializedName("failure_list")
    private List<FaceUrl> failureList;

    public List<FaceUrl> getSuccessList() {
        return successList;
    }

    public List<FaceUrl> getFailureList() {
        return failureList;
    }

    public static class FaceUrl {
        /**
         * errcode : 1
         * img_name : 1.jpg
         * img_url : http://sunmi-wifi/1.jpg
         */

        @SerializedName("errcode")
        private int code;
        @SerializedName("img_name")
        private String imgName;
        @SerializedName("img_url")
        private String imgUrl;

        public int getCode() {
            return code;
        }

        public String getImgName() {
            return imgName;
        }

        public String getImgUrl() {
            return imgUrl;
        }
    }
}
