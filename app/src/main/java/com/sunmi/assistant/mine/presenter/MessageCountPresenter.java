package com.sunmi.assistant.mine.presenter;

import com.google.gson.Gson;
import com.sunmi.assistant.mine.contract.MessageCountContract;
import com.sunmi.assistant.mine.model.MessageCountBean;
import com.sunmi.assistant.rpc.MessageCenterApi;

import sunmi.common.base.BasePresenter;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.notification.BaseNotification;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.FileHelper;
import sunmi.common.utils.FileUtils;
import sunmi.common.utils.SpUtils;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-14.
 */
public class MessageCountPresenter extends BasePresenter<MessageCountContract.View>
        implements MessageCountContract.Presenter {

    @Override
    public void getMessageCount() {
        MessageCenterApi.getInstance().getMessageCount(new RetrofitCallback<MessageCountBean>() {
            @Override
            public void onSuccess(int code, String msg, MessageCountBean data) {
                int unreadMsg = data.getUnreadCount();
                int remindUnreadMsg = data.getRemindUnreadCount();
                if (SpUtils.getUnreadMsg() != unreadMsg || SpUtils.getRemindUnreadMsg() != remindUnreadMsg) {
                    SpUtils.setUnreadMsg(unreadMsg);
                    SpUtils.setRemindUnreadMsg(remindUnreadMsg);
                    SpUtils.setUnreadDeviceMsg(data.getModelCountList().get(0).getUnreadCount());
                    SpUtils.setUnreadSystemMsg(data.getModelCountList().get(1).getUnreadCount());
                    BaseNotification.newInstance().postNotificationName(CommonNotifications.msgUpdated);
                }
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.getMessageCountSuccess(data);
                }
            }

            @Override
            public void onFail(int code, String msg, MessageCountBean data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.getMessageCountFail(code, msg);
                }
            }
        });
    }
}
