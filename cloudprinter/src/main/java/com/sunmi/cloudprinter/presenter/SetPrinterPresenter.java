package com.sunmi.cloudprinter.presenter;

import android.util.Log;

import com.sunmi.cloudprinter.R;
import com.sunmi.cloudprinter.bean.Router;
import com.sunmi.cloudprinter.constant.Constants;
import com.sunmi.cloudprinter.contract.SetPrinterContract;
import com.sunmi.cloudprinter.utils.Utility;

import sunmi.common.base.BasePresenter;

public class SetPrinterPresenter extends BasePresenter<SetPrinterContract.View> implements SetPrinterContract.Presenter {

    byte[] data;
    int receivedLen;

    @Override
    public void initMtuSuccess() {
        mView.onInitNotify();
    }

    @Override
    public void initMtuFailed() {
//        mView.onInitMtu();
    }

    @Override
    public void initNotifySuccess(byte version) {
        mView.onSendMessage(Utility.cmdGetSn(version));
    }

    @Override
    public void initNotifyFailed() {
        mView.onInitNotify();
    }

    @Override
    public void onNotify(byte[] value, byte version) {
        Log.e("spp", "555555 onNotify, value = " + value);
        if (value.length > 0) {
            if (Utility.isFirstPac(value)) {
                data = new byte[Utility.getPacLength(value)];
                System.arraycopy(value, 0, data, 0, value.length);
            } else {
                System.arraycopy(value, receivedLen, data, 0, value.length);
            }
            receivedLen += value.length;
            if (data.length == receivedLen) {
                onDataReceived(data, version);
            }
        }
    }

    private void onDataReceived(byte[] value, byte version) {
        int cmd = Utility.getCmd(value);
        if (cmd == Constants.SRV2CLI_SEND_SN) {
            mView.setSn(Utility.getSn(value));
            mView.onSendMessage(Utility.cmdGetWifi(version));
        } else if (cmd == Constants.SRV2CLI_SEND_WIFI_ERROR) {
            mView.shortTip(R.string.str_get_wifi_msg_error);
        } else if (cmd == Constants.SRV2CLI_SEND_WIFI_AP) {
            Router router = Utility.getRouter(value);
            mView.initRouter(router);
        } else if (cmd == Constants.SRV2CLI_SEND_WIFI_AP_COMPLETELY) {
            mView.shortTip(R.string.str_wifi_msg_completely);
            mView.hideProgressBar();
        } else if (cmd == Constants.SRV2CLI_SEND_ALREADY_CONNECTED_WIFI) {
            mView.onSendMessage(Utility.cmdAlreadyConnectedWifi(version));
        }
    }

}
