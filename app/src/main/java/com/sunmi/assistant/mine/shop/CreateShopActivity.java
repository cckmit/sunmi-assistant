package com.sunmi.assistant.mine.shop;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.sunmi.apmanager.utils.SomeMonitorEditText;
import com.sunmi.assistant.R;
import com.sunmi.assistant.utils.GetUserInfoUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.CreateShopInfo;
import sunmi.common.notification.BaseNotification;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.GotoActivityUtils;
import sunmi.common.utils.RegexUtils;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.ClearableEditText;
import sunmi.common.view.TextLengthWatcher;
import sunmi.common.view.TitleBarView;

/**
 * @author yangShiJie
 * @date 2019/7/31
 */
@SuppressLint("Registered")
@EActivity(R.layout.company_activity_shop_create)
public class CreateShopActivity extends BaseActivity {

    private static final int CREATE_SHOP_ALREADY_EXIST = 5035;
    private static final int SHOP_NAME_MAX_LENGTH = 20;

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
    @ViewById(R.id.rl_mobile)
    RelativeLayout rlMobile;
    @Extra
    int companyId;
    @Extra
    String companyName;
    @Extra
    int saasExist;
    @Extra
    boolean isLoginSuccessSwitchCompany;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        if (!CommonHelper.isGooglePlay()) {
            rlMobile.setVisibility(View.VISIBLE);
        }
        new SomeMonitorEditText().setMonitorEditText(btnComplete, etShop);
        etShop.addTextChangedListener(new TextLengthWatcher(etShop, SHOP_NAME_MAX_LENGTH) {
            @Override
            public void onLengthExceed(EditText view, String content) {
                shortTip(R.string.company_create_shop_max_length);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Click(R.id.btn_complete)
    void completeClick() {
        String shopName = etShop.getText() == null ? null : etShop.getText().toString().trim();
        String contact = etContact.getText() == null ? null : etContact.getText().toString().trim();
        String mobile = etMobile.getText() == null ? null : etMobile.getText().toString().trim();
        if (!TextUtils.isEmpty(mobile) && !RegexUtils.isCorrectAccount(mobile)) {
            if (CommonHelper.isGooglePlay()) {
                shortTip(R.string.str_invalid_email);
            } else {
                shortTip(getString(R.string.str_invalid_phone));
            }
            return;
        }
        showLoadingDialog();
        SunmiStoreApi.getInstance().createShop(companyId, shopName, contact, mobile,
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
        shortTip(getString(R.string.company_shop_create_success));
        if (SpUtils.isLoginSuccess()) {
            BaseNotification.newInstance().postNotificationName(CommonNotifications.shopCreate);
            if (isLoginSuccessSwitchCompany) {
                CommonHelper.saveCompanyShopInfo(companyId, companyName, saasExist, resp.getShop_id(), resp.getShop_name());
                GotoActivityUtils.gotoMainActivity(context);
            } else {
                setResult(RESULT_OK);
            }
            finish();
        } else {
            GetUserInfoUtils.userInfo(this, companyId, companyName, saasExist, resp.getShop_id(), resp.getShop_name());
        }
    }

}
