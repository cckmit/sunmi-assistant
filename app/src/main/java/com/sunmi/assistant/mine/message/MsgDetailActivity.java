package com.sunmi.assistant.mine.message;

import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.contract.MessageDetailContract;
import com.sunmi.assistant.mine.model.MessageListBean;
import com.sunmi.assistant.mine.presenter.MessageDetailPresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import java.util.List;

import sunmi.common.base.BaseMvpActivity;

@EActivity(R.layout.activity_msg_detail)
public class MsgDetailActivity extends BaseMvpActivity<MessageDetailPresenter>
        implements MessageDetailContract.View {


    @AfterViews
    void init(){

    }

    @Override
    public void getMessageListSuccess(List<MessageListBean.MsgListBean> beans) {

    }

    @Override
    public void getMessageListFail(int code, String msg) {

    }

    @Override
    public void deleteMessageSuccess() {

    }

    @Override
    public void deleteMessageFail(int code, String msg) {

    }

    @Override
    public void updateReceiveStatusSuccess() {

    }

    @Override
    public void updateReceiveStatusFail(int code, String msg) {

    }
}
