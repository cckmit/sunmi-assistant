package com.sunmi.assistant.mine.presenter;

import com.sunmi.apmanager.constant.NotificationConstant;
import com.sunmi.assistant.R;
import com.sunmi.assistant.data.SunmiStoreRemote;
import com.sunmi.assistant.mine.contract.ChangeCompanyNameContract;

import sunmi.common.base.BasePresenter;
import sunmi.common.model.CompanyInfoResp;
import sunmi.common.notification.BaseNotification;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.log.LogCat;

/**
 * @author bruce
 * @date 2019/6/6
 */
public class ChangeCompanyNamePresenter extends BasePresenter<ChangeCompanyNameContract.View>
        implements ChangeCompanyNameContract.Presenter {

    private static final String TAG = ChangeCompanyNamePresenter.class.getSimpleName();

    @Override
    public void getCompanyInfo() {
        SunmiStoreRemote.get().getCompanyInfo(SpUtils.getCompanyId(), new RetrofitCallback<CompanyInfoResp>() {
            @Override
            public void onSuccess(int code, String msg, CompanyInfoResp data) {
                if (isViewAttached()) {
                    mView.updateNameView(data.getCompany_name());
                }
            }

            @Override
            public void onFail(int code, String msg, CompanyInfoResp data) {
                LogCat.e(TAG, "Get company name Failed. " + msg);
                if (isViewAttached()) {
                    mView.getNameFailed();
                }
            }
        });
    }

    @Override
    public void updateCompanyName(String name) {
        SunmiStoreApi.updateCompanyName(SpUtils.getCompanyId(), name, new RetrofitCallback<CompanyInfoResp>() {
            @Override
            public void onSuccess(int code, String msg, CompanyInfoResp data) {
                SpUtils.setCompanyName(name);
                BaseNotification.newInstance().postNotificationName(NotificationConstant.companyNameChanged);
                if (isViewAttached()) {
                    mView.updateSuccess();
                }
            }

            @Override
            public void onFail(int code, String msg, CompanyInfoResp data) {
                LogCat.e(TAG, "Update company name Failed. " + msg);
                if (isViewAttached()) {
                    mView.shortTip(R.string.tip_set_fail);
                }
            }
        });

    }
}
