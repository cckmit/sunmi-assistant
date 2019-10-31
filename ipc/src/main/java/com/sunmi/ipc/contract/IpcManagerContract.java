package com.sunmi.ipc.contract;

import com.sunmi.ipc.model.StorageListResp;
import com.sunmi.ipc.utils.IOTCClient;

/**
 * Description:
 * Created by bruce on 2019/9/11.
 */
public interface IpcManagerContract {

    interface View extends VideoPlayContract.View {
        void changeQualitySuccess(int quality);

        void getStorageSuccess(StorageListResp.DeviceListBean data);
    }

    interface Presenter extends VideoPlayContract.Presenter {
        void changeQuality(int quality, IOTCClient iotcClient);

        void getStorageInfo(int deviceId);
    }

}
