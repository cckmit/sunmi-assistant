package com.sunmi.assistant.data;

import com.sunmi.assistant.data.response.AvgUnitSaleResponse;
import com.sunmi.assistant.data.response.DetailListResponse;
import com.sunmi.assistant.data.response.OrderListResponse;
import com.sunmi.assistant.data.response.OrderTypeListResponse;
import com.sunmi.assistant.data.response.PurchaseTypeListResponse;
import com.sunmi.assistant.data.response.PurchaseTypeRankResponse;
import com.sunmi.assistant.data.response.QuantityRankResponse;
import com.sunmi.assistant.data.response.TimeDistributionResponse;
import com.sunmi.assistant.data.response.TotalAmountResponse;
import com.sunmi.assistant.data.response.TotalCountResponse;
import com.sunmi.assistant.data.response.TotalRefundCountResponse;
import com.sunmi.assistant.utils.Utils;
import com.sunmi.ipc.rpc.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
            mService.getTotalAmount(Utils.createRequestBody(params)).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getTotalCount(int companyId, int shopId, long timeStart, long timeEnd,
                              int rateFlag, RetrofitCallback<TotalCountResponse> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("time_range_start", timeStart)
                    .put("time_range_end", timeEnd)
                    .put("rate_required", rateFlag)
                    .toString();
            mService.getTotalCount(Utils.createRequestBody(params)).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getRefundCount(int companyId, int shopId, long timeStart, long timeEnd,
                               int rateFlag, RetrofitCallback<TotalRefundCountResponse> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("time_range_start", timeStart)
                    .put("time_range_end", timeEnd)
                    .put("rate_required", rateFlag)
                    .toString();
            mService.getRefundCount(Utils.createRequestBody(params)).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getAvgUnitSale(int companyId, int shopId, long timeStart, long timeEnd,
                               int rateFlag, RetrofitCallback<AvgUnitSaleResponse> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("time_range_start", timeStart)
                    .put("time_range_end", timeEnd)
                    .put("rate_required", rateFlag)
                    .toString();
            mService.getAvgUnitSale(Utils.createRequestBody(params)).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getQuantityRank(int companyId, int shopId, long timeStart, long timeEnd,
                                RetrofitCallback<QuantityRankResponse> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("time_range_start", timeStart)
                    .put("time_range_end", timeEnd)
                    .toString();
            mService.getQuantityRank(Utils.createRequestBody(params)).enqueue(callback);
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

    public void getList(int companyId, int shopId, long timeStart, long timeEnd,
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
            mService.getList(Utils.createRequestBody(params)).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getDetailList(int orderId, RetrofitCallback<DetailListResponse> callback) {
        try {
            String params = new JSONObject()
                    .put("order_id", orderId)
                    .toString();
            mService.getDetailList(Utils.createRequestBody(params)).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getPurchaseTypeRank(int companyId, int shopId, long timeStart, long timeEnd,
                                    RetrofitCallback<PurchaseTypeRankResponse> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("start_time", timeStart)
                    .put("end_time", timeEnd)
                    .toString();
            mService.getPurchaseTypeRank(Utils.createRequestBody(params)).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getTimeDistribution(int companyId, int shopId, long timeStart, long timeEnd,
                                    int interval, RetrofitCallback<TimeDistributionResponse> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("start_time", timeStart)
                    .put("end_time", timeEnd)
                    .put("time_interval", interval)
                    .toString();
            mService.getTimeDistribution(Utils.createRequestBody(params)).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
