package com.sunmi.assistant.mine.presenter;

import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.contract.MessageDetailContract;
import com.sunmi.assistant.mine.model.MessageCountBean;
import com.sunmi.assistant.mine.model.MessageListBean;
import com.sunmi.assistant.rpc.MessageCenterApi;
import com.sunmi.assistant.utils.PushUtils;

import sunmi.common.base.BasePresenter;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.notification.BaseNotification;
import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-20.
 */
public class MessageDetailPresenter extends BasePresenter<MessageDetailContract.View>
        implements MessageDetailContract.Presenter {

    @Override
    public void getMessageList(int modelId, int pageNum, int pageSize,
                               boolean forceUpdateStatus) {
        MessageCenterApi.getInstance().getMessageList(modelId, pageNum, pageSize, new RetrofitCallback<MessageListBean>() {
            @Override
            public void onSuccess(int code, String msg, MessageListBean data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.getMessageListSuccess(data.getMsgList(),
                            data.getTotalCount(), data.getReturnCount());
                }
                if (data.getTotalCount() <= 0) {
                    return;
                }
                if (forceUpdateStatus ||
                        (pageNum == 1 && (data.getMsgList().get(0).getReceiveStatus() == 0))) {//0是未读
                    updateReceiveStatus(modelId);
                }
            }

            @Override
            public void onFail(int code, String msg, MessageListBean data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.shortTip(R.string.toast_network_error);
                    mView.getMessageListFail(code, msg);
                }
            }
        });
    }

    @Override
    public void deleteMessage(int msgId) {
        MessageCenterApi.getInstance().deleteMessage(msgId, new RetrofitCallback<Object>() {
            @Override
            public void onSuccess(int code, String msg, Object data) {

                if (isViewAttached()) {
                    mView.deleteMessageSuccess();
                }
            }

            @Override
            public void onFail(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.deleteMessageFail(code, msg);
                }
            }
        });
    }

    @Override
    public void updateReceiveStatus(int modelId) {
        MessageCenterApi.getInstance().updateReceiveStatusByModel(modelId,
                1, new RetrofitCallback<Object>() {
                    @Override
                    public void onSuccess(int code, String msg, Object data) {
                        refreshMsgCount();
                    }

                    @Override
                    public void onFail(int code, String msg, Object data) {

                    }
                });
    }

    private void refreshMsgCount() {
        MessageCenterApi.getInstance().getMessageCount(new RetrofitCallback<MessageCountBean>() {
            @Override
            public void onSuccess(int code, String msg, MessageCountBean data) {
                PushUtils.resetUnReadCount(data);
                BaseNotification.newInstance().postNotificationName(CommonNotifications.homePageBadgeUpdate);
                BaseNotification.newInstance().postNotificationName(CommonNotifications.msgCenterBadgeUpdate);
            }

            @Override
            public void onFail(int code, String msg, MessageCountBean data) {

            }
        });
    }

}
