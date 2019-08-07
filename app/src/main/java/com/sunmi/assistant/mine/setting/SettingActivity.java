package com.sunmi.assistant.mine.setting;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.sunmi.apmanager.constant.Constants;
import com.sunmi.apmanager.utils.CommonUtils;
import com.sunmi.assistant.R;
import com.sunmi.assistant.ui.activity.login.LoginActivity_;

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
 * @author yangshijie
 */
@EActivity(R.layout.activity_setting)
public class SettingActivity extends BaseMvpActivity<SettingPresenter> implements SettingContract.View {

    @ViewById(R.id.tvVersion)
    TextView tvVersion;
    @ViewById(R.id.tvCash)
    TextView tvCash;

    private BottomPopMenu choosePhotoMenu;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        mPresenter = new SettingPresenter();
        mPresenter.attachView(this);
        tvVersion.setText("版本" + CommonHelper.getAppVersionName(this));
    }

    @Click({R.id.rlAccountSafe, R.id.rlAbout, R.id.rlClearCash, R.id.btnLogout})
    public void click(View v) {
        switch (v.getId()) {
            case R.id.rlAccountSafe:
                SecurityActivity_.intent(context).start();
                break;
            case R.id.rlAbout:
                AboutActivity_.intent(context).start();
                break;
            case R.id.btnLogout:
                CommonUtils.trackCommonEvent(context, "settingLogout",
                        "主页_设置_退出登录", Constants.EVENT_MY_INFO);
                showChoosePhoto();
                break;
            default:
                break;
        }
    }

    void showChoosePhoto() {
        if (choosePhotoMenu == null) {
            choosePhotoMenu = new BottomPopMenu.Builder(this)
                    .setTitle(R.string.msg_quit_confirm)
                    .setIsShowCircleBackground(true)
                    .addItemAction(new PopItemAction(R.string.str_confirm,
                            PopItemAction.PopItemStyle.Warning,
                            this::logout))
                    .addItemAction(new PopItemAction(R.string.sm_cancel,
                            PopItemAction.PopItemStyle.Cancel))
                    .create();
        }
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
        mPresenter.logout();
    }

}
