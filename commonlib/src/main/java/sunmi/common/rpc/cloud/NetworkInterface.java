package sunmi.common.rpc.cloud;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import sunmi.common.model.HealthInfoBean;
import sunmi.common.model.NetEventListBean;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * @author yangShiJie
 * @date 2019-12-23
 */
public interface NetworkInterface {
    String URL = "/network/api/network/";

    /**
     * 获取 一段时间内网络实时事件(设备上下线，与saas连接网络延迟高,公网延迟高等
     */
    @POST(URL + "getEventList")
    Call<BaseResponse<NetEventListBean>> getEventList(@Body BaseRequest request);

    /**
     * 获取 最新的网路健康数据(公网延迟，saas延迟，干扰)
     */
    @POST(URL + "getHealthInfo")
    Call<BaseResponse<HealthInfoBean>> getHealthInfo(@Body BaseRequest request);

    /**
     * app一段时间内的saas和公网延迟数据
     */
    @POST(URL + "getNetHealthList")
    Call<BaseResponse<Object>> getNetHealthList(@Body BaseRequest request);
}
