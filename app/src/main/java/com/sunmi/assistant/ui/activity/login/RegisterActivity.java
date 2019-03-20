package com.sunmi.assistant.ui.activity.login;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sunmi.apmanager.config.AppConfig;
import com.sunmi.apmanager.constant.Constants;
import com.sunmi.apmanager.rpc.RpcCallback;
import com.sunmi.apmanager.rpc.cloud.CloudApi;
import com.sunmi.apmanager.rpc.sso.SSOApi;
import com.sunmi.apmanager.ui.activity.ProtocolActivity;
import com.sunmi.apmanager.ui.view.MergeDialog;
import com.sunmi.apmanager.utils.BundleUtils;
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
 * register
 */
@EActivity(R.layout.activity_register)
public class RegisterActivity extends BaseActivity {

    @ViewById(R.id.etMobile)
    ClearableEditText etMobile;
    @ViewById(R.id.btnNext)
    Button btnNext;
    @ViewById(R.id.BtnIsSelected)
    ImageButton BtnIsSelected;

    private boolean isSelectedPro = true;//是否选择了协议

    @AfterViews
    protected void init() {
        HelpUtils.setStatusBarFullTransparent(this);//透明标题栏
        etMobile.setClearIcon(R.mipmap.ic_edit_delete_white);
        new SomeMonitorEditText().setMonitorEditText(btnNext, etMobile);
        //初始化
        BtnIsSelected.setBackgroundResource(R.mipmap.ic_selected_protocol);
        isSelectedPro = true;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String mobile = bundle.getString("mobile");
            if (!TextUtils.isEmpty(mobile)) {
                etMobile.setText(mobile);
                HelpUtils.setSelectionEnd(etMobile);
            }
        }
        CommonUtils.trackDurationEventBegin(context, "registerUsernameDuration",
                "注册流程_输入联系方式_耗时", Constants.EVENT_DURATION_REGISTER_NAME);
    }

    @Click({R.id.BtnIsSelected, R.id.btnNext, R.id.rlUserProtocol, R.id.rlUserPrivate})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.BtnIsSelected://是否选择了协议
                if (isSelectedPro) {
                    BtnIsSelected.setBackgroundResource(R.mipmap.ic_normal_protocol);
                    isSelectedPro = false;
                } else {
                    BtnIsSelected.setBackgroundResource(R.mipmap.ic_selected_protocol);
                    isSelectedPro = true;
                }
                break;
            case R.id.btnNext://获取验证码
                if (isFastClick(1500)) return;
                String mobile = etMobile.getText().toString().trim();
                if (!isSelectedPro) {
                    shortTip(R.string.textView_tip_protocol);
                    break;
                }
                if (!RegexUtils.isChinaPhone(mobile)) {
                    shortTip(R.string.str_invalid_phone);
                    return;
                }
                invalidAccount(mobile);
                break;
            case R.id.rlUserProtocol://用户协议
                if (isFastClick(1500)) return;
                CommonUtils.trackCommonEvent(context, "userAgreement", "注册_用户协议", Constants.EVENT_REGISTER);
                openActivity(context, ProtocolActivity.class, BundleUtils.protocol(AppConfig.USER_PROTOCOL), false);
                overridePendingTransition(R.anim.activity_open_down_up, 0);
                break;
            case R.id.rlUserPrivate://隐私协议
                if (isFastClick(1500)) return;
                CommonUtils.trackCommonEvent(context, "privacyAgreement", "注册_隐私协议", Constants.EVENT_REGISTER);
                openActivity(context, ProtocolActivity.class, BundleUtils.protocol(AppConfig.USER_PRIVATE), false);
                overridePendingTransition(R.anim.activity_open_down_up, 0);
                break;
        }
    }

    private void invalidAccount(final String mobile) {
        showLoadingDialog();
        CloudApi.isUserExist(mobile, new RpcCallback(context) {
            @Override
            public void onSuccess(int code, String msg, String data) {
                hideLoadingDialog();
                if (code == 1) {
                    userMerge(mobile);
                } else {
                    CommonUtils.trackDurationEventEnd(context, "registerUsernameDuration",
                            "注册流程_输入联系方式_耗时", Constants.EVENT_DURATION_REGISTER_NAME);
                    CommonUtils.trackCommonEvent(context, "registerSendVerificationCode",
                            "注册_获取验证码", Constants.EVENT_REGISTER);
                    InputCaptchaActivity_.intent(context)
                            .extra("mobile", mobile)
                            .extra("source", "register")
                            .start();
                }
            }
        });
    }

    //手机号已注册
    private void mobileRegistered(final String mobile) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new CommonDialog.Builder(RegisterActivity.this)
                        .setTitle(R.string.tip_register_already)
                        .setCancelButton(R.string.str_cancel)
                        .setConfirmButton(R.string.str_goto_register, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CommonUtils.trackCommonEvent(context, "registerDialogLogin",
                                        "注册_账号已注册_弹窗_立即登录", Constants.EVENT_REGISTER);
                                LoginActivity_.intent(context)
                                        .extra("mobile", mobile)
                                        .start();
                                finish();
                            }
                        }).create().show();
            }
        });
    }

    //账号合并
    public void userMerge(final String mobile) {
        if (etMobile.getText() == null) return;
        String user = etMobile.getText().toString();//email test: esyzim06497@chacuo.net
        if (RegexUtils.isChinaPhone(user) || RegexUtils.isEmail(user)) {
            showLoadingDialog();
            SSOApi.checkUserName(user, new RpcCallback(context, false) {
                @Override
                public void onSuccess(int code, String msg, String data) {
                    checkSuccess(code, data, mobile);
                }
            });
        }
    }

    private void checkSuccess(int code, String data, String mobile) {
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
                        mobileRegistered(mobile);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
