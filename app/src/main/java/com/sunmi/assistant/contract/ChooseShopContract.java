package com.sunmi.assistant.contract;

import com.sunmi.assistant.data.response.CompanyInfoResp;
import com.sunmi.assistant.data.response.CompanyListResp;
import com.sunmi.assistant.data.response.ShopListResp;

import java.util.List;

import sunmi.common.base.BaseView;

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
    }

    interface Presenter {
        void getShopList(int companyId);

        void getCompanyList();
    }
}
