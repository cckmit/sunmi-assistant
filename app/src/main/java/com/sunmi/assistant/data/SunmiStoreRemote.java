package com.sunmi.assistant.data;

import sunmi.common.model.CompanyInfoResp;
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
import sunmi.common.model.ShopInfoResp;
import sunmi.common.model.ShopListResp;
import com.sunmi.assistant.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import sunmi.common.rpc.cloud.CompanyInterface;
import sunmi.common.rpc.cloud.ShopInterface;
import sunmi.common.rpc.cloud.SunmiStoreRetrofitClient;
import sunmi.common.rpc.retrofit.BaseResponse;
import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * 订单管理远程接口
 *
 * @author yinhui
 * @since 2019-06-20
 */
public class SunmiStoreRemote {

    private static final class Holder {
        private static final SunmiStoreRemote INSTANCE = new SunmiStoreRemote();
    }

    public static SunmiStoreRemote get() {
        return Holder.INSTANCE;
    }

    private SunmiStoreRemote() {
    }

    // ---------- Company Management start ----------

    public Call<BaseResponse<CompanyInfoResp>> getCompanyInfo(int companyId, RetrofitCallback<CompanyInfoResp> callback) {
        Call<BaseResponse<CompanyInfoResp>> call = null;
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .toString();
            call = SunmiStoreRetrofitClient.getInstance().create(CompanyInterface.class)
                    .getInfo(Utils.createRequestBody(params));
            call.enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return call;
    }

    // ---------- Company Management end ----------

    // ---------- Shop Management start ----------

    public Call<BaseResponse<ShopListResp>> getShopList(int companyId, RetrofitCallback<ShopListResp> callback) {
        Call<BaseResponse<ShopListResp>> call = null;
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("page_num", 1)
                    .put("page_size", 999)
                    .toString();
            call = SunmiStoreRetrofitClient.getInstance().create(ShopInterface.class)
                    .getList(Utils.createRequestBody(params));
            call.enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return call;
    }

