package com.sunmi.sunmiservice;

import com.sunmi.sunmiservice.cloud.CloudServiceMangeActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import sunmi.common.base.BaseActivity;
import sunmi.common.utils.StatusBarUtils;

@EActivity(resName = "activity_service_manage")
public class ServiceManageActivity extends BaseActivity {


    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
    }

    @Click(resName = "sil_cloud_storage")
    void cloudClick() {
        CloudServiceMangeActivity_.intent(context).start();
    }
}
