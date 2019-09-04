package com.sunmi.assistant.mine.contract;

import java.util.ArrayList;

import sunmi.common.base.BaseView;
import sunmi.common.model.AuthStoreInfo;

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
