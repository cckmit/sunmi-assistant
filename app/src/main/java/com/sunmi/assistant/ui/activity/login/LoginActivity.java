package com.sunmi.assistant.ui.activity.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sunmi.apmanager.constant.Constants;
import com.sunmi.apmanager.ui.view.MergeDialog;
import com.sunmi.apmanager.utils.CommonUtils;
import com.sunmi.apmanager.utils.HelpUtils;
import com.sunmi.apmanager.utils.SomeMonitorEditText;
import com.sunmi.assistant.R;
import com.sunmi.assistant.contract.LoginContract;
import com.sunmi.assistant.presenter.LoginPresenter;
import com.sunmi.assistant.ui.activity.merchant.CreateCompanyActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.constant.CommonConstants;
import sunmi.common.model.CompanyInfoResp;
import sunmi.common.utils.PermissionUtils;
import sunmi.common.utils.RegexUtils;
import sunmi.common.utils.SpUtils;
import sunmi.common.view.ClearableEditText;
import sunmi.common.view.dialog.CommonDialog;

/**
 * 登录
 */
@EActivity(R.layout.activity_login)
public class LoginActivity extends BaseMvpActivity<LoginPresenter> implements LoginContract.View {

    @ViewById(R.id.etUser)
    ClearableEditText etUser;
    @ViewById(R.id.et_password)
    ClearableEditText etPassword;
    @ViewById(R.id.tvSMSLogin)
    TextView tvSMSLogin;
    @ViewById(R.id.tvForgetPassword)
    TextView tvForgetPassword;
    @ViewById(R.id.ib_visible)
    ImageButton passwordIsVisible;
    @ViewById(R.id.btnLogin)
    Button btnLogin;
    @ViewById(R.id.btnRegister)
    Button btnRegister;
    @ViewById(R.id.btnFixPassword)
    Button btnFixPassword;
    @ViewById(R.id.btnLogout)
    Button btnLogout;

    @Extra
    String reason;
    @Extra
    String mobile;

    private boolean psdIsVisible;//密码是否可见

    private CommonDialog kickedDialog;

    @AfterViews
    protected void init() {
        mPresenter = new LoginPresenter();
        mPresenter.attachView(this);
        PermissionUtils.checkPermissionActivity(this);//手机权限
        HelpUtils.setStatusBarFullTransparent(this);//透明标题栏
        etUser.setClearIcon(R.mipmap.ic_edit_delete_white);
        etPassword.setClearIcon(R.mipmap.ic_edit_delete_white);
        if (TextUtils.isEmpty(mobile)) {
            mobile = SpUtils.getMobile();
        }
        new SomeMonitorEditText().setMonitorEditText(btnLogin, etUser, etPassword);//button 是否可点击
        if (!TextUtils.isEmpty(mobile)) {
            etUser.setText(mobile);
        } else if (!TextUtils.isEmpty(SpUtils.getEmail())) {
            etUser.setText(SpUtils.getEmail());
        }
        showTip();
    }

