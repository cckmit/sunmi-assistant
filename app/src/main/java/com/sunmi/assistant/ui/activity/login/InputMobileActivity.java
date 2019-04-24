package com.sunmi.assistant.ui.activity.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;

import com.sunmi.apmanager.utils.HelpUtils;
import com.sunmi.apmanager.utils.SomeMonitorEditText;
import com.sunmi.assistant.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.utils.RegexUtils;
import sunmi.common.utils.ViewUtils;
import sunmi.common.view.ClearableEditText;

@EActivity(R.layout.activity_register)
public class InputMobileActivity extends BaseActivity {

    @ViewById(R.id.etMobile)
    ClearableEditText etMobile;
    @ViewById(R.id.btnNext)
    Button btnNext;
    @ViewById(R.id.ctv_privacy)
    CheckedTextView ctvPrivacy;

    @AfterViews
    protected void init() {
        HelpUtils.setStatusBarFullTransparent(this);//透明标题栏
        ViewUtils.setPrivacy(this, ctvPrivacy, R.color.white_40a,false);
        etMobile.setClearIcon(R.mipmap.ic_edit_delete_white);
        new SomeMonitorEditText().setMonitorEditText(btnNext, etMobile);
        //初始化
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String mobile = bundle.getString("mobile");
            if (!TextUtils.isEmpty(mobile)) {
                etMobile.setText(mobile);
            }
        }
    }

    @Click(R.id.btnNext)
    public void onClick(View v) {
        if (isFastClick(1500)) return;
        String mobile = etMobile.getText().toString().trim();
        if (!ctvPrivacy.isChecked()) {
            shortTip(R.string.tip_agree_protocol);
            return;
        }
        if (!RegexUtils.isChinaPhone(mobile)) {
            shortTip(R.string.str_invalid_phone);
            return;
        }
        InputCaptchaActivity_.intent(context)
                .extra("mobile", mobile)
                .extra("source", "register")
                .start();
    }

}
