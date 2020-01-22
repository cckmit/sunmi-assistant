package com.sunmi.ipc.face.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author yinhui
 * @date 2019-08-20
 */
public class FaceAge {

    /**
     * age_range : 0~6
     * age_range_code : 1
     */

    @SerializedName("age_range_code")
    private int code;
    @SerializedName("age_range")
    private String name;

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
