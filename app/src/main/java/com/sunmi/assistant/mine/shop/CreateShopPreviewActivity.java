package com.sunmi.assistant.mine.shop;

import android.annotation.SuppressLint;
import android.view.View;

import com.sunmi.assistant.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.utils.CommonHelper;
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

    private void gotoCreateShopActivity() {
        if (CommonHelper.isGooglePlay()) {
            CreateShopActivity_.intent(context)
                    .companyId(companyId)
                    .companyName(companyName)
                    .saasExist(saasExist)
                    .isLoginSuccessSwitchCompany(isLoginSuccessSwitchCompany)
                    .start();
        } else {
            CreateShopNewActivity_.intent(context)
                    .companyId(companyId)
                    .companyName(companyName)
                    .saasExist(saasExist)
                    .isLoginSuccessSwitchCompany(isLoginSuccessSwitchCompany)
                    .start();
        }
    }
}
