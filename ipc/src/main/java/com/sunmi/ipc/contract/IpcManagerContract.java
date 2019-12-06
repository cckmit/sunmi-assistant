package com.sunmi.ipc.contract;

import com.sunmi.ipc.model.IpcManageBean;
import com.sunmi.ipc.utils.IOTCClient;

import java.util.ArrayList;

import sunmi.common.model.CashVideoServiceBean;
import sunmi.common.base.BaseView;

/**
 * Description:
 * Created by bruce on 2019/9/11.
 */
public interface IpcManagerContract {

    interface View extends BaseView {
        void startLiveSuccess();

        void changeQualitySuccess(int quality);

        void getStorageSuccess(IpcManageBean bean);

        void getCashVideoServiceSuccess(ArrayList<CashVideoServiceBean> devices, boolean alreadySubscribe);
    }

    interface Presenter {
        void startLive(IOTCClient iotcClient);

        void changeQuality(int quality, IOTCClient iotcClient);

        void getStorageList(String deviceSn, IpcManageBean item);

        void getCashVideoService(int deviceId);
    }

}
