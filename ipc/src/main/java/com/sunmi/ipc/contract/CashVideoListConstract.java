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
        void getCashVideoSuccess(List<CashVideoResp.AuditVideoListBean> beans, int total);

        void netWorkError();

        void endRefresh();
    }

    interface Presenter {
        void load(int deviceId, int videoType, long startTime, long endTime, int pageNum, int pageSize);

        void loadMore(int pageNum, int pageSize);
    }
}