    public Call<BaseResponse<ShopInfoResp>> getShopInfo(int shopId, RetrofitCallback<ShopInfoResp> callback) {
        Call<BaseResponse<ShopInfoResp>> call = null;
        try {
            String params = new JSONObject()
                    .put("shop_id", shopId)
                    .toString();
            call = SunmiStoreRetrofitClient.getInstance().create(ShopInterface.class)
                    .getInfo(Utils.createRequestBody(params));
            call.enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return call;
    }

    // ---------- Shop Management end ----------

    // ---------- Order Management start ----------

    public Call<BaseResponse<OrderTotalAmountResp>> getOrderTotalAmount(int companyId, int shopId, long timeStart, long timeEnd,
                                                                        int rateFlag, RetrofitCallback<OrderTotalAmountResp> callback) {
        Call<BaseResponse<OrderTotalAmountResp>> call = null;
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("time_range_start", timeStart)
                    .put("time_range_end", timeEnd)
                    .put("rate_required", rateFlag)
                    .toString();
            call = SunmiStoreRetrofitClient.getInstance().create(PaymentInterface.class)
                    .getTotalAmount(Utils.createRequestBody(params));
            call.enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return call;
    }

    public Call<BaseResponse<OrderTotalCountResp>> getOrderTotalCount(int companyId, int shopId, long timeStart, long timeEnd,
                                                                      int rateFlag, RetrofitCallback<OrderTotalCountResp> callback) {
        Call<BaseResponse<OrderTotalCountResp>> call = null;
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("time_range_start", timeStart)
                    .put("time_range_end", timeEnd)
                    .put("rate_required", rateFlag)
                    .toString();
            call = SunmiStoreRetrofitClient.getInstance().create(PaymentInterface.class)
                    .getTotalCount(Utils.createRequestBody(params));
            call.enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return call;
    }

    public Call<BaseResponse<OrderTotalRefundsResp>> getOrderRefundCount(int companyId, int shopId, long timeStart, long timeEnd,
                                                                         int rateFlag, RetrofitCallback<OrderTotalRefundsResp> callback) {
        Call<BaseResponse<OrderTotalRefundsResp>> call = null;
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("time_range_start", timeStart)
                    .put("time_range_end", timeEnd)
                    .put("rate_required", rateFlag)
                    .toString();
            call = SunmiStoreRetrofitClient.getInstance().create(PaymentInterface.class)
                    .getRefundCount(Utils.createRequestBody(params));
            call.enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return call;
    }

    public Call<BaseResponse<OrderAvgUnitSaleResp>> getOrderAvgUnitSale(int companyId, int shopId, long timeStart, long timeEnd,
                                                                        int rateFlag, RetrofitCallback<OrderAvgUnitSaleResp> callback) {
        Call<BaseResponse<OrderAvgUnitSaleResp>> call = null;
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("time_range_start", timeStart)
                    .put("time_range_end", timeEnd)
                    .put("rate_required", rateFlag)
                    .toString();
            call = SunmiStoreRetrofitClient.getInstance().create(PaymentInterface.class)
                    .getAvgUnitSale(Utils.createRequestBody(params));
            call.enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return call;
    }

    public Call<BaseResponse<OrderQuantityRankResp>> getOrderQuantityRank(int companyId, int shopId, long timeStart, long timeEnd,
                                                                          RetrofitCallback<OrderQuantityRankResp> callback) {
        Call<BaseResponse<OrderQuantityRankResp>> call = null;
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("time_range_start", timeStart)
                    .put("time_range_end", timeEnd)
                    .toString();
            call = SunmiStoreRetrofitClient.getInstance().create(PaymentInterface.class)
                    .getQuantityRank(Utils.createRequestBody(params));
            call.enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return call;
    }

    public Call<BaseResponse<OrderTypeListResp>> getOrderTypeList(RetrofitCallback<OrderTypeListResp> callback) {
        Call<BaseResponse<OrderTypeListResp>> call = SunmiStoreRetrofitClient.getInstance().create(PaymentInterface.class)
                .getOrderTypeList(Utils.createRequestBody(""));
        call.enqueue(callback);
        return call;
    }

    public Call<BaseResponse<OrderPayTypeListResp>> getOrderPurchaseTypeList(RetrofitCallback<OrderPayTypeListResp> callback) {
        Call<BaseResponse<OrderPayTypeListResp>> call = SunmiStoreRetrofitClient.getInstance().create(PaymentInterface.class)
                .getPurchaseTypeList(Utils.createRequestBody(""));
        call.enqueue(callback);
        return call;
    }

    public Call<BaseResponse<OrderListResp>> getOrderList(int companyId, int shopId, long timeStart, long timeEnd,
                                                          int amountOrder, int timeOrder, List<Integer> orderType, List<Integer> purchaseType,
                                                          int pageNum, int pageSize, RetrofitCallback<OrderListResp> callback) {
        Call<BaseResponse<OrderListResp>> call = null;
        try {
            JSONArray orderArray = new JSONArray();
            if (orderType != null && !orderType.isEmpty()) {
                for (int order : orderType) {
                    orderArray.put(order);
                }
            }
            JSONArray purchaseArray = new JSONArray();
            if (purchaseType != null && !purchaseType.isEmpty()) {
                for (int purchase : purchaseType) {
                    purchaseArray.put(purchase);
                }
            }
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("time_range_start", timeStart)
                    .put("time_range_end", timeEnd)
                    .put("sort_by_amount", amountOrder)
                    .put("sort_by_time", timeOrder)
                    .put("order_type_list", orderArray)
                    .put("purchase_type_list", purchaseArray)
                    .put("page_num", pageNum)
                    .put("page_size", pageSize)
                    .toString();
            call = SunmiStoreRetrofitClient.getInstance().create(PaymentInterface.class)
                    .getList(Utils.createRequestBody(params));
            call.enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return call;
    }

    public Call<BaseResponse<OrderDetailListResp>> getOrderDetailList(int orderId, RetrofitCallback<OrderDetailListResp> callback) {
        Call<BaseResponse<OrderDetailListResp>> call = null;
        try {
            String params = new JSONObject()
                    .put("order_id", orderId)
                    .toString();
            call = SunmiStoreRetrofitClient.getInstance().create(PaymentInterface.class)
                    .getDetailList(Utils.createRequestBody(params));
            call.enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return call;
    }

    public Call<BaseResponse<OrderPayTypeRankResp>> getOrderPurchaseTypeRank(int companyId, int shopId, long timeStart, long timeEnd,
                                                                             RetrofitCallback<OrderPayTypeRankResp> callback) {
        Call<BaseResponse<OrderPayTypeRankResp>> call = null;
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("start_time", timeStart)
                    .put("end_time", timeEnd)
                    .toString();
            call = SunmiStoreRetrofitClient.getInstance().create(PaymentInterface.class)
                    .getPurchaseTypeRank(Utils.createRequestBody(params));
            call.enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return call;
    }

    public Call<BaseResponse<OrderTimeDistributionResp>> getOrderTimeDistribution(int companyId, int shopId, long timeStart, long timeEnd,
                                                                                  int interval, RetrofitCallback<OrderTimeDistributionResp> callback) {
        Call<BaseResponse<OrderTimeDistributionResp>> call = null;
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("start_time", timeStart)
                    .put("end_time", timeEnd)
                    .put("time_interval", interval)
                    .toString();
            call = SunmiStoreRetrofitClient.getInstance().create(PaymentInterface.class)
                    .getTimeDistribution(Utils.createRequestBody(params));
            call.enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return call;
    }

    // ---------- Order Management end ----------

}
