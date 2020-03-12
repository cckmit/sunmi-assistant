package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

public class CountListBean {
    /**
     * age_range_code : 1
     * gender : 1
     * uniq_count : 12
     * stranger_uniq_count : 12
     * regular_uniq_count : 13
     */

    @SerializedName("age_range_code")
    private int ageRangeCode;
    @SerializedName("gender")
    private int gender;
    @SerializedName("uniq_count")
    private int uniqCount;
    @SerializedName("stranger_uniq_count")
    private int strangerUniqCount;
    @SerializedName("regular_uniq_count")
    private int regularUniqCount;

    public int getAgeRangeCode() {
        return ageRangeCode;
    }

    public void setAgeRangeCode(int ageRangeCode) {
        this.ageRangeCode = ageRangeCode;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getUniqCount() {
        return uniqCount;
    }

    public void setUniqCount(int uniqCount) {
        this.uniqCount = uniqCount;
    }

    public int getStrangerUniqCount() {
        return strangerUniqCount;
    }

    public void setStrangerUniqCount(int strangerUniqCount) {
        this.strangerUniqCount = strangerUniqCount;
    }

    public int getRegularUniqCount() {
        return regularUniqCount;
    }

    public void setRegularUniqCount(int regularUniqCount) {
        this.regularUniqCount = regularUniqCount;
    }
}
