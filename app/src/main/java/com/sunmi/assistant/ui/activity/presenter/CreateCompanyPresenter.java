package com.sunmi.assistant.ui.activity.presenter;

import com.sunmi.assistant.ui.activity.contract.CreateCompanyContract;

import sunmi.common.base.BasePresenter;
import sunmi.common.model.CompanyInfoResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.log.LogCat;

/**
 * @author yangShiJie
 * @date 2019/8/1
 */
public class CreateCompanyPresenter extends BasePresenter<CreateCompanyContract.View>
        implements CreateCompanyContract.Presenter {
    private static final String TAG = CreateCompanyPresenter.class.getSimpleName();

    /**
     * 创建商户
     *
     * @param name 商户名称
     */
    @Override
    public void createCompany(String name) {
        mView.showLoadingDialog();
        SunmiStoreApi.getInstance().createCompany(name, new RetrofitCallback<CompanyInfoResp>() {
            @Override
            public void onSuccess(int code, String msg, CompanyInfoResp data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.createCompanySuccessView(data);
                }
            }

            @Override
            public void onFail(int code, String msg, CompanyInfoResp data) {
                LogCat.e(TAG, "Company  Failed code=" + code + "; msg=" + msg);
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.createCompanyFailView(code, msg);
                }
            }
        });

    }
}
