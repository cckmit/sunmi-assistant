package com.sunmi.ipc.presenter;

import android.text.TextUtils;

import com.sunmi.ipc.contract.IpcSettingWiFiContract;
import com.sunmi.ipc.model.IpcListResp;
import com.sunmi.ipc.rpc.IPCCloudApi;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.DeviceTypeUtils;
import sunmi.common.utils.SpUtils;

/**
 * Created by YangShiJie on 2019/7/15.
 */
public class IpcSettingWifiPresenter extends BasePresenter<IpcSettingWiFiContract.View>
        implements IpcSettingWiFiContract.Presenter {

    @Override
    public void getIpcStatus(final String deviceid, final String model) {
        IPCCloudApi.getDetailList(SpUtils.getCompanyId(), SpUtils.getShopId(),
                new RetrofitCallback<IpcListResp>() {
                    @Override
                    public void onSuccess(int code, String msg, IpcListResp data) {
                        if (DeviceTypeUtils.getInstance().isFS1(model)) {
                            if (data.getFs_list() != null && data.getFs_list().size() > 0) {
                                for (IpcListResp.SsListBean bean : data.getFs_list()) {
                                    if (isViewAttached() && TextUtils.equals(deviceid, bean.getSn())) {
                                        mView.ipcStatusSuccessView(bean.getActive_status());
                                    }
                                }
                            }
                        } else if (DeviceTypeUtils.getInstance().isSS1(model)) {
                            if (data.getSs_list() != null && data.getSs_list().size() > 0) {
                                for (IpcListResp.SsListBean bean : data.getSs_list()) {
                                    if (isViewAttached() && TextUtils.equals(deviceid, bean.getSn())) {
                                        mView.ipcStatusSuccessView(bean.getActive_status());
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, IpcListResp data) {
                    }
                });
    }
}
