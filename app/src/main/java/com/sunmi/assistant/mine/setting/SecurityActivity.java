package com.sunmi.assistant.mine.setting;

import android.view.View;

import com.sunmi.apmanager.constant.Constants;
import com.sunmi.apmanager.utils.CommonUtils;
import com.sunmi.assistant.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import sunmi.common.base.BaseActivity;
import sunmi.common.utils.StatusBarUtils;

/**
 * 账号安全
 * @author yangshijie
 */
@EActivity(R.layout.activity_setting_account)
public class SecurityActivity extends BaseActivity {

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
    }

    @Click({R.id.rlPassword, R.id.rlMobile, R.id.rlEmail})
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rlPassword) {
            CommonUtils.trackCommonEvent(context, "accountSecurityChangePwd",
                    "主页_设置_账号安全_密码修改", Constants.EVENT_MY_INFO);
            ChangePasswordActivity_.intent(context).start();
        }
    }

}
