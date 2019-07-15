package com.sunmi.ipc.ipcset;

import com.sunmi.ipc.R;
import com.sunmi.ipc.rpc.IPCCloudApi;

import sunmi.common.base.BasePresenter;
import sunmi.common.model.SunmiDevice;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.log.LogCat;

/**
 * @author yinhui
 * @since 2019-07-15
 */
public class IpcSettingPresenter extends BasePresenter<IpcSettingContract.View>
        implements IpcSettingContract.Presenter {

    private static final String TAG = IpcSettingPresenter.class.getSimpleName();
    private SunmiDevice mDevice;

    @Override
    public void loadConfig(SunmiDevice device) {
        this.mDevice = device;
        // TODO: api，拉取摄像头信息
    }

    @Override
    public void updateName(final String name) {
        IPCCloudApi.updateBaseInfo(SpUtils.getCompanyId(), SpUtils.getShopId(), mDevice.getId(),
                name, new RetrofitCallback<Object>() {
                    @Override
                    public void onSuccess(int code, String msg, Object data) {
                        mDevice.setName(name);
                        if (isViewAttached()) {
                            mView.updateNameView(name);
                            mView.shortTip(R.string.ipc_setting_success);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, Object data) {
                        LogCat.e(TAG, "Update IPC name Failed. code=" + code + "; msg=" + msg);
                        if (isViewAttached()) {
                            mView.shortTip(R.string.ipc_setting_fail);
                        }
                    }
                });
    }

}
