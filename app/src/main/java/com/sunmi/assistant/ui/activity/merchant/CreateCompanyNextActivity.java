package com.sunmi.assistant.ui.activity.merchant;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import com.sunmi.assistant.R;
import com.sunmi.assistant.ui.activity.contract.CreateCompanyContract;
import com.sunmi.assistant.ui.activity.model.AuthStoreInfo;
import com.sunmi.assistant.ui.activity.presenter.CreateCompanyPresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.nio.charset.Charset;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.model.CompanyInfoResp;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.ClearableEditText;

/**
 * @author yangShiJie
 * @date 2019/7/31
 */
@SuppressLint("Registered")
@EActivity(R.layout.company_activity_create_next)
public class CreateCompanyNextActivity extends BaseMvpActivity<CreateCompanyPresenter>
        implements CreateCompanyContract.View {
    private static final int COMPANY_NAME_MAX_LENGTH = 20;
    private static final int COMPANY_STR_MAX_LENGTH = 36;
    @ViewById(R.id.et_company)
    ClearableEditText etCompany;
    private String companyName;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        mPresenter = new CreateCompanyPresenter();
        mPresenter.attachView(this);
        companyAddTextChangedListener(etCompany);
    }

    @Click(R.id.btn_create_company)
    void newCreateCompany() {
        mPresenter.createCompany(companyName);
    }

    @Override
    public void createCompanySuccessView(CompanyInfoResp resp) {
        LogCat.e(TAG, "resp=" + resp.getSaas_exist() + ", " + resp.getCompany_id() + " ," + resp.getContact_tel());
        shortTip(R.string.company_create_success);
        SpUtils.setCompanyId(resp.getCompany_id());
        SpUtils.setCompanyName(resp.getCompany_name());
        SpUtils.setSaasExist(resp.getSaas_exist());
        //无匹配saas数据
        if (resp.getSaas_exist() == 0) {
            new BottomDialog.Builder(CreateCompanyNextActivity.this)
                    .setMessage(getString(R.string.company_shop_new_create_or_import))
                    .setTopButton((dialog, which) -> {
                        //新建门店
                        CommonSaasUtils.gotoCreateShopActivity(context, resp.getCompany_id());
                    })
                    .setBottomButton((dialog, which) -> {
                        //导入门店
                        SelectPlatformActivity_.intent(context).start();
                    })
                    .create()
                    .show();
            return;
        }
        if (!TextUtils.isEmpty(resp.getContact_tel())) {
            mPresenter.getSaas(resp.getContact_tel());
        } else {
            mPresenter.getSaas(SpUtils.getMobile());
        }
    }

    @Override
    public void createCompanyFailView(int code, String msg) {
        if (code == -1) {
            shortTip(R.string.company_create_check_name);
        } else {
            shortTip(R.string.company_create_fail);
        }
    }

    //通过手机号获取saas信息
    @Override
    public void getSaasSuccessView(AuthStoreInfo bean) {
        CommonSaasUtils.getSaasData(context, bean.getSaas_user_info_list());
    }

    @Override
    public void getSaasFailView(int code, String msg) {

    }

    private void companyAddTextChangedListener(ClearableEditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            private int editStart;
            private int editEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                temp = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                companyName = s.toString().trim();
            }

            @Override
            public void afterTextChanged(Editable s) {
                editStart = editText.getSelectionStart();
                editEnd = editText.getSelectionEnd();
                if (temp.toString().trim().getBytes(Charset.defaultCharset()).length > COMPANY_NAME_MAX_LENGTH
                        || temp.length() > COMPANY_STR_MAX_LENGTH) {
                    s.delete(Math.max(editStart - 1, 0), editEnd);
                    shortTip(R.string.company_create_check_length);
                    int tempSelection = editStart;
                    editText.setText(s);
                    editText.setSelection(tempSelection);
                }
            }
        });
    }
}
