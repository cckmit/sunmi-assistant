package com.sunmi.cloudprinter.presenter;

import com.sunmi.cloudprinter.R;
import com.sunmi.cloudprinter.bean.Router;
import com.sunmi.cloudprinter.constant.Constants;
import com.sunmi.cloudprinter.contract.SetPrinterContract;
import com.sunmi.cloudprinter.utils.Utility;

import sunmi.common.base.BasePresenter;
import sunmi.common.utils.log.LogCat;

public class SetPrinterPresenter extends BasePresenter<SetPrinterContract.View> implements SetPrinterContract.Presenter {

    byte[] data;
    int receivedLen;

    @Override
    public void initMtuSuccess() {
        if (isViewAttached()) mView.onInitNotify();
    }

    @Override
    public void initMtuFailed() {
//        mView.onInitMtu();
    }

    @Override
    public void initNotifySuccess(byte version) {
//        mView.onSendMessage(Utility.cmdGetSn());
        if (isViewAttached()) mView.onSendMessage(Utility.cmdGetWifi());
    }

    @Override
    public void initNotifyFailed() {
        if (isViewAttached()) mView.onInitNotify();
    }

    @Override
    public void onNotify(byte[] value, byte version) {
        if (value.length > 0) {
            if (Utility.isFirstPac(value)) {
                receivedLen = 0;
                data = new byte[Utility.getPacLength(value)];
                System.arraycopy(value, 0, data, 0, value.length);
            } else {
                System.arraycopy(value, 0, data, receivedLen, value.length);
            }
            receivedLen += value.length;
            if (data.length == receivedLen) {
                onDataReceived(data, version);
            }
        }
    }

    private void onDataReceived(byte[] value, byte version) {
        if (!isViewAttached()) return;
        int cmd = Utility.getCmdId(value);
        if (cmd == Constants.CMD_RESP_SN) {
//            mView.setSn(Utility.getSn(value));
            if (isViewAttached()) mView.onSendMessage(Utility.cmdGetWifi());
        } else if (cmd == Constants.CMD_RESP_GET_WIFI_ERROR) {
            LogCat.e("spp", "222222");
            if (isViewAttached()) mView.hideLoadingDialog();
            if (isViewAttached()) mView.shortTip(R.string.str_get_wifi_msg_error);
        } else if (cmd == Constants.CMD_RESP_GET_WIFI_SUCCESS) {
            Router router = Utility.getRouter(value);
            mView.initRouter(router);
        } else if (cmd == Constants.CMD_RESP_WIFI_AP_COMPLETELY) {
            mView.shortTip(R.string.str_wifi_msg_completely);
            mView.hideLoadingDialog();
        } else if (cmd == Constants.CMD_RESP_WIFI_CONNECTED) {
            mView.wifiSetSuccess();
            mView.onSendMessage(Utility.cmdAlreadyConnectedWifi());
        }
    }

}
