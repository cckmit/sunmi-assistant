package com.sunmi.ipc.presenter;

import com.sunmi.ipc.R;
import com.sunmi.ipc.contract.CashVideoListConstract;
import com.sunmi.ipc.model.CashVideoModel;
import com.sunmi.ipc.model.CashVideoResp;

import java.util.HashMap;
import java.util.List;

import sunmi.common.base.BasePresenter;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-12-06.
 */
public class CashVideoListPresenter extends BasePresenter<CashVideoListConstract.View>
        implements CashVideoListConstract.Presenter, CashVideoModel.CallBack {

    private CashVideoModel videoModel;
    private int deviceId, videoType;
    private long startTime, endTime;

    public CashVideoListPresenter() {
        videoModel = new CashVideoModel();
    }

    @Override
    public void load(int deviceId, int videoType, long startTime, long endTime, int pageNum, int pageSize) {
        this.deviceId = deviceId;
        this.videoType = videoType;
        this.startTime = startTime;
        this.endTime = endTime;
        videoModel.load(deviceId, videoType, startTime, endTime, pageNum, pageSize, this);
    }

    @Override
    public void loadMore(int pageNum, int pageSize) {
        videoModel.load(deviceId, videoType, startTime, endTime, pageNum, pageSize, this);
    }

    @Override
    public void getCashVideoSuccess(List<CashVideoResp.AuditVideoListBean> beans, int total) {
        if (isViewAttached()) {
            mView.getCashVideoSuccess(beans, total);
            mView.hideLoadingDialog();
            mView.endRefresh();
        }
    }

    @Override
    public void getCashVideoFail(int code, String msg) {
        if (isViewAttached()) {
            mView.shortTip(R.string.toast_network_error);
            mView.hideLoadingDialog();
            mView.endRefresh();
            mView.netWorkError();
        }
    }

    public HashMap<Integer, String> getIpcName() {
        return videoModel.getIpcNameMap();
    }

}
