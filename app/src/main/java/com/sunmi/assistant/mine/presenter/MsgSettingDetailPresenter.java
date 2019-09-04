package com.sunmi.assistant.mine.presenter;

import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.contract.MsgSettingDetailContract;
import com.sunmi.assistant.rpc.MessageCenterApi;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-23.
 */
public class MsgSettingDetailPresenter extends BasePresenter<MsgSettingDetailContract.View>
        implements MsgSettingDetailContract.Presenter {

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
                    mView.shortTip(R.string.str_modify_success);
                    mView.updateSettingStatusSuccess(settingId, status);
                }
            }

            @Override
            public void onFail(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.shortTip(R.string.toast_network_error);
                    mView.updateSettingStatusFail(settingId, status);
                }
            }
        });
    }
}
