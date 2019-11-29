package com.sunmi.assistant.ui.activity.login;

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
import com.sunmi.apmanager.rpc.mqtt.MQTTManager;
import com.sunmi.apmanager.ui.view.MergeDialog;
import com.sunmi.apmanager.utils.CommonUtils;
import com.sunmi.apmanager.utils.HelpUtils;
import com.sunmi.apmanager.utils.SomeMonitorEditText;
import com.sunmi.assistant.R;
import com.sunmi.assistant.contract.LoginContract;
import com.sunmi.assistant.presenter.LoginPresenter;
import com.sunmi.assistant.ui.activity.merchant.CreateCompanyActivity_;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.impl.RouterRequest;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.constant.CommonConstants;
import sunmi.common.constant.RouterConfig;
import sunmi.common.model.CompanyInfoResp;
import sunmi.common.utils.CommonHelper;
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
    @ViewById(R.id.tvLogo)
    TextView tvLogo;

    @Extra
    String reason;
    @Extra
    String mobile;

    private boolean psdIsVisible;//密码是否可见

    private CommonDialog kickedDialog;

    @RouterAnno(
            path = RouterConfig.App.LOGIN
    )
    public static Intent start(RouterRequest request){
        Intent intent = new Intent(request.getRawContext(), LoginActivity_.class);
        return intent;
    }

    @AfterViews
    protected void init() {
        mPresenter = new LoginPresenter();
        mPresenter.attachView(this);
        PermissionUtils.checkPermissionActivity(this);//手机权限
        HelpUtils.setStatusBarFullTransparent(this);//透明标题栏
        if (CommonHelper.isGooglePlay()) {
            tvSMSLogin.setVisibility(View.GONE);
            etUser.setHint(R.string.hint_input_email);
            tvLogo.setBackgroundResource(R.mipmap.ic_sunmi_logo_en);
        }
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
        MQTTManager.getInstance().disconnect();//todo 为了多端登录后断连，等以后w1迁到商米store去掉
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
        CommonHelper.setSelectionEnd(etUser);
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

    @Click({R.id.btnLogin, R.id.btnRegister, R.id.ib_visible,
            R.id.tvForgetPassword, R.id.tvSMSLogin})
    public void onClick(View v) {
        if (etUser.getText() == null || etPassword.getText() == null) {
            return;
        }
        mobile = RegexUtils.handleIllegalCharacter(etUser.getText().toString().trim());
        String password = etPassword.getText().toString();
        switch (v.getId()) {
            case R.id.btnLogin: //密码登录
                if (isFastClick(1500)) {
                    return;
                }
                if (!RegexUtils.isCorrectAccount(mobile)) {
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
                InputMobileActivity_.intent(context).mobile(RegexUtils.isCorrectAccount(mobile) ? mobile : "")
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
                InputMobileActivity_.intent(context).mobile(RegexUtils.isCorrectAccount(mobile) ? mobile : "")
                        .checkSource(InputMobileActivity.SOURCE_RETRIEVE_PWD).start();
                break;
            case R.id.tvSMSLogin:  //短信登录
                if (isFastClick(1500)) {
                    return;
                }
                CommonUtils.trackCommonEvent(context, "loginBySms", "短信验证码登录", Constants.EVENT_LOGIN);
                CommonUtils.trackDurationEventBegin(context, "quickLoginDuration",
                        "登录流程开始到结束", Constants.EVENT_DURATION_LOGIN_BY_SMS);
                InputMobileActivity_.intent(context).mobile(RegexUtils.isCorrectAccount(mobile) ? mobile : "")
                        .checkSource(InputMobileActivity.SOURCE_SMS_LOGIN).start();
                break;
            default:
                break;
        }
    }

    //账号合并
    private void userMerge(final String password) {
        CommonUtils.trackCommonEvent(context, "login", "登录", Constants.EVENT_LOGIN);
        showDarkLoading();
        mPresenter.userMerge(mobile, mobile, password);
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
        new CommonDialog.Builder(context)
                .setTitle(R.string.tip_unregister)
                .setCancelButton(R.string.sm_cancel)
                .setConfirmButton(R.string.str_register_now, (dialog, which) -> {
                    CommonUtils.trackCommonEvent(context, "loginUnRegisterDialogRegister",
                            "登录_未注册弹框-立即注册", Constants.EVENT_LOGIN);
                    InputMobileActivity_.intent(context).mobile(RegexUtils.isCorrectAccount(mobile) ? mobile : "")
                            .checkSource(InputMobileActivity.SOURCE_REGISTER).start();
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
            CreateCompanyActivity_.intent(context).createCompanyCannotBack(true).start();
        } else {
            LoginChooseShopActivity_.intent(context)
                    .action(CommonConstants.ACTION_LOGIN_CHOOSE_COMPANY).start();
        }
    }

    @Override
    public void getCompanyListFail(int code, String msg) {

    }

}
