package com.sunmi.assistant.presenter;

import com.google.gson.Gson;
import com.sunmi.apmanager.model.LoginDataBean;
import com.sunmi.apmanager.rpc.cloud.CloudApi;
import com.sunmi.apmanager.rpc.sso.SSOApi;
import com.sunmi.assistant.R;
import com.sunmi.assistant.contract.InputCaptchaContract;

import org.json.JSONException;
import org.json.JSONObject;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.cloud.SunmiStoreRetrofitClient;
import sunmi.common.rpc.http.HttpCallback;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;

/**
 * Description:
 * Created by bruce on 2019/7/25.
 */
public class InputCaptchaPresenter extends BasePresenter<InputCaptchaContract.View>
        implements InputCaptchaContract.Presenter {

    @Override
    public void getCaptcha(int type, String mobile, String imgCode, String key) {
        SSOApi.getCaptcha(type, mobile, imgCode, key, new HttpCallback<String>(null) {
            @Override
            public void onSuccess(int code, String msg, String data) {
                if (isViewAttached()) {
                    mView.getCaptchaSuccess(code, msg, data);
                }
            }

            @Override
            public void onFail(int code, String msg, String data) {
                if (isViewAttached()) {
                    mView.getCaptchaFail(code, msg, data);
                }
            }
        });
    }

    @Override
    public void getImgCaptcha() {
        SSOApi.getImgCaptcha(new HttpCallback<String>(mView) {
            @Override
            public void onSuccess(int code, String msg, String data) {
                if (isViewAttached()) {
                    mView.getImgCaptchaSuccess(code, msg, data);
                }
            }

            @Override
            public void onFail(int code, String msg, String data) {
                if (isViewAttached()) {
                    mView.shortTip(R.string.tip_image_captcha_refresh_fail);
                }
            }
        });
    }

    @Override
    public void checkSmsCode(String mobile, String captcha) {
        SSOApi.checkSmsCode(mobile, captcha, new HttpCallback<String>(mView) {
            @Override
            public void onSuccess(int code, String msg, String data) {
                if (isViewAttached()) {
                    mView.captchaCheckSuccess(code, msg, data);
                }
            }

            @Override
            public void onFail(int code, String msg, String data) {
                if (isViewAttached()) {
                    if (code == 208) {
                        mView.shortTip(R.string.sms_invalid);
                    } else if (code == 2003) {
                        mView.shortTip(R.string.sms_error);
                    } else {
                        mView.shortTip(R.string.yanzheng_error);
                    }
                }
            }
        });
    }

    //验证码登录
    @Override
    public void captchaLogin(String mobile, String captcha) {
        mView.showLoadingDialog();
        CloudApi.quickLogin(mobile, captcha, new HttpCallback<String>(null) {
            @Override
            public void onSuccess(int code, String msg, String data) {
                if (isViewAttached()) {
                    getStoreToken(new Gson().fromJson(data, LoginDataBean.class));
                }
            }

            @Override
            public void onFail(int code, String msg, String data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.shortTip(R.string.login_error);
                }
            }
        });
//                new RpcCallback(context) {
//                    @Override
//                    public void onSuccess(int code, String msg, String data) {
//                        quickLoginSuccess(code, msg, data);
//                    }
//                });
    }

    public void getStoreToken(LoginDataBean loginData) {
        SunmiStoreApi.getStoreToken(loginData.getUid() + "", loginData.getToken(),
                loginData.getMerchant_uid(), new RetrofitCallback() {
                    @Override
                    public void onSuccess(int code, String msg, Object data) {
                        if (isViewAttached()) {
                            mView.hideLoadingDialog();
                            try {
                                JSONObject jsonObject = new JSONObject(data.toString());
                                SpUtils.setStoreToken(jsonObject.getString("store_token"));
                                SunmiStoreRetrofitClient.createInstance();//初始化retrofit
                                mView.getStoreTokenSuccess(loginData);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, Object data) {
                        if (isViewAttached()) {
                            mView.hideLoadingDialog();
                        }
                    }
                });
    }

}
