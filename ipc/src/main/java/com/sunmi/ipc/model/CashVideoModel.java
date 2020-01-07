package com.sunmi.ipc.model;

import android.annotation.SuppressLint;

import com.sunmi.ipc.cash.model.CashVideo;
import com.sunmi.ipc.rpc.IpcCloudApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sunmi.common.model.CashVideoServiceBean;
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
    private HashMap<Integer, CashVideoServiceBean> map = new HashMap<>();
    private ArrayList<CashVideoServiceBean> beans;

    public CashVideoModel(ArrayList<CashVideoServiceBean> beans) {
        this.beans = beans;
        initMap();
    }

    private void initMap() {
        ThreadPool.getSingleThreadPool().submit(new Runnable() {
            @Override
            public void run() {
                for (CashVideoServiceBean bean : beans) {
                    map.put(bean.getDeviceId(), bean);
                }
            }
        });
    }

    public void loadCashVideo(int deviceId, int videoType, long startTime, long endTime, int pageNum, int pageSize, CallBack callBack) {
        IpcCloudApi.getInstance().getCashVideoList(deviceId, videoType, startTime,
                endTime, pageNum, pageSize, new RetrofitCallback<CashVideoResp>() {
                    @Override
                    public void onSuccess(int code, String msg, CashVideoResp data) {
                        List<CashVideo> videoList = data.getAuditVideoList();
                        int n = videoList.size();
                        if (n > 0) {
                            for (int i = 0; i < videoList.size(); i++) {
                                CashVideoServiceBean serviceBean = map.get(videoList.get(i).getDeviceId());
                                if (serviceBean!=null){
                                    videoList.get(i).setDeviceName(serviceBean.getDeviceName());
                                    videoList.get(i).setHasCashLossPrevent(serviceBean.isHasCashLossPrevent());
                                }
                            }
                        }
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
                        int n = videoList.size();
                        if (n > 0) {
                            for (int i = 0; i < videoList.size(); i++) {
                                CashVideoServiceBean serviceBean = map.get(videoList.get(i).getDeviceId());
                                if (serviceBean!=null){
                                    videoList.get(i).setDeviceName(serviceBean.getDeviceName());
                                    videoList.get(i).setHasCashLossPrevent(serviceBean.isHasCashLossPrevent());
                                }
                            }
                        }
                        callBack.getCashVideoSuccess(videoList, data.getTotalCount());
                    }

                    @Override
                    public void onFail(int code, String msg, CashVideoResp data) {
                        callBack.getCashVideoFail(code, msg);
                    }
                });

    }


    public HashMap<Integer, CashVideoServiceBean> getIpcNameMap() {
        return map;
    }

    public interface CallBack {
        void getCashVideoSuccess(List<CashVideo> beans, int total);

        void getCashVideoFail(int code, String msg);

    }
}
