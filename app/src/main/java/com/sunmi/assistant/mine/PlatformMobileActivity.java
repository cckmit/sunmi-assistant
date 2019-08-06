package com.sunmi.assistant.mine;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sunmi.apmanager.config.AppConfig;
import com.sunmi.apmanager.utils.SomeMonitorEditText;
import com.sunmi.assistant.R;
import com.sunmi.assistant.ui.activity.merchant.AuthDialog;
import com.sunmi.assistant.ui.activity.merchant.CommonSaasUtils;
import com.sunmi.assistant.ui.activity.model.AuthStoreInfo;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.utils.GotoActivityUtils;
import sunmi.common.utils.SpUtils;
import sunmi.common.view.ClearableEditText;
import sunmi.common.view.dialog.CommonDialog;

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
                .setAllowButton((dialog, which) ->
                        SelectStoreActivity_.intent(this)
                                .isBack(true)
                                .list(target)
                                .start())
                .setCancelButton((dialog, which) ->
                        GotoActivityUtils.gotoMainActivity(PlatformMobileActivity.this))
                .create().show();
    }

    private void showCreateShopDialog() {
        new CommonDialog.Builder(this).setTitle(getString(R.string.str_dialog_auto_create_store))
                .setCancelButton(com.sunmi.apmanager.R.string.sm_cancel)
                .setConfirmButton(R.string.company_shop_new_create, (dialog, which) ->
                        CommonSaasUtils.gotoCreateShopActivity(context, SpUtils.getCompanyId())).create().show();
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
