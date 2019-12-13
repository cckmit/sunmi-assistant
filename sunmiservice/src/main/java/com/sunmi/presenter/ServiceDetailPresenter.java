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

    @Override
    public void getServiceDetailByDevice(String deviceSn, int category) {
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

}
