package com.sunmi.ipc.model;

import android.annotation.SuppressLint;

import com.sunmi.ipc.cash.model.CashVideo;
import com.sunmi.ipc.rpc.IpcCloudApi;

import java.util.HashMap;
import java.util.List;

import sunmi.common.model.CashServiceInfo;
import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-12-05.
 */
public class CashVideoModel {

    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, CashServiceInfo> map;

    public CashVideoModel(HashMap<Integer, CashServiceInfo> map) {
        this.map = map;
    }

    public void loadCashVideo(int deviceId, int videoType, long startTime, long endTime, int pageNum, int pageSize, CallBack callBack) {
        IpcCloudApi.getInstance().getCashVideoList(deviceId, videoType, startTime,
                endTime, pageNum, pageSize, new RetrofitCallback<CashVideoResp>() {
                    @Override
                    public void onSuccess(int code, String msg, CashVideoResp data) {
                        List<CashVideo> videoList = data.getAuditVideoList();
                        callBack.getCashVideoSuccess(videoList, data.getTotalCount());
                    }

                    @Override
                    public void onFail(int code, String msg, CashVideoResp data) {
                        callBack.getCashVideoFail(code, msg);
                    }
                });
    }

    public void loadAbnormalBehaviorVideo(int deviceId, long startTime, long endTime, int pageNum, int pageSize, CallBack callBack) {
        IpcCloudApi.getInstance().getAbnormalBehaviorVideoList(deviceId, startTime, endTime, pageNum,
                pageSize, new RetrofitCallback<CashVideoResp>() {
                    @Override
                    public void onSuccess(int code, String msg, CashVideoResp data) {
                        List<CashVideo> videoList = data.getAuditVideoList();
                        callBack.getCashVideoSuccess(videoList, data.getTotalCount());
                    }

                    @Override
                    public void onFail(int code, String msg, CashVideoResp data) {
                        callBack.getCashVideoFail(code, msg);
                    }
                });

    }

    public interface CallBack {
        void getCashVideoSuccess(List<CashVideo> beans, int total);

        void getCashVideoFail(int code, String msg);

    }
}
