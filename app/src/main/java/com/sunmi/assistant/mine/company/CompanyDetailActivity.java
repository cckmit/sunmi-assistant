package com.sunmi.assistant.mine.company;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.sunmi.assistant.R;
import com.sunmi.assistant.ui.activity.login.LoginChooseShopActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import sunmi.common.base.BaseActivity;
import sunmi.common.constant.CommonConstants;
import sunmi.common.model.CompanyInfoResp;
import sunmi.common.model.CompanyListResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.SettingItemLayout;

import static sunmi.common.utils.CommonHelper.isGooglePlay;

/**
 * 我的商户
 *
 * @author yangshijie
 */
@SuppressLint("Registered")
@EActivity(R.layout.activity_mine_company_detatils)
public class CompanyDetailActivity extends BaseActivity {

    public static final int TYPE_NAME = 0;
    public static final int TYPE_CONTACT = 1;
    public static final int TYPE_CONTACT_TEL = 2;
    public static final int TYPE_EMAIL = 3;
    static final String INTENT_EXTRA_NAME = "company_name";
    static final String INTENT_EXTRA_CONTACT = "company_contact";
    static final String INTENT_EXTRA_CONTACT_TEL = "company_contact_tel";
    static final String INTENT_EXTRA_EMAIL = "company_email";
    private static final int REQUEST_CODE_NAME = 100;
    private static final int REQUEST_CODE_CONTACT = 101;
    private static final int REQUEST_CODE_CONTACT_TEL = 102;
    private static final int REQUEST_CODE_EMAIL = 103;

    @ViewById(R.id.sil_company_name)
    SettingItemLayout silCompanyName;
    @ViewById(R.id.sil_company_id)
    SettingItemLayout silCompanyId;
    @ViewById(R.id.sil_company_create_time)
    SettingItemLayout silCompanyCreateTime;
    @ViewById(R.id.sil_company_contact)
    SettingItemLayout silCompanyContact;
    @ViewById(R.id.sil_company_contact_tel)
    SettingItemLayout silCompanyContactTel;
    @ViewById(R.id.sil_company_email)
    SettingItemLayout silCompanyEmail;
    @ViewById(R.id.rl_company_switch)
    RelativeLayout rlCompanySwitch;

    private CompanyInfoResp mCompanyInfo;

    @AfterViews
    protected void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        if (!isGooglePlay()) {
            silCompanyContactTel.setVisibility(View.VISIBLE);
        }
        setSingleLine();
        silCompanyId.setRightImage(null);
        silCompanyCreateTime.setRightImage(null);
        companyList();
        getCompanyInfo();
    }

    private void setSingleLine() {
        silCompanyName.getRightText().setSingleLine();
        silCompanyName.getRightText().setEllipsize(TextUtils.TruncateAt.END);
        silCompanyContact.getRightText().setSingleLine();
        silCompanyContact.getRightText().setEllipsize(TextUtils.TruncateAt.END);
        silCompanyContactTel.getRightText().setSingleLine();
        silCompanyContactTel.getRightText().setEllipsize(TextUtils.TruncateAt.END);
        silCompanyEmail.getRightText().setSingleLine();
        silCompanyEmail.getRightText().setEllipsize(TextUtils.TruncateAt.END);
    }

    private String createTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date(time));
    }

    private void companyList() {
        SunmiStoreApi.getInstance().getCompanyList(new RetrofitCallback<CompanyListResp>() {
            @Override
            public void onSuccess(int code, String msg, CompanyListResp data) {
                if (data.getCompany_list().size() == 1) {
                    rlCompanySwitch.setVisibility(View.GONE);
                } else {
                    rlCompanySwitch.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFail(int code, String msg, CompanyListResp data) {
            }
        });
    }

    private void getCompanyInfo() {
        showLoadingDialog();
        SunmiStoreApi.getInstance().getCompanyInfo(SpUtils.getCompanyId(), new RetrofitCallback<CompanyInfoResp>() {
            @Override
            public void onSuccess(int code, String msg, CompanyInfoResp mInfo) {
                hideLoadingDialog();
                mCompanyInfo = mInfo;
                silCompanyName.setRightText(mInfo.getCompany_name());
                silCompanyId.setRightText(mInfo.getCompany_id() + "");
                silCompanyCreateTime.setRightText(createTime(mInfo.getCreated_time() * 1000));
                silCompanyContact.setRightText(mInfo.getContact_person());
                silCompanyContactTel.setRightText(mInfo.getContact_tel());
                silCompanyEmail.setRightText(mInfo.getContact_email());
            }

            @Override
            public void onFail(int code, String msg, CompanyInfoResp data) {
                LogCat.e(TAG, "Get shop info Failed. " + msg);
                hideLoadingDialog();
                shortTip(R.string.toast_network_Exception);
            }
        });
    }

    @Click(R.id.rl_company_switch)
    public void toChangeCompany() {
        LoginChooseShopActivity_.intent(context)
                .action(CommonConstants.ACTION_CHANGE_COMPANY)
                .isLoginSuccessSwitchCompany(true)
                .start();
    }

    @Click(R.id.sil_company_name)
    public void toModifyName() {
        if (mCompanyInfo == null) {
            return;
        }
        CompanyUpdateActivity_.intent(this).mInfo(mCompanyInfo)
                .type(TYPE_NAME)
                .startForResult(REQUEST_CODE_NAME);
    }

    @Click(R.id.sil_company_contact)
    public void toModifyContact() {
        if (mCompanyInfo == null) {
            return;
        }
        CompanyUpdateActivity_.intent(this).mInfo(mCompanyInfo)
                .type(TYPE_CONTACT)
                .startForResult(REQUEST_CODE_CONTACT);
    }

    @Click(R.id.sil_company_contact_tel)
    public void toModifyContactTel() {
        if (mCompanyInfo == null) {
            return;
        }
        CompanyUpdateActivity_.intent(this).mInfo(mCompanyInfo)
                .type(TYPE_CONTACT_TEL)
                .startForResult(REQUEST_CODE_CONTACT_TEL);
    }

    @Click(R.id.sil_company_email)
    public void toModifyEmail() {
        if (mCompanyInfo == null) {
            return;
        }
        CompanyUpdateActivity_.intent(this).mInfo(mCompanyInfo)
                .type(TYPE_EMAIL)
                .startForResult(REQUEST_CODE_EMAIL);
    }

    @OnActivityResult(REQUEST_CODE_NAME)
    void onNameResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            mCompanyInfo.setCompany_name(data.getStringExtra(INTENT_EXTRA_NAME));
            silCompanyName.setRightText(mCompanyInfo.getCompany_name());
        }
    }

    @OnActivityResult(REQUEST_CODE_CONTACT)
    void onContactResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            mCompanyInfo.setContact_person(data.getStringExtra(INTENT_EXTRA_CONTACT));
            silCompanyContact.setRightText(mCompanyInfo.getContact_person());
        }
    }

    @OnActivityResult(REQUEST_CODE_CONTACT_TEL)
    void onContactTelResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            mCompanyInfo.setContact_tel(data.getStringExtra(INTENT_EXTRA_CONTACT_TEL));
            silCompanyContactTel.setRightText(mCompanyInfo.getContact_tel());
        }
    }

    @OnActivityResult(REQUEST_CODE_EMAIL)
    void onEmailResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            mCompanyInfo.setContact_email(data.getStringExtra(INTENT_EXTRA_EMAIL));
            silCompanyEmail.setRightText(mCompanyInfo.getContact_email());
        }
    }

}
