package com.sunmi.assistant.ui.activity.merchant;

import android.annotation.SuppressLint;

import com.sunmi.assistant.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import sunmi.common.base.BaseActivity;
import sunmi.common.utils.StatusBarUtils;

/**
 * @author yangShiJie
 * @date 2019/7/31
 */
@SuppressLint("Registered")
@EActivity(R.layout.company_activity_create)
public class CreateCompanyActivity extends BaseActivity {
    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
    }

    @Click(R.id.btn_create_company)
    void newCreateCompany() {
        CreateCompanyNextActivity_.intent(context).start();
    }
}
