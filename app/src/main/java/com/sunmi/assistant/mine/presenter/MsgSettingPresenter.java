package com.sunmi.assistant.mine.presenter;

import com.sunmi.assistant.mine.contract.MsgSettingContract;
import com.sunmi.assistant.mine.model.MsgSettingListBean;
import com.sunmi.assistant.rpc.MessageCenterApi;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-23.
 */
public class MsgSettingPresenter extends BasePresenter<MsgSettingContract.View>
        implements MsgSettingContract.Presenter {

    @Override
    public void getSettingList() {
        if (isViewAttached()) {
            mView.showLoadingDialog();
        }
        MessageCenterApi.getInstance().getSettingList(new RetrofitCallback<MsgSettingListBean>() {
            @Override
            public void onSuccess(int code, String msg, MsgSettingListBean data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.getSettingListSuccess(data.getReminderSettingList());
                }
            }

            @Override
            public void onFail(int code, String msg, MsgSettingListBean data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.getSettingListFail(code, msg);
                }
            }
        });
    }

    @Override
    public void updateSettingStatus(int settingId, int status) {
        if (isViewAttached()) {
            mView.showLoadingDialog();
        }
        MessageCenterApi.getInstance().updateSettingStatus(settingId, status, new RetrofitCallback<Object>() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.updateSettingStatusSuccess();
                }
            }

            @Override
            public void onFail(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.getSettingListFail(code, msg);
                }
            }
        });
    }
}
