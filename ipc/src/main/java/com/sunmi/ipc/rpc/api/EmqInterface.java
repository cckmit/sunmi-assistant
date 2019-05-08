package com.sunmi.ipc.rpc.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import sunmi.common.rpc.mqtt.EmqTokenResp;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * Description:
 * Created by bruce on 2019/5/8.
 */
public interface EmqInterface {

    String path = "ipc/api/mqtt/token/";

    /**
     * 创建emq token去连mqtt
     */
    @POST(path + "create")
    Call<BaseResponse<EmqTokenResp>> create(@Body BaseRequest request);

}
