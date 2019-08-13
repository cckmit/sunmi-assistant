package com.sunmi.assistant.ui.activity.merchant;

import android.app.Activity;
import android.content.Context;

import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.platform.SelectPlatformActivity_;
import com.sunmi.assistant.mine.platform.SelectStoreActivity_;
import com.sunmi.assistant.mine.shop.CreateShopActivity_;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.model.AuthStoreInfo;
import sunmi.common.utils.SpUtils;

/**
 * @author yangShiJie
 * @date 2019/8/2
 */
public class CommonSaasUtils {

    /**
     * 创建完商户匹配saas数据
     *
     * @param list list
     */
    public static void getSaasData(Context context, List<AuthStoreInfo.SaasUserInfoListBean> list) {
        //匹配到平台数据
        if (list.size() > 0) {
            StringBuilder saasName = new StringBuilder();
            for (AuthStoreInfo.SaasUserInfoListBean bean : list) {
                if (!saasName.toString().contains(bean.getSaas_name())) {
                    saasName.append(bean.getSaas_name()).append(",");
                }
            }
            new AuthDialog.Builder((Activity) context)
                    .setMessage(context.getString(R.string.str_dialog_auth_message,
                            saasName.replace(saasName.length() - 1, saasName.length(), "")))
                    .setAllowButton((dialog, which) -> SelectStoreActivity_.intent(context)
                            .isBack(false)
                            .list((ArrayList) list)
                            .start())
                    .setCancelButton((dialog, which) -> {
                        gotoCreateShopActivity(context, SpUtils.getCompanyId());
                    })
                    .create().show();
        } else {
            //未匹配平台数据
            new BottomDialog.Builder((Activity) context)
                    .setMessage(context.getString(R.string.company_shop_new_create_or_import))
                    .setTopButton((dialog, which) -> {
                        //新建门店
                        gotoCreateShopActivity(context, SpUtils.getCompanyId());
                    })
                    .setBottomButton((dialog, which) -> {
                        //导入门店
                        SelectPlatformActivity_.intent(context).start();
                    })
                    .create()
                    .show();
        }
    }

    /**
     * 新建门店
     *
     * @param context   context
     * @param companyId 商户id
     */
    public static void gotoCreateShopActivity(Context context, int companyId) {
        CreateShopActivity_.intent(context)
                .companyId(companyId)
                .start();
    }

}
