package com.sunmi.assistant.ui.activity.merchant;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;

import com.sunmi.apmanager.utils.CommonUtils;
import com.sunmi.apmanager.utils.SomeMonitorEditText;
import com.sunmi.assistant.R;
import com.sunmi.assistant.ui.activity.contract.CreateShopContract;
import com.sunmi.assistant.ui.activity.presenter.CreateShopPresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.nio.charset.Charset;
import java.util.Objects;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.model.CreateShopInfo;
import sunmi.common.utils.GotoActivityUtils;
import sunmi.common.utils.RegexUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.ClearableEditText;

/**
 * @author yangShiJie
 * @date 2019/7/31
 */
@SuppressLint("Registered")
@EActivity(R.layout.company_activity_shop_create)
public class CreateShopActivity extends BaseMvpActivity<CreateShopPresenter>
        implements CreateShopContract.View {
    private static final int COMPANY_NAME_MAX_LENGTH = 20;
    private static final int COMPANY_STR_MAX_LENGTH = 36;
    @ViewById(R.id.et_shop)
    ClearableEditText etShop;
    @ViewById(R.id.et_contact)
    ClearableEditText etContact;
    @ViewById(R.id.et_mobile)
    ClearableEditText etMobile;
    @ViewById(R.id.btn_complete)
    Button btnComplete;
    @Extra
    int companyId;
    @Extra
    boolean isLoginSuccess;

    private String shopName;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        mPresenter = new CreateShopPresenter();
        mPresenter.attachView(this);
        new SomeMonitorEditText().setMonitorEditText(btnComplete, etShop);
        shopAddTextChangedListener(etShop);
    }

    @Click(R.id.btn_complete)
    void completeClick() {
        String contact = Objects.requireNonNull(etContact.getText()).toString().trim();
        String mobile = Objects.requireNonNull(etMobile.getText()).toString().trim();
        if (!TextUtils.isEmpty(mobile) && !RegexUtils.isChinaPhone(mobile)) {
            shortTip(getString(R.string.company_shop_check_mobile));
            return;
        }
        mPresenter.createShop(shopName, contact, mobile);
    }

    @Override
    public void createShopSuccessView(CreateShopInfo resp) {
        shortTip(R.string.company_create_success);
        if (isLoginSuccess) {
            //登录成功后设置--创建门店
            finish();
        } else {
            //初始化创建门店
            CommonUtils.saveSelectShop(resp.getShop_id(), resp.getShop_name());
            GotoActivityUtils.gotoMainActivity(this);
        }
    }

    private void shopAddTextChangedListener(ClearableEditText editText) {
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
                shopName = s.toString().trim();
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
