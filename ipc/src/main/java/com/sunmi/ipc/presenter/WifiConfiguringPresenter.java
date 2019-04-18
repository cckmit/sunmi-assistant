package com.sunmi.ipc.presenter;

import com.sunmi.ipc.rpc.IPCCloudApi;
import com.sunmi.ipc.rpc.IpcConstants;
import com.sunmi.ipc.contract.WifiConfiguringContract;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.http.HttpCallback;

/**
 * Description:
 * Created by bruce on 2019/3/29.
 */
public class WifiConfiguringPresenter extends BasePresenter<WifiConfiguringContract.View>
        implements WifiConfiguringContract.Presenter {

    @Override
    public void ipcBind(String shopId, String token, float longitude, float latitude) {
        IPCCloudApi.bindIPC(Integer.parseInt(shopId), IpcConstants.IPC_SN,
                token, longitude, latitude, new HttpCallback<Object>(mView) {
                    @Override
                    public void onSuccess(int code, String msg, Object data) {
                        mView.ipcBindWifiSuccess();
                    }
                });
    }

}
