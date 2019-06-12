package com.sunmi.cloudprinter.constant;

import com.inuker.bluetooth.library.BluetoothClient;

import sunmi.common.base.BaseView;

/**
 * Description:
 * Created by bruce on 2019/1/11.
 */
public interface BtBleContract {

    interface View extends BaseView {

        void onResponse(byte[] value);
    }

    interface Presenter {

        void sendData(BluetoothClient client, String bleAddress, byte[] data);
    }

}
