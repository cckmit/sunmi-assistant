package sunmi.common.rpc.cloud;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import sunmi.common.model.AdListResp;
import sunmi.common.model.ServiceListResp;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * Description:
 * Created by bruce on 2019/6/27.
 */
public interface AdInterface {
    String path = "/api/ad/";

    /**
     * banner广告列表获取
     */
    @POST(path + "getList")
    Call<BaseResponse<AdListResp>> getAdList(@Body BaseRequest request);

    @POST(path + "service/getList")
    Call<BaseResponse<ServiceListResp>> getServiceList(@Body BaseRequest request);

}
