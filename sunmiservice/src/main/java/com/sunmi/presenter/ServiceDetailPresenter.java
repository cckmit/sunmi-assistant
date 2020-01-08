package com.sunmi.presenter;

import com.sunmi.bean.ServiceDetailBean;
import com.sunmi.contract.ServiceDetailContract;
import com.sunmi.rpc.ServiceApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-10-24.
 */
public class ServiceDetailPresenter extends BasePresenter<ServiceDetailContract.View>
        implements ServiceDetailContract.Presenter {

    private String deviceSn;

    public ServiceDetailPresenter(String deviceSn) {
        this.deviceSn = deviceSn;
    }

    @Override
    public void getServiceDetailByDevice(int category) {
        ServiceApi.getInstance().getServiceDetailByDevice(deviceSn, category, new RetrofitCallback<ServiceDetailBean>() {
            @Override
            public void onSuccess(int code, String msg, ServiceDetailBean data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.getServiceDetail(data);
                }
            }

            @Override
            public void onFail(int code, String msg, ServiceDetailBean data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.getServiceDetail(null);
                }
            }
        });
    }

    public String getCloudStorageParams(String productNo) {
        String params = "";
        try {
            ArrayList<String> snList = new ArrayList<>();
            snList.add(deviceSn);
            JSONObject userInfo = new JSONObject()
                    .put("token", SpUtils.getStoreToken())
                    .put("company_id", SpUtils.getCompanyId())
                    .put("shop_id", SpUtils.getShopId());
            JSONObject cloudStorage = new JSONObject()
                    .put("sn_list", new JSONArray(snList))
                    .put("productNo", productNo);
            params = new JSONObject()
                    .put("userInfo", userInfo)
                    .put("cloudStorage", cloudStorage)
                    .toString();
            return params;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return params;
    }

}
