package com.sunmi.assistant.ui.activity.login;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sunmi.apmanager.config.AppConfig;
import com.sunmi.apmanager.constant.Constants;
import com.sunmi.apmanager.model.ImgSmsBean;
import com.sunmi.apmanager.model.LoginDataBean;
import com.sunmi.apmanager.rpc.RpcCallback;
import com.sunmi.apmanager.rpc.cloud.CloudApi;
import com.sunmi.apmanager.rpc.sso.SSOApi;
import com.sunmi.apmanager.ui.view.ImageCaptchaDialog;
import com.sunmi.apmanager.utils.CommonUtils;
import com.sunmi.apmanager.utils.HelpUtils;
import com.sunmi.apmanager.utils.SomeMonitorEditText;
import com.sunmi.apmanager.utils.SpUtils;
import com.sunmi.assistant.R;
import com.sunmi.assistant.ui.activity.MainActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.ClearableEditText;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.VerifyCodeInputView;

@EActivity(R.layout.activity_input_captcha)
public class InputCaptchaActivity extends BaseActivity implements ImageCaptchaDialog.Callback {

    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.ll_register_step)
    LinearLayout llRegisterStep;
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

    //    private Dialog smsDialog = null;
    ImageCaptchaDialog imageCaptchaDialog;//图形验证码dialog

    //倒计时对象,总共的时间,每隔多少秒更新一次时间
    final MyCountDownTimer mTimer = new MyCountDownTimer(AppConfig.SMS_TIME, 1000);

    @AfterViews
    protected void init() {
        HelpUtils.setStatusBarFullTransparent(this);//透明标题栏
        etSmsCode.setClearIcon(R.mipmap.ic_edit_delete_white);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mobile = bundle.getString("mobile");
            tvMobile.setText(String.format(getString(R.string.str_captcha_sent), mobile));
            source = bundle.getString("source");
            if (TextUtils.equals("register", source)) {
                llRegisterStep.setVisibility(View.VISIBLE);
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
                checkSmsCode();
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
    private void cancelTimer() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTimer.cancel();
                btnSendSMS.setText(getResources().getString(R.string.str_resend));
                btnSendSMS.setClickable(true);
            }
        });
    }

    private void showImgCaptchaDialog(String url) {
        imageVerify();
        if (imageCaptchaDialog == null) {
            imageCaptchaDialog = new ImageCaptchaDialog(context, this);
        }
        imageCaptchaDialog.show();
        imageCaptchaDialog.refreshImage(url);
        imageCaptchaDialog.clearInput();
        imageCaptchaDialog.setOnCompleteListener(new VerifyCodeInputView.Listener() {
            @Override
            public void onComplete(String content) {
                sendSms(content, smsKey);
            }
        });
    }

    private void clearImgCaptcha() {
        if (imageCaptchaDialog != null) {
            imageCaptchaDialog.clearInput();
        }
    }

    //发送验证码
    private void sendSms(String imgCode, String key) {
        SSOApi.getCaptcha(TextUtils.equals(source, "register") ? "1" : "2",
                mobile, imgCode, key, new RpcCallback(context) {
                    @Override
                    public void onSuccess(int code, String msg, String data) {
                        sendSmsSuccess(code, msg, data);
                    }
                });
    }

    private void sendSmsSuccess(final int code, String msg, final String data) {
        //1手机短信 2-弹出图形验证码 3-图形验证码错误 216-已注册 3603-用户不存在 306-发送短信失败 308，309-发送短信太频
        //code不等2或3时data为空字符串
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (code == 1) {//短信验证码
                    if (imageCaptchaDialog != null) imageCaptchaDialog.dismiss();
//                    if (smsDialog != null) smsDialog.dismiss();
                    startDownTimer();
                    shortTip(R.string.sms_send);
                } else if (code == 2) {//弹出图形验证码
                    ImgSmsBean.DataBean img = new Gson().fromJson(data, ImgSmsBean.DataBean.class);
                    smsKey = img.getKey();
                    showImgCaptchaDialog(img.getUrl());
//                    showSMSDialog(smsKey, img.getUrl());
                } else if (code == 3) {//图形验证码验证错误
                    clearImgCaptcha();
                    shortTip(R.string.sms_error);
                    getImgCaptcha();
                } else if (code == 309) {//send SMS too often
                    if (imageCaptchaDialog != null) imageCaptchaDialog.dismiss();
//                    if (smsDialog != null) smsDialog.dismiss();
                    shortTip("发送短信验证码太频繁，请稍后再试");
                } else if (code == 306) {//send SMS too often
                    if (imageCaptchaDialog != null) imageCaptchaDialog.dismiss();
//                    if (smsDialog != null) smsDialog.dismiss();
                    shortTip("发送短信验证码失败");
                } else if (code == 216) {
                    LoginActivity_.intent(context).extra("mobile", mobile).start();
                    shortTip(R.string.sms_send_have_mobile);
                }
            }
        });
    }

    //校验验证码
    private void checkSmsCode() {
        SSOApi.checkSmsCode(mobile, etSmsCode.getText().toString(), new RpcCallback(context) {
            @Override
            public void onSuccess(int code, String msg, String data) {
                if (code == 1) {
                    captchaCheckSuccess();
                    cancelTimer();//登录成功后取消计时
                } else if (code == 208) {
                    shortTip(R.string.sms_invalid);
                } else if (code == 2003) {
                    shortTip(R.string.sms_error);
                } else {
                    shortTip(R.string.yanzheng_error);
                }
            }
        });
    }

    private void captchaCheckSuccess() {
        if (TextUtils.equals("register", source)) {
            CommonUtils.trackDurationEventEnd(context, "registerVerificationDuration",
                    "注册流程_输入验证码_耗时", Constants.EVENT_DURATION_REGISTER_CODE);
            SetPasswordActivity_.intent(context)
                    .extra("mobile", mobile)
                    .extra(AppConfig.SET_PASSWORD_SMS, "")
                    .extra(AppConfig.SET_PASSWORD, AppConfig.SET_PASSWORD_REGISTER).start();
        } else if (TextUtils.equals("login", source)) {
            captchaLogin();
        } else if (TextUtils.equals("password", source)) {
            SetPasswordActivity_.intent(context)
                    .extra("mobile", mobile)
                    .extra(AppConfig.SET_PASSWORD_SMS, etSmsCode.getText().toString().trim())
                    .extra(AppConfig.SET_PASSWORD, AppConfig.SET_PASSWORD_RESET).start();
        }
    }

    //验证码登录
    private void captchaLogin() {
        CloudApi.quickLogin(mobile, etSmsCode.getText().toString(),
                new RpcCallback(context) {
                    @Override
                    public void onSuccess(int code, String msg, String data) {
                        quickLoginSuccess(code, msg, data);
                    }
                });
    }

    private void quickLoginSuccess(int code, String msg, String data) {
        if (code == 1) {
            CommonUtils.trackDurationEventEnd(context, "quickLoginDuration",
                    "登录流程开始到结束", Constants.EVENT_DURATION_LOGIN_BY_SMS);
            cancelTimer();//登录成功后取消计时
            Gson gson = new GsonBuilder().create();
            LoginDataBean smsLogin = gson.fromJson(data, LoginDataBean.class);
            SpUtils.saveLoginInfo(smsLogin);
            gotoMainActivity();
        } else {
            shortTip(R.string.login_error);
        }
    }

    private void gotoMainActivity() {
        MainActivity_.intent(context).start();
        finish();
    }

    private void getImgCaptcha() {
        SSOApi.getImgCaptcha(new RpcCallback(context) {
            @Override
            public void onSuccess(int code, String msg, String data) {
                getImgCaptchaSuccess(code, msg, data);
            }
        });
    }

    private void getImgCaptchaSuccess(final int code, String msg, final String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (code == 1) {//获取图形验证码成功
                    clearImgCaptcha();
                    ImgSmsBean.DataBean imgRefresh = new Gson().fromJson(data, ImgSmsBean.DataBean.class);
                    smsKey = imgRefresh.getKey();
                    //刷新图形
                    if (!TextUtils.isEmpty(imgRefresh.getUrl())) {
                        if (imageCaptchaDialog != null)
                            imageCaptchaDialog.refreshImage(imgRefresh.getUrl());
//                          final Bitmap bitmap = ImageUtils.base64ToBitmap(imgRefresh.getUrl());
//                          ImageView imgCode = smsDialog.findViewById(R.id.imgCode);
//                          imgCode.setImageBitmap(bitmap);
                    }
                } else {
                    shortTip("图片验证码刷新失败");
                }
            }
        });
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

