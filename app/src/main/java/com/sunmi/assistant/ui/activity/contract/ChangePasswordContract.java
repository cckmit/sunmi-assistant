package com.sunmi.assistant.ui.activity.contract;

import sunmi.common.base.BaseView;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-05.
 */
public interface ChangePasswordContract {

    interface View extends BaseView {
        void changePasswordSuccess();

        void changePasswordFail(int code, String msg);
    }

    interface Presenter{
        void changePassword(String oldPsw, String newPsw);
    }
}
