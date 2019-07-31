package com.sunmi.ipc.setting;

import com.sunmi.ipc.R;
import com.sunmi.ipc.model.IpcNewFirmwareResp;
import com.sunmi.ipc.rpc.IPCCall;
import com.sunmi.ipc.rpc.IPCCloudApi;

import sunmi.common.base.BasePresenter;
import sunmi.common.constant.CommonConstants;
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
        mView.showLoadingDialog();
        IPCCall.getInstance().getIpcNightIdeRotation(mView.getContext(), mDevice.getModel(), mDevice.getDeviceid());
        IPCCall.getInstance().getIpcDetection(mView.getContext(), mDevice.getModel(), mDevice.getDeviceid());
        SunmiDevice localDevice = CommonConstants.SUNMI_DEVICE_MAP.get(mDevice.getDeviceid());
        SunmiDevice bean = CommonConstants.SUNMI_DEVICE_MAP.get(mDevice.getDeviceid());
        if (localDevice != null) {
            // ipc连接wifi信息 有线.无线
            IPCCall.getInstance().getIsWire(mView.getContext(), bean.getIp());
        }
    }

    @Override
    public void updateName(final String name) {
        IPCCloudApi.updateBaseInfo(SpUtils.getCompanyId(), SpUtils.getShopId(), mDevice.getId(),
                name, new RetrofitCallback<Object>() {
                    @Override
                    public void onSuccess(int code, String msg, Object data) {
                        mDevice.setName(name);
                        if (isViewAttached()) {
                            mView.hideLoadingDialog();
                            mView.updateNameView(name);
                            mView.shortTip(R.string.ipc_setting_success);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, Object data) {
                        LogCat.e(TAG, "Update IPC name Failed. code=" + code + "; msg=" + msg);
                        if (isViewAttached()) {
                            mView.hideLoadingDialog();
                            mView.shortTip(R.string.ipc_setting_fail);
                        }
                    }
                });
    }

    @Override
    public void currentVersion() {
        IPCCloudApi.newFirmware(SpUtils.getCompanyId(), SpUtils.getShopId(), mDevice.getId(), new RetrofitCallback<IpcNewFirmwareResp>() {
            @Override
            public void onSuccess(int code, String msg, IpcNewFirmwareResp data) {
                if (isViewAttached()) {
                    mView.currentVersionView(data);
                }
            }

            @Override
            public void onFail(int code, String msg, IpcNewFirmwareResp data) {
                LogCat.e(TAG, "IPC currentVersion Failed. code=" + code + "; msg=" + msg);
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.shortTip(R.string.str_net_exception);
                }
            }
        });
    }

}
