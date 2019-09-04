package com.sunmi.assistant.contract;

import java.util.List;

import sunmi.common.base.BaseView;
import sunmi.common.model.CompanyInfoResp;

/**
 * Description:
 * Created by bruce on 2019/7/2.
 */
public interface LoginContract {

    interface View extends BaseView {
        void showMergeDialog(String url);

        void mobileUnregister();

        void loginSuccess();

        void getCompanyListSuccess(List<CompanyInfoResp> companyList);

        void getCompanyListFail(int code, String msg);
    }

    interface Presenter {

        void userMerge(String user, String mobile, String password);

        void login(String mobile, String password);

        void getCompanyList();

    }

}
