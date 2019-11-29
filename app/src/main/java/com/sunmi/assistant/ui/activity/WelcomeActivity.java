package com.sunmi.assistant.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.sunmi.apmanager.update.AppUpdate;
import com.sunmi.assistant.R;
import com.sunmi.assistant.ui.activity.contract.WelcomeContract;
import com.sunmi.assistant.ui.activity.login.LoginActivity_;
import com.sunmi.assistant.ui.activity.presenter.WelcomePresenter;
import com.tencent.stat.MtaSDkException;
import com.tencent.stat.StatService;
import com.tencent.stat.common.StatConstants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseApplication;
import sunmi.common.base.BaseMvpActivity;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.SpUtils;
import sunmi.common.view.dialog.CommonDialog;

/**
 * 欢迎页
 */
@SuppressLint("Registered")
@EActivity(R.layout.activity_welcome)
public class WelcomeActivity extends BaseMvpActivity<WelcomePresenter>
        implements WelcomeContract.View {

    @ViewById(R.id.name)
    TextView name;

    private static final int INSTALL_PERMISSION_CODE = 900;
    private static final String TENCENT_MAT = "A6INR132MGAI";

    private String appUrl = "";

    @AfterViews
    protected void init() {
        mPresenter = new WelcomePresenter();
        mPresenter.attachView(this);
        if (!CommonHelper.isGooglePlay()) {
            name.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> mPresenter.checkUpgrade(), 500);
        } else {
            gotoLeadPagesActivity();
        }
        initMTA();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            finish();
            return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == INSTALL_PERMISSION_CODE) {
            requestPackagePermission();
        } else {
            BaseApplication.getInstance().quit();
        }
    }

    /**
     * 运营统计
     */
    private void initMTA() {
        try {
            StatService.startStatService(context, TENCENT_MAT, StatConstants.VERSION);
        } catch (MtaSDkException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleLaunch() {
        //状态登录保存，退出登录置空，检查token是否有效
        if (SpUtils.isLoginSuccess()) {
            gotoMainActivity();
        } else {
            gotoLoginActivity();
        }
    }

    private void gotoLoginActivity() {
        LoginActivity_.intent(context).start();
        finish();
    }

    private void gotoMainActivity() {
        MainActivity_.intent(context).start();
        finish();
    }

    @Override
    public void gotoLeadPagesActivity() {
        if (!TextUtils.equals(SpUtils.getLead(), "TRUE")) {
            LeadPagesActivity_.intent(context).start();
            finish();
        }else {
            handleLaunch();
        }
    }

    @UiThread
    @Override
    public void forceUpdate(final String url) {
        appUrl = url;
        getUpgradeDialog().show();
    }

    private CommonDialog getUpgradeDialog() {
        CommonDialog commonDialog = new CommonDialog.Builder(context)
                .setTitle(R.string.tip_title_upgrade)
                .setMessage(R.string.tip_message_upgrade)
                .setConfirmButton(R.string.go_upgrade, (dialog, which) -> {
                    //8.0上授权是否允许安装未知来源
                    requestPackagePermission();
                }).create();
        commonDialog.showWithOutTouchable(false);
        return commonDialog;
    }

    /**
     * 8.0上授权是否允许安装未知来源
     */
    private void requestPackagePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O
                || TextUtils.equals("ZUK", android.os.Build.BRAND)
                || haveInstallPermission()) {
            AppUpdate.versionUpdate((Activity) context, appUrl);
        } else {
            shortTip(R.string.str_open_permission_to_update);
            //跳转设置开启允许安装
            Uri packageURI = Uri.parse("package:" + context.getPackageName());
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
            startActivityForResult(intent, INSTALL_PERMISSION_CODE);
        }
    }

    private boolean haveInstallPermission() {
        //先获取是否有安装未知来源应用的权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return context.getPackageManager().canRequestPackageInstalls();
        }
        return false;
    }

}
