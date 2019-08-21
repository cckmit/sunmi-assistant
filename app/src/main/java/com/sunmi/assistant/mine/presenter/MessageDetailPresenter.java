package com.sunmi.assistant.mine.presenter;

import com.sunmi.assistant.mine.contract.MessageDetailContract;
import com.sunmi.assistant.mine.model.MessageListBean;
import com.sunmi.assistant.rpc.MessageCenterApi;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BasePresenter;
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
    public void getMessageList(int modelId, int pageNum, int pageSize) {
        list.clear();
        list.add(modelId);
        MessageCenterApi.getMessageList(list, pageNum, pageSize, new RetrofitCallback<MessageListBean>() {
            @Override
            public void onSuccess(int code, String msg, MessageListBean data) {
                if (isViewAttached()) {
                    mView.getMessageListSuccess(data.getMsgList());
                }
            }

            @Override
            public void onFail(int code, String msg, MessageListBean data) {
                if (isViewAttached()) {
                    mView.deleteMessageFail(code, msg);
                }
            }
        });
    }

    @Override
    public void deleteMessage(int modelId) {
        list.clear();
        list.add(modelId);
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
                    mView.getMessageListFail(code, msg);
                }
            }
        });
    }

    @Override
    public void updateReceiveStatus(int modelId) {
        list.clear();
        list.add(modelId);
        MessageCenterApi.updateReceiveStatusByModel(list, new RetrofitCallback<Object>() {
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
