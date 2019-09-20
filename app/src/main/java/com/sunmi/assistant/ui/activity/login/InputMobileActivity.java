package com.sunmi.assistant.ui.activity.login;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.sunmi.apmanager.constant.Constants;
import com.sunmi.apmanager.ui.view.MergeDialog;
import com.sunmi.apmanager.utils.CommonUtils;
import com.sunmi.apmanager.utils.HelpUtils;
import com.sunmi.apmanager.utils.SomeMonitorEditText;
import com.sunmi.assistant.R;
import com.sunmi.assistant.ui.activity.contract.InputMobileContract;
import com.sunmi.assistant.ui.activity.presenter.InputMobilePresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.RegexUtils;
import sunmi.common.utils.ViewUtils;
import sunmi.common.view.ClearableEditText;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.dialog.CommonDialog;

@EActivity(R.layout.activity_register)
public class InputMobileActivity extends BaseMvpActivity<InputMobilePresenter>
        implements InputMobileContract.View {

    @ViewById(R.id.etMobile)
    ClearableEditText etMobile;
    @ViewById(R.id.btnNext)
    Button btnNext;
    @ViewById(R.id.ctv_privacy)
    CheckedTextView ctvPrivacy;
    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.tv)
    TextView tv;

    @Extra
    String mobile;
    @Extra
    int checkSource;

    public static final int SOURCE_REGISTER = 0;
    public static final int SOURCE_RETRIEVE_PWD = 1;
    public static final int SOURCE_SMS_LOGIN = 2;


    @AfterViews
    protected void init() {
        HelpUtils.setStatusBarFullTransparent(this);//透明标题栏
        mPresenter = new InputMobilePresenter();
        mPresenter.attachView(this);
        if (CommonHelper.isGooglePlay()) {
            tv.setText(R.string.tip_input_email_address);
        }
        etMobile.setClearIcon(R.mipmap.ic_edit_delete_white);
        new SomeMonitorEditText().setMonitorEditText(btnNext, etMobile);
        if (!TextUtils.isEmpty(mobile)) {
            etMobile.setText(mobile);
            HelpUtils.setSelectionEnd(etMobile);
        }
        switch (checkSource) {
            case SOURCE_REGISTER:
                initRegister();
                break;
            case SOURCE_RETRIEVE_PWD:
                ctvPrivacy.setVisibility(View.GONE);
                titleBar.setAppTitle(R.string.str_retrieve_password);
                break;
            case SOURCE_SMS_LOGIN:
                ctvPrivacy.setVisibility(View.GONE);
                titleBar.setAppTitle(R.string.str_sms_login);
                break;
            default:
                break;
        }
    }

    @Click(R.id.btnNext)
    public void onClick(View v) {
        if (isFastClick(1500)) {
            return;
        }
        mobile = etMobile.getText().toString().trim();
        if (!RegexUtils.isChinaPhone(mobile)) {
            if (CommonHelper.isGooglePlay()) {
                shortTip(R.string.str_invalid_email);
            } else {
                shortTip(R.string.str_invalid_phone);
            }
            return;
        }
        if (checkSource == SOURCE_REGISTER) {
            if (!ctvPrivacy.isChecked()) {
                shortTip(R.string.tip_agree_protocol);
                return;
            }
        } else if (checkSource == SOURCE_RETRIEVE_PWD) {
            CommonUtils.trackCommonEvent(context, "forgetPwdSendVerificationCode",
                    "找回密码_获取验证码", Constants.EVENT_FORGET_PSW);
        } else {
            CommonUtils.trackCommonEvent(context, "loginBySmsSendVerificationCode",
                    "短信验证码登录_获取验证码", Constants.EVENT_LOGIN_BY_SMS);
        }
        invalidAccount();
    }

    private void initRegister() {
        titleBar.setAppTitle(R.string.str_register);
        ctvPrivacy.setVisibility(View.VISIBLE);
        ViewUtils.setPrivacy(this, ctvPrivacy, R.color.white_40a, false);
        CommonUtils.trackDurationEventBegin(context, "registerUsernameDuration",
                "注册流程_输入联系方式_耗时", Constants.EVENT_DURATION_REGISTER_NAME);
    }

    private void invalidAccount() {
        showLoadingDialog();
        mPresenter.isUserExist(mobile);
    }

    //账号合并
    public void userMerge() {
        if (etMobile.getText() == null) {
            return;
        }
        String user = etMobile.getText().toString();//email test: esyzim06497@chacuo.net
        if (RegexUtils.isChinaPhone(user) || RegexUtils.isEmail(user)) {
            showLoadingDialog();
            mPresenter.checkUserName(user);
        }
    }

    //手机号未注册
    @UiThread
    void mobileUnregister() {
        new CommonDialog.Builder(context)
                .setTitle(R.string.tip_unregister)
                .setCancelButton(R.string.sm_cancel)
                .setConfirmButton(R.string.str_register_now, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        initRegister();
                        checkSource = SOURCE_REGISTER;
                    }
                }).create().show();
    }

    //手机号已注册
    @UiThread
    void mobileRegistered() {
        new CommonDialog.Builder(context)
                .setTitle((CommonHelper.isGooglePlay()?R.string.tip_register_already_email:R.string.tip_register_already))
                .setCancelButton(R.string.sm_cancel)
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

    @Override
    public void isUserExistSuccess() {
        hideLoadingDialog();
        userMerge();
    }

    @Override
    public void isUserExistFail(int code, String msg) {
        hideLoadingDialog();
        if (checkSource == SOURCE_REGISTER) {
            CommonUtils.trackDurationEventEnd(context, "registerUsernameDuration",
                    "注册流程_输入联系方式_耗时", Constants.EVENT_DURATION_REGISTER_NAME);
            CommonUtils.trackCommonEvent(context, "registerSendVerificationCode",
                    "注册_获取验证码", Constants.EVENT_REGISTER);
            InputCaptchaActivity_.intent(context)
                    .extra("mobile", mobile)
                    .extra("source", "register")
                    .start();
        } else {
            mobileUnregister();
        }
    }

    @Override
    public void checkSuccess(int code, String data) {
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
                        switch (checkSource) {
                            case SOURCE_REGISTER:
                                mobileRegistered();
                                break;
                            case SOURCE_RETRIEVE_PWD:
                                InputCaptchaActivity_.intent(context)
                                        .extra("mobile", RegexUtils.isChinaPhone(mobile) ? mobile : "")
                                        .extra("source", "password").start();
                                break;
                            case SOURCE_SMS_LOGIN:
                                InputCaptchaActivity_.intent(context)
                                        .extra("mobile", RegexUtils.isChinaPhone(mobile) ? mobile : "")
                                        .extra("source", "login").start();
                                break;
                            default:
                                break;
                        }

                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
