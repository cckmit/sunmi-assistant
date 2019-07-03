package com.sunmi.assistant.data;

import com.sunmi.assistant.data.response.ShopInfoResp;
import com.sunmi.assistant.data.response.ShopListResp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * 门店管理Http接口
 *
 * @author yinhui
 * @since 2019-06-20
 */
public interface ShopManageService {

    /**
     * 根据companyId获取所有的门店列表
     */
    @POST("/api/shop/getList")
    Call<BaseResponse<ShopListResp>> getList(@Body BaseRequest request);

    /**
     * 根据shopId获取门店信息
     */
    @POST("/api/shop/getInfo")
    Call<BaseResponse<ShopInfoResp>> getInfo(@Body BaseRequest request);

}
