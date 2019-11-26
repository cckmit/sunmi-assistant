package com.sunmi.assistant.presenter;

import com.sunmi.apmanager.rpc.sso.SSOApi;
import com.sunmi.assistant.R;
import com.sunmi.assistant.contract.InputCaptchaContract;

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
        mView.showDarkLoading();
        SunmiStoreApi.getInstance().quickLogin(mobile, captcha, new RetrofitCallback<Object>() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                if (isViewAttached()) {
                    SpUtils.setStoreToken(data.toString());
                    SunmiStoreRetrofitClient.createInstance();//初始化retrofit
                    mView.captchaLoginSuccess();
                }
            }

            @Override
            public void onFail(int code, String msg, Object data) {
              if (isViewAttached()){
                  if (code == 208) {
                      mView.shortTip(R.string.sms_invalid);
                  }else {
                      mView.shortTip(R.string.login_error);
                  }
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

  /*  public void getStoreToken(LoginDataBean loginData) {
        SunmiStoreApi.getStoreToken(SpUtils.getUID(), SpUtils.getSsoToken(),
                SpUtils.getCompanyId() + "", new RetrofitCallback() {
                    @Override
                    public void onSuccess(int code, String msg, Object data) {
                        if (isViewAttached()) {
                            mView.hideLoadingDialog();
                            try {
                                JSONObject jsonObject = new JSONObject(data.toString());
                                SpUtils.setStoreToken(jsonObject.getString("store_token"));
                                SunmiStoreRetrofitClient.createInstance();//初始化retrofit
                                mView.captchaLoginSuccess();
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
    }*/

}