//    private void showSMSDialog(final String key, final String url) {
//        imageVerify();
//        smsDialog = new Dialog(context, R.style.Son_dialog);
//        LayoutInflater inflater = getLayoutInflater();
//        View view = inflater.inflate(R.layout.dialog_sms, null);
//        final ImageView ivRefreshImgCode = view.findViewById(R.id.ivRefreshImgCode);
//        final ImageView ivDismiss = view.findViewById(R.id.ivDismiss);
//        ImageView imgCode = view.findViewById(R.id.imgCode);
//        final EditText tvNum1 = view.findViewById(R.id.tvNum1);
//        final EditText tvNum2 = view.findViewById(R.id.tvNum2);
//        final EditText tvNum3 = view.findViewById(R.id.tvNum3);
//        final EditText tvNum4 = view.findViewById(R.id.tvNum4);
//
//        final Bitmap bitmap = ImageUtils.base64ToBitmap(url);
//        imgCode.setImageBitmap(bitmap);
//        //自动跳转下一个editText
//        HelpUtils.editTextSkipNext(tvNum1, tvNum2);
//        HelpUtils.editTextSkipNext(tvNum2, tvNum3);
//        HelpUtils.editTextSkipNext(tvNum3, tvNum4);
//        //调整到最后一个编辑框
//        tvNum4.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                int len = charSequence.toString().length();
//                if (len == 1) {
//                    String a = tvNum1.getText().toString().trim();
//                    String b = tvNum2.getText().toString().trim();
//                    String c = tvNum3.getText().toString().trim();
//                    String d = tvNum4.getText().toString().trim();
//                    sendSms(a + b + c + d, smsKey);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
//
//        //刷新验证码
//        ivRefreshImgCode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!isFastClick(1000))
//                    getImgCaptcha();
//            }
//        });
//        ivDismiss.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                smsDialog.dismiss();
//                //回收资源
//                if (bitmap != null) {
//                    bitmap.recycle();
//                }
//            }
//        });
//        smsDialog.setContentView(view);
//        smsDialog.setCancelable(false);
//        smsDialog.show();
//    }

}
