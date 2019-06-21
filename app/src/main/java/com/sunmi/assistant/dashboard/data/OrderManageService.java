package com.sunmi.assistant.dashboard.data;

import com.sunmi.assistant.dashboard.data.response.AvgUnitSaleResponse;
import com.sunmi.assistant.dashboard.data.response.DetailListResponse;
import com.sunmi.assistant.dashboard.data.response.OrderListResponse;
import com.sunmi.assistant.dashboard.data.response.OrderTypeListResponse;
import com.sunmi.assistant.dashboard.data.response.PurchaseTypeListResponse;
import com.sunmi.assistant.dashboard.data.response.PurchaseTypeRankResponse;
import com.sunmi.assistant.dashboard.data.response.QuantityRankResponse;
import com.sunmi.assistant.dashboard.data.response.TimeDistributionResponse;
import com.sunmi.assistant.dashboard.data.response.TotalAmountResponse;
import com.sunmi.assistant.dashboard.data.response.TotalCountResponse;
import com.sunmi.assistant.dashboard.data.response.TotalRefundCountResponse;

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
public interface OrderManageService {

    @POST("/api/payment/getTotalAmount")
    Call<BaseResponse<TotalAmountResponse>> getTotalAmount(@Body BaseRequest request);

    @POST("/api/payment/getTotalCount")
    Call<BaseResponse<TotalCountResponse>> getTotalCount(@Body BaseRequest request);

    @POST("/api/payment/getRefundCount")
    Call<BaseResponse<TotalRefundCountResponse>> getRefundCount(@Body BaseRequest request);

    @POST("/api/payment/getAvgUnitSale")
    Call<BaseResponse<AvgUnitSaleResponse>> getAvgUnitSale(@Body BaseRequest request);

    @POST("/api/payment/getQuantityRank")
    Call<BaseResponse<QuantityRankResponse>> getQuantityRank(@Body BaseRequest request);

    @POST("/api/payment/getOrderTypeList")
    Call<BaseResponse<OrderTypeListResponse>> getOrderTypeList();

    @POST("/api/payment/getPurchaseTypeList")
    Call<BaseResponse<PurchaseTypeListResponse>> getPurchaseTypeList();

    @POST("/api/payment/getList")
    Call<BaseResponse<OrderListResponse>> getList(@Body BaseRequest request);

    @POST("/api/payment/getDetailList")
    Call<BaseResponse<DetailListResponse>> getDetailList(@Body BaseRequest request);

    @POST("/api/payment/getPurchaseTypeStatistics")
    Call<BaseResponse<PurchaseTypeRankResponse>> getPurchaseTypeRank(@Body BaseRequest request);

    @POST("/api/payment/getTimeDistribution")
    Call<BaseResponse<TimeDistributionResponse>> getTimeDistribution(@Body BaseRequest request);

}
