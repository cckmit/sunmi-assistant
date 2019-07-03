package com.sunmi.assistant.ui.activity.merchant;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sunmi.apmanager.config.AppConfig;
import com.sunmi.apmanager.utils.SomeMonitorEditText;
import com.sunmi.assistant.R;
import com.sunmi.assistant.ui.activity.MainActivity_;
import com.sunmi.assistant.ui.activity.contract.PlatformMobileContract;
import com.sunmi.assistant.ui.activity.model.AuthStoreInfo;
import com.sunmi.assistant.ui.activity.model.CreateStoreInfo;
import com.sunmi.assistant.ui.activity.presenter.PlatformMobilePresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.ClearableEditText;
import sunmi.common.view.dialog.CommonDialog;

/**
 * Created by YangShiJie on 2019/6/27.
 */
@SuppressLint("Registered")
@EActivity(R.layout.activity_merchant_check_mobile)
public class CheckPlatformMobileActivity extends BaseMvpActivity<PlatformMobilePresenter>
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
    //倒计时对象,总共的时间,每隔多少秒更新一次时间
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
        String mobile = etMobile.getText().toString();
        switch (v.getId()) {
            case R.id.tv_get_code:
                if (TextUtils.isEmpty(mobile)) {
                    shortTip(R.string.str_please_input_mobile);
                    return;
                }
                startDownTimer();
                sendSmsCode(mobile);
                break;
            case R.id.btn_check:
                String code = etCode.getText().toString();
                if (TextUtils.isEmpty(mobile)) {
                    shortTip(R.string.str_please_input_mobile);
                    return;
                }
                if (TextUtils.isEmpty(code)) {
                    shortTip(R.string.str_please_input_sms_code);
                    return;
                }
                checkSmsCode(mobile, code);
                break;
        }
    }

    //发送验证码
    private void sendSmsCode(String mobile) {
        mPresenter.sendMobileCode(mobile);
    }

    //验证码
    private void checkSmsCode(String mobile, String code) {
        showLoadingDialog();
        mPresenter.checkMobileCode(mobile, code);
    }

    //通过手机号获取saas信息
    private void getSaasInfo(String mobile) {
        mPresenter.getSaasInfo(mobile);
    }

    //默认创建门店
    public void createStore() {
        showLoadingDialog();
        mPresenter.createStore(String.format(getString(R.string.str_unkunw_store), SpUtils.getMobile()));
    }

    /**
     * 发送验证码
     *
     * @param data
     */
    @Override
    public void sendMobileCodeSuccess(String data) {

    }

    @Override
    public void sendMobileCodeFail(int code, String msg) {

    }

    /**
     * 校验验证码
     *
     * @param data
     */
    @Override
    public void checkMobileCodeSuccess(Object data) {
        getSaasInfo(etMobile.getText().toString());
    }

    @Override
    public void checkMobileCodeFail(int code, String msg) {
        LogCat.e(TAG, "data checkMobileCodeFail onFail code=" + code + "," + msg);
        hideLoadingDialog();
        shortTip(R.string.str_platform_sms_code_error);
    }

    /**
     * 获取saas信息
     *
     * @param data
     */
    @Override
    public void getSaasInfoSuccess(Object data) {
        hideLoadingDialog();
        AuthStoreInfo bean = new Gson().fromJson(data.toString(), AuthStoreInfo.class);
        getSaasData(bean.getSaas_user_info_list());
    }

    @Override
    public void getSaasInfoFail(int code, String msg) {
        LogCat.e(TAG, "data getSaasInfoFail onFail code=" + code + "," + msg);
        hideLoadingDialog();
    }

    /**
     * 创建门店
     *
     * @param data
     */
    @Override
    public void createStoreSuccess(CreateStoreInfo data) {
        hideLoadingDialog();
        gotoMainActivity();
    }

    @Override
    public void createStoreFail(int code, String msg) {
        LogCat.e(TAG, "data onFail code=" + code + "," + msg);
        hideLoadingDialog();
        gotoMainActivity();
    }

    private void getSaasData(List<AuthStoreInfo.SaasUserInfoListBean> list) {
        if (list.size() > 0) {  //匹配到平台数据
            StringBuilder saasName = new StringBuilder();
            if (list.size() == 1) {
                saasName.append(list.get(0).getSaas_name());
            } else {
                for (AuthStoreInfo.SaasUserInfoListBean bean : list) {
                    saasName.append(bean.getSaas_name()).append(",");
                }
            }
            new AuthDialog.Builder(this)
                    .setMessage(getString(R.string.str_dialog_auth_message, saasName))
                    .setAllowButton((dialog, which) -> SelectStoreActivity_.intent(this)
                            .list((ArrayList) list)
                            .start())
                    .setCancelButton((dialog, which) -> {
                        createStore();
                    })
                    .create().show();
        } else { //未匹配平台数据
            createStoreDialog();
        }
    }

    private void createStoreDialog() {
        new CommonDialog.Builder(this).setTitle(getString(R.string.str_dialog_auto_create_store))
                .setCancelButton(com.sunmi.apmanager.R.string.sm_cancel)
                .setConfirmButton(R.string.str_button_auto_create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        createStore();
                    }
                }).create().show();
    }

    private void gotoMainActivity() {
        MainActivity_.intent(context).start();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
    }

    //初始化启动倒计时
    private void startDownTimer() {
        mTimer.start();
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
            cancelTimer();
            tvGetCode.setTextColor(getResources().getColor(R.color.common_orange));
            tvGetCode.setText(getResources().getString(R.string.str_resend));//重新给Button设置文字
            tvGetCode.setClickable(true);
        }
    }

    // 登陆成功后取消计时操作
    private void cancelTimer() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTimer.cancel();
                tvGetCode.setText(getResources().getString(R.string.str_resend));
                tvGetCode.setClickable(true);
            }
        });
    }
}
