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

import com.sunmi.apmanager.update.AppUpdate;
import com.sunmi.apmanager.utils.CommonUtils;
import com.sunmi.assistant.MyApplication;
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

import sunmi.common.base.BaseApplication;
import sunmi.common.base.BaseMvpActivity;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.utils.SpUtils;
import sunmi.common.view.dialog.CommonDialog;

/**
 * 欢迎页
 */
@SuppressLint("Registered")
@EActivity(R.layout.activity_welcome)
public class WelcomeActivity extends BaseMvpActivity<WelcomePresenter>
        implements WelcomeContract.View {
    private static final int INSTALL_PERMISSION_CODE = 900;
    private static final String TENCENT_MAT = "A6INR132MGAI";

    private String appUrl = "";

    @AfterViews
    protected void init() {
        mPresenter = new WelcomePresenter();
        mPresenter.attachView(this);
        new Handler().postDelayed(() -> mPresenter.checkUpgrade(), 500);
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

    @Override
    public void checkTokenSuccess(String response) {
        //todo 云端接口有问题，先不校验token，允许多端登录
        /*try {
            if (response != null) {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("code") && jsonObject.getInt("code") == 1) {
                    MyApplication.isCheckedToken = true;
                    gotoMainActivity();
                    return;
                }
            }
            logout();
        } catch (Exception e) {
            e.printStackTrace();
            logout();
        }*/
        MyApplication.isCheckedToken = true;
        gotoMainActivity();
    }

    @Override
    public void checkTokenFail(int code, String msg) {
        logout();
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
            if (!NetworkUtils.isNetworkAvailable(context)) {
                gotoMainActivity();
            } else {
//                mPresenter.checkToken();
                checkTokenSuccess("");//todo
            }
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
        LeadPagesActivity_.intent(context).start();
        finish();
    }

    private void logout() {
        CommonUtils.logout();
        gotoLoginActivity();
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

//    private void handlerDelay(long delayMillis, final Class<?> mClass) {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                openActivity(context, mClass, true);
//            }
//        }, delayMillis);
//    }

    //ping判断是否假网
//    private void launch() {
//        ThreadPool.getCachedThreadPool().submit(new Runnable() {
//            @Override
//            public void run() {
//                LogCat.e(TAG, "ping time -- 111");
//                if (!NetworkUtils.isNetPingUsable()) {
//                    LogCat.e(TAG, "ping time -- 222");
//                    if (SpUtils.isLoginSuccess()) {
//                        gotoMainActivity();
//                    } else {
//                        gotoLoginActivity();
//                    }
//                } else {
//                    LogCat.e(TAG, "ping time -- 333");
//                }
//            }
//        });
//    }

    /*private void checkToken() {
        CloudApi.checkToken(new StringCallback() {
            @Override
            public void onError(Call call, Response response, Exception e, int id) {
                logout();
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    if (response != null) {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("code") && jsonObject.getInt("code") == 1) {
                            MyApplication.isCheckedToken = true;
                            gotoMainActivity();
                            return;
                        }
                    }
                    logout();
                } catch (Exception e) {
                    e.printStackTrace();
                    logout();
                }
            }
        });
    }*/

    /*private void checkUpdate() {
        CloudApi.checkUpgrade(new StringCallback() {
            @Override
            public void onError(Call call, Response response, Exception e, int id) {
                handleLaunch();
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    if (response != null) {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("code") && jsonObject.getInt("code") == 1) {
                            JSONObject object = (JSONObject) jsonObject.getJSONArray("data").opt(0);
                            if (object.has("is_force_upgrade")) {
                                // 是否需要强制升级 0-否 1-是
                                int needMerge = object.getInt("is_force_upgrade");
                                if (needMerge == 1) {
                                    appUrl = object.getString("url");
                                    forceUpdate(appUrl);
                                    return;
                                } else {
                                    //首次安装或清空数据时
                                    if (!TextUtils.equals(SpUtils.getLead(), "TRUE")) {
                                        gotoLeadPagesActivity();
                                        return;
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handleLaunch();
            }
        });
    }*/

}
