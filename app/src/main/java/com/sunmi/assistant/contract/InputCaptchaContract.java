package com.sunmi.assistant.contract;

import com.sunmi.apmanager.model.LoginDataBean;

import sunmi.common.base.BaseView;

/**
 * Description:
 * Created by bruce on 2019/7/25.
 */
public interface InputCaptchaContract {

    interface View extends BaseView {
        void getCaptchaSuccess(int code, String msg, String data);

        void getCaptchaFail(int code, String msg, String data);

        void captchaCheckSuccess(int code, String msg, String data);

        void getImgCaptchaSuccess(int code, String msg, String data);

        void getStoreTokenSuccess(LoginDataBean loginData);

    }

    interface Presenter {
        void getCaptcha(int type, String mobile, String imgCode, String key);

        void captchaLogin(String mobile, String captcha);

        void getImgCaptcha();

        void checkSmsCode(String mobile, String captcha);
    }
}
