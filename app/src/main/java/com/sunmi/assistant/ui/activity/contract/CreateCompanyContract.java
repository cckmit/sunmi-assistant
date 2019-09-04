package com.sunmi.assistant.ui.activity.contract;

import sunmi.common.base.BaseView;
import sunmi.common.model.CompanyInfoResp;

/**
 * @author yangShiJie
 * @date 2019/8/1
 */
public interface CreateCompanyContract {
    interface View extends BaseView {

        void createCompanySuccessView(CompanyInfoResp resp);

        void createCompanyFailView(int code, String msg);

//        void getSaasSuccessView(AuthStoreInfo bean);
//
//        void getSaasFailView(int code, String msg);

    }

    interface Presenter {

        void createCompany(String name);

//        void getSaas(String mobile);

    }
}
