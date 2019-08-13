package com.sunmi.assistant.mine.shop;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.sunmi.apmanager.utils.SomeMonitorEditText;
import com.sunmi.assistant.R;
import com.sunmi.assistant.utils.GetUserInfoUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.nio.charset.Charset;
import java.util.Objects;

import sunmi.common.base.BaseActivity;
import sunmi.common.model.CreateShopInfo;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.RegexUtils;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.ClearableEditText;
import sunmi.common.view.TitleBarView;

/**
 * @author yangShiJie
 * @date 2019/7/31
 */
@SuppressLint("Registered")
@EActivity(R.layout.company_activity_shop_create)
public class CreateShopActivity extends BaseActivity {

    private static final int CREATE_SHOP_ALREADY_EXIST = 5035;

    private static final int COMPANY_NAME_MAX_LENGTH = 20;
    private static final int COMPANY_STR_MAX_LENGTH = 20;

    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
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
    private String shopName;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        new SomeMonitorEditText().setMonitorEditText(btnComplete, etShop);
        shopAddTextChangedListener(etShop);
        if (!SpUtils.isLoginSuccess()) {
            titleBar.setLeftImageVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (!SpUtils.isLoginSuccess()) {
            return;
        }
        super.onBackPressed();
    }

    @Click(R.id.btn_complete)
    void completeClick() {
        String contact = Objects.requireNonNull(etContact.getText()).toString().trim();
        String mobile = Objects.requireNonNull(etMobile.getText()).toString().trim();
        if (!TextUtils.isEmpty(mobile) && !RegexUtils.isChinaPhone(mobile)) {
            shortTip(getString(R.string.company_shop_check_mobile));
            return;
        }
        showLoadingDialog();
        SunmiStoreApi.createShop(SpUtils.getCompanyId(), shopName, contact, mobile,
                new RetrofitCallback<CreateShopInfo>() {
                    @Override
                    public void onSuccess(int code, String msg, CreateShopInfo data) {
                        hideLoadingDialog();
                        createShopSuccessView(data);
                    }

                    @Override
                    public void onFail(int code, String msg, CreateShopInfo data) {
                        hideLoadingDialog();
                        LogCat.e(TAG, "getSaas  Failed code=" + code + "; msg=" + msg);
                        if (code == CREATE_SHOP_ALREADY_EXIST) {
                            shortTip(R.string.str_create_store_alredy_exit);
                        } else {
                            shortTip(R.string.str_create_store_fail);
                        }
                    }
                });
    }

    private void createShopSuccessView(CreateShopInfo resp) {
        shortTip(R.string.company_create_success);
        if (SpUtils.isLoginSuccess()) {
            finish();
        } else {
            SpUtils.setShopId(resp.getShop_id());
            SpUtils.setShopName(resp.getShop_name());
            GetUserInfoUtils.userInfo(this);
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
