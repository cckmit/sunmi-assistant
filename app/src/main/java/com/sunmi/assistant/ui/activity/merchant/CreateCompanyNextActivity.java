package com.sunmi.assistant.ui.activity.merchant;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.sunmi.apmanager.utils.SomeMonitorEditText;
import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.shop.CreateShopPreviewActivity_;
import com.sunmi.assistant.ui.activity.contract.CreateCompanyContract;
import com.sunmi.assistant.ui.activity.presenter.CreateCompanyPresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.model.CompanyInfoResp;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.ClearableEditText;
import sunmi.common.view.TextLengthWatcher;
import sunmi.common.view.TitleBarView;

/**
 * @author yangShiJie
 * @date 2019/7/31
 */
@SuppressLint("Registered")
@EActivity(R.layout.company_activity_create_next)
public class CreateCompanyNextActivity extends BaseMvpActivity<CreateCompanyPresenter>
        implements CreateCompanyContract.View {

    private static final int COMPANY_NAME_MAX_LENGTH = 40;

    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.et_company)
    ClearableEditText etCompany;
    @ViewById(R.id.btn_create_company)
    Button btnCreateCompany;
    @Extra
    boolean createCompanyCannotBack;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        new SomeMonitorEditText().setMonitorEditText(btnCreateCompany, etCompany);
        mPresenter = new CreateCompanyPresenter();
        mPresenter.attachView(this);
        etCompany.addTextChangedListener(new TextLengthWatcher(etCompany, COMPANY_NAME_MAX_LENGTH) {
            @Override
            public void onLengthExceed(EditText view, String content) {
                shortTip(R.string.company_create_check_length);
            }
        });
    }

    @Click(R.id.btn_create_company)
    void newCreateCompany() {
        String companyName = etCompany.getText() == null ? null : etCompany.getText().toString().trim();
        if (TextUtils.isEmpty(companyName)) {
            return;
        }
        mPresenter.createCompany(companyName);
    }

    @Override
    public void createCompanySuccessView(CompanyInfoResp resp) {
        shortTip(R.string.company_create_success);
        //CommonUtils.saveSelectCompany(resp.getCompany_id(), resp.getCompany_name(), resp.getSaas_exist());
        CreateShopPreviewActivity_.intent(context)
                .companyId(resp.getCompany_id())
                .companyName(resp.getCompany_name())
                .saasExist(resp.getSaas_exist())
                .start();
    }

    @Override
    public void createCompanyFailView(int code, String msg) {
        if (code == -1) {
            if (msg.contains("invalid character")) {
                shortTip(getString(R.string.company_create_invalid_character));
            } else {
                shortTip(R.string.company_create_fail);
            }
        } else {
            shortTip(R.string.company_create_check_name);
        }
    }
}