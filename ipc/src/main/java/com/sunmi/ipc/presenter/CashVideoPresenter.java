package com.sunmi.ipc.presenter;

import com.sunmi.ipc.contract.CashVideoContract;
import com.sunmi.ipc.model.CashOrderResp;
import com.sunmi.ipc.model.CashVideoResp;
import com.sunmi.ipc.rpc.IpcCloudApi;

import java.util.List;
import java.util.Map;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.log.LogCat;

/**
 * @author yangShiJie
 * @date 2019-12-05
 */
public class CashVideoPresenter extends BasePresenter<CashVideoContract.View>
        implements CashVideoContract.Presenter {
    private static final String TAG = "CashVideoPresenter";

    @Override
    public void updateTag(int auditVideoId, String description, int videoType) {
        IpcCloudApi.getInstance().updateTag(auditVideoId, description, videoType, new RetrofitCallback<Object>() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.updateTagSuccess(videoType, description);
                }
            }

            @Override
            public void onFail(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.updateTagFail(code, msg, videoType);
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
    public void getCashVideoList(Map<Integer, String> ipcName, int deviceId, int videoType,
                                 long startTime, long endTime, int pageNum, int pageSize) {
        IpcCloudApi.getInstance().getCashVideoList(deviceId, videoType, startTime,
                endTime, pageNum, pageSize, new RetrofitCallback<CashVideoResp>() {
                    @Override
                    public void onSuccess(int code, String msg, CashVideoResp data) {
                        List<CashVideoResp.AuditVideoListBean> videoList = data.getAuditVideoList();
                        int mSize = videoList.size();
                        if (mSize > 0) {
                            for (int i = 0; i < mSize; i++) {
                                videoList.get(i).setDeviceName(ipcName.get(videoList.get(i).getDeviceId()));
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


}
