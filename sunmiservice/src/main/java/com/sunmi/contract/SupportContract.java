package com.sunmi.contract;

import java.util.List;

import sunmi.common.base.BaseView;
import sunmi.common.model.CashServiceInfo;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-12-30.
 */
public interface SupportContract {

    interface View extends BaseView {

        void loadSuccess(List<CashServiceInfo> infoList);

        void loadFailed();

    }

    interface Presenter {

        void load();

    }
}
