package com.sunmi.ipc.rpc.api;

import com.sunmi.ipc.model.CashOrderResp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * @author yangShiJie
 * @date 2019-12-05
 */
public interface CashInterface {
    String URL = "/ipc/api/";

    /**
     * 异常视频标记
     *
     * @param request
     * @return
     */
    @POST(URL + "audit/video/updateType")
    Call<BaseResponse<Object>> updateTag(@Body BaseRequest request);

    /**
     * 订单信息详情
     *
     * @param request
     * @return
     */
    @POST(URL + "payment/getOrderInfo")
    Call<BaseResponse<CashOrderResp>> getOrderInfo(@Body BaseRequest request);
}
