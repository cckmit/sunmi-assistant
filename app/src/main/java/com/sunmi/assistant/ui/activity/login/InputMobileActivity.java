package com.sunmi.assistant.ui.activity.login;

import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.sunmi.assistant.ui.MergeDialog;
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

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.rpc.RpcErrorCode;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.utils.RegexUtils;
import sunmi.common.utils.ViewUtils;
import sunmi.common.view.ClearableEditText;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.dialog.CommonDialog;

import static sunmi.common.view.activity.ProtocolActivity.USER_PRIVATE;
import static sunmi.common.view.activity.ProtocolActivity.USER_PROTOCOL;

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
    @ViewById(R.id.tv86)
    TextView tv86;
    @ViewById(R.id.tvLine)
    TextView tvLine;

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
            etMobile.setInputType(EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        } else {
            tv86.setVisibility(View.VISIBLE);
            tvLine.setVisibility(View.VISIBLE);
            etMobile.setMaxLines(11);
        }
        etMobile.setClearIcon(R.mipmap.ic_edit_delete_white);
        new SomeMonitorEditText().setMonitorEditText(btnNext, etMobile);
        if (!TextUtils.isEmpty(mobile)) {
            etMobile.setText(mobile);
            CommonHelper.setSelectionEnd(etMobile);
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
        if (!RegexUtils.isCorrectAccount(mobile)) {
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
        }
        if (!NetworkUtils.isNetworkAvailable(context)) {
            shortTip(R.string.toast_network_error);
            return;
        }
        invalidAccount();
    }

    private void initRegister() {
        titleBar.setAppTitle(R.string.str_register);
        ctvPrivacy.setVisibility(View.VISIBLE);
        ViewUtils.setPrivacy(this, ctvPrivacy, R.color.white_40a, USER_PROTOCOL, USER_PRIVATE);
    }

    private void invalidAccount() {
        showDarkLoading(getString(R.string.str_get_sms_code));
        mPresenter.isUserExist(mobile);
    }

    //账号合并
    public void userMerge() {
        if (etMobile.getText() == null) {
            return;
        }
        String user = etMobile.getText().toString();//email test: esyzim06497@chacuo.net
        if (RegexUtils.isCorrectAccount(user)) {
            showDarkLoading();
            mPresenter.checkUserName(user);
        }
    }

    //手机号未注册
    @UiThread
    void mobileUnregister() {
        new CommonDialog.Builder(context)
                .setTitle(R.string.tip_unregister)
                .setCancelButton(R.string.sm_cancel)
                .setConfirmButton(R.string.str_register_now,
                        (dialog, which) -> {
                            InputMobileActivity_.intent(context).checkSource(SOURCE_REGISTER).mobile(mobile).start();
                        }).create().show();
    }

    //手机号已注册
    @UiThread
    void mobileRegistered() {
        new CommonDialog.Builder(context)
                .setTitle((CommonHelper.isGooglePlay() ? R.string.tip_register_already_email
                        : R.string.tip_register_already))
                .setCancelButton(R.string.sm_cancel)
                .setConfirmButton(R.string.str_goto_register,
                        (dialog, which) -> {
                            LoginActivity_.intent(context).mobile(mobile).start();
                            finish();
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
        if (code == RpcErrorCode.ERROR_USER_NOT_EXIST) {
            if (checkSource == SOURCE_REGISTER) {
                InputCaptchaActivity_.intent(context).mobile(mobile).source("register").start();
            } else {
                mobileUnregister();
            }
        }
    }

    @Override
    public void checkSuccess(int needMerge, String url) {
        if (needMerge == 1) {
            new MergeDialog(context, url).show();
        } else {
            switch (checkSource) {
                case SOURCE_REGISTER:
                    mobileRegistered();
                    break;
                case SOURCE_RETRIEVE_PWD:
                    InputCaptchaActivity_.intent(context)
                            .mobile(RegexUtils.isCorrectAccount(mobile) ? mobile : "")
                            .source("password").start();
                    break;
                case SOURCE_SMS_LOGIN:
                    InputCaptchaActivity_.intent(context)
                            .mobile(RegexUtils.isCorrectAccount(mobile) ? mobile : "")
                            .source("login").start();
                    break;
                default:
                    break;
            }
        }
    }

}
