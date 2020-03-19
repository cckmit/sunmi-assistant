package com.sunmi.assistant.contract;

import java.util.List;

import sunmi.common.base.BaseView;
import sunmi.common.model.CompanyInfoResp;
import sunmi.common.model.CompanyListResp;
import sunmi.common.model.ShopInfo;

/**
 * Description: ChooseShopContract
 * Created by Bruce on 2019/7/1.
 */
public interface ChooseShopContract {

    interface View extends BaseView {
        void getShopListSuccess(int authority, List<ShopInfo> shopList);

        void getShopListFail();

        void getCompanyListSuccess(List<CompanyInfoResp> companyList);

        void getCompanyListFail(int code, String msg, CompanyListResp data);
    }

    interface Presenter {
        void getShopList(int companyId);

        void getCompanyList();
    }
}
