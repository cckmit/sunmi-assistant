package com.sunmi.ipc.presenter;

import android.text.TextUtils;

import com.sunmi.ipc.contract.IpcSettingWiFiContract;
import com.sunmi.ipc.model.IpcListResp;
import com.sunmi.ipc.rpc.IpcCloudApi;

import java.util.List;

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
        IpcCloudApi.getDetailList(SpUtils.getCompanyId(), SpUtils.getShopId(),
                new RetrofitCallback<IpcListResp>() {
                    @Override
                    public void onSuccess(int code, String msg, IpcListResp data) {
                        if (DeviceTypeUtils.getInstance().isFS1(model)) {
                            ipcStatusSuccessView(deviceid, data.getFs_list());
                        } else if (DeviceTypeUtils.getInstance().isSS1(model)) {
                            ipcStatusSuccessView(deviceid, data.getSs_list());
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, IpcListResp data) {
                    }
                });
    }

    private void ipcStatusSuccessView(final String deviceid, List<IpcListResp.SsListBean> list) {
        if (list != null && list.size() > 0) {
            for (IpcListResp.SsListBean bean : list) {
                if (isViewAttached() && TextUtils.equals(deviceid, bean.getSn())) {
                    mView.ipcStatusSuccessView(bean.getActive_status());
                    return;
                }
            }
        }
    }
}
