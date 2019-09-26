package com.sunmi.assistant.mine.shop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.widget.Button;

import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.platform.SelectPlatformActivity_;
import com.sunmi.assistant.mine.platform.SelectStoreActivity_;
import com.sunmi.assistant.ui.activity.merchant.AuthDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseActivity;
import sunmi.common.model.AuthStoreInfo;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.TitleBarView;

/**
 * @author yangShiJie
 * @date 2019/8/20
 */

@SuppressLint("Registered")
@EActivity(R.layout.company_activity_shop_create_preview)
public class CreateShopPreviewActivity extends BaseActivity {
    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.btn_shop_import)
    Button btnShopImport;
    @Extra
    int companyId;
    @Extra
    String companyName;
    /**
     * saasExist 1有数据 0无数据
     */
    @Extra
    int saasExist;
    @Extra
    boolean isLoginSuccessSwitchCompany;
    /**
     * 创建商户进入不能back
     */
    @Extra
    boolean isCannotBackPreview;
    private boolean isImportSaas;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        if (isCannotBackPreview) {
            titleBar.setLeftImageVisibility(View.GONE);
        }
        if (!CommonHelper.isGooglePlay()) {
            btnShopImport.setVisibility(View.VISIBLE);
        }
        getSaasInfo();
    }

    @Override
    public void onBackPressed() {
        if (isCannotBackPreview) {
            return;
        }
        super.onBackPressed();
    }

    @Click(R.id.btn_create_shop)
    void createShopClick() {
        gotoCreateShopActivity();
    }

    @Click(R.id.btn_shop_import)
    void importShopClick() {
        isImportSaas = true;
        getSaasInfo();
    }

    private void getSaasInfo() {
        showLoadingDialog();
        SunmiStoreApi.getInstance().getSaasUserInfo(SpUtils.getMobile(), new RetrofitCallback<AuthStoreInfo>() {
            @Override
            public void onSuccess(int code, String msg, AuthStoreInfo bean) {
                hideLoadingDialog();
                showSaasInfo(bean.getSaas_user_info_list());
            }

            @Override
            public void onFail(int code, String msg, AuthStoreInfo data) {
                hideLoadingDialog();
            }
        });
    }

    private void showSaasInfo(List<AuthStoreInfo.SaasUserInfoListBean> list) {
        if (list.size() > 0) {
            //匹配到平台数据
            StringBuilder saasName = new StringBuilder();
            for (AuthStoreInfo.SaasUserInfoListBean bean : list) {
                if (!saasName.toString().contains(bean.getSaas_name())) {
                    saasName.append(bean.getSaas_name()).append(",");
                }
            }
            AuthDialog authDialog = new AuthDialog.Builder((Activity) context)
                    .setMessage(context.getString(R.string.str_dialog_auth_message,
                            saasName.replace(saasName.length() - 1, saasName.length(), "")))
                    .setAllowButton((dialog, which) -> SelectStoreActivity_.intent(context)
                            .list((ArrayList) list)
                            .companyId(companyId)
                            .companyName(companyName)
                            .saasExist(saasExist)
                            .isLoginSuccessSwitchCompany(isLoginSuccessSwitchCompany)
                            .start())
                    .setCancelButton((dialog, which) -> {
                        gotoCreateShopActivity();
                    }).create();
            authDialog.setCanceledOnTouchOutside(true);
            authDialog.show();
        } else {
            if (isImportSaas) {
                //未匹配平台数据-->平台列表
                SelectPlatformActivity_.intent(context)
                        .companyId(companyId)
                        .companyName(companyName)
                        .saasExist(saasExist)
                        .isLoginSuccessSwitchCompany(isLoginSuccessSwitchCompany)
                        .start();
            }
        }
    }

    private void gotoCreateShopActivity() {
        CreateShopActivity_.intent(context)
                .companyId(companyId)
                .companyName(companyName)
                .saasExist(saasExist)
                .isLoginSuccessSwitchCompany(isLoginSuccessSwitchCompany)
                .start();
    }
}
