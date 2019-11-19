package com.sunmi.assistant.contract;

import java.util.List;

import sunmi.common.base.BaseView;
import sunmi.common.model.AdListResp;
import sunmi.common.model.ShopInfo;
import sunmi.common.model.SunmiDevice;

/**
 * Description:
 * Created by bruce on 2019/6/27.
 */
public interface DeviceContract {

    interface View extends BaseView {
        void getAdListSuccess(AdListResp adListResp);

        void getRouterListSuccess(List<SunmiDevice> devices);

        void getIpcListSuccess(List<SunmiDevice> devices);

        void unbindIpcSuccess(int code, String msg, Object data);

        void getPrinterListSuccess(List<SunmiDevice> devices);

        void unbindPrinterSuccess(String sn);

        void endRefresh();

        void getShopListSuccess(List<ShopInfo> shopList);
    }

    interface Presenter {

        void getBannerList();

        void getRouterList();

        void getIpcList();

        void getPrinterList();

        void getPrinterStatus(String sn);

        void unbind(SunmiDevice device);

        void getShopList();
    }

}
