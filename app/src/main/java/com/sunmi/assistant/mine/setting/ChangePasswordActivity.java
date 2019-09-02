package com.sunmi.assistant.mine.setting;

import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.ImageButton;

import com.sunmi.apmanager.utils.DialogUtils;
import com.sunmi.apmanager.utils.HelpUtils;
import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.contract.ChangePasswordContract;
import com.sunmi.assistant.mine.presenter.ChangePasswordPresenter;
import com.sunmi.assistant.ui.activity.login.LoginActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.ClearableEditText;
import sunmi.common.view.TitleBarView;

/**
 * 修改密码
 *
 * @author yangshijie
 */
@EActivity(R.layout.activity_setting_fixpsd)
public class ChangePasswordActivity extends BaseMvpActivity<ChangePasswordPresenter>
        implements ChangePasswordContract.View, View.OnClickListener {

    private static final int FAST_CLICK_INTERVAL = 1500;
    private static final int PASSWORD_LENGTH_MIN = 8;

    @ViewById(R.id.title_bar)
    TitleBarView titleBar;

    @ViewById(R.id.etOldPassword)
    ClearableEditText etOldPassword;
    @ViewById(R.id.ibOldPasswordShow)
    ImageButton ibOldPasswordShow;

    @ViewById(R.id.etNewPassword)
    ClearableEditText etNewPassword;
    @ViewById(R.id.ibNewPasswordShow)
    ImageButton ibNewPasswordShow;

    @ViewById(R.id.etConfirmPassword)
    ClearableEditText etConfirmPassword;
    @ViewById(R.id.ibConfirmPasswordShow)
    ImageButton ibConfirmPasswordShow;

    private boolean isOldPasswordShow;
    private boolean isNewPasswordShow;
    private boolean isConfirmPasswordShow;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        mPresenter = new ChangePasswordPresenter();
        mPresenter.attachView(this);
        titleBar.setLeftImgOnClickListener().setOnClickListener(v -> showExitConfirmDialog());
        titleBar.getRightText().setOnClickListener(this);
    }

    @Click({R.id.ibOldPasswordShow, R.id.ibNewPasswordShow, R.id.ibConfirmPasswordShow})
    public void click(View v) {
        switch (v.getId()) {
            case R.id.ibOldPasswordShow:
                isOldPasswordShow = !isOldPasswordShow;
                updatePasswordShow(etOldPassword, ibOldPasswordShow, isOldPasswordShow);
                break;
            case R.id.ibNewPasswordShow:
                isNewPasswordShow = !isNewPasswordShow;
                updatePasswordShow(etNewPassword, ibNewPasswordShow, isNewPasswordShow);
                break;
            case R.id.ibConfirmPasswordShow:
                isConfirmPasswordShow = !isConfirmPasswordShow;
                updatePasswordShow(etConfirmPassword, ibConfirmPasswordShow, isConfirmPasswordShow);
                break;
            default:
        }
    }

    private void updatePasswordShow(ClearableEditText text, ImageButton icon, boolean isShow) {
        text.setTransformationMethod(isShow ?
                HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
        icon.setBackgroundResource(isShow ? R.mipmap.ic_eye_dark_open : R.mipmap.ic_eye_dark_hide);
    }

    /**
     * 完成按钮点击事件
     *
     * @param v 完成按钮
     */
    @Override
    public void onClick(View v) {
        if (isFastClick(FAST_CLICK_INTERVAL)) {
            return;
        }
        String oldPassword = etOldPassword.getText() == null ? null : etOldPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText() == null ? null : etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText() == null ? null : etConfirmPassword.getText().toString().trim();
        if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            shortTip(R.string.hint_input_password);
            return;
        }
        //noinspection ConstantConditions
        if (!HelpUtils.isLetterDigit(newPassword) && newPassword.length() < PASSWORD_LENGTH_MIN) {
            shortTip(R.string.tip_password_non_standard);
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            shortTip(R.string.tip_two_password_not_same);
            return;
        }
        mPresenter.changePassword(oldPassword, newPassword);
        showLoadingDialog();
    }

    @Override
    public void changePasswordSuccess() {
        hideLoadingDialog();
        shortTip(R.string.tip_password_change_success);
        CommonHelper.logout();
        LoginActivity_.intent(context).start();
        finish();
    }

    @Override
    public void changePasswordFail(int code, String msg) {
        hideLoadingDialog();
        if (code == 201) {
            shortTip(R.string.tip_old_password_error);
        } else if (code == 3604) {
            shortTip(R.string.tip_old_and_new_psw_same);
        } else if (code == 2066) {
            shortTip(getString(R.string.str_password_fomat_error));
        } else {
            shortTip(R.string.tip_password_change_fail);
        }
    }

    @Override
    public void onBackPressed() {
        showExitConfirmDialog();
    }

    /**
     * 显示二次确认
     */
    private void showExitConfirmDialog() {
        String oldPassword = etOldPassword.getText() == null ? null : etOldPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText() == null ? null : etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText() == null ? null : etConfirmPassword.getText().toString().trim();
        if (TextUtils.isEmpty(oldPassword) && TextUtils.isEmpty(newPassword) && TextUtils.isEmpty(confirmPassword)) {
            finish();
        } else {
            DialogUtils.isCancelSetting(this);
        }
    }

    /*private void changePassword(String old, String new_psd) {
        showLoadingDialog();
        CloudApi.changePassword(old, new_psd, new RpcCallback(context) {
            @Override
            public void onSuccess(int code, String msg, String data) {
                hideLoadingDialog();
                if (code == 1) {
                    shortTip(R.string.tip_password_change_success);
                    CommonUtils.logout();
                    LoginActivity_.intent(context).start();
                    finish();
                } else if (code == 201) {
                    shortTip(R.string.tip_old_password_error);
                } else if (code == 3604) {
                    shortTip(R.string.tip_old_and_new_psw_same);
                } else if (code == 2066) {
                    shortTip(getString(R.string.str_password_fomat_error));
                } else {
                    shortTip(R.string.tip_password_change_fail);
                }
            }
        });
    }*/

}
