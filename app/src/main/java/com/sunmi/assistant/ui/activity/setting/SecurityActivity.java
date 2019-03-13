package com.sunmi.assistant.ui.activity.setting;

import android.view.View;
import android.widget.TextView;

import com.sunmi.apmanager.constant.Constants;
import com.sunmi.apmanager.utils.CommonUtils;
import com.sunmi.assistant.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.utils.StatusBarUtils;

/**
 * 账号安全
 */
@EActivity(R.layout.activity_setting_account)
public class SecurityActivity extends BaseActivity {

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this,
                StatusBarUtils.TYPE_DARK);//状态栏
    }

    @Click({R.id.rlPassword, R.id.rlMobile, R.id.rlEmail})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlPassword:
                CommonUtils.trackCommonEvent(context, "accountSecurityChangePwd",
                        "主页_设置_账号安全_密码修改", Constants.EVENT_MY_INFO);
                ChangePasswordActivity_.intent(context).start();
                break;
        }
    }

}
