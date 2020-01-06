package com.sunmi.ipc.rpc.api;

import com.sunmi.ipc.model.CashOrderResp;
import com.sunmi.ipc.model.CashVideoCountResp;
import com.sunmi.ipc.model.CashVideoEventResp;
import com.sunmi.ipc.model.CashVideoListBean;
import com.sunmi.ipc.model.CashVideoResp;
import com.sunmi.ipc.model.CashVideoTimeSlotBean;

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
     * 订单信息详情
     *
     * @param request
     * @return
     */
    @POST(URL + "payment/getOrderInfo")
    Call<BaseResponse<CashOrderResp>> getOrderInfo(@Body BaseRequest request);

    /**
     * 获取收银视频的日期信息，返回已有收银视频的日期时间戳列表
     * @param request
     * @return
     */
    @POST(URL+"audit/video/getTimeSlots")
    Call<BaseResponse<CashVideoTimeSlotBean>> getCashVidoTimeSlots(@Body BaseRequest request);

    /**
     * 获取指定店铺下的收银视频统计信息
     * @param request
     * @return
     */
    @POST(URL+"audit/video/getStatsInfoByShop")
    Call<BaseResponse<CashVideoListBean>> getShopCashVideoCount(@Body BaseRequest request);

    /**
     * 获取指定店铺下指定设备的收银视频统计信息
     * @param request
     * @return
     */
    @POST(URL+"audit/video/getStatsInfoByDevice")
    Call<BaseResponse<CashVideoCountResp>> getIpcCashVideoCount(@Body BaseRequest request);

    /**
     * 获取指定店铺/指定设备的收银视频列表
     * @param request
     * @return
     */
    @POST(URL+"audit/video/getList")
    Call<BaseResponse<CashVideoResp>> getCashVideoList(@Body BaseRequest request);

    /**
     * 获取行为异常视频列表
     * @param request
     * @return
     */
    @POST(URL +"audit/video/behavior/getList")
    Call<BaseResponse<CashVideoResp>> getAbnormalBehaviorVideoList(@Body BaseRequest request);

    /**
     * 获取异常视频的异常事件信息（视频加框信息）
     */
    @POST(URL + "audit/video/getEventInfo")
    Call<BaseResponse<CashVideoEventResp>> getCashVideoAbnormalEvent(@Body BaseRequest request);

    /**
     * 异常视频标记
     *
     * @param request
     * @return
     */
    @POST(URL + "audit/video/updateType")
    Call<BaseResponse<Object>> updateTag(@Body BaseRequest request);

}
