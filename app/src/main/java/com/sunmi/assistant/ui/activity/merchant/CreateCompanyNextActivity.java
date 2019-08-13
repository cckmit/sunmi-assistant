package com.sunmi.assistant.ui.activity.merchant;

import android.annotation.SuppressLint;
import android.widget.Button;
import android.widget.EditText;

import com.sunmi.apmanager.utils.CommonUtils;
import com.sunmi.apmanager.utils.SomeMonitorEditText;
import com.sunmi.assistant.R;
import com.sunmi.assistant.ui.activity.contract.CreateCompanyContract;
import com.sunmi.assistant.ui.activity.presenter.CreateCompanyPresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.model.AuthStoreInfo;
import sunmi.common.model.CompanyInfoResp;
import sunmi.common.utils.SpUtils;
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
        mPresenter.createCompany(companyName);
    }


    @Override
    public void createCompanySuccessView(CompanyInfoResp resp) {
        shortTip(R.string.company_create_success);
        CommonUtils.saveSelectCompany(resp.getCompany_id(), resp.getCompany_name(), resp.getSaas_exist());
        //获取saas数据
        mPresenter.getSaas(SpUtils.getMobile());
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

    /**
     * @param bean saas数据
     */
    @Override
    public void getSaasSuccessView(AuthStoreInfo bean) {
        CommonSaasUtils.getSaasData(context, bean.getSaas_user_info_list());
    }

    @Override
    public void getSaasFailView(int code, String msg) {
    }

}