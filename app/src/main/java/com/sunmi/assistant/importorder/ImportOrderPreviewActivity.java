package com.sunmi.assistant.importorder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.text.Html;
import android.view.View;

import com.sunmi.assistant.R;
import com.sunmi.assistant.ui.activity.merchant.AuthDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseActivity;
import sunmi.common.model.AuthStoreInfo;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.dialog.CommonDialog;

/**
 * @author yangShiJie
 * @date 2019-10-14
 */
@SuppressLint("Registered")
@EActivity(R.layout.import_order_activity_preview)
public class ImportOrderPreviewActivity extends BaseActivity {

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
    }

    @Click(R.id.btn_import_current_mobile)
    void currentMobileClick(View v) {
        getSaasInfo();
    }

    @Click(R.id.btn_import_other_mobile)
    void otherMobileClick(View v) {
        ImportOrderSelectPlatformActivity_.intent(context).start();
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
                    .setTextAuthTip(Html.fromHtml(context.getString(R.string.import_order_agree_auth_shop)
                            + "<font color= '#2896FE'>" + context.getString(R.string.str_auth_protocol_text)
                            + "</font> "))
                    .setMessage(context.getString(R.string.str_dialog_auth_message,
                            saasName.replace(saasName.length() - 1, saasName.length(), "")))
                    .setAllowButton((dialog, which) -> {
                        ImportOrderSelectShopActivity_.intent(context)
                                .list((ArrayList<AuthStoreInfo.SaasUserInfoListBean>) list)
                                .start();
                    })
                    .setCancelButton((dialog, which) -> {
                    }).create();
            authDialog.setCanceledOnTouchOutside(true);
            authDialog.show();
        } else {
            //未匹配平台数据
            cannotSaasDialog();
        }
    }

    private void cannotSaasDialog() {
        CommonDialog commonDialog = new CommonDialog.Builder(context)
                .setTitle(R.string.import_order_no_find_shop)
                .setMessage(R.string.import_order_mobile_no_match_shop)
                .setConfirmButton(getString(R.string.import_order_continue_try), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ImportOrderSelectPlatformActivity_.intent(context).start();
                    }
                })
                .setCancelButton(com.sunmi.ipc.R.string.str_confirm).create();
        commonDialog.showWithOutTouchable(false);
    }
}
