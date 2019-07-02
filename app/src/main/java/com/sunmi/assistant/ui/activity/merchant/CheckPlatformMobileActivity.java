package com.sunmi.assistant.ui.activity.merchant;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sunmi.apmanager.config.AppConfig;
import com.sunmi.apmanager.utils.SomeMonitorEditText;
import com.sunmi.assistant.R;
import com.sunmi.assistant.rpc.CloudCall;
import com.sunmi.assistant.ui.activity.MainActivity_;
import com.sunmi.assistant.ui.activity.model.AuthStoreInfo;
import com.sunmi.assistant.ui.activity.model.CreateStoreInfo;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.Serializable;
import java.util.List;

import sunmi.common.base.BaseActivity;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.dialog.CommonDialog;

/**
 * Created by YangShiJie on 2019/6/27.
 */
@SuppressLint("Registered")
@EActivity(R.layout.activity_merchant_check_mobile)
public class CheckPlatformMobileActivity extends BaseActivity {
    @ViewById(R.id.tv_select_platform)
    TextView tvSelectPlatform;
    @ViewById(R.id.et_mobile)
    EditText etMobile;
    @ViewById(R.id.et_code)
    EditText etCode;
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
    }


    @Click({R.id.tv_get_code, R.id.btn_check})

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_get_code:
                String mobile = etMobile.getText().toString();
                if (TextUtils.isEmpty(mobile)) {
                    shortTip(R.string.str_please_input_mobile);
                    return;
                }
                startDownTimer();
                sendSmsCode();
                break;
            case R.id.btn_check:
                String code = etCode.getText().toString();
                checkSmsCode(code);
                break;
        }
    }

    //发送验证码
    private void sendSmsCode() {
        CloudCall.sendSaasVerifyCode(SpUtils.getMobile(), new RetrofitCallback() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
            }

            @Override
            public void onFail(int code, String msg, Object data) {
            }
        });
    }

    //验证码
    private void checkSmsCode(String code) {
        showLoadingDialog();
        CloudCall.confirmSaasVerifyCode(SpUtils.getMobile(), code, new RetrofitCallback() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                getSaasInfo();//当验证匹配成功
            }

            @Override
            public void onFail(int code, String msg, Object data) {
                LogCat.e(TAG, "data onFail code=" + code + "," + msg);
                hideLoadingDialog();
                shortTip(R.string.str_platform_sms_code_error);
            }
        });
    }

    //通过手机号获取saas信息
    private void getSaasInfo() {
        CloudCall.getSaasUserInfo(SpUtils.getMobile(), new RetrofitCallback() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                hideLoadingDialog();
                AuthStoreInfo bean = new Gson().fromJson(data.toString(), AuthStoreInfo.class);
                getSaasData(bean.getSaasUserInfoList());
            }

            @Override
            public void onFail(int code, String msg, Object data) {
                LogCat.e(TAG, "data onFail code=" + code + "," + msg);
                hideLoadingDialog();
            }
        });
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
                            .extra("list", (Serializable) list)
                            .start())
                    .setCancelButton((dialog, which) -> {
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

    //默认创建门店
    public void createStore() {
        showLoadingDialog();
        CloudCall.createShop(SpUtils.getCompanyId() + "", String.format(getString(R.string.str_unkunw_store), SpUtils.getMobile()), new RetrofitCallback<CreateStoreInfo>() {
            @Override
            public void onSuccess(int code, String msg, CreateStoreInfo data) {
                hideLoadingDialog();
                gotoMainActivity();
            }

            @Override
            public void onFail(int code, String msg, CreateStoreInfo data) {
                LogCat.e(TAG, "data onFail code=" + code + "," + msg);
                hideLoadingDialog();
                gotoMainActivity();
            }
        });
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
