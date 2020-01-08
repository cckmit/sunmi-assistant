package com.sunmi.ipc.presenter;

import com.sunmi.ipc.contract.SDCardPlaybackContract;
import com.sunmi.ipc.model.IotcCmdResp;
import com.sunmi.ipc.model.VideoTimeSlotBean;
import com.sunmi.ipc.utils.IOTCClient;
import com.tutk.IOTC.P2pCmdCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.utils.SpUtils;

/**
 * Description:
 * Created by bruce on 2019/12/3.
 */
public class SDCardPlaybackPresenter extends BasePresenter<SDCardPlaybackContract.View>
        implements SDCardPlaybackContract.Presenter {

    private List<VideoTimeSlotBean> allTimeSlotBeans = new ArrayList<>();

    @Override
    public void getPlaybackList(IOTCClient iotcClient, long start, long end) {
        if (iotcClient == null) {
            return;
        }
        iotcClient.getPlaybackList(start, end, new P2pCmdCallback<List<VideoTimeSlotBean>>() {
            @Override
            public void onResponse(int cmd, IotcCmdResp<List<VideoTimeSlotBean>> result) {
                List<VideoTimeSlotBean> slots = new ArrayList<>();
                if (result.getData() != null && result.getData().size() > 0) {
                    for (VideoTimeSlotBean slotBean : result.getData().get(0).getResult()) {
                        slotBean.setApPlay(true);
                        slots.add(slotBean);
                    }
                }
                if (isViewAttached()) {
                    mView.getDeviceTimeSlotSuccess(slots);
                }
            }

            @Override
            public void onError() {
                if (isViewAttached()) {
                    mView.getDeviceTimeSlotSuccess(null);
                }
            }
        });
    }

    public void getPlaybackListForCalendar(IOTCClient iotcClient, long start, long end) {
        if (iotcClient == null) {
            return;
        }
        iotcClient.getPlaybackList(start, end, new P2pCmdCallback<List<VideoTimeSlotBean>>() {
            @Override
            public void onResponse(int cmd, IotcCmdResp<List<VideoTimeSlotBean>> result) {
                if (result.getData() != null && result.getData().size() > 0
                        && result.getData().get(0).getResult().size() > 0) {
                    allTimeSlotBeans.addAll(result.getData().get(0).getResult());
                    getPlaybackListForCalendar(iotcClient,
                            allTimeSlotBeans.get(allTimeSlotBeans.size() - 1).getEndTime(), end);
                } else {
                    if (isViewAttached()) {
                        mView.getAllTimeSlotSuccess(allTimeSlotBeans);
                    }
                }
            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    public void startPlayback(IOTCClient iotcClient, long start, long end) {
        if (iotcClient == null) {
            return;
        }
        iotcClient.startPlayback(start, end, new P2pCmdCallback<List<VideoTimeSlotBean>>() {
            @Override
            public void onResponse(int cmd, IotcCmdResp<List<VideoTimeSlotBean>> result) {
                if (isViewAttached()) {
                    mView.startPlaybackSuccess();
                }
            }

            @Override
            public void onError() {

            }
        });
    }

    public String getCloudStorageParams(String deviceSn) {
        String params = "";
        try {
            ArrayList<String> snList = new ArrayList<>();
            snList.add(deviceSn);
            JSONObject userInfo = new JSONObject()
                    .put("token", SpUtils.getStoreToken())
                    .put("company_id", SpUtils.getCompanyId())
                    .put("shop_id", SpUtils.getShopId());
            JSONObject cloudStorage = new JSONObject()
                    .put("sn_list", new JSONArray(snList))
                    .put("productNo", "");
            params = new JSONObject()
                    .put("userInfo", userInfo)
                    .put("cloudStorage", cloudStorage)
                    .toString();
            return params;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return params;
    }
}
