package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author yinhui
 * @date 2019-09-18
 */
public class CustomerCountResp {

    /**
     * latest_count : 378
     * early_count : 11470
     * latest_pass_count : 11470
     * early_pass_count : 11470
     * latest_entry_head_count : 11470
     * early_entry_head_count : 11470
     */

    @SerializedName("latest_count")
    private int latestCount;
    @SerializedName("early_count")
    private int earlyCount;
    @SerializedName("latest_pass_count")
    private int latestPassCount;
    @SerializedName("early_pass_count")
    private int earlyPassCount;
    @SerializedName("latest_entry_head_count")
    private int latestEntryHeadCount;
    @SerializedName("early_entry_head_count")
    private int earlyEntryHeadCount;

    public int getLatestCount() {
        return latestCount;
    }

    public int getEarlyCount() {
        return earlyCount;
    }

    public int getLatestPassCount() {
        return latestPassCount;
    }

    public int getEarlyPassCount() {
        return earlyPassCount;
    }

    public int getLatestEntryHeadCount() {
        return latestEntryHeadCount;
    }

    public int getEarlyEntryHeadCount() {
        return earlyEntryHeadCount;
    }
}
