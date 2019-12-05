package com.sunmi.ipc.presenter;

import com.sunmi.ipc.contract.CashVideoContract;
import com.sunmi.ipc.model.CashOrderResp;
import com.sunmi.ipc.rpc.IpcCloudApi;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * @author yangShiJie
 * @date 2019-12-05
 */
public class CashVideoPresenter extends BasePresenter<CashVideoContract.View>
        implements CashVideoContract.Presenter {
    @Override
    public void updateTag(int auditVideoId, String description, int videoType) {
        IpcCloudApi.getInstance().updateTag(auditVideoId, description, videoType, new RetrofitCallback<Object>() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.updateTagSuccess();
                }
            }

            @Override
            public void onFail(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.updateTagFail(code, msg);
                }
            }
        });
    }

    @Override
    public void getOrderInfo(int orderNo) {
        IpcCloudApi.getInstance().getOrderInfo(orderNo, new RetrofitCallback<CashOrderResp>() {
            @Override
            public void onSuccess(int code, String msg, CashOrderResp data) {
                if (isViewAttached()) {
                    mView.getOrderInfoSuccess(data);
                }
            }

            @Override
            public void onFail(int code, String msg, CashOrderResp data) {
                if (isViewAttached()) {
                    mView.getOrderInfoFail(code, msg);
                }
            }
        });
    }


}
