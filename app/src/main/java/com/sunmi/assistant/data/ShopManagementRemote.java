package com.sunmi.assistant.data;

import com.sunmi.assistant.data.response.ShopInfoResponse;
import com.sunmi.assistant.data.response.ShopListResponse;
import com.sunmi.assistant.utils.Utils;
import com.sunmi.ipc.rpc.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * 订单管理远程接口
 *
 * @author yinhui
 * @since 2019-06-20
 */
public class ShopManagementRemote {

    private ShopManageService mService;

    private ShopManagementRemote() {
        mService = RetrofitClient.getInstance().create(ShopManageService.class);
    }

    private static final class Holder {
        private static final ShopManagementRemote INSTANCE = new ShopManagementRemote();
    }

    public static ShopManagementRemote get() {
        return Holder.INSTANCE;
    }

    public void getShopList(int companyId, RetrofitCallback<ShopListResponse> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .toString();
            mService.getList(Utils.createRequestBody(params)).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getShopInfo(int shopId, RetrofitCallback<ShopInfoResponse> callback) {
        try {
            String params = new JSONObject()
                    .put("shop_id", shopId)
                    .toString();
            mService.getInfo(Utils.createRequestBody(params)).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
