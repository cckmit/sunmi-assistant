package com.sunmi.assistant.presenter;

import com.sunmi.apmanager.rpc.sso.SSOApi;
import com.sunmi.assistant.R;
import com.sunmi.assistant.contract.LoginContract;

import org.json.JSONException;
import org.json.JSONObject;

import sunmi.common.base.BasePresenter;
import sunmi.common.model.CompanyListResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.cloud.SunmiStoreRetrofitClient;
import sunmi.common.rpc.http.HttpCallback;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;

/**
 * Description:
 * Created by bruce on 2019/7/2.
 */
public class LoginPresenter extends BasePresenter<LoginContract.View>
        implements LoginContract.Presenter {

    @Override
    public void userMerge(String user, String mobile, String password) {
        SSOApi.checkUserName(user, new HttpCallback<String>(null) {
            @Override
            public void onSuccess(int code, String msg, String data) {
                if (!isViewAttached()) {
                    return;
                }
                try {
                    JSONObject object = new JSONObject(data);
                    if (object.has("needMerge")) {
                        int needMerge = object.getInt("needMerge");//是否需要合并 0-否 1-是
                        String url = object.getString("url");
                        if (needMerge == 1) {
                            mView.showMergeDialog(url);
                        } else {
                            login(mobile, password);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void login(String mobile, String password) {
        if (!isViewAttached()) {
            return;
        }
        SunmiStoreApi.getInstance().login(mobile, password, new RetrofitCallback<Object>() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                if (isViewAttached()) {
                    SpUtils.setStoreToken(data.toString());
                    SunmiStoreRetrofitClient.createInstance();//初始化retrofit
                    mView.loginSuccess();
                }
            }

            @Override
            public void onFail(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    if (code == 201) {//用户名或密码错误
                        mView.shortTip(R.string.textView_user_password_error);
                    } else if (code == 3603) {
                        mView.mobileUnregister();//手机号未注册
                    } else {
                        mView.shortTip(R.string.login_error);
                    }
                }
            }
        });
    }

    @Override
    public void getCompanyList() {
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
