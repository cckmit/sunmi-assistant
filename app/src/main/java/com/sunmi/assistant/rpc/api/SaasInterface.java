package com.sunmi.assistant.rpc.api;

import com.google.gson.JsonObject;
import com.sunmi.assistant.ui.activity.model.AuthStoreInfo;
import com.sunmi.assistant.ui.activity.model.PlatformInfo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * Created by YangShiJie on 2019/7/1.
 */
public interface SaasInterface {
    String path = "/api/shop/saas/";

    //saas信息
    @POST(path + "getUserInfo")
    Call<BaseResponse<AuthStoreInfo>> getSaasUserInfo(@Body BaseRequest request);

    //米商引擎所支持的Saas平台信息l
    @POST(path + "getList")
    Call<BaseResponse<PlatformInfo>> getPlatformList(@Body BaseRequest request);

    //米商引擎手机发送验证码
    @POST(path + "sendVerifyCode")
    Call<BaseResponse<Object>> sendSaasVerifyCode(@Body BaseRequest request);

    //米商引擎手机校验验证码
    @POST(path + "confirmVerifyCode")
    Call<BaseResponse<JsonObject>> confirmSaasVerifyCode(@Body BaseRequest request);

    //用户授权获取Saas平台数据
    @POST(path + "authorize")
    Call<BaseResponse<Object>> authorizeSaas(@Body BaseRequest request);

}
