package com.sunmi.assistant.ui.activity.login;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.sunmi.apmanager.config.AppConfig;
import com.sunmi.apmanager.constant.Constants;
import com.sunmi.apmanager.utils.CommonUtils;
import com.sunmi.apmanager.utils.HelpUtils;
import com.sunmi.apmanager.utils.SomeMonitorEditText;
import com.sunmi.assistant.R;
import com.sunmi.assistant.ui.activity.contract.SetPasswordContract;
import com.sunmi.assistant.ui.activity.merchant.CreateCompanyActivity_;
import com.sunmi.assistant.ui.activity.presenter.SetPasswordPresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.constant.CommonConstants;
import sunmi.common.model.CompanyInfoResp;
import sunmi.common.utils.RegexUtils;
import sunmi.common.view.ClearableEditText;

/**
 * set password
 */
@SuppressLint("Registered")
@EActivity(R.layout.activity_set_password)
public class SetPasswordActivity extends BaseMvpActivity<SetPasswordPresenter>
        implements SetPasswordContract.View {

    @ViewById(R.id.et_password)
    ClearableEditText etPassword;
    @ViewById(R.id.ibPasswordShow)
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
        mPresenter = new SetPasswordPresenter();
        mPresenter.attachView(this);
        etPassword.setClearIcon(R.mipmap.ic_edit_delete_white);
        Bundle bundle = getIntent().getExtras();
        type = bundle.getInt(AppConfig.SET_PASSWORD);
        smsCode = bundle.getString(AppConfig.SET_PASSWORD_SMS);
        mobile = bundle.getString("mobile");
        //button是否可点击
        new SomeMonitorEditText().setMonitorEditText(btnComplete, etPassword);
        CommonUtils.trackDurationEventBegin(context, "registerPasswordDuration",
                "注册流程_设置密码_耗时", Constants.EVENT_DURATION_REGISTER_PSW);
    }

    @Click({R.id.ibPasswordShow, R.id.btnComplete})
    public void onClick(View v) {
        String password = etPassword.getText().toString();
        switch (v.getId()) {
            case R.id.ibPasswordShow: //密码是否可见
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
                if (isFastClick(1500)) {
                    return;
                }
                if (!RegexUtils.isValidPassword(password) || password.length() < 8) {
                    shortTip(R.string.tip_password_non_standard);
                    return;
                }
                trackFinish();
                if (type == AppConfig.SET_PASSWORD_REGISTER) {//注册
                    mPresenter.register(mobile, password, smsCode);
                } else if (type == AppConfig.SET_PASSWORD_RESET) {//找回密码
                    mPresenter.resetPassword(mobile, password, smsCode);
                }
                break;
            default:
                break;
        }
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

    @Override
    public void registerSuccess() {
        shortTip(R.string.register_success);
        CommonUtils.trackDurationEventEnd(context, "registerPasswordDuration",
                "注册流程_设置密码_耗时", Constants.EVENT_DURATION_REGISTER_PSW);
        CommonUtils.trackDurationEventEnd(context, "registerDuration",
                "注册流程开始和结束时调用", Constants.EVENT_DURATION_REGISTER);
        mPresenter.getUserInfo();
    }

    @Override
    public void registerFail(int code, String msg) {
        shortTip(R.string.register_error);
    }

    @Override
    public void resetPasswordSuccess() {
        shortTip(R.string.textView_reset_password_success);
        LoginActivity_.intent(context).start();
    }

    @Override
    public void reSetPasswordFail(int code, String msg) {
        shortTip(R.string.textView_reset_password_error);
    }

    /**
     * 注册完成时查询商户列表
     * 当companyList==0创建商户
     * 当companyList>0获取saas数据
     *
     * @param companyList 商户list
     */
    @Override
    public void getCompanyListSuccess(List<CompanyInfoResp> companyList) {
        if (companyList.size() == 0) {
            CreateCompanyActivity_.intent(context).start();
            return;
        }
        LoginChooseShopActivity_.intent(context)
                .isCreateCompany(true)
                .action(CommonConstants.ACTION_LOGIN_CHOOSE_COMPANY).start();
    }

    @Override
    public void getCompanyListFail(int code, String msg) {
        shortTip(R.string.str_net_exception);
    }
}
