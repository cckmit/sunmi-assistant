package com.sunmi.ipc.contract;

import com.sunmi.ipc.model.CashVideoResp;

import java.util.List;

import sunmi.common.base.BaseView;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-12-06.
 */
public interface CashVideoListConstract {

    interface View extends BaseView {
        void getCashVideoSuccess(List<CashVideoResp.AuditVideoListBean> beans, boolean hasMore, int total);

        void netWorkError();
    }

    interface Presenter {
        void load(int deviceId, int videoType, long startTime, long endTime);

        void loadMore();
    }
}

