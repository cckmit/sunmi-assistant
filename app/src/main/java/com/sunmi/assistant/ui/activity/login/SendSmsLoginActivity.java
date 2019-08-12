package com.sunmi.assistant.ui.activity.login;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.sunmi.apmanager.constant.Constants;
import com.sunmi.apmanager.rpc.sso.SSOApi;
import com.sunmi.apmanager.ui.view.MergeDialog;
import com.sunmi.apmanager.utils.CommonUtils;
import com.sunmi.apmanager.utils.HelpUtils;
import com.sunmi.apmanager.utils.SomeMonitorEditText;
import com.sunmi.assistant.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import sunmi.common.base.BaseActivity;
import sunmi.common.rpc.http.RpcCallback;
import sunmi.common.utils.RegexUtils;
import sunmi.common.view.ClearableEditText;
import sunmi.common.view.dialog.CommonDialog;

/**
 * sms login
 */
@Deprecated
@EActivity(R.layout.activity_login_sms)
public class SendSmsLoginActivity extends BaseActivity {
    @ViewById(R.id.btnGetSMS)
    Button btnGetSMS;
    @ViewById(R.id.etMobile)
    ClearableEditText etMobile;

    private String mobile;

    @AfterViews
    protected void init() {
        HelpUtils.setStatusBarFullTransparent(this);//透明标题栏
        etMobile.setClearIcon(R.mipmap.ic_edit_delete_white);
        new SomeMonitorEditText().setMonitorEditText(btnGetSMS, etMobile);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mobile = bundle.getString("mobile");
            if (!TextUtils.isEmpty(mobile)) {
                etMobile.setText(mobile);
                HelpUtils.setSelectionEnd(etMobile);
            }
        }
    }

    @Click(R.id.btnGetSMS)
    public void onClick(View v) {
        if (isFastClick(1500)) return;
        mobile = etMobile.getText().toString().trim();
        if (!RegexUtils.isChinaPhone(mobile)) {
            shortTip(R.string.str_invalid_phone);
            return;
        }
        CommonUtils.trackCommonEvent(context, "loginBySmsSendVerificationCode",
                "短信验证码登录_获取验证码", Constants.EVENT_LOGIN_BY_SMS);
        invalidAccount();
    }

    private void invalidAccount() {
//        SunmiStoreApi.isUserExist(mobile, new RpcCallback(context) {
//            @Override
//            public void onSuccess(int code, String msg, String data) {
//                if (code == 1) {
//                    userMerge();
//                } else {
//                    mobileUnregister();
//                }
//            }
//        });
    }

    //手机号未注册
    private void mobileUnregister() {
        new CommonDialog.Builder(this)
                .setTitle(R.string.tip_unregister)
                .setCancelButton(R.string.sm_cancel)
                .setConfirmButton(R.string.str_register_now, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RegisterActivity_.intent(context)
                                .extra("mobile", RegexUtils.isChinaPhone(mobile) ? mobile : "")
                                .start();
                        finish();
                    }
                }).create().show();
    }

    private Bundle getMobileBundle() {
        if (!RegexUtils.isChinaPhone(mobile)) {
            return new Bundle();
        }
        Bundle bundle = new Bundle();
        bundle.putString("mobile", mobile);
        return bundle;
    }

    //账号合并
    public void userMerge() {
        if (etMobile.getText() == null) return;
        String user = etMobile.getText().toString();//email test: esyzim06497@chacuo.net
        if (RegexUtils.isChinaPhone(user) || RegexUtils.isEmail(user)) {
            showLoadingDialog();
            SSOApi.checkUserName(user, new RpcCallback(context, false) {
                @Override
                public void onSuccess(int code, String msg, String data) {
                    checkSuccess(code, data);
                }
            });
        }
    }

    private void checkSuccess(int code, String data) {
        try {
            if (code == 1) {
                JSONObject object = new JSONObject(data);
                if (object.has("needMerge")) {
                    //needMerge 是否需要合并 0-否 1-是
                    int needMerge = object.getInt("needMerge");
                    String url = object.getString("url");
                    if (needMerge == 1) {
                        new MergeDialog(context, url).show();
                    } else {
                        InputCaptchaActivity_.intent(context)
                                .extra("mobile", RegexUtils.isChinaPhone(mobile) ? mobile : "")
                                .extra("source", "login").start();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
