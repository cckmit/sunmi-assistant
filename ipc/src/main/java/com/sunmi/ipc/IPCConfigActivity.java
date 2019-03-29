package com.sunmi.ipc;

import android.widget.CheckedTextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.utils.ViewUtils;
import sunmi.common.utils.log.LogCat;

/**
 * Description:
 * Created by bruce on 2019/3/27.
 */
@EActivity(resName = "activity_ipc_config")
class IPCConfigActivity extends BaseActivity {
    @ViewById(resName = "ctv_privacy")
    CheckedTextView ctvPrivacy;

    @AfterViews
    void init() {
        ViewUtils.setPrivacy(this, ctvPrivacy, R.color.colorOrange, false);
    }

    @Click(resName = "btn_config")
    void configClick() {
        LogCat.e(TAG, "555555 122" + ctvPrivacy.isChecked());
        if (!ctvPrivacy.isChecked()) {
            shortTip("");
            return;
        }
    }

}
