package com.sunmi.assistant.data;

import com.sunmi.assistant.data.response.CompanyInfoResp;
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
import com.sunmi.assistant.data.response.ShopInfoResp;
import com.sunmi.assistant.data.response.ShopListResp;
import com.sunmi.assistant.utils.Utils;
import com.sunmi.ipc.rpc.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

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

    public void getCompanyInfo(int companyId, RetrofitCallback<CompanyInfoResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .toString();
            RetrofitClient.getInstance().create(CompanyInterface.class)
                    .getInfo(Utils.createRequestBody(params)).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // ---------- Company Management end ----------

    // ---------- Shop Management start ----------

    public void getShopList(int companyId, RetrofitCallback<ShopListResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("page_num", 0)
                    .put("page_size", 0)
                    .toString();
            RetrofitClient.getInstance().create(ShopInterface.class)
                    .getList(Utils.createRequestBody(params)).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getShopInfo(int shopId, RetrofitCallback<ShopInfoResp> callback) {
        try {
            String params = new JSONObject()
                    .put("shop_id", shopId)
                    .toString();
            RetrofitClient.getInstance().create(ShopInterface.class)
                    .getInfo(Utils.createRequestBody(params)).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // ---------- Shop Management end ----------

    // ---------- Order Management start ----------

    public void getOrderTotalAmount(int companyId, int shopId, long timeStart, long timeEnd,
                                    int rateFlag, RetrofitCallback<OrderTotalAmountResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("time_range_start", timeStart)
                    .put("time_range_end", timeEnd)
                    .put("rate_required", rateFlag)
                    .toString();
            RetrofitClient.getInstance().create(PaymentInterface.class)
                    .getTotalAmount(Utils.createRequestBody(params)).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getOrderTotalCount(int companyId, int shopId, long timeStart, long timeEnd,
                                   int rateFlag, RetrofitCallback<OrderTotalCountResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("time_range_start", timeStart)
                    .put("time_range_end", timeEnd)
                    .put("rate_required", rateFlag)
                    .toString();
            RetrofitClient.getInstance().create(PaymentInterface.class)
                    .getTotalCount(Utils.createRequestBody(params)).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getOrderRefundCount(int companyId, int shopId, long timeStart, long timeEnd,
                                    int rateFlag, RetrofitCallback<OrderTotalRefundsResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("time_range_start", timeStart)
                    .put("time_range_end", timeEnd)
                    .put("rate_required", rateFlag)
                    .toString();
            RetrofitClient.getInstance().create(PaymentInterface.class)
                    .getRefundCount(Utils.createRequestBody(params)).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getOrderAvgUnitSale(int companyId, int shopId, long timeStart, long timeEnd,
                                    int rateFlag, RetrofitCallback<OrderAvgUnitSaleResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("time_range_start", timeStart)
                    .put("time_range_end", timeEnd)
                    .put("rate_required", rateFlag)
                    .toString();
            RetrofitClient.getInstance().create(PaymentInterface.class)
                    .getAvgUnitSale(Utils.createRequestBody(params)).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getOrderQuantityRank(int companyId, int shopId, long timeStart, long timeEnd,
                                     RetrofitCallback<OrderQuantityRankResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("time_range_start", timeStart)
                    .put("time_range_end", timeEnd)
                    .toString();
            RetrofitClient.getInstance().create(PaymentInterface.class)
                    .getQuantityRank(Utils.createRequestBody(params)).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getOrderTypeList(RetrofitCallback<OrderTypeListResp> callback) {
        RetrofitClient.getInstance().create(PaymentInterface.class)
                .getOrderTypeList().enqueue(callback);
    }

    public void getOrderPurchaseTypeList(RetrofitCallback<OrderPayTypeListResp> callback) {
        RetrofitClient.getInstance().create(PaymentInterface.class)
                .getPurchaseTypeList().enqueue(callback);
    }

    public void getOrderList(int companyId, int shopId, long timeStart, long timeEnd,
                             int amountOrder, int timeOrder, List<Integer> orderType, List<Integer> purchaseType,
                             int pageNum, int pageSize, RetrofitCallback<OrderListResp> callback) {
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
            RetrofitClient.getInstance().create(PaymentInterface.class)
                    .getList(Utils.createRequestBody(params)).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getOrderDetailList(int orderId, RetrofitCallback<OrderDetailListResp> callback) {
        try {
            String params = new JSONObject()
                    .put("order_id", orderId)
                    .toString();
            RetrofitClient.getInstance().create(PaymentInterface.class)
                    .getDetailList(Utils.createRequestBody(params)).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getOrderPurchaseTypeRank(int companyId, int shopId, long timeStart, long timeEnd,
                                         RetrofitCallback<OrderPayTypeRankResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("start_time", timeStart)
                    .put("end_time", timeEnd)
                    .toString();
            RetrofitClient.getInstance().create(PaymentInterface.class)
                    .getPurchaseTypeRank(Utils.createRequestBody(params)).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getOrderTimeDistribution(int companyId, int shopId, long timeStart, long timeEnd,
                                         int interval, RetrofitCallback<OrderTimeDistributionResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("start_time", timeStart)
                    .put("end_time", timeEnd)
                    .put("time_interval", interval)
                    .toString();
            RetrofitClient.getInstance().create(PaymentInterface.class)
                    .getTimeDistribution(Utils.createRequestBody(params)).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // ---------- Order Management end ----------

}
