package com.sunmi.ipc.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author yinhui
 * @date 2019-08-15
 */
public class FaceCheckResp {
    /**
     * file_name : abcdefg12345
     * verify_result : 1
     */

    @SerializedName("file_name")
    private String fileName;
    @SerializedName("verify_result")
    private int verifyResult;

    public String getFileName() {
        return fileName;
    }

    public int getVerifyResult() {
        return verifyResult;
    }
}
