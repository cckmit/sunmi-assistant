package com.sunmi.assistant.mine.presenter;

import com.google.gson.Gson;
import com.sunmi.assistant.mine.contract.MessageCountContract;
import com.sunmi.assistant.mine.model.MessageCountBean;
import com.sunmi.assistant.rpc.MessageCenterApi;
import com.sunmi.assistant.utils.PushUtils;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.FileHelper;
import sunmi.common.utils.FileUtils;

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
                PushUtils.resetUnReadCount(data);
                FileUtils.writeFileToSD(FileHelper.FILE_PATH, "msgCount.json", new Gson().toJson(data));
                if (isViewAttached()) {
                    mView.getMessageCountSuccess(data);
                }
            }

            @Override
            public void onFail(int code, String msg, MessageCountBean data) {
                if (isViewAttached()) {
                    mView.getMessageCountFail(code, msg);
                }
            }
        });
    }

}
