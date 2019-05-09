package com.sunmi.ipc.rpc.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import sunmi.common.rpc.mqtt.EmqTokenResp;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * Description:
 * Created by bruce on 2019/5/7.
 */
public interface UserInterface {
    String path = "api/user/";

    /**
     * 获取token
     */
    @POST(path + "getStoreToken")
    Call<BaseResponse<Object>> getStoreToken(@Body BaseRequest request);

    /**
     * 创建emq token去连mqtt,以后用，暂时用emqInterface里的create
     */
    @POST(path + "createEmqToken")
    Call<BaseResponse<EmqTokenResp>> createEmqToken(@Body BaseRequest request);

}
