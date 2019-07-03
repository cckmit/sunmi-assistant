package com.sunmi.assistant.presenter;

import com.google.gson.Gson;
import com.sunmi.apmanager.model.LoginDataBean;
import com.sunmi.apmanager.rpc.cloud.CloudApi;
import com.sunmi.apmanager.rpc.sso.SSOApi;
import com.sunmi.assistant.R;
import com.sunmi.assistant.contract.LoginContract;
import com.sunmi.assistant.rpc.CloudCall;
import com.sunmi.ipc.rpc.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.http.HttpCallback;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.RegexUtils;
import sunmi.common.utils.SpUtils;

/**
 * Description:
 * Created by bruce on 2019/7/2.
 */
public class LoginPresenter extends BasePresenter<LoginContract.View>
        implements LoginContract.Presenter {

    @Override
    public void userMerge(String user, String mobile, String password) {
        if (RegexUtils.isChinaPhone(user) || RegexUtils.isEmail(user)) {
            SSOApi.checkUserName(user, new HttpCallback<String>(null) {
                @Override
                public void onSuccess(int code, String msg, String data) {
                    if (!isViewAttached()) return;
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
    }

    @Override
    public void login(String mobile, String password) {
        if (isViewAttached()) mView.showLoadingDialog();
        CloudApi.login(mobile, password, new HttpCallback<String>(null) {
            @Override
            public void onSuccess(int code, String msg, String data) {
                if (!isViewAttached()) return;
//                    CommonUtils.saveLoginInfo(new Gson().fromJson(data, LoginDataBean.class));
                getStoreToken(new Gson().fromJson(data, LoginDataBean.class));//todo
            }

            @Override
            public void onFail(int code, String msg, String data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    if (code == 201) {//用户名或密码错误
                        mView.shortTip(R.string.textView_user_password_error);
                    } else if (code == 3603) {
                        mView.mobileNoRegister();//手机号未注册
                    }
                }
            }
        });
    }

    @Override
    public void getStoreToken(LoginDataBean loginData) {
        CloudCall.getStoreToken(loginData, new RetrofitCallback() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(data.toString());
                        SpUtils.setSsoToken(jsonObject.getString("store_token"));
                        RetrofitClient.createInstance();//初始化retrofit
                        mView.getStoreTokenSuccess(loginData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFail(int code, String msg, Object data) {
                if (isViewAttached()) mView.hideLoadingDialog();
            }
        });
    }

}
