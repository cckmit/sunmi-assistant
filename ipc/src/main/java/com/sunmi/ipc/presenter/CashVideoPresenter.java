package com.sunmi.ipc.presenter;

import com.sunmi.ipc.R;
import com.sunmi.ipc.cash.model.CashBox;
import com.sunmi.ipc.cash.model.CashTagFilter;
import com.sunmi.ipc.cash.model.CashVideo;
import com.sunmi.ipc.config.IpcConstants;
import com.sunmi.ipc.contract.CashVideoContract;
import com.sunmi.ipc.model.CashOrderResp;
import com.sunmi.ipc.model.CashVideoEventResp;
import com.sunmi.ipc.model.CashVideoResp;
import com.sunmi.ipc.rpc.IpcCloudApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sunmi.common.base.BasePresenter;
import sunmi.common.model.CashServiceInfo;
import sunmi.common.model.ServiceResp;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.log.LogCat;

/**
 * @author yangShiJie
 * @date 2019-12-05
 */
public class CashVideoPresenter extends BasePresenter<CashVideoContract.View>
        implements CashVideoContract.Presenter {

    private static final String TAG = CashVideoPresenter.class.getSimpleName();

    @Override
    public void updateTag(long videoId, int source, CashTagFilter selected) {
        int videoType;
        List<Integer> videoTags = new ArrayList<>(1);
        String desc = null;
        if (selected.getId() == CashTagFilter.TAG_ID_NORMAL) {
            videoType = IpcConstants.CASH_VIDEO_NORMAL;
        } else {
            videoType = IpcConstants.CASH_VIDEO_ABNORMAL;
            videoTags.add(selected.getId());
            if (selected.getId() == CashTagFilter.TAG_ID_CUSTOM) {
                desc = selected.getDesc();
            }
        }

        IpcCloudApi.getInstance().updateTag(videoId, source, videoType, videoTags, desc, new RetrofitCallback<Object>() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.updateTagSuccess(selected);
                }
            }

            @Override
            public void onFail(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.updateTagFail(code, msg, selected);
                }
            }
        });
    }

    @Override
    public void getOrderInfo(String orderNo) {
        IpcCloudApi.getInstance().getOrderInfo(orderNo, new RetrofitCallback<CashOrderResp>() {
            @Override
            public void onSuccess(int code, String msg, CashOrderResp data) {
                if (isViewAttached()) {
                    mView.getOrderInfoSuccess(data);
                }
            }

            @Override
            public void onFail(int code, String msg, CashOrderResp data) {
                LogCat.e(TAG, "getOrderInfo code=" + code + ", msg=" + msg);
                if (isViewAttached()) {
                    mView.getOrderInfoFail(code, msg);
                }
            }
        });
    }

    /**
     * 获取视频列表
     */
    @Override
    public void getCashVideoList(Map<Integer, CashServiceInfo> ipcName, int deviceId, int videoType,
                                 long startTime, long endTime, int pageNum, int pageSize) {
        IpcCloudApi.getInstance().getCashVideoList(deviceId, videoType, startTime,
                endTime, pageNum, pageSize, new RetrofitCallback<CashVideoResp>() {
                    @Override
                    public void onSuccess(int code, String msg, CashVideoResp data) {
                        List<CashVideo> videoList = data.getAuditVideoList();
                        for (CashVideo video : videoList) {
                            CashServiceInfo bean = ipcName.get(video.getDeviceId());
                            if (bean != null) {
                                video.setDeviceName(bean.getDeviceName());
                                video.setHasCashLossPrevent(bean.isHasCashLossPrevention());
                            }
                        }
                        if (isViewAttached()) {
                            mView.cashVideoListSuccess(videoList);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, CashVideoResp data) {
                        LogCat.e(TAG, "getCashVideoList code=" + code + ", msg=" + msg);
                        if (isViewAttached()) {
                            mView.cashVideoListFail(code, msg);
                        }
                    }
                });
    }

    @Override
    public void getAbnormalBehaviorList(Map<Integer, CashServiceInfo> ipcName, int deviceId, int videoType,
                                        long startTime, long endTime, int pageNum, int pageSize) {
        IpcCloudApi.getInstance().getAbnormalBehaviorVideoList(deviceId, startTime,
                endTime, pageNum, pageSize, new RetrofitCallback<CashVideoResp>() {
                    @Override
                    public void onSuccess(int code, String msg, CashVideoResp data) {
                        List<CashVideo> videoList = data.getAuditVideoList();
                        for (CashVideo video : videoList) {
                            CashServiceInfo bean = ipcName.get(video.getDeviceId());
                            if (bean != null) {
                                video.setDeviceName(bean.getDeviceName());
                                video.setHasCashLossPrevent(bean.isHasCashLossPrevention());
                            }
                        }
                        if (isViewAttached()) {
                            mView.cashVideoListSuccess(videoList);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, CashVideoResp data) {
                        LogCat.e(TAG, "getAbnormalBehaviorList code=" + code + ", msg=" + msg);
                        if (isViewAttached()) {
                            mView.cashVideoListFail(code, msg);
                        }
                    }
                });
    }

    @Override
    public void getStorageList(String deviceSn) {
        List<String> snList = new ArrayList<>();
        snList.add(deviceSn);
        IpcCloudApi.getInstance().getStorageList(snList, new RetrofitCallback<ServiceResp>() {
            @Override
            public void onSuccess(int code, String msg, ServiceResp data) {
                if (isViewAttached()) {
                    if (data.getList().size() > 0) {
                        mView.getStorageSuccess(data.getList().get(0));
                    } else {
                        mView.shortTip(R.string.tip_cloud_storage_error);
                    }
                }
            }

            @Override
            public void onFail(int code, String msg, ServiceResp data) {
                if (isViewAttached()) {
                    mView.shortTip(R.string.tip_cloud_storage_error);
                }
            }
        });
    }

    @Override
    public void getAbnormalEvent(long eventId, long beginTime) {
        final long begin = beginTime * 1000;
        IpcCloudApi.getInstance().getCashVideoAbnormalEvent(eventId, new RetrofitCallback<CashVideoEventResp>() {
            @Override
            public void onSuccess(int code, String msg, CashVideoEventResp data) {
                if (data == null) {
                    if (isViewAttached()) {
                        mView.getAbnormalEventSuccess(-1, null);
                    }
                    return;
                }
                List<CashBox> result = new ArrayList<>();
                List<CashVideoEventResp.Box> boxes = data.getKeyObjects();
                for (CashVideoEventResp.Box box : boxes) {
                    double[] timestamp = box.getTimestamp();
                    int start = (int) (timestamp[0] * 1000 - begin);
                    int end = (int) (timestamp[1] * 1000 - begin);
                    if (end <= 0) {
                        continue;
                    }
                    start = Math.max(0, start);
                    CashBox item = new CashBox(start, end, box.getBox());
                    result.add(item);
                }
                if (isViewAttached()) {
                    mView.getAbnormalEventSuccess(data.getRiskScore(), result);
                }
            }

            @Override
            public void onFail(int code, String msg, CashVideoEventResp data) {
                if (isViewAttached()) {
                    mView.getAbnormalEventFail(code, msg);
                }
            }
        });
    }
}
