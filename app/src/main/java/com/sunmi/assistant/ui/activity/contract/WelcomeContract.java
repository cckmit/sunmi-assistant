package com.sunmi.assistant.ui.activity.contract;

import sunmi.common.base.BaseView;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-05.
 */
public interface WelcomeContract {

    interface View extends BaseView {
        void checkTokenSuccess(String response);

        void checkTokenFail(int code, String msg);

        void forceUpdate(String downloadUrl);

        void gotoLeadPagesActivity();

        void handleLaunch();

    }

    interface Presenter {
        void checkToken();

        void checkUpgrade();
    }

}
