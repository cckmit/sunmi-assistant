package com.sunmi.assistant.contract;

import com.sunmi.assistant.data.response.AdListResp;

import java.util.List;

import sunmi.common.base.BaseView;
import sunmi.common.model.SunmiDevice;

/**
 * Description:
 * Created by bruce on 2019/6/27.
 */
public interface DeviceContract {

    interface View extends BaseView {
        void getAdListSuccess(AdListResp adListResp);

        void getRouterListSuccess(List<SunmiDevice> devices);

        void unbindRouterSuccess(String sn, int code, String msg, Object data);

        void getIpcListSuccess(List<SunmiDevice> devices);

        void unbindIpcSuccess(int code, String msg, Object data);

        void unbindIpcFail(int code, String msg);

        void getPrinterListSuccess(List<SunmiDevice> devices);

        void getPrinterStatusSuccess(SunmiDevice device);

        void unbindPrinterSuccess(String sn);

        void endRefresh();
    }

    interface Presenter {

        void getBannerList();

        void getRouterList();

        void unbindRouter(String sn);

        void getIpcList();

        void unbindIPC(int deviceId);

        void getPrinterList();

        void getPrinterStatus(String sn);

        void unBindPrinter(String sn);
    }

}
