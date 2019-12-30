package com.sunmi.ipc.model;

import android.annotation.SuppressLint;

import com.sunmi.ipc.rpc.IpcCloudApi;

import org.litepal.crud.DataSupport;

import java.util.HashMap;
import java.util.List;

import sunmi.common.model.SunmiDevice;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.ThreadPool;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-12-05.
 */
public class CashVideoModel {

    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, String> ipcName = new HashMap<>();

    public CashVideoModel() {
        initMap();
    }

    private void initMap() {
        ThreadPool.getSingleThreadPool().submit(new Runnable() {
            @Override
            public void run() {
                List<SunmiDevice> devices = DataSupport.where("type=?", "IPC").find(SunmiDevice.class);
                for (SunmiDevice device : devices) {
                    ipcName.put(device.getId(), device.getName());
                }
            }
        });
    }

    public void loadCashVideo(int deviceId, int videoType, long startTime, long endTime, int pageNum, int pageSize, CallBack callBack) {
        IpcCloudApi.getInstance().getCashVideoList(deviceId, videoType, startTime,
                endTime, pageNum, pageSize, new RetrofitCallback<CashVideoResp>() {
                    @Override
                    public void onSuccess(int code, String msg, CashVideoResp data) {
                        List<CashVideoResp.AuditVideoListBean> beans = data.getAuditVideoList();
                        int n = beans.size();
                        if (n > 0) {
                            for (int i = 0; i < beans.size(); i++) {
                                beans.get(i).setDeviceName(ipcName.get(beans.get(i).getDeviceId()));
                            }
                        }
                        callBack.getCashVideoSuccess(beans, data.getTotalCount());
                    }

                    @Override
                    public void onFail(int code, String msg, CashVideoResp data) {
                        callBack.getCashVideoFail(code, msg);
                    }
                });
    }

    public void AbnormalBehaviorVideo(int deviceId, long startTime, long endTime, int pageNum, int pageSize, CallBack callBack) {
        IpcCloudApi.getInstance().getAbnormalBehaviorVideoList(deviceId, startTime, endTime, pageNum, pageSize, new RetrofitCallback<CashVideoResp>() {
            @Override
            public void onSuccess(int code, String msg, CashVideoResp data) {
                List<CashVideoResp.AuditVideoListBean> beans = data.getAuditVideoList();
                int n = beans.size();
                if (n > 0) {
                    for (int i = 0; i < beans.size(); i++) {
                        beans.get(i).setDeviceName(ipcName.get(beans.get(i).getDeviceId()));
                    }
                }
                callBack.getCashVideoSuccess(beans, data.getTotalCount());
            }

            @Override
            public void onFail(int code, String msg, CashVideoResp data) {
                callBack.getCashVideoFail(code, msg);
            }
        });

    }


    public HashMap<Integer, String> getIpcNameMap() {
        return ipcName;
    }

    public interface CallBack {
        void getCashVideoSuccess(List<CashVideoResp.AuditVideoListBean> beans, int total);

        void getCashVideoFail(int code, String msg);

    }
}
