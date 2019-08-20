package com.sunmi.assistant.mine.platform;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sunmi.apmanager.config.AppConfig;
import com.sunmi.apmanager.utils.SomeMonitorEditText;
import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.contract.PlatformMobileContract;
import com.sunmi.assistant.mine.presenter.PlatformMobilePresenter;
import com.sunmi.assistant.mine.shop.CreateShopActivity_;
import com.sunmi.assistant.ui.activity.merchant.AuthDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.model.AuthStoreInfo;
import sunmi.common.view.ClearableEditText;
import sunmi.common.view.dialog.CommonDialog;

import static com.sunmi.assistant.mine.shop.ShopListActivity.INTENT_EXTRA_SUCCESS;
import static com.sunmi.assistant.mine.shop.ShopListActivity.REQUEST_CODE_SHOP;

/**
 * @author YangShiJie
 * @date 2019/6/27
 */
@SuppressLint("Registered")
@EActivity(R.layout.activity_merchant_check_mobile)
public class PlatformMobileActivity extends BaseMvpActivity<PlatformMobilePresenter>
        implements PlatformMobileContract.View {

    @ViewById(R.id.tv_select_platform)
    TextView tvSelectPlatform;
    @ViewById(R.id.et_mobile)
    ClearableEditText etMobile;
    @ViewById(R.id.et_code)
    ClearableEditText etCode;
    @ViewById(R.id.tv_get_code)
    TextView tvGetCode;
    @ViewById(R.id.btn_check)
    Button btnCheck;

    @Extra
    String platform;
    @Extra
    int saasSource;
    @Extra
    int companyId;
    @Extra
    String companyName;
    @Extra
    int saasExist;

    /**
     * 倒计时对象,总共的时间,每隔多少秒更新一次时间
     */
    final MyCountDownTimer mTimer = new MyCountDownTimer(AppConfig.SMS_TIME, 1000);

    @AfterViews
    void init() {
        new SomeMonitorEditText().setMonitorEditText(btnCheck, etMobile, etCode);
        tvSelectPlatform.setText(getString(R.string.str_please_input_platform_mobile, platform));
        mPresenter = new PlatformMobilePresenter();
        mPresenter.attachView(this);
    }

    @Click({R.id.tv_get_code, R.id.btn_check})
    public void onClick(View v) {
        String mobile = etMobile.getText() == null ? null : etMobile.getText().toString().trim();
        switch (v.getId()) {
            case R.id.tv_get_code:
                if (TextUtils.isEmpty(mobile)) {
                    shortTip(R.string.str_please_input_mobile);
                    return;
                }
                startDownTimer();
                mPresenter.sendMobileCode(mobile);
                break;
            case R.id.btn_check:
                String code = etCode.getText() == null ? null : etCode.getText().toString().trim();
                if (TextUtils.isEmpty(mobile)) {
                    shortTip(R.string.str_please_input_mobile);
                    return;
                }
                if (TextUtils.isEmpty(code)) {
                    shortTip(R.string.str_please_input_sms_code);
                    return;
                }
                showLoadingDialog();
                mPresenter.checkMobileCode(mobile, code, saasSource);
                break;
            default:
        }
    }

    @Override
    public void showAuthDialog(ArrayList<AuthStoreInfo.SaasUserInfoListBean> list) {
        if (list.size() > 0) {
            showSelectShopDialog(list);
        } else {
            showCreateShopDialog();
        }
    }

    private void showSelectShopDialog(ArrayList<AuthStoreInfo.SaasUserInfoListBean> target) {
        new AuthDialog.Builder(this)
                .setMessage(getString(R.string.str_dialog_auth_message, platform))
                .setAllowButton(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SelectStoreActivity_.intent(context)
                                .isBack(true)
                                .list(target)
                                .companyId(companyId)
                                .companyName(companyName)
                                .saasExist(saasExist)
                                .startForResult(REQUEST_CODE_SHOP);
                    }
                })
                .setCancelButton((dialog, which) ->
                        gotoCreateShopActivity())
                .create().show();
    }

    private void showCreateShopDialog() {
        new CommonDialog.Builder(this).setTitle(getString(R.string.str_dialog_auto_create_store))
                .setCancelButton(com.sunmi.apmanager.R.string.sm_cancel)
                .setConfirmButton(R.string.company_shop_new_create, (dialog, which) ->
                        gotoCreateShopActivity())
                .create().show();
    }

    private void gotoCreateShopActivity() {
        CreateShopActivity_.intent(context)
                .companyId(companyId)
                .companyName(companyName)
                .saasExist(saasExist)
                .startForResult(REQUEST_CODE_SHOP);
    }

    private void startDownTimer() {
        mTimer.start();
    }

    @UiThread
    void stopDownTimer() {
        mTimer.cancel();
        tvGetCode.setText(getResources().getString(R.string.str_resend));
        tvGetCode.setClickable(true);
    }

    @OnActivityResult(REQUEST_CODE_SHOP)
    void onResult(int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null
                && data.getBooleanExtra(INTENT_EXTRA_SUCCESS, false)) {
            Intent intent = getIntent();
            intent.putExtra(INTENT_EXTRA_SUCCESS, true);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onFailSendMobileCode() {
        shortTip(R.string.toast_network_Exception);
        stopDownTimer();
    }

    @Override
    public void onFailCheckMobileCode() {
        shortTip(R.string.str_platform_sms_code_error);
    }

    //倒计时函数
    private class MyCountDownTimer extends CountDownTimer {
        MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        //计时过程
        @Override
        public void onTick(long l) {
            tvGetCode.setClickable(false);//防止计时过程中重复点击
            tvGetCode.setTextColor(getResources().getColor(R.color.common_orange_alpha));
            tvGetCode.setText(String.format(getString(R.string.str_count_down_second), l / 1000));
        }

        //计时完毕的方法
        @Override
        public void onFinish() {
            stopDownTimer();
            tvGetCode.setTextColor(getResources().getColor(R.color.common_orange));
            tvGetCode.setText(getResources().getString(R.string.str_resend));//重新给Button设置文字
            tvGetCode.setClickable(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopDownTimer();
    }


}
