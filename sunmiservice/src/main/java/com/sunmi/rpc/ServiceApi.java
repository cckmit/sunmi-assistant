package com.sunmi.rpc;

import com.sunmi.bean.BundleServiceMsg;
import com.sunmi.bean.ServiceDetailBean;
import com.sunmi.bean.SubscriptionListBean;

import org.json.JSONException;
import org.json.JSONObject;

import sunmi.common.rpc.cloud.SunmiStoreRetrofitClient;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-10-22.
 */
public class ServiceApi {

    private ServiceApi() {

    }

    private static class Singleton {
        private static final ServiceApi INSTANCE = new ServiceApi();
    }

    public static ServiceApi getInstance() {
        return Singleton.INSTANCE;
    }

    public void getSubscriptionList(int pageNum, int pageSize, int category, RetrofitCallback<SubscriptionListBean> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", SpUtils.getCompanyId())
                    .put("shop_id", SpUtils.getShopId())
                    .put("service_category", category)
                    .put("page_num", pageNum)
                    .put("page_size", pageSize)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(ServiceInterface.class)
                    .getSubscriptionList(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getServiceDetailByDevice(String deviceSn, int category, RetrofitCallback<ServiceDetailBean> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", SpUtils.getCompanyId())
                    .put("shop_id", SpUtils.getShopId())
                    .put("device_sn", deviceSn)
                    .put("service_category", category)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(ServiceInterface.class)
                    .getServiceDetailByDevice(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getBundledList(RetrofitCallback<BundleServiceMsg> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", SpUtils.getCompanyId())
                    .put("shop_id", SpUtils.getShopId())
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(ServiceInterface.class)
                    .getBundledList(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
