package com.sunmi.assistant.mine.contract;

import sunmi.common.base.BaseView;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-05.
 */
public interface SettingContract {

    interface View extends BaseView {

        void logoutSuccess();

        void logoutFail(int code, String msg);
    }

    interface Presenter {
        void logout();
    }

}
