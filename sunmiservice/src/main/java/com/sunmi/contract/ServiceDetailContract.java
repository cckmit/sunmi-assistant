package com.sunmi.contract;

import com.sunmi.bean.ServiceDetailBean;

import sunmi.common.base.BaseView;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-10-24.
 */
public interface ServiceDetailContract {

    interface View extends BaseView {
        void getServiceDetail(ServiceDetailBean bean);
    }

    interface Presenter {
        void getServiceDetailByDevice(int category);

        void getServiceDetailByServiceNo(String serviceNo);
    }

}
