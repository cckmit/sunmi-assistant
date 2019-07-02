package com.sunmi.assistant.contract;

import com.sunmi.apmanager.model.LoginDataBean;

import sunmi.common.base.BaseView;

/**
 * Description:
 * Created by bruce on 2019/7/2.
 */
public interface LoginContract {

    interface View extends BaseView {
        void showMergeDialog(String url);

        void mobileNoRegister();

        void getStoreTokenSuccess(LoginDataBean loginData);
    }

    interface Presenter {
        void getStoreToken(LoginDataBean loginData);

        void userMerge(String user, String mobile, String password);

        void login(String mobile, String password);

    }

}
