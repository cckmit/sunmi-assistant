package com.sunmi.assistant.mine.company;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.sunmi.apmanager.utils.DialogUtils;
import com.sunmi.apmanager.utils.HelpUtils;
import com.sunmi.assistant.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.model.CompanyInfoResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.RegexUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.ClearableEditText;
import sunmi.common.view.TitleBarView;

/**
 * 更改商户名称页面
 *
 * @author bruce
 * @date 2019/6/6
 */
@SuppressLint("Registered")
@EActivity(R.layout.activity_change_username)
public class CompanyUpdateActivity extends BaseActivity
        implements View.OnClickListener, TextWatcher {

    private static final int COMPANY_NAME_MAX_LENGTH = 40;
    private static final int EMAIL_MAX_LENGTH = 100;

    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.cet_username)
    ClearableEditText cetUserInfo;
    @Extra
    CompanyInfoResp mInfo;
    @Extra
    int type;
    private String companyInfo;

    @AfterViews
    void init() {
        titleBar.setRightTextViewText(R.string.str_save);
        titleBar.setRightTextViewColor(R.color.colorText);
        titleBar.getLeftLayout().setOnClickListener(v -> onBackPressed());
        titleBar.getRightTextView().setOnClickListener(this);
        if (type == CompanyDetailActivity.TYPE_EMAIL) {
            cetUserInfo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(EMAIL_MAX_LENGTH)});
        } else {
            cetUserInfo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(COMPANY_NAME_MAX_LENGTH)});
        }
        cetUserInfo.addTextChangedListener(this);
        cetUserInfo.requestFocus();
        if (type == CompanyDetailActivity.TYPE_NAME) {
            titleBar.setAppTitle(R.string.str_change_company_name);
            cetUserInfo.setHint(R.string.tip_input_company_name);
            if (!TextUtils.isEmpty(mInfo.getCompany_name())) {
                cetUserInfo.setText(mInfo.getCompany_name());
                cetUserInfo.setSelection(mInfo.getCompany_name().length());
            }
        } else if (type == CompanyDetailActivity.TYPE_CONTACT) {
            titleBar.setAppTitle(getString(R.string.str_change_company_contact));
            cetUserInfo.setHint(R.string.tip_input_company_contact);
            if (!TextUtils.isEmpty(mInfo.getContact_person())) {
                cetUserInfo.setText(mInfo.getContact_person());
                cetUserInfo.setSelection(mInfo.getContact_person().length());
            }
        } else if (type == CompanyDetailActivity.TYPE_CONTACT_TEL) {
            titleBar.setAppTitle(getString(R.string.str_change_company_tel));
            cetUserInfo.setInputType(InputType.TYPE_CLASS_PHONE);
            cetUserInfo.setHint(R.string.tip_input_company_tel);
            if (!TextUtils.isEmpty(mInfo.getContact_tel())) {
                cetUserInfo.setText(mInfo.getContact_tel());
                cetUserInfo.setSelection(mInfo.getContact_tel().length());
            }
        } else if (type == CompanyDetailActivity.TYPE_EMAIL) {
            titleBar.setAppTitle(getString(R.string.str_change_company_email));
            cetUserInfo.setHint(R.string.tip_input_company_email);
            if (!TextUtils.isEmpty(mInfo.getContact_email())) {
                cetUserInfo.setText(mInfo.getContact_email());
                cetUserInfo.setSelection(mInfo.getContact_email().length());
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (isUpdateMessage()) {
            return;
        }
        showLoadingDialog();
        SunmiStoreApi.getInstance().updateCompanyInfo(mInfo, new RetrofitCallback<CompanyInfoResp>() {
            @Override
            public void onSuccess(int code, String msg, CompanyInfoResp data) {
                hideLoadingDialog();
                Intent intent = getIntent();
                if (type == CompanyDetailActivity.TYPE_NAME) {
                    intent.putExtra(CompanyDetailActivity.INTENT_EXTRA_NAME, companyInfo);
                } else if (type == CompanyDetailActivity.TYPE_CONTACT) {
                    intent.putExtra(CompanyDetailActivity.INTENT_EXTRA_CONTACT, companyInfo);
                } else if (type == CompanyDetailActivity.TYPE_CONTACT_TEL) {
                    intent.putExtra(CompanyDetailActivity.INTENT_EXTRA_CONTACT_TEL, companyInfo);
                } else if (type == CompanyDetailActivity.TYPE_EMAIL) {
                    intent.putExtra(CompanyDetailActivity.INTENT_EXTRA_EMAIL, companyInfo);
                }
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFail(int code, String msg, CompanyInfoResp data) {
                LogCat.e(TAG, "Update shop name Failed. " + msg);
                hideLoadingDialog();
                shortTip(R.string.tip_save_fail);
            }
        });

    }

    private boolean isUpdateMessage() {
        companyInfo = cetUserInfo.getText() == null ? null : cetUserInfo.getText().toString().trim();
        if (type == CompanyDetailActivity.TYPE_NAME) {
            if (TextUtils.isEmpty(companyInfo)) {
                shortTip(R.string.tip_input_company_name);
                return true;
            }
            if (HelpUtils.isContainEmoji(companyInfo)) {
                shortTip(getString(com.sunmi.apmanager.R.string.str_no_contain_emoji));
                return true;
            }
            if (TextUtils.equals(companyInfo, mInfo.getCompany_name())) {
                finish();
                return true;
            }
            mInfo.setCompany_name(companyInfo);
        } else if (type == CompanyDetailActivity.TYPE_CONTACT) {
            if (TextUtils.isEmpty(companyInfo)) {
                shortTip(getString(R.string.company_shop_contact_tip));
                return true;
            }
            if (TextUtils.equals(companyInfo, mInfo.getContact_person())) {
                finish();
                return true;
            }
            mInfo.setContact_person(companyInfo);
        } else if (type == CompanyDetailActivity.TYPE_CONTACT_TEL) {
            if (TextUtils.isEmpty(companyInfo)) {
                shortTip(getString(R.string.company_shop_contact_tel_tip));
                return true;
            }
            if (TextUtils.equals(companyInfo, mInfo.getContact_tel())) {
                finish();
                return true;
            }
            if (!RegexUtils.isChinaPhone(companyInfo) && !RegexUtils.isFixedPhone(companyInfo)) {
                shortTip(getString(R.string.check_mobile_fixedphone_tip));
                return true;
            }
            mInfo.setContact_tel(companyInfo);
        } else if (type == CompanyDetailActivity.TYPE_EMAIL) {
            if (TextUtils.isEmpty(companyInfo)) {
                shortTip(getString(R.string.tip_input_company_email));
                return true;
            }
            if (TextUtils.equals(companyInfo, mInfo.getContact_email())) {
                finish();
                return true;
            }
            if (!RegexUtils.isEmail(companyInfo)) {
                shortTip(getString(R.string.tip_input_company_correct_email));
                return true;
            }
            mInfo.setContact_email(companyInfo);
        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(s.toString())) {
            titleBar.setRightTextViewColor(com.sunmi.assistant.R.color.colorText_40);
            titleBar.getRightTextView().setClickable(false);
        } else {
            titleBar.setRightTextViewColor(com.sunmi.assistant.R.color.colorText);
            titleBar.getRightTextView().setClickable(true);
        }
    }

    @Override
    public void onBackPressed() {
        String companyInfo = cetUserInfo.getText() == null ? null : cetUserInfo.getText().toString().trim();
        if (type == CompanyDetailActivity.TYPE_NAME &&
                TextUtils.equals(mInfo.getCompany_name(), companyInfo)
                || type == CompanyDetailActivity.TYPE_CONTACT &&
                TextUtils.equals(mInfo.getContact_person(), companyInfo)
                || type == CompanyDetailActivity.TYPE_CONTACT_TEL &&
                TextUtils.equals(mInfo.getContact_tel(), companyInfo)
                || type == CompanyDetailActivity.TYPE_EMAIL &&
                TextUtils.equals(mInfo.getContact_email(), companyInfo)) {
            super.onBackPressed();
            return;
        }
        DialogUtils.isCancelSetting(this);
    }
}
