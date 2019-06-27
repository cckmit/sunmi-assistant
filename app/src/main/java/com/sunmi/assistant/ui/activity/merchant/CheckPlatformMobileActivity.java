package com.sunmi.assistant.ui.activity.merchant;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sunmi.apmanager.config.AppConfig;
import com.sunmi.apmanager.utils.SomeMonitorEditText;
import com.sunmi.assistant.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;

/**
 * Created by YangShiJie on 2019/6/27.
 */
@EActivity(R.layout.activity_merchant_check_mobile)
public class CheckPlatformMobileActivity extends BaseActivity {
    @ViewById(R.id.et_mobile)
    EditText etMobile;
    @ViewById(R.id.et_code)
    EditText etCode;
    @ViewById(R.id.tv_get_code)
    TextView tvGetCode;
    @ViewById(R.id.btn_check)
    Button btnCheck;
    //倒计时对象,总共的时间,每隔多少秒更新一次时间
    final MyCountDownTimer mTimer = new MyCountDownTimer(AppConfig.SMS_TIME, 1000);

    @AfterViews
    void init() {
        new SomeMonitorEditText().setMonitorEditText(btnCheck, etMobile, etCode);
    }

    @Click({R.id.tv_get_code, R.id.btn_check})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_get_code:
                startDownTimer();
                break;
            case R.id.btn_check:
                new AuthDialog.Builder(this)
                        .setMessage("申请获取您在客无忧sass平台、收银专家sass平台、XXXX平台的门店数据。")
                        .setAllowButton((dialog, which) -> SelectStoreActivity_.intent(this).start())
                        .setCancelButton((dialog, which) -> {
                        })
                        .create().show();
                break;
        }
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
