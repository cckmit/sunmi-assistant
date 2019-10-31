package com.sunmi.sunmiservice.cloud;

import android.webkit.JavascriptInterface;

import com.sunmi.sunmiservice.R;
import com.sunmi.sunmiservice.ServiceManageActivity_;

import sunmi.common.base.BaseActivity;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.notification.BaseNotification;
import sunmi.common.view.activity.ProtocolActivity;
import sunmi.common.view.activity.ProtocolActivity_;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-10-25.
 */
public class ServiceJSCall {

    private BaseActivity mActivity;


    public ServiceJSCall(BaseActivity mActivity) {
        this.mActivity = mActivity;
    }

    @JavascriptInterface
    public void lastPageBack() {
        mActivity.finish();
    }

    @JavascriptInterface
    public void showCloudServiceAgreement() {
        ProtocolActivity_.intent(mActivity)
                .protocolType(ProtocolActivity.USER_PROTOCOL).start();
        mActivity.overridePendingTransition(R.anim.activity_open_down_up, 0);
    }

    @JavascriptInterface
    public void servicesPageBack() {
        mActivity.finish();
        BaseNotification.newInstance().postNotificationName(CommonNotifications.cloudStorageChange);
    }

}
