package com.sunmi.assistant.contract;

import java.util.List;

import sunmi.common.base.BaseView;
import sunmi.common.model.ShopInfo;
import sunmi.common.model.SunmiDevice;
import sunmi.common.rpc.sunmicall.ResponseBean;

/**
 * Description:
 * Created by bruce on 2019/6/27.
 */
public interface DeviceContract {

    interface View extends BaseView {

        void getRouterListSuccess(List<SunmiDevice> devices);

        void getIpcListSuccess(List<SunmiDevice> devices);

        void unbindIpcSuccess(int code, String msg, Object data);

        void getPrinterListSuccess(List<SunmiDevice> devices);

        void unbindPrinterSuccess(String sn);

        void endRefresh();

        void getShopListSuccess(List<ShopInfo> shopList);

        void getPosListSuccess(List<SunmiDevice> devices);

        void getApConfigSuccess(String factory);

        void apEventStatus(String sn, boolean isOnline);

        void refreshApEventStatus();

        void getCheckApLoginSuccess();

        void getCheckApLoginFail(String errorCode);
    }

    interface Presenter {

        void getRouterList();

        void getIpcList();

        void getPrinterList();

        void getPrinterStatus(String sn);

        void unbind(SunmiDevice device);

        void getShopList();

        void getPosList();

        void apConfig(String sn);

        void apCheckLogin(String password);

        void getApConfig(ResponseBean res);

        void getStatusEvent(String result, List<SunmiDevice> routerList);

        void checkApLoginPassword(ResponseBean res);
    }

}
