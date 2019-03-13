package com.sunmi.assistant.ui.activity.setting;

import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sunmi.apmanager.rpc.RpcCallback;
import com.sunmi.apmanager.rpc.cloud.CloudApi;
import com.sunmi.apmanager.utils.HelpUtils;
import com.sunmi.apmanager.utils.SpUtils;
import com.sunmi.assistant.R;
import com.sunmi.assistant.ui.activity.login.LoginActivity;
import com.sunmi.assistant.ui.activity.login.LoginActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.ClearableEditText;
import sunmi.common.view.TitleBarView;

/**
 * 修改密码
 */
@EActivity(R.layout.activity_setting_fixpsd)
public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener {

    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.tvOriPassword)
    ClearableEditText tvOriPassword;
    @ViewById(R.id.passwordIsVisible)
    ImageButton passwordIsVisible;
    @ViewById(R.id.tvNewPassword)
    ClearableEditText tvNewPassword;
    @ViewById(R.id.passwordIsVisibleNew)
    ImageButton passwordIsVisibleNew;
    @ViewById(R.id.tvSureNewPassword)
    ClearableEditText tvSureNewPassword;
    @ViewById(R.id.passwordIsVisibleSureNew)
    ImageButton passwordIsVisibleSureNew;

    private boolean psdIsVisible;//密码是否可见
    private boolean psdIsVisibleNew;//密码是否可见
    private boolean psdIsVisibleSure;//密码是否可见

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this,
                StatusBarUtils.TYPE_DARK);//状态栏
        titleBar.getRightText().setOnClickListener(this);
    }

    @Click({R.id.passwordIsVisible, R.id.passwordIsVisibleNew, R.id.passwordIsVisibleSureNew})
    public void click(View v) {
        switch (v.getId()) {
            case R.id.passwordIsVisible: //密码是否可见
                if (psdIsVisible) {
                    tvOriPassword.setTransformationMethod(PasswordTransformationMethod.getInstance()); //隐藏密码
                    passwordIsVisible.setBackgroundResource(R.mipmap.ic_eye_dark_hide);
                    psdIsVisible = false;
                } else {
                    tvOriPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance()); //显示密码
                    passwordIsVisible.setBackgroundResource(R.mipmap.ic_eye_dark_open);
                    psdIsVisible = true;
                }
                break;
            case R.id.passwordIsVisibleNew: //密码是否可见
                if (psdIsVisibleNew) {
                    tvNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance()); //隐藏密码
                    passwordIsVisibleNew.setBackgroundResource(R.mipmap.ic_eye_dark_hide);
                    psdIsVisibleNew = false;
                } else {
                    tvNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance()); //显示密码
                    passwordIsVisibleNew.setBackgroundResource(R.mipmap.ic_eye_dark_open);
                    psdIsVisibleNew = true;
                }
                break;
            case R.id.passwordIsVisibleSureNew: //密码是否可见
                if (psdIsVisibleSure) {
                    tvSureNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance()); //隐藏密码
                    passwordIsVisibleSureNew.setBackgroundResource(R.mipmap.ic_eye_dark_hide);
                    psdIsVisibleSure = false;
                } else {
                    tvSureNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance()); //显示密码
                    passwordIsVisibleSureNew.setBackgroundResource(R.mipmap.ic_eye_dark_open);
                    psdIsVisibleSure = true;
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {//完成
        if (isFastClick(1500)) return;
        String old = tvOriPassword.getText().toString().trim();
        String new_psd = tvNewPassword.getText().toString().trim();
        String sure_psd = tvSureNewPassword.getText().toString().trim();
        if (TextUtils.isEmpty(old) || TextUtils.isEmpty(new_psd) || TextUtils.isEmpty(sure_psd)) {
            shortTip(R.string.hint_input_password);
            return;
        }
        if (!HelpUtils.isLetterDigit(new_psd) && new_psd.length() < 8) {
            shortTip(R.string.tip_password_non_standard);
            return;
        }
        if (!new_psd.equals(sure_psd)) {
            shortTip(R.string.tip_two_password_not_same);
            return;
        }
        changePassword(old, new_psd);
    }

    private void changePassword(String old, String new_psd) {
        showLoadingDialog();
        CloudApi.changePassword(old, new_psd, new RpcCallback(context) {
            @Override
            public void onSuccess(int code, String msg, String data) {
                hideLoadingDialog();
                if (code == 1) {
                    shortTip(R.string.tip_password_change_success);
                    SpUtils.logout();
                    LoginActivity_.intent(context).start();
                    finish();
                } else if (code == 201) {
                    shortTip(R.string.tip_old_password_error);
                } else if (code == 3604) {
                    shortTip(R.string.tip_old_and_new_psw_same);
                } else {
                    shortTip(R.string.tip_password_change_fail);
                }
            }
        });
    }

}
