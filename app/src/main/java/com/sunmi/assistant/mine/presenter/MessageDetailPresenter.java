package com.sunmi.assistant.mine.presenter;

import com.sunmi.apmanager.constant.NotificationConstant;
import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.contract.MessageDetailContract;
import com.sunmi.assistant.mine.model.MessageListBean;
import com.sunmi.assistant.rpc.MessageCenterApi;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.notification.BaseNotification;
import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-20.
 */
public class MessageDetailPresenter extends BasePresenter<MessageDetailContract.View>
        implements MessageDetailContract.Presenter {

    private List<Integer> list = new ArrayList<>();

    @Override
    public void getMessageList(int modelId, int pageNum, int pageSize,boolean needUpdate) {
        list.clear();
        list.add(modelId);
        MessageCenterApi.getMessageList(list, pageNum, pageSize, new RetrofitCallback<MessageListBean>() {
            @Override
            public void onSuccess(int code, String msg, MessageListBean data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.getMessageListSuccess(data.getMsgList(), data.getTotalCount(), data.getReturnCount(),needUpdate);
                }
            }

            @Override
            public void onFail(int code, String msg, MessageListBean data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.shortTip(R.string.tip_get_data_fail);
                    mView.getMessageListFail(code, msg);
                }
            }
        });
    }

    @Override
    public void deleteMessage(int msgId) {
        list.clear();
        list.add(msgId);
        MessageCenterApi.deleteMessage(list, new RetrofitCallback<Object>() {
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
        MessageCenterApi.updateReceiveStatusByModel(modelId, 1, new RetrofitCallback<Object>() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.updateReceiveStatusSuccess();
                }
            }

            @Override
            public void onFail(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.updateReceiveStatusFail(code, msg);
                }
            }
        });
    }
}
