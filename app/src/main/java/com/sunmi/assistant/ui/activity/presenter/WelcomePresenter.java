package com.sunmi.assistant.ui.activity.presenter;

import com.sunmi.apmanager.rpc.cloud.CloudApi;
import com.sunmi.assistant.ui.activity.contract.WelcomeContract;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;
import okhttp3.Response;
import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-05.
 */
public class WelcomePresenter extends BasePresenter<WelcomeContract.View>
        implements WelcomeContract.Presenter {

    @Override
    public void checkToken() {
       /* CloudApi.checkToken(new StringCallback() {
            @Override
            public void onError(Call call, Response response, Exception e, int id) {
                if (isViewAttached()){
                    mView.checkTokenFail(0,null);
                }
            }

            @Override
            public void onResponse(String response, int id) {
                if (isViewAttached()){
                    mView.checkTokenSuccess(response);
                }
            }
        });*/

        SunmiStoreApi.checkToken(new RetrofitCallback<Object>() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.checkTokenSuccess(null);
                }
            }

            @Override
            public void onFail(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.checkTokenFail(code, msg);
                }
            }
        });
    }

    @Override
    public void checkUpgrade() {
        CloudApi.checkUpgrade(new StringCallback() {
            @Override
            public void onError(Call call, Response response, Exception e, int id) {
                if (isViewAttached()) {
                    mView.chekUpgradeFail();
                }
            }

            @Override
            public void onResponse(String response, int id) {
                if (isViewAttached()) {
                    mView.chekUpgradeSuccess(response);
                }
            }
        });
    }
}
