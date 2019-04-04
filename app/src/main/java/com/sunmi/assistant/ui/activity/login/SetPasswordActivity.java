package com.sunmi.assistant.ui.activity.login;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.sunmi.apmanager.config.AppConfig;
import com.sunmi.apmanager.constant.Constants;
import com.sunmi.apmanager.model.LoginDataBean;
import sunmi.common.rpc.http.RpcCallback;
import com.sunmi.apmanager.rpc.cloud.CloudApi;
import com.sunmi.apmanager.utils.CommonUtils;
import com.sunmi.apmanager.utils.HelpUtils;
import com.sunmi.apmanager.utils.SomeMonitorEditText;
import com.sunmi.assistant.R;
import com.sunmi.assistant.ui.activity.MainActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.utils.RegexUtils;
import sunmi.common.view.ClearableEditText;

/**
 * set password
 */
@EActivity(R.layout.activity_set_password)
public class SetPasswordActivity extends BaseActivity {

    @ViewById(R.id.llTitleLine)
    LinearLayout llTitleLine;
    @ViewById(R.id.etPassword)
    ClearableEditText etPassword;
    @ViewById(R.id.passwordIsVisible)
    ImageButton passwordIsVisible;
    @ViewById(R.id.btnComplete)
    Button btnComplete;

    private int type;
    private String smsCode = "";//重置密码的时候传参
    private String mobile = "";
    private boolean psdIsVisible;//密码是否可见

    @AfterViews
    protected void init() {
        HelpUtils.setStatusBarFullTransparent(this);//透明标题栏
        etPassword.setClearIcon(R.mipmap.ic_edit_delete_white);
        Bundle bundle = getIntent().getExtras();
        type = bundle.getInt(AppConfig.SET_PASSWORD);
        smsCode = bundle.getString(AppConfig.SET_PASSWORD_SMS);
        mobile = bundle.getString("mobile");

        if (type == AppConfig.SET_PASSWORD_REGISTER) {//注册
            llTitleLine.setVisibility(View.VISIBLE);
        } else if (type == AppConfig.SET_PASSWORD_RESET) {//找回密码
            llTitleLine.setVisibility(View.GONE);
        }
        //button是否可点击
        new SomeMonitorEditText().setMonitorEditText(btnComplete, etPassword);
        CommonUtils.trackDurationEventBegin(context, "registerPasswordDuration",
                "注册流程_设置密码_耗时", Constants.EVENT_DURATION_REGISTER_PSW);
    }

    @Click({R.id.passwordIsVisible, R.id.btnComplete})
    public void onClick(View v) {
        String password = etPassword.getText().toString().trim();
        switch (v.getId()) {
            case R.id.passwordIsVisible: //密码是否可见
                if (psdIsVisible) {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance()); //隐藏密码
                    passwordIsVisible.setBackgroundResource(R.mipmap.ic_eye_light_hide);
                    psdIsVisible = false;
                } else {
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance()); //显示密码
                    passwordIsVisible.setBackgroundResource(R.mipmap.ic_eye_light_open);
                    psdIsVisible = true;
                }
                break;
            case R.id.btnComplete:
                if (isFastClick(1500)) return;
                if (!RegexUtils.isValidPassword(password)) {
                    shortTip(R.string.textView_tip_psd);
                    return;
                }
                trackFinish();
                if (type == AppConfig.SET_PASSWORD_REGISTER) {//注册
                    register(password);
                } else if (type == AppConfig.SET_PASSWORD_RESET) {//找回密码
                    reSetPassword(password);
                }
                break;
        }
    }

    //忘记密码后重置密码
    private void reSetPassword(String password) {
        CloudApi.resetPassword(mobile, password, smsCode, new RpcCallback(context) {
            @Override
            public void onSuccess(int code, String msg, String data) {
                if (code == 1) {
                    shortTip(R.string.textView_reset_password_success);
                    LoginActivity_.intent(context).start();
                } else {
                    shortTip(R.string.textView_reset_password_error);
                }
            }
        });
    }

    //注册成功后设置密码
    private void register(String password) {
        CloudApi.register(mobile, password, new RpcCallback(context) {
            @Override
            public void onSuccess(int code, String msg, String data) {
                if (code == 1) {
                    LoginDataBean bean = new Gson().fromJson(data, LoginDataBean.class);
                    CommonUtils.saveLoginInfo(bean);
                    shortTip(R.string.register_success);
                    CommonUtils.trackDurationEventEnd(context, "registerPasswordDuration",
                            "注册流程_设置密码_耗时", Constants.EVENT_DURATION_REGISTER_PSW);
                    CommonUtils.trackDurationEventEnd(context, "registerDuration",
                            "注册流程开始和结束时调用", Constants.EVENT_DURATION_REGISTER);
                    //注册成功后直接登录
                    gotoMainActivity();
                }
            }
        });
    }

    private void trackFinish() {
        if (type == AppConfig.SET_PASSWORD_REGISTER) {//注册
            CommonUtils.trackCommonEvent(context, "registerPassFinish",
                    "注册_设置密码_完成", Constants.EVENT_REGISTER);
        } else if (type == AppConfig.SET_PASSWORD_RESET) {//找回密码
            CommonUtils.trackCommonEvent(context, "forgetPwdPassFinish",
                    "找回密码_设置密码完成", Constants.EVENT_FORGET_PSW);
            CommonUtils.trackDurationEventEnd(context, "retrievePasswordDuration",
                    "找回密码流程开始和结束", Constants.EVENT_DURATION_FORGET_PSW);
        }
    }

    private void gotoMainActivity() {
        MainActivity_.intent(context).start();
        finish();
    }

}
