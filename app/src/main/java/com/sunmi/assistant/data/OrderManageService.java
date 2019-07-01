package com.sunmi.assistant.data;

import com.sunmi.assistant.data.response.OrderAvgUnitSaleResp;
import com.sunmi.assistant.data.response.OrderDetailListResp;
import com.sunmi.assistant.data.response.OrderListResp;
import com.sunmi.assistant.data.response.OrderPayTypeListResp;
import com.sunmi.assistant.data.response.OrderPayTypeRankResp;
import com.sunmi.assistant.data.response.OrderQuantityRankResp;
import com.sunmi.assistant.data.response.OrderTimeDistributionResp;
import com.sunmi.assistant.data.response.OrderTotalAmountResp;
import com.sunmi.assistant.data.response.OrderTotalCountResp;
import com.sunmi.assistant.data.response.OrderTotalRefundsResp;
import com.sunmi.assistant.data.response.OrderTypeListResp;

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
    Call<BaseResponse<OrderTotalAmountResp>> getTotalAmount(@Body BaseRequest request);

    @POST("/api/payment/getTotalCount")
    Call<BaseResponse<OrderTotalCountResp>> getTotalCount(@Body BaseRequest request);

    @POST("/api/payment/getRefundCount")
    Call<BaseResponse<OrderTotalRefundsResp>> getRefundCount(@Body BaseRequest request);

    @POST("/api/payment/getAvgUnitSale")
    Call<BaseResponse<OrderAvgUnitSaleResp>> getAvgUnitSale(@Body BaseRequest request);

    @POST("/api/payment/getQuantityRank")
    Call<BaseResponse<OrderQuantityRankResp>> getQuantityRank(@Body BaseRequest request);

    @POST("/api/payment/getOrderTypeList")
    Call<BaseResponse<OrderTypeListResp>> getOrderTypeList();

    @POST("/api/payment/getPurchaseTypeList")
    Call<BaseResponse<OrderPayTypeListResp>> getPurchaseTypeList();

    @POST("/api/payment/getList")
    Call<BaseResponse<OrderListResp>> getList(@Body BaseRequest request);

    @POST("/api/payment/getDetailList")
    Call<BaseResponse<OrderDetailListResp>> getDetailList(@Body BaseRequest request);

    @POST("/api/payment/getPurchaseTypeStatistics")
    Call<BaseResponse<OrderPayTypeRankResp>> getPurchaseTypeRank(@Body BaseRequest request);

    @POST("/api/payment/getTimeDistribution")
    Call<BaseResponse<OrderTimeDistributionResp>> getTimeDistribution(@Body BaseRequest request);

}
