package com.sunmi.assistant.importorder;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sunmi.apmanager.config.AppConfig;
import com.sunmi.apmanager.utils.SomeMonitorEditText;
import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.contract.PlatformMobileContract;
import com.sunmi.assistant.mine.presenter.PlatformMobilePresenter;
import com.sunmi.assistant.ui.activity.merchant.AuthDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.model.AuthStoreInfo;
import sunmi.common.model.PlatformInfo;
import sunmi.common.view.ClearableEditText;
import sunmi.common.view.dialog.CommonDialog;

/**
 * @author YangShiJie
 * @date 2019/10/14
 */
@SuppressLint("Registered")
@EActivity(R.layout.activity_merchant_check_mobile)
public class ImportOrderPlatformMobileActivity extends BaseMvpActivity<PlatformMobilePresenter>
        implements PlatformMobileContract.View {

    /**
     * 倒计时对象,总共的时间,每隔多少秒更新一次时间
     */
    final MyCountDownTimer mTimer = new MyCountDownTimer(AppConfig.SMS_TIME, 1000);
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
    PlatformInfo.SaasListBean selectPlatformBean;

    private boolean isTimerFinish;

    @AfterViews
    void init() {
        new SomeMonitorEditText().setMonitorEditText(btnCheck, etMobile, etCode);
        tvSelectPlatform.setText(getString(R.string.import_order_platform_mobile_search_data, selectPlatformBean.getSaas_name()));
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
                mPresenter.checkMobileCode(mobile, code, selectPlatformBean.getSaas_source());
                break;
            default:
        }
    }

    @Override
    public void showAuthDialog(ArrayList<AuthStoreInfo.SaasUserInfoListBean> list) {
        if (list.size() > 0) {
            showSelectShopDialog(list);
        } else {
            matchNoShopDialog();
        }
    }

    private void showSelectShopDialog(ArrayList<AuthStoreInfo.SaasUserInfoListBean> target) {
        new AuthDialog.Builder(this)
                .setMessage(getString(R.string.str_dialog_auth_message, selectPlatformBean.getSaas_name()))
                .setAllowButton((dialog, which) -> ImportOrderSelectShopActivity_.intent(context)
                        .list(target)
                        .start())
                .setCancelButton((dialog, which) -> dialog.dismiss())
                .create().show();
    }

    private void matchNoShopDialog() {
        new CommonDialog.Builder(this)
                .setTitle(R.string.import_order_check_mobile_no_data)
                .setCancelButton(R.string.str_retry, (dialog, which) -> {
                    dialog.dismiss();
                    if (!isTimerFinish) {
                        stopDownTimer();
                    }
                })
                .create().show();
    }

    private void startDownTimer() {
        isTimerFinish = false;
        mTimer.start();
    }

    @UiThread
    void stopDownTimer() {
        isTimerFinish = true;
        mTimer.cancel();
        tvGetCode.setText(getResources().getString(R.string.str_resend));
        tvGetCode.setClickable(true);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopDownTimer();
    }

    //倒计时函数
    private class MyCountDownTimer extends CountDownTimer {
        MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        //计时过程
        @Override
        public void onTick(long l) {
            tvGetCode.setClickable(false);
            tvGetCode.setTextColor(ContextCompat.getColor(context, R.color.common_orange_60a));
            tvGetCode.setText(String.format(getString(R.string.str_count_down_second), l / 1000));
        }

        //计时完毕的方法
        @Override
        public void onFinish() {
            stopDownTimer();
            tvGetCode.setTextColor(ContextCompat.getColor(context, R.color.common_orange));
            tvGetCode.setText(getResources().getString(R.string.str_resend));
            tvGetCode.setClickable(true);
        }
    }
}
