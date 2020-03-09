package com.sunmi.presenter;

import com.sunmi.bean.ServiceDetailBean;
import com.sunmi.contract.ServiceDetailContract;
import com.sunmi.rpc.ServiceApi;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.retrofit.RetrofitCallback;

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

    @Override
    public void getServiceDetailByServiceNo(String serviceNo) {
        ServiceApi.getInstance().getServiceDetailByServiceNo(serviceNo, new RetrofitCallback<ServiceDetailBean>() {
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
}
