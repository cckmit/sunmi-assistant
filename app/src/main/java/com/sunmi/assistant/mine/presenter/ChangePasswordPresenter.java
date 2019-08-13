package com.sunmi.assistant.mine.presenter;

import com.sunmi.assistant.mine.contract.ChangePasswordContract;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-05.
 */
public class ChangePasswordPresenter extends BasePresenter<ChangePasswordContract.View>
        implements ChangePasswordContract.Presenter {

    @Override
    public void changePassword(String oldPsw, String newPsw) {
        SunmiStoreApi.changePassword(oldPsw, newPsw, new RetrofitCallback<Object>() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.changePasswordSuccess();
                }
            }

            @Override
            public void onFail(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.changePasswordFail(code, msg);
                }
            }
        });
    }
}
