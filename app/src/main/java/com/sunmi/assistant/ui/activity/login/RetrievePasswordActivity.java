package com.sunmi.assistant.ui.activity.login;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sunmi.apmanager.constant.Constants;
import com.sunmi.apmanager.rpc.RpcCallback;
import com.sunmi.apmanager.rpc.cloud.CloudApi;
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
import sunmi.common.utils.RegexUtils;
import sunmi.common.view.ClearableEditText;
import sunmi.common.view.dialog.CommonDialog;

/**
 * Forget Password
 */
@EActivity(R.layout.activity_forget_psd)
public class RetrievePasswordActivity extends BaseActivity {

    @ViewById(R.id.etMobile)
    ClearableEditText etMobile;
    @ViewById(R.id.btn_next)
    Button btnNext;

    private String mobile;

    @AfterViews
    protected void init() {
        etMobile.setClearIcon(R.mipmap.ic_edit_delete_white);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String phoneNum = bundle.getString("mobile");
            if (!TextUtils.isEmpty(phoneNum)) {
                etMobile.setText(phoneNum);
            }
        }
        HelpUtils.setStatusBarFullTransparent(this);//透明标题栏
        new SomeMonitorEditText().setMonitorEditText(btnNext, etMobile);
    }

    @Click(R.id.btn_next)
    public void onClick(View v) {
        if (isFastClick(1500)) return;
        mobile = etMobile.getText().toString().trim();
        if (!RegexUtils.isChinaPhone(mobile)) {
            shortTip(R.string.str_invalid_phone);
            return;
        }
        CommonUtils.trackCommonEvent(context, "forgetPwdSendVerificationCode",
                "找回密码_获取验证码", Constants.EVENT_FORGET_PSW);
        invalidAccount();
    }

    private void invalidAccount() {
        showLoadingDialog();
        CloudApi.isUserExist(mobile, new RpcCallback(context) {
            @Override
            public void onSuccess(int code, String msg, String data) {
                hideLoadingDialog();
                if (code == 1) {
                    userMerge();
                } else {
                    mobileUnregister();
                }
            }
        });
    }

    //手机号未注册
    private void mobileUnregister() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new CommonDialog.Builder(RetrievePasswordActivity.this)
                        .setTitle(R.string.tip_unregister)
                        .setCancelButton(R.string.str_cancel)
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
        });
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
                                .extra("source", "password").start();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
