package com.sunmi.assistant.ui.activity.contract;

import java.util.List;

import sunmi.common.base.BaseView;
import sunmi.common.model.CompanyInfoResp;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-02.
 */
public interface SetPasswordContract {

    interface View extends BaseView {
        void registerSuccess();

        void registerFail(int code, String msg);

        void resetPasswordSuccess();

        void reSetPasswordFail(int code, String msg);

        void getCompanyListSuccess(List<CompanyInfoResp> companyList);

        void getCompanyListFail(int code, String msg);
    }

    interface Presenter {
        void register(String username, String password, String code);

        void resetPassword(String username, String password, String code);

        void getUserInfo();

        void getCompanyList();
    }
}
