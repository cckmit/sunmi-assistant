package com.sunmi.assistant.ui.activity.presenter;

import com.sunmi.assistant.ui.activity.contract.SetPasswordContract;

import sunmi.common.base.BasePresenter;
import sunmi.common.model.CompanyListResp;
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
        SunmiStoreApi.getInstance().register(username, password, code, new RetrofitCallback<Object>() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                if (isViewAttached()) {
                    SpUtils.setStoreToken(data.toString());
                    //初始化retrofit
                    SunmiStoreRetrofitClient.createInstance();
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
        SunmiStoreApi.getInstance().resetPassword(username, password, code, new RetrofitCallback<Object>() {
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
    public void getCompanyList() {
        mView.showLoadingDialog();
        SunmiStoreApi.getInstance().getCompanyList(new RetrofitCallback<CompanyListResp>() {
            @Override
            public void onSuccess(int code, String msg, CompanyListResp data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.getCompanyListSuccess(data.getCompany_list());
                }
            }

            @Override
            public void onFail(int code, String msg, CompanyListResp data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.getCompanyListFail(code, msg);
                }
            }
        });

    }
}
