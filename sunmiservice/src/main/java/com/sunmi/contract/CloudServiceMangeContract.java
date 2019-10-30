package com.sunmi.contract;

import com.sunmi.bean.ServiceDetailBean;

import java.util.List;

import sunmi.common.base.BaseView;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-10-22.
 */
public interface CloudServiceMangeContract {

    interface View extends BaseView {

        void getSubscriptionListSuccess(List<ServiceDetailBean> beans, int totalCount);

        void getSubscriptionListFail(int code, String msg);

        void getIpcDetailListSuccess();
    }

    interface Presenter {
        void getSubscriptionList(int pageNum, int pageSize);

        void getIpcDetailList();
    }
}
