package com.sunmi.ipc.presenter;

import android.text.TextUtils;

import com.sunmi.ipc.contract.IpcConfiguringContract;
import com.sunmi.ipc.rpc.IPCCloudApi;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.log.LogCat;

/**
 * Description:
 * Created by bruce on 2019/3/29.
 */
public class IpcConfiguringPresenter extends BasePresenter<IpcConfiguringContract.View>
        implements IpcConfiguringContract.Presenter {

    @Override
    public void ipcBind(String shopId, final String sn, String token, float longitude, float latitude) {
        IPCCloudApi.bindIPC(SpUtils.getCompanyId() + "", shopId, sn, TextUtils.isEmpty(token) ? 1 : 0,
                token, longitude, latitude, new RetrofitCallback() {
                    @Override
                    public void onSuccess(int code, String msg, Object data) {
                        LogCat.e("IpcConfiguringPresenter", "onSuccess 111");
                        if (mView != null)
                            mView.ipcBindWifiSuccess(sn);
                    }

                    @Override
                    public void onFail(int code, String msg, Object data) {
                        LogCat.e("IpcConfiguringPresenter", "onFail 111");
                        if (mView != null)
                            mView.ipcBindWifiFail(sn, code, msg);
                    }
                });
    }

}
