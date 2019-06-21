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
import com.sunmi.ipc.rpc.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * 订单管理远程接口
 *
 * @author yinhui
 * @since 2019-06-20
 */
public class OrderManagementRemote {

    private OrderManageService mService;

    private OrderManagementRemote() {
        mService = RetrofitClient.getInstance().create(OrderManageService.class);
    }

    private static final class Holder {
        private static final OrderManagementRemote INSTANCE = new OrderManagementRemote();
    }

    public static OrderManagementRemote get() {
        return Holder.INSTANCE;
    }

    public void getTotalAmount(int companyId, int shopId, long timeStart, long timeEnd,
                               int rateFlag, RetrofitCallback<TotalAmountResponse> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("time_range_start", timeStart)
                    .put("time_range_end", timeEnd)
                    .put("rate_required", rateFlag)
                    .toString();
            mService.getTotalAmount(createRequestBody(params)).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getTotalCount(int companyId, int storeId, long timeStart, long timeEnd,
                              int rateFlag, RetrofitCallback<TotalCountResponse> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", storeId)
                    .put("time_range_start", timeStart)
                    .put("time_range_end", timeEnd)
                    .put("rate_required", rateFlag)
                    .toString();
            mService.getTotalCount(createRequestBody(params)).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getRefundCount(int companyId, int storeId, long timeStart, long timeEnd,
                               int rateFlag, RetrofitCallback<TotalRefundCountResponse> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", storeId)
                    .put("time_range_start", timeStart)
                    .put("time_range_end", timeEnd)
                    .put("rate_required", rateFlag)
                    .toString();
            mService.getRefundCount(createRequestBody(params)).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getAvgUnitSale(int companyId, int storeId, long timeStart, long timeEnd,
                               int rateFlag, RetrofitCallback<AvgUnitSaleResponse> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", storeId)
                    .put("time_range_start", timeStart)
                    .put("time_range_end", timeEnd)
                    .put("rate_required", rateFlag)
                    .toString();
            mService.getAvgUnitSale(createRequestBody(params)).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getQuantityRank(int companyId, int storeId, long timeStart, long timeEnd,
                                RetrofitCallback<QuantityRankResponse> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", storeId)
                    .put("time_range_start", timeStart)
                    .put("time_range_end", timeEnd)
                    .toString();
            mService.getQuantityRank(createRequestBody(params)).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getOrderTypeList(RetrofitCallback<OrderTypeListResponse> callback) {
        mService.getOrderTypeList().enqueue(callback);
    }

    public void getPurchaseTypeList(RetrofitCallback<PurchaseTypeListResponse> callback) {
        mService.getPurchaseTypeList().enqueue(callback);
    }

    public void getList(int companyId, int storeId, long timeStart, long timeEnd,
                        int amountOrder, int timeOrder, int[] orderType, int[] purchaseType,
                        int pageNum, int pageSize, RetrofitCallback<OrderListResponse> callback) {
        try {
            JSONArray orderArray = new JSONArray();
            if (orderType != null && orderType.length > 0) {
                for (int order : orderType) {
                    orderArray.put(order);
                }
            }
            JSONArray purchaseArray = new JSONArray();
            if (purchaseType != null && purchaseType.length > 0) {
                for (int purchase : purchaseType) {
                    purchaseArray.put(purchase);
                }
            }
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", storeId)
                    .put("time_range_start", timeStart)
                    .put("time_range_end", timeEnd)
                    .put("sort_by_amount", amountOrder)
                    .put("sort_by_time", timeOrder)
                    .put("order_type_list", orderArray)
                    .put("purchase_type_list", purchaseArray)
                    .put("page_num", pageNum)
                    .put("page_size", pageSize)
                    .toString();
            mService.getList(createRequestBody(params)).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getDetailList(int orderId, RetrofitCallback<DetailListResponse> callback) {
        try {
            String params = new JSONObject()
                    .put("order_id", orderId)
                    .toString();
            mService.getDetailList(createRequestBody(params)).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getPurchaseTypeRank(int companyId, int storeId, long timeStart, long timeEnd,
                                    RetrofitCallback<PurchaseTypeRankResponse> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", storeId)
                    .put("time_range_start", timeStart)
                    .put("time_range_end", timeEnd)
                    .toString();
            mService.getPurchaseTypeRank(createRequestBody(params)).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getTimeDistribution(int companyId, int storeId, long timeStart, long timeEnd,
                                    int rateFlag, RetrofitCallback<TimeDistributionResponse> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", storeId)
                    .put("time_range_start", timeStart)
                    .put("time_range_end", timeEnd)
                    .put("rate_required", rateFlag)
                    .toString();
            mService.getTimeDistribution(createRequestBody(params)).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private BaseRequest createRequestBody(String params) {
        return new BaseRequest.Builder()
                .setParams(params)
                .setLang("zh").createBaseRequest();
    }

}
