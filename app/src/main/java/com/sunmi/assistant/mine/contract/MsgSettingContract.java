package com.sunmi.assistant.mine.contract;

import com.sunmi.assistant.mine.model.MsgSettingListBean;

import java.util.List;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-23.
 */
public interface MsgSettingContract {

    interface View extends MsgSettingDetailContract.View {
        void getSettingListSuccess(List<MsgSettingListBean.ReminderSettingListBean> beans);

        void getSettingListFail(int code, String msg);
    }

    interface Presenter extends MsgSettingDetailContract.Presenter {
        void getSettingList();
    }
}
