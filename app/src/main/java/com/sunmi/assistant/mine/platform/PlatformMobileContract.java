package com.sunmi.assistant.mine.platform;

import com.sunmi.assistant.ui.activity.model.AuthStoreInfo;

import java.util.ArrayList;

import sunmi.common.base.BaseView;

/**
 * Created by YangShiJie on 2019/7/3.
 */
public interface PlatformMobileContract {
    interface View extends BaseView {

        void showAuthDialog(ArrayList<AuthStoreInfo.SaasUserInfoListBean> list);

        void onFailSendMobileCode();

        void onFailCheckMobileCode();

    }

    interface Presenter {

        void sendMobileCode(String mobile);

        void checkMobileCode(String mobile, String code, int saas);

    }
}
