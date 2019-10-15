package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author yinhui
 * @date 2019-10-14
 */
public class CustomerHistoryResp {

    /**
     * total_count : 1213
     * regular_count : 123
     * stranger_count : 321
     * member_count : 321
     */

    @SerializedName("total_count")
    private int totalCount;
    @SerializedName("regular_count")
    private int regularCount;
    @SerializedName("stranger_count")
    private int strangerCount;
    @SerializedName("member_count")
    private int memberCount;

    public int getTotalCount() {
        return totalCount;
    }

    public int getRegularCount() {
        return regularCount;
    }

    public int getStrangerCount() {
        return strangerCount;
    }

    public int getMemberCount() {
        return memberCount;
    }

}
