package com.sunmi.assistant.ui.activity.merchant;

import android.annotation.SuppressLint;
import android.view.View;

import com.sunmi.assistant.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.TitleBarView;

/**
 * @author yangShiJie
 * @date 2019/7/31
 */
@SuppressLint("Registered")
@EActivity(R.layout.company_activity_create)
public class CreateCompanyActivity extends BaseActivity implements View.OnClickListener {
    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @Extra
    boolean createCompanyCannotBack;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        titleBar.getLeftLayout().setOnClickListener(this);
        if (createCompanyCannotBack) {
            titleBar.setLeftImageVisibility(View.GONE);
        }
    }

    @Click(R.id.btn_create_company)
    void newCreateCompany() {
        CreateCompanyNextActivity_.intent(context)
                .createCompanyCannotBack(createCompanyCannotBack)
                .start();
    }

    @Override
    public void onBackPressed() {
        if (createCompanyCannotBack) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        onBackPressed();
    }
}
