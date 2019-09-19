package sunmi.common.rpc.cloud;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import sunmi.common.model.ConsumerAgeGenderResp;
import sunmi.common.model.ConsumerAgeNewOldResp;
import sunmi.common.model.ConsumerCountResp;
import sunmi.common.model.ConsumerRateResp;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * @author yinhui
 * @date 2019-09-18
 */
public interface ConsumerInterface {

    String path = "api/passengerFlow/statistic/";

    /**
     * 按时间获取客流统计数据（今日、本周、本月，昨日、上周、上月）
     */
    @POST(path + "getLatest")
    Call<BaseResponse<ConsumerCountResp>> getConsumer(@Body BaseRequest request);

    /**
     * 按时间获取交易转化率，客流数和订单数（今日，本周，本月）
     */
    @POST(path + "order/getLatest")
    Call<BaseResponse<ConsumerRateResp>> getConsumerRate(@Body BaseRequest request);

    /**
     * 按时间获取客流年龄性别分布
     */
    @POST(path + "age/getByGender")
    Call<BaseResponse<ConsumerAgeGenderResp>> getConsumerByAgeGender(@Body BaseRequest request);

    /**
     * 按时间获取客流年龄生熟分布
     */
    @POST(path + "age/getByRegular")
    Call<BaseResponse<ConsumerAgeNewOldResp>> getConsumerByAgeNewOld(@Body BaseRequest request);

}
