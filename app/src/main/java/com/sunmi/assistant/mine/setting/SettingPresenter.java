package com.sunmi.assistant.mine.setting;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-05.
 */
public class SettingPresenter extends BasePresenter<SettingContract.View>
        implements SettingContract.Presenter {

    @Override
    public void logout() {
        SunmiStoreApi.logout(new RetrofitCallback<Object>() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.logoutSuccess();
                }
            }

            @Override
            public void onFail(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.logoutFail(code, msg);
                }
            }
        });
    }
}
