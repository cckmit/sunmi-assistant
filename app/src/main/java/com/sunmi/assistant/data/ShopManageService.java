package com.sunmi.assistant.data;

import com.sunmi.assistant.data.response.ShopInfoResp;
import com.sunmi.assistant.data.response.ShopListResp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * 订单管理Http接口
 *
 * @author yinhui
 * @since 2019-06-20
 */
public interface ShopManageService {

    @POST("/api/shop/getList")
    Call<BaseResponse<ShopListResp>> getList(@Body BaseRequest request);

    @POST("/api/shop/getInfo")
    Call<BaseResponse<ShopInfoResp>> getInfo(@Body BaseRequest request);

}
