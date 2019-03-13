package com.sunmi.assistant.ui.activity.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.sunmi.apmanager.config.AppConfig;
import com.sunmi.apmanager.ui.activity.ProtocolActivity;
import com.sunmi.apmanager.utils.BundleUtils;
import com.sunmi.apmanager.utils.HelpUtils;
import com.sunmi.apmanager.utils.SomeMonitorEditText;
import com.sunmi.assistant.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.utils.RegexUtils;
import sunmi.common.view.ClearableEditText;

@EActivity(R.layout.activity_register)
public class InputMobileActivity extends BaseActivity {

    @ViewById(R.id.etMobile)
    ClearableEditText etMobile;
    @ViewById(R.id.btnNext)
    Button btnNext;

    @ViewById(R.id.BtnIsSelected)
    ImageButton BtnIsSelected;

    private boolean isSelectedPro = true;//是否选择了协议

    @AfterViews
    protected void init() {
        HelpUtils.setStatusBarFullTransparent(this);//透明标题栏
        etMobile.setClearIcon(R.mipmap.ic_edit_delete_white);
        new SomeMonitorEditText().setMonitorEditText(btnNext, etMobile);
        //初始化
        BtnIsSelected.setBackgroundResource(R.mipmap.ic_selected_protocol);
        isSelectedPro = true;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String mobile = bundle.getString("mobile");
            if (!TextUtils.isEmpty(mobile)) {
                etMobile.setText(mobile);
            }
        }
    }

    @Click({R.id.BtnIsSelected, R.id.btnNext, R.id.rlUserProtocol, R.id.rlUserPrivate})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.BtnIsSelected://是否选择了协议
                if (isSelectedPro) {
                    BtnIsSelected.setBackgroundResource(R.mipmap.ic_normal_protocol);
                    isSelectedPro = false;
                } else {
                    BtnIsSelected.setBackgroundResource(R.mipmap.ic_selected_protocol);
                    isSelectedPro = true;
                }
                break;
            case R.id.btnNext://获取验证码
                if (isFastClick(1500)) return;
                String mobile = etMobile.getText().toString().trim();
                if (!isSelectedPro) {
                    Toast.makeText(this, getResources().getString(R.string.textView_tip_protocol), Toast.LENGTH_SHORT).show();
                    break;
                }
                if (!RegexUtils.isChinaPhone(mobile)) {
                    shortTip(R.string.str_invalid_phone);
                    return;
                }
                InputCaptchaActivity_.intent(context)
                        .extra("mobile", mobile)
                        .extra("source", "register")
                        .start();
                break;
            case R.id.rlUserProtocol://用户协议
                openActivity(context, ProtocolActivity.class, BundleUtils.protocol(AppConfig.USER_PROTOCOL), false);
                overridePendingTransition(R.anim.activity_open_down_up, 0);
                break;
            case R.id.rlUserPrivate://隐私协议
                openActivity(context, ProtocolActivity.class, BundleUtils.protocol(AppConfig.USER_PRIVATE), false);
                overridePendingTransition(R.anim.activity_open_down_up, 0);
                break;
        }
    }

}
