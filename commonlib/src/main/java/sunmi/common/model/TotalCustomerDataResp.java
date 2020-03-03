package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

/**
 * Description: T+1客流分析概览
 *
 * @author linyuanpeng on 2020-01-13.
 */
public class TotalCustomerDataResp {

    /**
     * passenger_count : 6898
     * early_passenger_count : 6600
     * pass_head_count : 30000
     * early_pass_head_count : 28000
     */

    @SerializedName("passenger_count")
    private int passengerCount;
    @SerializedName("early_passenger_count")
    private int earlyPassengerCount;
    @SerializedName("pass_head_count")
    private int passHeadCount;
    @SerializedName("early_pass_head_count")
    private int earlyPassHeadCount;

    public int getPassengerCount() {
        return passengerCount;
    }

    public int getEarlyPassengerCount() {
        return earlyPassengerCount;
    }

    public int getPassHeadCount() {
        return passHeadCount;
    }

    public int getEarlyPassHeadCount() {
        return earlyPassHeadCount;
    }
}