    private void showTip() {
        if (TextUtils.equals("1", reason)) {
            if (kickedDialog != null && kickedDialog.isShowing()) {
                return;
            }
            kickedDialog = new CommonDialog.Builder(context)
                    .setTitle(R.string.tip_kick_off)
                    .setConfirmButton(R.string.str_confirm).create();
            kickedDialog.show();
        } else if (TextUtils.equals("2", reason)) {
            shortTip(R.string.tip_password_changed_login);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (!TextUtils.isEmpty(bundle.getString("mobile"))) {
                mobile = bundle.getString("mobile");
                etUser.setText(mobile);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        HelpUtils.setSelectionEnd(etUser);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length <= 0) {
            return;
        }
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {//用户拒绝
            shortTip(R.string.tip_permission_ungranted);
        }
    }

    @Click({R.id.btnLogin, R.id.btnRegister, R.id.btnFixPassword, R.id.ib_visible,
            R.id.btnLogout, R.id.tvForgetPassword, R.id.tvSMSLogin})
    public void onClick(View v) {
        mobile = etUser.getText().toString().trim();
        String password = etPassword.getText().toString();
        switch (v.getId()) {
            case R.id.btnLogin: //密码登录
                if (isFastClick(1500)) {
                    return;
                }
                if (!RegexUtils.isChinaPhone(mobile) && !RegexUtils.isEmail(mobile)) {
                    shortTip(R.string.tip_invalid_phone_number);
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    shortTip(R.string.textView_config_psd8);
                    return;
                }
                userMerge(password);
                break;
            case R.id.btnRegister: //注册
                if (isFastClick(1500)) {
                    return;
                }
                CommonUtils.trackCommonEvent(context, "register", "注册按钮", Constants.EVENT_LOGIN);
                CommonUtils.trackDurationEventBegin(context, "registerDuration",
                        "注册流程开始和结束时调用", Constants.EVENT_DURATION_REGISTER);
                /*RegisterActivity_.intent(context)
                        .extra("mobile", RegexUtils.isChinaPhone(mobile) ? mobile : "")
                        .start();*/
                InputMobileActivity_.intent(context).mobile(RegexUtils.isChinaPhone(mobile) ? mobile : "")
                        .checkSource(InputMobileActivity.SOURCE_REGISTER).start();
                break;
            case R.id.ib_visible: //密码是否可见
                if (psdIsVisible) {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance()); //隐藏密码
                    passwordIsVisible.setBackgroundResource(R.mipmap.ic_eye_light_hide);
                    psdIsVisible = false;
                } else {
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance()); //显示密码
                    passwordIsVisible.setBackgroundResource(R.mipmap.ic_eye_light_open);
                    psdIsVisible = true;
                }
                if (etPassword.getText() != null) {
                    etPassword.setSelection(etPassword.getText().length());
                }
                break;
            case R.id.tvForgetPassword: //忘记密码
                if (isFastClick(1500)) {
                    return;
                }
                CommonUtils.trackCommonEvent(context, "forgetPassword", "忘记密码按钮", Constants.EVENT_LOGIN);
                CommonUtils.trackDurationEventBegin(context, "retrievePasswordDuration",
                        "找回密码流程开始和结束", Constants.EVENT_DURATION_FORGET_PSW);
               /* RetrievePasswordActivity_.intent(context)
                        .extra("mobile", RegexUtils.isChinaPhone(mobile) ? mobile : "")
                        .start();*/
                InputMobileActivity_.intent(context).mobile(RegexUtils.isChinaPhone(mobile) ? mobile : "")
                        .checkSource(InputMobileActivity.SOURCE_RETRIEVE_PWD).start();
                break;
            case R.id.tvSMSLogin:  //短信登录
                if (isFastClick(1500)) {
                    return;
                }
                CommonUtils.trackCommonEvent(context, "loginBySms", "短信验证码登录", Constants.EVENT_LOGIN);
                CommonUtils.trackDurationEventBegin(context, "quickLoginDuration",
                        "登录流程开始到结束", Constants.EVENT_DURATION_LOGIN_BY_SMS);
                InputMobileActivity_.intent(context).mobile(RegexUtils.isChinaPhone(mobile) ? mobile : "")
                        .checkSource(InputMobileActivity.SOURCE_SMS_LOGIN).start();
                break;
            default:
                break;
        }
    }

    //账号合并
    private void userMerge(final String password) {
        if (etUser.getText() == null) {
            return;
        }
        CommonUtils.trackCommonEvent(context, "login", "登录", Constants.EVENT_LOGIN);
        showLoadingDialog();
        mPresenter.userMerge(etUser.getText().toString(), mobile, password);
    }

    @UiThread
    @Override
    public void showMergeDialog(String url) {
        new MergeDialog(context, url).show();
    }

    //手机号未注册
    @UiThread
    @Override
    public void mobileUnregister() {
        new CommonDialog.Builder(LoginActivity.this)
                .setTitle(R.string.tip_unregister)
                .setCancelButton(R.string.sm_cancel)
                .setConfirmButton(R.string.str_register_now, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CommonUtils.trackCommonEvent(context, "loginUnRegisterDialogRegister",
                                "登录_未注册弹框-立即注册", Constants.EVENT_LOGIN);
                        RegisterActivity_.intent(context)
                                .extra("mobile", RegexUtils.isChinaPhone(mobile) ? mobile : "")
                                .start();
                    }
                }).create().show();
    }

    @Override
    public void loginSuccess() {
        SpUtils.setMobile(mobile);
        mPresenter.getCompanyList();
    }

    @Override
    public void getCompanyListSuccess(List<CompanyInfoResp> companyList) {
        if (companyList.size() == 0) {
            CreateCompanyActivity_.intent(context)
                    .createCompanyCannotBack(true)
                    .start();
            return;
        }
        LoginChooseShopActivity_.intent(context)
                .action(CommonConstants.ACTION_LOGIN_CHOOSE_COMPANY).start();
    }

    @Override
    public void getCompanyListFail(int code, String msg) {

    }

}
