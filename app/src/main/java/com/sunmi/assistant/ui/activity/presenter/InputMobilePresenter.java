package com.sunmi.assistant.ui.activity.presenter;

import com.sunmi.apmanager.rpc.sso.SSOApi;
import com.sunmi.assistant.ui.activity.contract.InputMobileContract;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.http.HttpCallback;
import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-07-31.
 */
public class InputMobilePresenter extends BasePresenter<InputMobileContract.View>
        implements InputMobileContract.Presenter {

    @Override
    public void isUserExist(String username) {
        SunmiStoreApi.isUserExist(username, new RetrofitCallback<Object>() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.isUserExistSuccess();
                }
            }

            @Override
            public void onFail(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.isUserExistFail(code, msg);
                }
            }
        });
    }

    @Override
    public void checkUserName(String username) {
        SSOApi.checkUserName(username, new HttpCallback<String>(mView) {
            @Override
            public void onSuccess(int code, String msg, String data) {
                if (isViewAttached()){
                    mView.checkSuccess(code,data);
                }
            }
        });
    }
}
