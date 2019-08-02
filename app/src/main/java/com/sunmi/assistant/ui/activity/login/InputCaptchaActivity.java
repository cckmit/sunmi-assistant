package com.sunmi.assistant.ui.activity.login;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sunmi.apmanager.config.AppConfig;
import com.sunmi.apmanager.constant.Constants;
import com.sunmi.apmanager.model.ImgSmsBean;
import com.sunmi.apmanager.model.LoginDataBean;
import com.sunmi.apmanager.ui.view.ImageCaptchaDialog;
import com.sunmi.apmanager.utils.CommonUtils;
import com.sunmi.apmanager.utils.HelpUtils;
import com.sunmi.apmanager.utils.SomeMonitorEditText;
import com.sunmi.assistant.R;
import com.sunmi.assistant.contract.InputCaptchaContract;
import com.sunmi.assistant.presenter.InputCaptchaPresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.constant.CommonConstants;
import sunmi.common.view.ClearableEditText;
import sunmi.common.view.TitleBarView;

@EActivity(R.layout.activity_input_captcha)
public class InputCaptchaActivity extends BaseMvpActivity<InputCaptchaPresenter>
        implements InputCaptchaContract.View, ImageCaptchaDialog.Callback {

    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.tv_mobile)
    TextView tvMobile;
    @ViewById(R.id.cet_captcha)
    ClearableEditText etSmsCode;
    @ViewById(R.id.btn_resend)
    Button btnSendSMS;
    @ViewById(R.id.btn_confirm)
    Button btnConfirm;

    private String source;//从哪个activity跳转过来，决定后续操作

    private String mobile = "";
    private String smsKey = "";//图形验证码返回的key

    ImageCaptchaDialog imageCaptchaDialog;//图形验证码dialog

    //倒计时对象,总共的时间,每隔多少秒更新一次时间
    final MyCountDownTimer mTimer = new MyCountDownTimer(AppConfig.SMS_TIME, 1000);

    @AfterViews
    protected void init() {
        HelpUtils.setStatusBarFullTransparent(this);//透明标题栏
        mPresenter = new InputCaptchaPresenter();
        mPresenter.attachView(this);
        etSmsCode.setClearIcon(R.mipmap.ic_edit_delete_white);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mobile = bundle.getString("mobile");
            tvMobile.setText(String.format(getString(R.string.str_captcha_sent), mobile));
            source = bundle.getString("source");
            if (TextUtils.equals("register", source)) {
                titleBar.setAppTitle(R.string.str_register);
            } else if (TextUtils.equals("login", source)) {
                titleBar.setAppTitle(R.string.str_sms_login);
                btnConfirm.setText(R.string.str_login);
            } else if (TextUtils.equals("password", source)) {
                titleBar.setAppTitle(R.string.str_retrieve_password);
            }
        }

        new SomeMonitorEditText().setMonitorEditText(btnConfirm, etSmsCode);
        sendSms("", ""); //初始化发送短信验证码
        CommonUtils.trackDurationEventBegin(context, "registerVerificationDuration",
                "注册流程_输入验证码_耗时", Constants.EVENT_DURATION_REGISTER_CODE);
    }

    @Click({R.id.btn_resend, R.id.btn_confirm})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_resend:
                if (isFastClick(1500)) return;
                startDownTimer();
                trackResend();
                sendSms("", "");
                break;
            case R.id.btn_confirm:
                if (isFastClick(1500)) return;
                trackNext();
                if (TextUtils.equals("login", source)){
                    mPresenter.captchaLogin(mobile, etSmsCode.getText().toString());
                }else {
                    mPresenter.checkSmsCode(mobile, etSmsCode.getText().toString());
                }
                break;
            default:
                break;
        }
    }

    //初始化启动倒计时
    private void startDownTimer() {
        mTimer.start();
    }

    @Override
    public void getImageCaptcha() {
        getImgCaptcha();
    }

    //倒计时函数
    private class MyCountDownTimer extends CountDownTimer {
        MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        //计时过程
        @Override
        public void onTick(long l) {
            btnSendSMS.setClickable(false);//防止计时过程中重复点击
            btnSendSMS.setTextColor(getResources().getColor(R.color.common_orange_alpha));
            btnSendSMS.setText(String.format(getString(R.string.str_count_down_second), l / 1000));
        }

        //计时完毕的方法
        @Override
        public void onFinish() {
            btnSendSMS.setTextColor(getResources().getColor(R.color.common_orange));
            btnSendSMS.setText(getResources().getString(R.string.str_resend));//重新给Button设置文字
            btnSendSMS.setClickable(true);
        }
    }

    // 登陆成功后取消计时操作
    @UiThread
    void cancelTimer() {
        mTimer.cancel();
        btnSendSMS.setText(getResources().getString(R.string.str_resend));
        btnSendSMS.setClickable(true);
    }

    @UiThread
    void showImgCaptchaDialog(String url) {
        imageVerify();
        if (imageCaptchaDialog == null) {
            imageCaptchaDialog = new ImageCaptchaDialog(context, this);
        }
        imageCaptchaDialog.show();
        imageCaptchaDialog.refreshImage(url);
        imageCaptchaDialog.clearInput();
        imageCaptchaDialog.setOnCompleteListener(content -> sendSms(content, smsKey));
    }

    @UiThread
    void clearImgCaptcha() {
        if (imageCaptchaDialog != null) {
            imageCaptchaDialog.clearInput();
        }
    }

    //发送验证码
    private void sendSms(String imgCode, String key) {
        mPresenter.getCaptcha(TextUtils.equals(source, "register") ? 1 : 2,
                mobile, imgCode, key);
    }

    @UiThread
    @Override
    public void getCaptchaSuccess(final int code, String msg, final String data) {
        //1手机短信 2-弹出图形验证码 3-图形验证码错误 216-已注册 3603-用户不存在 306-发送短信失败 308，309-发送短信太频
        //code不等2或3时data为空字符串
        dismissImageDialog();
        startDownTimer();
        shortTip(R.string.sms_send);
    }

    private void dismissImageDialog() {
        if (imageCaptchaDialog != null) {
            imageCaptchaDialog.dismiss();
        }
    }

    @Override
    public void getCaptchaFail(final int code, String msg, final String data) {
        //1手机短信 2-弹出图形验证码 3-图形验证码错误 216-已注册 3603-用户不存在 306-发送短信失败 308，309-发送短信太频
        //code不等2或3时data为空字符串
        if (code == 2) {//弹出图形验证码
            ImgSmsBean.DataBean img = new Gson().fromJson(data, ImgSmsBean.DataBean.class);
            smsKey = img.getKey();
            showImgCaptchaDialog(img.getUrl());
        } else if (code == 3) {//图形验证码验证错误
            clearImgCaptcha();
            shortTip(R.string.sms_error);
            getImgCaptcha();
        } else if (code == 309) {//send SMS too often
            dismissImageDialog();
            shortTip(R.string.tip_get_captcha_too_often);
        } else if (code == 306) {
            dismissImageDialog();
            shortTip(R.string.tip_get_captcha_fail);
        } else if (code == 216) {
            LoginActivity_.intent(context).extra("mobile", mobile).start();
            shortTip(R.string.sms_send_have_mobile);
        }
    }

    @Override
    public void captchaLoginSuccess() {
        CommonUtils.trackDurationEventEnd(context, "quickLoginDuration",
                "登录流程开始到结束", Constants.EVENT_DURATION_LOGIN_BY_SMS);
        cancelTimer();//登录成功后取消计时
        LoginChooseShopActivity_.intent(context)
                .action(CommonConstants.ACTION_LOGIN_CHOOSE_COMPANY).start();
    }

    private void getImgCaptcha() {
        mPresenter.getImgCaptcha();
    }

    @Override
    public void captchaCheckSuccess(int code, String msg, String data) {
        cancelTimer();//登录成功后取消计时
        if (TextUtils.equals("register", source)) {
            CommonUtils.trackDurationEventEnd(context, "registerVerificationDuration",
                    "注册流程_输入验证码_耗时", Constants.EVENT_DURATION_REGISTER_CODE);
            SetPasswordActivity_.intent(context)
                    .extra("mobile", mobile)
                    .extra(AppConfig.SET_PASSWORD_SMS, etSmsCode.getText().toString().trim())
                    .extra(AppConfig.SET_PASSWORD, AppConfig.SET_PASSWORD_REGISTER).start();
        } else if (TextUtils.equals("password", source)) {
            SetPasswordActivity_.intent(context)
                    .extra("mobile", mobile)
                    .extra(AppConfig.SET_PASSWORD_SMS, etSmsCode.getText().toString().trim())
                    .extra(AppConfig.SET_PASSWORD, AppConfig.SET_PASSWORD_RESET).start();
        }
        /*else if (TextUtils.equals("login", source)) { //验证码登录
            mPresenter.captchaLogin(mobile, etSmsCode.getText().toString());
        }*/
    }

    @UiThread
    @Override
    public void getImgCaptchaSuccess(final int code, String msg, final String data) {
        clearImgCaptcha();
        ImgSmsBean.DataBean imgRefresh = new Gson().fromJson(data, ImgSmsBean.DataBean.class);
        smsKey = imgRefresh.getKey();
        //刷新图形
        if (!TextUtils.isEmpty(imgRefresh.getUrl())) {
            if (imageCaptchaDialog != null)
                imageCaptchaDialog.refreshImage(imgRefresh.getUrl());
        }
    }

    private void trackNext() {
        if (TextUtils.equals("register", source)) {
            CommonUtils.trackCommonEvent(context, "registerSendVerificationCodeNext",
                    "注册_获取验证码_下一步", Constants.EVENT_REGISTER);
        } else if (TextUtils.equals("login", source)) {
            CommonUtils.trackCommonEvent(context, "loginBySmsCodeFinish",
                    "短信验证码登录_获取验证码_登陆", Constants.EVENT_LOGIN_BY_SMS);
        } else if (TextUtils.equals("password", source)) {
            CommonUtils.trackCommonEvent(context, "forgetPwdSendVerificationCodeNext",
                    "找回密码_获取验证码_下一步", Constants.EVENT_FORGET_PSW);
        }
    }

    private void trackResend() {
        if (TextUtils.equals("register", source)) {
            CommonUtils.trackCommonEvent(context, "registerRetrySendVerificationCode",
                    "注册_获取验证码_重新发送", Constants.EVENT_REGISTER);
        } else if (TextUtils.equals("login", source)) {
            CommonUtils.trackCommonEvent(context, "loginBySmsRetrySendVerificationCode",
                    "短信验证码登录_获取验证码_重新发送", Constants.EVENT_LOGIN_BY_SMS);
        } else if (TextUtils.equals("password", source)) {
            CommonUtils.trackCommonEvent(context, "forgetPwdRetrySendVerificationCode",
                    "找回密码_获取验证码_重新发送", Constants.EVENT_FORGET_PSW);
        }
    }

    private void imageVerify() {
        if (TextUtils.equals("register", source)) {
            CommonUtils.trackCommonEvent(context, "registerPngCodeDialog",
                    "注册_获取验证码_图形码弹窗", Constants.EVENT_REGISTER);
        } else if (TextUtils.equals("login", source)) {
            CommonUtils.trackCommonEvent(context, "loginBySmsPngCodeDialog",
                    "短信验证码登录_获取验证码_图形码弹窗", Constants.EVENT_LOGIN_BY_SMS);
        } else if (TextUtils.equals("password", source)) {
            CommonUtils.trackCommonEvent(context, "forgetPwdPngCodeDialog",
                    "找回密码_获取验证码_图形码弹窗", Constants.EVENT_FORGET_PSW);
        }
    }

}
