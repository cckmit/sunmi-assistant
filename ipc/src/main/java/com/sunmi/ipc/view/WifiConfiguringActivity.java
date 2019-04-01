package com.sunmi.ipc.view;

import android.content.DialogInterface;

import com.sunmi.ipc.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;

import sunmi.common.base.BaseActivity;
import sunmi.common.view.dialog.CommonDialog;

/**
 * Description: WifiConfiguringActivity
 * Created by Bruce on 2019/3/31.
 */
@EActivity(resName = "activity_wifi_configuring")
public class WifiConfiguringActivity extends BaseActivity {

    @AfterViews
    void init() {
        bindFailDialog();
    }

    @UiThread
    void configFailDialog() {
        new CommonDialog.Builder(context)
                .setTitle(R.string.tip_set_fail)
                .setMessage(R.string.msg_in_same_wifi)
                .setCancelButton(R.string.str_cancel)
                .setConfirmButton(R.string.str_retry,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).create().show();
    }


    @UiThread
    void bindFailDialog() {
        new CommonDialog.Builder(context)
                .setTitle("摄像头已绑定其他商米账号，如要添加请先从原账号解绑")
                .setConfirmButton(R.string.str_confirm).create().show();
    }

}
