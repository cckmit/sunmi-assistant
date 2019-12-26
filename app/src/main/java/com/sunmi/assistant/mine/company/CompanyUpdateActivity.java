package com.sunmi.assistant.mine.company;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.sunmi.apmanager.utils.DialogUtils;
import com.sunmi.apmanager.utils.HelpUtils;
import com.sunmi.assistant.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.CompanyInfoResp;
import sunmi.common.notification.BaseNotification;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.RegexUtils;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.SettingItemEditTextLayout;
import sunmi.common.view.TextLengthWatcher;
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
    private static final int CREATE_SHOP_ALREADY_EXIST = 5035;
    private static final int COMPANY_NAME_MAX_LENGTH = 40;
    private static final int EMAIL_MAX_LENGTH = 100;

    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.cet_username)
    SettingItemEditTextLayout cetUserInfo;

    @Extra
    CompanyInfoResp mInfo;
    @Extra
    int type;
    private String companyInfo;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        titleBar.setRightTextViewText(R.string.str_save);
        titleBar.setRightTextViewColor(R.color.text_main);
        titleBar.getLeftLayout().setOnClickListener(v -> onBackPressed());
        titleBar.getRightTextView().setOnClickListener(this);
        if (type == CompanyDetailActivity.TYPE_EMAIL) {
            cetUserInfo.getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(EMAIL_MAX_LENGTH)});
        } else {
            cetUserInfo.getEditText().addTextChangedListener(new TextLengthWatcher(cetUserInfo.getEditText(),
                    COMPANY_NAME_MAX_LENGTH) {
                @Override
                public void onLengthExceed(EditText view, String content) {
                    shortTip(getString(R.string.editetxt_max_length));
                }
            });
        }
        cetUserInfo.getEditText().addTextChangedListener(this);
        cetUserInfo.requestFocus();
        if (type == CompanyDetailActivity.TYPE_NAME) {
            initData(R.string.str_change_company_name, R.string.tip_input_company_name, mInfo.getCompany_name());
        } else if (type == CompanyDetailActivity.TYPE_CONTACT) {
            initData(R.string.str_change_company_contact, R.string.tip_input_company_contact, mInfo.getContact_person());
        } else if (type == CompanyDetailActivity.TYPE_CONTACT_TEL) {
            cetUserInfo.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);
            cetUserInfo.getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
            initData(R.string.str_change_company_tel, R.string.tip_input_company_tel, mInfo.getContact_tel());
        } else if (type == CompanyDetailActivity.TYPE_EMAIL) {
            initData(R.string.str_change_company_email, R.string.tip_input_company_email, mInfo.getContact_email());
        }
    }

    private void initData(int str_change_company_email, int tip_input_company_email, String contact_email) {
        titleBar.setAppTitle(getString(str_change_company_email));
        cetUserInfo.getEditText().setHint(tip_input_company_email);
        if (!TextUtils.isEmpty(contact_email)) {
            cetUserInfo.setEditTextText(contact_email);
            cetUserInfo.getEditText().setSelection(contact_email.length());
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
                    if (data.getCompany_id() == SpUtils.getCompanyId()) {
                        SpUtils.setCompanyName(data.getCompany_name());
                        BaseNotification.newInstance().postNotificationName(CommonNotifications.companyNameChanged);
                    }
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
                if (code == CREATE_SHOP_ALREADY_EXIST) {
                    shortTip(R.string.str_create_company_alredy_exit);
                } else {
                    shortTip(R.string.tip_save_fail);
                }
            }
        });

    }

    private boolean isUpdateMessage() {
        companyInfo = cetUserInfo.getEditTextText().trim();
        if (type == CompanyDetailActivity.TYPE_NAME) {
            if (TextUtils.isEmpty(companyInfo)) {
                shortTip(R.string.tip_input_company_name);
                return true;
            }
            if (HelpUtils.isContainEmoji(companyInfo)) {
                shortTip(getString(R.string.specital_text_cannot_support));
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
            if (HelpUtils.isContainEmoji(companyInfo)) {
                shortTip(getString(R.string.specital_text_cannot_support));
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
            if (!RegexUtils.isCorrectAccount(companyInfo)) {
                shortTip(getString(R.string.str_invalid_phone));
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
            titleBar.setRightTextViewColor(com.sunmi.assistant.R.color.text_caption);
            titleBar.getRightTextView().setClickable(false);
        } else {
            titleBar.setRightTextViewColor(com.sunmi.assistant.R.color.text_main);
            titleBar.getRightTextView().setClickable(true);
        }
    }

    @Override
    public void onBackPressed() {
        String companyInfo = cetUserInfo.getEditTextText().trim();
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
