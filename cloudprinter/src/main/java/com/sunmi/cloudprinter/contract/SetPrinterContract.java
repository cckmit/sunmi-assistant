package com.sunmi.cloudprinter.contract;

import com.sunmi.cloudprinter.bean.Router;

import sunmi.common.base.BaseView;

public interface SetPrinterContract {

    interface View extends BaseView {
        void onInitMtu();

        void onSendMessage(final byte[] data);

        void onInitNotify();

        void initRouter(Router router);

//        void hideProgressBar();

        void setSn(String sn);

    }

    interface Presenter {
        void initMtuSuccess();

        void initMtuFailed();

        void initNotifySuccess(byte version);

        void initNotifyFailed();

        void onNotify(byte[] value, byte version);
    }
}
