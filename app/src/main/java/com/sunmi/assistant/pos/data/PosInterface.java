package com.sunmi.assistant.pos.data;

import com.sunmi.assistant.pos.response.PosDetailsResp;
import com.sunmi.assistant.pos.response.PosListResp;
import com.sunmi.assistant.pos.response.PosWarrantyResp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * @author yangShiJie
 * @date 2019-11-20
 */
public interface PosInterface {
    String posPath = "/api/pos/device/";

    /**
     * 获取门店下绑定pos设备列表
     */
    @POST(posPath + "getList")
    Call<BaseResponse<PosListResp>> getList(@Body BaseRequest request);

    /**
     * 获取设备基本信息
     */
    @POST(posPath + "getBaseInfo")
    Call<BaseResponse<PosDetailsResp>> getBaseInfo(@Body BaseRequest request);

    /**
     * 获取设备保修信息
     */
    @POST(posPath + "getWarrantyInfo")
    Call<BaseResponse<PosWarrantyResp>> getWarrantyInfo(@Body BaseRequest request);

    /**
     * 获取POS设备类别
     */
    @POST(posPath + "getCategoryByModel")
    Call<BaseResponse<Object>> getCategoryByModel(@Body BaseRequest request);

}

