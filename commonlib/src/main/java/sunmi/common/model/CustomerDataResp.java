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
     * latest_regular_passenger_count : 1213
     * early_regular_passenger_count : 1213
     * latest_stranger_passenger_count : 1213
     * early_stranger_passenger_count : 1213
     * latest_member_passenger_count : 1213
     * early_member_passenger_count : 1213
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
    @SerializedName("latest_regular_passenger_count")
    private int latestRegularPassengerCount;
    @SerializedName("early_regular_passenger_count")
    private int earlyRegularPassengerCount;
    @SerializedName("latest_stranger_passenger_count")
    private int latestStrangerPassengerCount;
    @SerializedName("early_stranger_passenger_count")
    private int earlyStrangerPassengerCount;
    @SerializedName("latest_member_passenger_count")
    private int latestMemberPassengerCount;
    @SerializedName("early_member_passenger_count")
    private int earlyMemberPassengerCount;

    public int getLatestPassengerCount() {
        return latestPassengerCount;
    }

    public int getEarlyPassengerCount() {
        return earlyPassengerCount;
    }

    public int getLatestPassPassengerCount() {
        return latestPassPassengerCount;
    }

    public int getEarlyPassPassengerCount() {
        return earlyPassPassengerCount;
    }

    public int getLatestUniqPassengerCount() {
        return latestUniqPassengerCount;
    }

    public int getEarlyUniqPassengerCount() {
        return earlyUniqPassengerCount;
    }

    public int getLatestRegularPassengerCount() {
        return latestRegularPassengerCount;
    }

    public int getEarlyRegularPassengerCount() {
        return earlyRegularPassengerCount;
    }

    public int getLatestStrangerPassengerCount() {
        return latestStrangerPassengerCount;
    }

    public int getEarlyStrangerPassengerCount() {
        return earlyStrangerPassengerCount;
    }

    public int getLatestMemberPassengerCount() {
        return latestMemberPassengerCount;
    }

    public int getEarlyMemberPassengerCount() {
        return earlyMemberPassengerCount;
    }
}
