package com.sunmi.assistant.contract;

import android.content.Context;

import java.util.List;
import java.util.Map;

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

        void getApConfigSuccess();

        void apEventStatus(String sn, boolean isOnline);

        void refreshApEventStatus(Map<String, SunmiDevice> map);

        void getCheckApLoginSuccess(boolean isAgainCheck);

        void getCheckApLoginFail(String type);

        void gotoPrimaryRouteStartActivity();
    }

    interface Presenter {

        void getRouterList();

        void getIpcList();

        void getPrinterList();

        void getPrinterStatus(String sn);

        void unbind(SunmiDevice device);

        void getShopList();

        void getPosList();

        void apConfig(Context context, String sn);

        void apCheckLogin(Context context, String password);

        void apCheckLoginAgain(Context context, String password);

        void getApConfig(Context context, ResponseBean res, SunmiDevice clickedDevice);

        void getStatusEvent(String result);

        void checkApLoginPassword(Context context, ResponseBean res, SunmiDevice clickedDevice);

        void checkApLoginPasswordAgain(Context context, ResponseBean res, SunmiDevice clickedDevice, String password);
    }

}
