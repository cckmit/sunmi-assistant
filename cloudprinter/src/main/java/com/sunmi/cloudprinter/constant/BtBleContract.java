package com.sunmi.cloudprinter.constant;

import library.BluetoothClient;
import sunmi.common.base.BaseView;

/**
 * Description:
 * Created by bruce on 2019/1/11.
 */
public interface BtBleContract {

    interface View extends BaseView {

        void bindSuccess(int code, String msg, String data);

        void bindFail(int code, String msg, String data);

        void onResponse(byte[] value);

    }

    interface Presenter {
        void bindPrinter(int shopId, String sn);

        void sendData(BluetoothClient client, String bleAddress, byte[] data);
    }

}
