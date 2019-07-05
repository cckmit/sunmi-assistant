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
public interface PaymentInterface {

    /**
     * 获取总销售额信息
     */
    @POST("/api/payment/getTotalAmount")
    Call<BaseResponse<OrderTotalAmountResp>> getTotalAmount(@Body BaseRequest request);

    /**
     * 获取订单数信息
     */
    @POST("/api/payment/getTotalCount")
    Call<BaseResponse<OrderTotalCountResp>> getTotalCount(@Body BaseRequest request);

    /**
     * 获取总退款订单数信息
     */
    @POST("/api/payment/getRefundCount")
    Call<BaseResponse<OrderTotalRefundsResp>> getRefundCount(@Body BaseRequest request);

    /**
     * 获取平均客单价信息
     */
    @POST("/api/payment/getAvgUnitSale")
    Call<BaseResponse<OrderAvgUnitSaleResp>> getAvgUnitSale(@Body BaseRequest request);

    /**
     * 获取商品销量排行
     */
    @POST("/api/payment/getQuantityRank")
    Call<BaseResponse<OrderQuantityRankResp>> getQuantityRank(@Body BaseRequest request);

    /**
     * 获取订单类型列表
     */
    @POST("/api/payment/getOrderTypeList")
    Call<BaseResponse<OrderTypeListResp>> getOrderTypeList();

    /**
     * 获取支付方式列表
     */
    @POST("/api/payment/getPurchaseTypeList")
    Call<BaseResponse<OrderPayTypeListResp>> getPurchaseTypeList();

    /**
     * 获取订单列表
     */
    @POST("/api/payment/getList")
    Call<BaseResponse<OrderListResp>> getList(@Body BaseRequest request);

    /**
     * 获取订单的商品列表
     */
    @POST("/api/payment/getDetailList")
    Call<BaseResponse<OrderDetailListResp>> getDetailList(@Body BaseRequest request);

    /**
     * 获取支付方式排行
     */
    @POST("/api/payment/getPurchaseTypeStatistics")
    Call<BaseResponse<OrderPayTypeRankResp>> getPurchaseTypeRank(@Body BaseRequest request);

    /**
     * 获取订单交易时间分布
     */
    @POST("/api/payment/getTimeDistribution")
    Call<BaseResponse<OrderTimeDistributionResp>> getTimeDistribution(@Body BaseRequest request);

}
