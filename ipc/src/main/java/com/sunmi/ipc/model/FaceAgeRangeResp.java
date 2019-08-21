package com.sunmi.ipc.model;

import com.google.gson.annotations.SerializedName;

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

    public static class FaceAge {

        /**
         * age_range : 0~6
         * age_range_code : 1
         */

        @SerializedName("age_range")
        private String ageRange;
        @SerializedName("age_range_code")
        private int ageRangeCode;

        public String getAgeRange() {
            return ageRange;
        }

        public int getAgeRangeCode() {
            return ageRangeCode;
        }
    }
}
