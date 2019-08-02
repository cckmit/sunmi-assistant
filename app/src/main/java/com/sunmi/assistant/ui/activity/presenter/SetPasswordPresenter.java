package com.sunmi.assistant.ui.activity.presenter;

import com.sunmi.assistant.data.SunmiStoreRemote;
import com.sunmi.assistant.rpc.CloudCall;
import com.sunmi.assistant.ui.activity.contract.SetPasswordContract;
import com.sunmi.assistant.ui.activity.model.AuthStoreInfo;

import sunmi.common.base.BasePresenter;
import sunmi.common.model.CompanyInfoResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.cloud.SunmiStoreRetrofitClient;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-02.
 */
public class SetPasswordPresenter extends BasePresenter<SetPasswordContract.View>
        implements SetPasswordContract.Presenter {

    @Override
    public void register(String username, String password, String code) {
        SunmiStoreApi.register(username, password, code, new RetrofitCallback<Object>() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                if (isViewAttached()) {
                    SpUtils.setStoreToken(data.toString());
                    SunmiStoreRetrofitClient.createInstance();//初始化retrofit
                    mView.registerSuccess();
                }
            }

            @Override
            public void onFail(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.registerFail(code, msg);
                }
            }
        });
    }

    @Override
    public void resetPassword(String username, String password, String code) {
        SunmiStoreApi.resetPassword(username, password, code, new RetrofitCallback<Object>() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.resetPasswordSuccess();
                }
            }

            @Override
            public void onFail(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.reSetPasswordFail(code, msg);
                }
            }
        });
    }

    @Override
    public void getCompanyInfo(int companyId) {
        SunmiStoreRemote.get().getCompanyInfo(companyId, new RetrofitCallback<CompanyInfoResp>() {
            @Override
            public void onSuccess(int code, String msg, CompanyInfoResp data) {
                if (isViewAttached()) {
                    mView.getCompanyInfoSuccess(data);
                }
            }

            @Override
            public void onFail(int code, String msg, CompanyInfoResp data) {
                if (isViewAttached()) {
                    mView.getCompanyInfoFail(code, msg);
                }
            }
        });
    }

    @Override
    public void getSaasUserInfo(String phone) {
        CloudCall.getSaasUserInfo(phone, new RetrofitCallback<AuthStoreInfo>() {
            @Override
            public void onSuccess(int code, String msg, AuthStoreInfo data) {
                if (isViewAttached()) {
                    mView.getSaasUserInfoSuccess(data);
                }
            }

            @Override
            public void onFail(int code, String msg, AuthStoreInfo data) {
                if (isViewAttached()) {
                    mView.getSaasUserInfoFail(code, msg);
                }
            }
        });
    }
}
