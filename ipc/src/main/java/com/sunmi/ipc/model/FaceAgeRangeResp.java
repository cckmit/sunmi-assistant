package com.sunmi.ipc.model;

import com.google.gson.annotations.SerializedName;
import com.sunmi.ipc.face.model.FaceAge;

import java.util.List;

/**
 * @author yinhui
 * @date 2019-08-15
 */
public class FaceAgeRangeResp {
    @SerializedName("age_range_list")
    private List<FaceAge> ageRangeList;

    public List<FaceAge> getAgeRangeList() {
        return ageRangeList;
    }

}
