package com.sunmi.assistant.ui.activity.presenter;

import com.sunmi.apmanager.rpc.merchant.MerchantApi;
import com.sunmi.assistant.ui.activity.contract.AuthStoreCompleteContract;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.http.HttpCallback;


/**
 * 授权门店完成
 * Created by YangShiJie on 2019/6/26.
 */
public class AuthStoreCompletePresenter extends BasePresenter<AuthStoreCompleteContract.View>
        implements AuthStoreCompleteContract.Presenter {
    @Override
    public void getAuthStoreCompleteInfo() {
        MerchantApi.getUserInfo(new HttpCallback<String>(mView) {

            @Override
            public void onFail(int code, String msg, String data) {
                mView.getAuthStoreCompleteFail(code,data);
            }

            @Override
            public void onSuccess(int code, String msg, String data) {
                mView.getAuthStoreCompleteSuccess(data);
            }
        });
    }
}
