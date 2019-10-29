package com.sunmi.presenter;

import com.sunmi.bean.SubscriptionListBean;
import com.sunmi.contract.CloudServiceMangeContract;
import com.sunmi.rpc.ServiceApi;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-10-22.
 */
public class CloudServiceMangePresenter extends BasePresenter<CloudServiceMangeContract.View>
        implements CloudServiceMangeContract.Presenter {
    @Override
    public void getSubscriptionList(int pageNum, int pageSize) {
        ServiceApi.getInstance().getSubscriptionList(pageNum, pageSize, new RetrofitCallback<SubscriptionListBean>() {
            @Override
            public void onSuccess(int code, String msg, SubscriptionListBean data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.getSubscriptionListSuccess(data.getServiceList(), data.getTotalCount());
                }
            }

            @Override
            public void onFail(int code, String msg, SubscriptionListBean data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.getSubscriptionListFail(code, msg);
                }
            }
        });
    }
}
