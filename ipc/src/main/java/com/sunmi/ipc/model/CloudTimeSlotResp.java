package com.sunmi.ipc.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Description:
 * Created by bruce on 2019/8/14.
 */
public class CloudTimeSlotResp {
    /**
     * timeslots : [{"start_time":1565444648,"end_time":1565498310},{"start_time":1565498545,"end_time":1565498969},{"start_time":1565499091,"end_time":1565501026},{"start_time":1565501286,"end_time":1565507718},{"start_time":1565507720,"end_time":1565507840},{"start_time":1565507965,"end_time":1565510607},{"start_time":1565510689,"end_time":1565511289},{"start_time":1565511547,"end_time":1565511787},{"start_time":1565511929,"end_time":1565512291},{"start_time":1565512348,"end_time":1565539176},{"start_time":1565577993,"end_time":1565578596},{"start_time":1565578703,"end_time":1565579670},{"start_time":1565579901,"end_time":1565580384},{"start_time":1565582333,"end_time":1565590068},{"start_time":1565590272,"end_time":1565590634},{"start_time":1565590701,"end_time":1565591426},{"start_time":1565591655,"end_time":1565592501},{"start_time":1565592562,"end_time":1565596066},{"start_time":1565596068,"end_time":1565599269}]
     * total_count : 0
     */

    @SerializedName("total_count")
    private int totalCount;
    @SerializedName("timeslots")
    private List<VideoTimeSlotBean> timeslots;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<VideoTimeSlotBean> getTimeslots() {
        return timeslots;
    }

    public void setTimeslots(List<VideoTimeSlotBean> timeslots) {
        this.timeslots = timeslots;
    }

}
