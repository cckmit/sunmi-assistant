package com.sunmi.ipc.presenter;

import com.sunmi.ipc.R;
import com.sunmi.ipc.contract.CashVideoListConstract;
import com.sunmi.ipc.model.CashVideoModel;
import com.sunmi.ipc.model.CashVideoResp;

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

    public CashVideoListPresenter() {
        videoModel = new CashVideoModel();
    }

    @Override
    public void load(int deviceId, int videoType, long startTime, long endTime) {
        videoModel.load(deviceId, videoType, startTime, endTime, this);
    }

    @Override
    public void loadMore() {
        videoModel.loadMore(this);
    }

    @Override
    public void getCashVideoSuccess(List<CashVideoResp.AuditVideoListBean> beans, boolean hasMore, int total) {
        if (isViewAttached()) {
            mView.getCashVideoSuccess(beans, hasMore, total);
            mView.hideLoadingDialog();
        }
    }

    @Override
    public void getCashVideofail(int code, String msg, int count) {
        if (isViewAttached()) {
            mView.shortTip(R.string.toast_network_error);
            mView.hideLoadingDialog();
            if (count == -1) {
                mView.netWorkError();
            }
        }
    }
}