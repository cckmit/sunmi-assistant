package com.sunmi.assistant.contract;

import android.content.Context;

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

        void captchaLoginSuccess();

    }

    interface Presenter {
        void getCaptcha(int type, String mobile, String imgCode, String key);

        void captchaLogin(Context context, String mobile, String captcha);

        void getImgCaptcha();

        void checkSmsCode(Context context, String mobile, String captcha);
    }
}
