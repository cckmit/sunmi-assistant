package com.sunmi.contract;

import java.util.ArrayList;

import sunmi.common.base.BaseView;
import sunmi.common.model.CashVideoServiceBean;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-12-30.
 */
public interface SupportContract {
    interface View extends BaseView {
        void getCashServiceSuccess(ArrayList<CashVideoServiceBean> beans, boolean hasCashLossPrevent);

        void getServiceFail();

        void getStorageSeviceSuccess(int cloudStatus);

    }

    interface Presenter {
        void getAuditVideoServiceList();

        void getStorageList();
    }
}
