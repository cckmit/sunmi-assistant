package com.sunmi.ipc.view;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import sunmi.common.base.BaseActivity;

/**
 * Description: WifiConfigCompletedActivity
 * Created by Bruce on 2019/3/31.
 */
@EActivity(resName = "activity_wifi_config_completed")
public class WifiConfigCompletedActivity extends BaseActivity {

    @Click(resName = "btn_complete")
    void completeClick() {
        finish();
    }

    @Click(resName = "btn_adjust_screen")
    void adjustClick() {

    }

}
