package com.sunmi.assistant.mine.contract;

import com.sunmi.assistant.mine.model.MessageCountBean;

import sunmi.common.base.BaseView;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-14.
 */
public interface MainContract {

    interface View extends BaseView {
        void getMessageCountSuccess(MessageCountBean data);

        void getMessageCountFail(int code, String msg);

        void getLoanStatus(boolean status);
    }

    interface Presenter {
        void getMessageCount();

        void getServiceList();
    }
}
