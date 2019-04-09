package com.sunmi.ipc.view;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.sunmi.ipc.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

import sunmi.common.base.BaseActivity;

/**
 * Description:
 * Created by bruce on 2019/4/9.
 */
@EActivity(resName = "activity_ipc_test")
public class IpcTestActivity extends BaseActivity {
    @Extra
    String shopId;

    @AfterViews
    void init() {
        IPCFragment ipcFragment = IPCFragment_.builder().shopId(shopId).build();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content_layout, ipcFragment);
        transaction.commit();
    }

}
