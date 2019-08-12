package com.sunmi.assistant.contract;

import java.util.List;

import sunmi.common.base.BaseView;
import sunmi.common.model.AuthStoreInfo;
import sunmi.common.model.CompanyInfoResp;
import sunmi.common.model.CompanyListResp;
import sunmi.common.model.ShopListResp;

/**
 * Description: ChooseShopContract
 * Created by Bruce on 2019/7/1.
 */
public interface ChooseShopContract {

    interface View extends BaseView {
        void getShopListSuccess(List<ShopListResp.ShopInfo> shopList);

        void getShopListFail(int code, String msg, ShopListResp data);

        void getCompanyListSuccess(List<CompanyInfoResp> companyList);

        void getCompanyListFail(int code, String msg, CompanyListResp data);

        void getSaasSuccessView(AuthStoreInfo data);

        void getSaasFailView(int code, String msg);
    }

    interface Presenter {
        void getShopList(int companyId);

        void getCompanyList();

        void getUserInfo();

        void getSaas(String mobile);

        void getSsoToken();
    }
}
