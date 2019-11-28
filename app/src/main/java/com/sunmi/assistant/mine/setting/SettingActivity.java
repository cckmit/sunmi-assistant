package com.sunmi.assistant.mine.setting;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import com.sunmi.apmanager.constant.Constants;
import com.sunmi.apmanager.utils.CommonUtils;
import com.sunmi.assistant.R;
import com.sunmi.assistant.ui.activity.login.LoginActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sunmi.common.base.BaseActivity;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.BaseResponse;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.SettingItemLayout;
import sunmi.common.view.bottompopmenu.BottomPopMenu;
import sunmi.common.view.bottompopmenu.PopItemAction;

/**
 * 设置
 *
 * @author yangshijie
 */
@EActivity(R.layout.activity_setting)
public class SettingActivity extends BaseActivity {
    @ViewById(R.id.sil_about)
    SettingItemLayout silAbout;

    private BottomPopMenu choosePhotoMenu;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        silAbout.setContent(getString(R.string.str_version_placeholder,
                CommonHelper.getAppVersionName(context)));
    }

    @Click({R.id.sil_accountSafe, R.id.sil_about, R.id.btnLogout})
    public void click(View v) {
        switch (v.getId()) {
            case R.id.sil_accountSafe:
                SecurityActivity_.intent(context).start();
                break;
            case R.id.sil_about:
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
                            PopItemAction.PopItemStyle.Warning, this::logout))
                    .addItemAction(new PopItemAction(R.string.sm_cancel,
                            PopItemAction.PopItemStyle.Cancel))
                    .create();
        }
        choosePhotoMenu.show();
    }

     void logoutSuccess() {
        shortTip(R.string.tip_logout_success);
        CommonHelper.logout();
        LoginActivity_.intent(context).flags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NO_HISTORY).start();
        finish();
        System.gc();
    }

    /**
     * 退出登录
     */
     void logout() {
        SunmiStoreApi.getInstance().logout(new Callback<BaseResponse<Object>>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse<Object>> call,
                                   @NonNull Response<BaseResponse<Object>> response) {
                logoutSuccess();
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse<Object>> call,
                                  @NonNull Throwable t) {
                logoutSuccess();
            }
        });
    }

}
