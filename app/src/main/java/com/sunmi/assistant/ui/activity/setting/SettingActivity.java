package com.sunmi.assistant.ui.activity.setting;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.sunmi.apmanager.constant.Constants;
import com.sunmi.apmanager.utils.CommonUtils;
import com.sunmi.assistant.R;
import com.sunmi.assistant.ui.activity.contract.SettingContract;
import com.sunmi.assistant.ui.activity.login.LoginActivity_;
import com.sunmi.assistant.ui.activity.presenter.SettingPresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.bottompopmenu.BottomPopMenu;
import sunmi.common.view.bottompopmenu.PopItemAction;

/**
 * 设置
 */
@EActivity(R.layout.activity_setting)
public class SettingActivity extends BaseMvpActivity<SettingPresenter> implements SettingContract.View {

    @ViewById(R.id.tvVersion)
    TextView tvVersion;
    @ViewById(R.id.tvCash)
    TextView tvCash;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this,
                StatusBarUtils.TYPE_DARK);//状态栏
        mPresenter = new SettingPresenter();
        mPresenter.attachView(this);
        tvVersion.setText("版本" + CommonHelper.getAppVersionName(this));
    }

    @Click({R.id.rlAccountSafe, R.id.rlAbout, R.id.rlClearCash, R.id.btnLogout})
    public void click(View v) {
        switch (v.getId()) {
            case R.id.rlAccountSafe://账号安全
                SecurityActivity_.intent(context).start();
                break;
            case R.id.rlAbout://关于
                AboutActivity_.intent(context).start();
                break;
            case R.id.btnLogout://退出
                CommonUtils.trackCommonEvent(context, "settingLogout",
                        "主页_设置_退出登录", Constants.EVENT_MY_INFO);
                showChoosePhoto();
                break;
            default:
                break;
        }
    }

    BottomPopMenu choosePhotoMenu;

    void showChoosePhoto() {
        if (choosePhotoMenu == null)
            choosePhotoMenu = new BottomPopMenu.Builder(this)
                    .setTitle(R.string.msg_quit_confirm)
                    .setIsShowCircleBackground(true)
                    .addItemAction(new PopItemAction(R.string.str_confirm,
                            PopItemAction.PopItemStyle.Warning,
                            this::logout))
                    .addItemAction(new PopItemAction(R.string.sm_cancel,
                            PopItemAction.PopItemStyle.Cancel))
                    .create();
        choosePhotoMenu.show();
    }

    @Override
    public void logoutSuccess() {
        shortTip(R.string.tip_logout_success);
        CommonUtils.logout();
        LoginActivity_.intent(context).flags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NO_HISTORY).start();
        finish();
        System.gc();
    }

    @Override
    public void logoutFail(int code, String msg) {
        shortTip(R.string.tip_logout_fail);
    }

    /**
     * 退出登录
     */
    private void logout() {
        /*CloudApi.loginOut(new RpcCallback(context) {
            @Override
            public void onSuccess(int code, String msg, String data) {
                if (code == 1) {
                    shortTip(R.string.tip_logout_success);
                    CommonUtils.logout();
                    LoginActivity_.intent(context).flags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_NO_HISTORY).start();
                    finish();
                    System.gc();
                } else {
                    shortTip(R.string.tip_logout_fail);
                }
            }
        });*/
        mPresenter.logout();
    }

}
