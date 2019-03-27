package com.sunmi.cloudprinter.presenter;

import com.sunmi.cloudprinter.R;
import com.sunmi.cloudprinter.bean.Router;
import com.sunmi.cloudprinter.constant.Constants;
import com.sunmi.cloudprinter.contract.ReadAndWriteContract;
import com.sunmi.cloudprinter.utils.Utility;

import sunmi.common.base.BasePresenter;

public class ReadAndWritePresenter extends BasePresenter<ReadAndWriteContract.View> implements ReadAndWriteContract.Presenter {

    @Override
    public void initMtuSuccess() {
        mView.onInitNotify();
    }

    @Override
    public void initMtuFailed() {
        mView.onInitMtu();
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

        if (Utility.getCmd(value) == Constants.SRV2CLI_SEND_SN) {
            mView.setSn(Utility.getSn(value));
            mView.onSendMessage(Utility.cmdGetWifi(version));
        } else if (Utility.getCmd(value) == Constants.SRV2CLI_SEND_WIFI_ERROR) {
            mView.shortTip(R.string.str_get_wifi_msg_error);
        } else if (Utility.getCmd(value) == Constants.SRV2CLI_SEND_WIFI_AP) {
            Router router = Utility.getRouter(value);
            mView.initRouter(router);
        } else if (Utility.getCmd(value) == Constants.SRV2CLI_SEND_WIFI_AP_COMPLETELY) {
            mView.shortTip(R.string.str_wifi_msg_completely);
            mView.hideProgressBar();
        } else if (Utility.getCmd(value) == Constants.SRV2CLI_SEND_ALREADY_CONNECTED_WIFI) {
            mView.onSendMessage(Utility.cmdAlreadyConnectedWifi(version));
        }

    }

}
