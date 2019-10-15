package sunmi.common.rpc.cloud;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import sunmi.common.model.CustomerAgeGenderResp;
import sunmi.common.model.CustomerAgeNewOldResp;
import sunmi.common.model.CustomerCountResp;
import sunmi.common.model.CustomerHistoryResp;
import sunmi.common.model.CustomerRateResp;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * @author yinhui
 * @date 2019-09-18
 */
public interface CustomerInterface {

    String path = "api/passengerFlow/statistic/";

    /**
     * 按时间获取历史客流统计数据（今日、本周、本月，昨日）
     */
    @POST(path + "getHistory")
    Call<BaseResponse<CustomerHistoryResp>> getHistoryCustomer(@Body BaseRequest request);

    /**
     * 按时间获取历史客流统计数据（自定义时间段）
     */
    @POST(path + "history/getByTimeRange")
    Call<BaseResponse<CustomerHistoryResp>> getHistoryCustomerByRange(@Body BaseRequest request);

    /**
     * 按时间获取客流统计数据（今日、本周、本月，昨日、上周、上月）
     */
    @POST(path + "getLatest")
    Call<BaseResponse<CustomerCountResp>> getCustomer(@Body BaseRequest request);

    /**
     * 按时间获取交易转化率，客流数和订单数（今日，本周，本月）
     */
    @POST(path + "order/getLatest")
    Call<BaseResponse<CustomerRateResp>> getCustomerRate(@Body BaseRequest request);

    /**
     * 按时间获取客流年龄性别分布
     */
    @POST(path + "age/getByGender")
    Call<BaseResponse<CustomerAgeGenderResp>> getCustomerByAgeGender(@Body BaseRequest request);

    /**
     * 按时间获取客流年龄生熟分布
     */
    @POST(path + "age/getByRegular")
    Call<BaseResponse<CustomerAgeNewOldResp>> getCustomerByAgeNewOld(@Body BaseRequest request);

}
