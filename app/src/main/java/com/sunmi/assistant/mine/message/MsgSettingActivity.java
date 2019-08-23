package com.sunmi.assistant.mine.message;

import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.contract.MsgSettingContract;
import com.sunmi.assistant.mine.model.MsgSettingListBean;
import com.sunmi.assistant.mine.presenter.MsgSettingPresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import java.util.List;

import sunmi.common.base.BaseMvpActivity;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-23.
 */
@EActivity(R.layout.activity_msg_setting)
public class MsgSettingActivity extends BaseMvpActivity<MsgSettingPresenter>
        implements MsgSettingContract.View {


    @AfterViews
    void init() {

    }

    @Override
    public void getSettingListSuccess(List<MsgSettingListBean.ReminderSettingListBean> beans) {

    }

    @Override
    public void getSettingListFail(int code, String msg) {

    }

    @Override
    public void updateSettingStatusSuccess() {

    }

    @Override
    public void updateSettingStatusFail(int code, String msg) {

    }
}
