package com.sunmi.assistant.ui.activity.contract;

import sunmi.common.base.BaseView;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-07-31.
 */
public interface InputMobileContract {

    interface View extends BaseView {
        void isUserExistSuccess();

        void isUserExistFail(int code, String msg);

        void checkSuccess(int needMerge, String url);
    }

    interface Presenter {
        void isUserExist(String username);

        void checkUserName(String username);
    }

}
