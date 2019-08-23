package com.sunmi.assistant.mine.contract;

import sunmi.common.base.BaseView;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-23.
 */
public interface MsgSettingDetailContract {

    interface View extends BaseView {
        void updateSettingStatusSuccess();

        void updateSettingStatusFail(int code, String msg);
    }

    interface Presenter {
        void updateSettingStatus(int settingId, int status);
    }
}
