package com.sunmi.assistant.rpc.api;

import com.google.gson.JsonObject;
import com.sunmi.assistant.ui.activity.model.CreateStoreInfo;
import com.sunmi.assistant.ui.activity.model.PlatformInfo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * Created by YangShiJie on 2019/7/1.
 */
public interface PlatformInterface {
    String path = "/api/shop/saas/";

    //创建商户
    @POST("/api/company/create")
    Call<BaseResponse<String>> createMerchant(@Body BaseRequest request);

    //创建门店
    @POST("/api/shop/create")
    Call<BaseResponse<CreateStoreInfo>> createShop(@Body BaseRequest request);

    //编辑门店
    @POST("/api/shop/update")
    Call<BaseResponse<String>> editShop(@Body BaseRequest request);

    //saas信息
    @POST(path + "getUserInfo")
    Call<BaseResponse<JsonObject>> getSaasUserInfo(@Body BaseRequest request);

    //米商引擎所支持的Saas平台信息l
    @POST(path + "getList")
    Call<BaseResponse<PlatformInfo>> getPlatformList(@Body BaseRequest request);

    //米商引擎手机发送验证码
    @POST(path + "sendVerifyCode")
    Call<BaseResponse<String>> sendSaasVerifyCode(@Body BaseRequest request);

    //米商引擎手机校验验证码
    @POST(path + "confirmVerifyCode")
    Call<BaseResponse<JsonObject>> confirmSaasVerifyCode(@Body BaseRequest request);

    //用户授权获取Saas平台数据
    @POST(path + "authorize")
    Call<BaseResponse<String>> authorizeSaas(@Body BaseRequest request);

}
