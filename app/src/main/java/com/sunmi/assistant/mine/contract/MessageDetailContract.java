package com.sunmi.assistant.mine.contract;

import com.sunmi.assistant.mine.model.MessageListBean;

import java.util.List;

import sunmi.common.base.BaseView;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-20.
 */
public interface MessageDetailContract {

    interface View extends BaseView {
        void getMessageListSuccess(List<MessageListBean.MsgListBean> beans);

        void getMessageListFail(int code, String msg);

        void deleteMessageSuccess();

        void deleteMessageFail(int code, String msg);

        void updateReceiveStatusSuccess();

        void updateReceiveStatusFail(int code, String msg);
    }

    interface Presenter {
        void getMessageList(int modelId, int pageNum, int pageSize);

        void deleteMessage(int modelId);

        void updateReceiveStatus(int modelId);
    }
}
