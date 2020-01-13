package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

/**
 * Description: T+1客流分析概览
 *
 * @author linyuanpeng on 2020-01-13.
 */
public class CustomerDataResp {


    /**
     * latest_passenger_count : 1213
     * early_passenger_count : 1213
     * latest_pass_passenger_count : 1213
     * early_pass_passenger_count : 1213
     * latest_uniq_passenger_count : 1213
     * early_uniq_passenger_count : 1213
     */

    @SerializedName("latest_passenger_count")
    private int latestPassengerCount;
    @SerializedName("early_passenger_count")
    private int earlyPassengerCount;
    @SerializedName("latest_pass_passenger_count")
    private int latestPassPassengerCount;
    @SerializedName("early_pass_passenger_count")
    private int earlyPassPassengerCount;
    @SerializedName("latest_uniq_passenger_count")
    private int latestUniqPassengerCount;
    @SerializedName("early_uniq_passenger_count")
    private int earlyUniqPassengerCount;

    public int getLatestPassengerCount() {
        return latestPassengerCount;
    }

    public void setLatestPassengerCount(int latestPassengerCount) {
        this.latestPassengerCount = latestPassengerCount;
    }

    public int getEarlyPassengerCount() {
        return earlyPassengerCount;
    }

    public void setEarlyPassengerCount(int earlyPassengerCount) {
        this.earlyPassengerCount = earlyPassengerCount;
    }

    public int getLatestPassPassengerCount() {
        return latestPassPassengerCount;
    }

    public void setLatestPassPassengerCount(int latestPassPassengerCount) {
        this.latestPassPassengerCount = latestPassPassengerCount;
    }

    public int getEarlyPassPassengerCount() {
        return earlyPassPassengerCount;
    }

    public void setEarlyPassPassengerCount(int earlyPassPassengerCount) {
        this.earlyPassPassengerCount = earlyPassPassengerCount;
    }

    public int getLatestUniqPassengerCount() {
        return latestUniqPassengerCount;
    }

    public void setLatestUniqPassengerCount(int latestUniqPassengerCount) {
        this.latestUniqPassengerCount = latestUniqPassengerCount;
    }

    public int getEarlyUniqPassengerCount() {
        return earlyUniqPassengerCount;
    }

    public void setEarlyUniqPassengerCount(int earlyUniqPassengerCount) {
        this.earlyUniqPassengerCount = earlyUniqPassengerCount;
    }
}
