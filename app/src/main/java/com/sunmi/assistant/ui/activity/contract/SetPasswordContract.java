package com.sunmi.assistant.ui.activity.contract;

import com.sunmi.assistant.ui.activity.model.AuthStoreInfo;

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

        void getCompanyInfoSuccess(CompanyInfoResp data);

        void getCompanyInfoFail(int code, String msg);

        void getSaasUserInfoSuccess(AuthStoreInfo data);

        void getSaasUserInfoFail(int code, String msg);
    }

    interface Presenter {
        void register(String username, String password, String code);

        void resetPassword(String username, String password, String code);

        void getCompanyInfo(int companyId);

        void getSaasUserInfo(String phone);
    }
}
