package com.sunmi.assistant.contract;

import sunmi.common.base.BaseView;
import sunmi.common.model.UserInfoBean;

/**
 * Description:
 * Created by bruce on 2019/7/2.
 */
public interface LoginContract {

    interface View extends BaseView {
        void showMergeDialog(String url);

        void mobileUnregister();

        void getStoreTokenSuccess(UserInfoBean loginData);
    }

    interface Presenter {
        void getUserInfo();

        void userMerge(String user, String mobile, String password);

        void login(String mobile, String password);

    }

}
