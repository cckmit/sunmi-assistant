package com.sunmi.assistant.pos.contract;


import com.sunmi.assistant.pos.data.PosApi;
import com.sunmi.assistant.pos.response.PosDetailsResp;
import com.sunmi.assistant.pos.response.PosWarrantyResp;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.log.LogCat;

/**
 * @author yangShiJie
 * @date 2019-11-21
 */
public class PosPresenter extends BasePresenter<PosContract.View>
        implements PosContract.Presenter {
    private String deviceid;
    private boolean isFirstLoad = true;

    public PosPresenter(String deviceid) {
        super();
        this.deviceid = deviceid;
    }

    @Override
    public void getPosDetails() {
        if (isFirstLoad) {
            mView.showLoadingDialog();
        }
        PosApi.getInstance().getBaseInfo(deviceid, new RetrofitCallback<PosDetailsResp>() {
            @Override
            public void onSuccess(int code, String msg, PosDetailsResp data) {
                if (isViewAttached()) {
                    isFirstLoad = false;
                    mView.hideLoadingDialog();
                    mView.getPosDetailsSuccess(data);
                }
            }

            @Override
            public void onFail(int code, String msg, PosDetailsResp data) {
                LogCat.e("TAG", "code=" + code + " , msg=" + msg);
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                }
            }
        });
    }

    @Override
    public void getPosGuarantee() {
        PosApi.getInstance().getWarrantyInfo(deviceid, new RetrofitCallback<PosWarrantyResp>() {
            @Override
            public void onSuccess(int code, String msg, PosWarrantyResp data) {
                if (isViewAttached()) {
                    mView.getPosGuaranteeSuccess(data);
                }
            }

            @Override
            public void onFail(int code, String msg, PosWarrantyResp data) {
                LogCat.e("TAG", "code=" + code + " , msg=" + msg);
            }
        });
    }
}
