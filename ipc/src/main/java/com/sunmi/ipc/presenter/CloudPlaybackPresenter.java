package com.sunmi.ipc.presenter;

import com.sunmi.ipc.contract.CloudPlaybackContract;
import com.sunmi.ipc.model.CloudTimeSlotResp;
import com.sunmi.ipc.model.VideoListResp;
import com.sunmi.ipc.rpc.IpcCloudApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;

/**
 * Description:
 * Created by bruce on 2019/8/14.
 */
public class CloudPlaybackPresenter extends BasePresenter<CloudPlaybackContract.View>
        implements CloudPlaybackContract.Presenter {

    @Override
    public void getTimeSlots(int deviceId, final long startTime, final long endTime) {
        IpcCloudApi.getInstance().getTimeSlots(deviceId, startTime, endTime,
                new RetrofitCallback<CloudTimeSlotResp>() {
                    @Override
                    public void onSuccess(int code, String msg, CloudTimeSlotResp data) {
                        if (isViewAttached()) {
                            if (data.getTotalCount() == 0) {
                                mView.showNoVideoTip();
                            } else {
                                mView.getCloudTimeSlotSuccess(startTime, endTime, data.getTimeslots());
                            }
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, CloudTimeSlotResp data) {
                        if (isViewAttached()) {
                            if (5087 == code) {//no matched record
                                mView.showNoVideoTip();
//                            } else if (5019 == code) {//failed to find ipc device info
                            } else {
                                mView.getCloudTimeSlotFail();
                            }
                        }
                    }
                });
    }

    @Override
    public void getCloudVideoList(int deviceId, long start, long end) {
        if (deviceId <= 0) {
            mView.shortTip("设备信息不完整");
            return;
        }
        IpcCloudApi.getInstance().getVideoList(deviceId, start, end, new RetrofitCallback<VideoListResp>() {
            @Override
            public void onSuccess(int code, String msg, VideoListResp data) {
                if (isViewAttached()) {
                    mView.getCloudVideosSuccess(data.getVideo_list());
                }
            }

            @Override
            public void onFail(int code, String msg, VideoListResp data) {
                if (isViewAttached()) {
                    mView.getCloudVideosFail();
                }
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
