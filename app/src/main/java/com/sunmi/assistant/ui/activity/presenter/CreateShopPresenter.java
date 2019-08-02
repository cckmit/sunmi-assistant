package com.sunmi.assistant.ui.activity.presenter;

import com.sunmi.assistant.R;
import com.sunmi.assistant.rpc.CloudCall;
import com.sunmi.assistant.ui.activity.contract.CreateShopContract;

import sunmi.common.base.BasePresenter;
import sunmi.common.model.CreateShopInfo;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.log.LogCat;

/**
 * 创建门店
 * Created by YangShiJie on 2019/6/26.
 */
public class CreateShopPresenter extends BasePresenter<CreateShopContract.View>
        implements CreateShopContract.Presenter {
    private static final String TAG = CreateShopPresenter.class.getSimpleName();

    @Override
    public void createShop(String name, String contact, String mobile) {
        mView.showLoadingDialog();
        CloudCall.createShop(SpUtils.getCompanyId() + "",
                name, contact, mobile, new RetrofitCallback<CreateShopInfo>() {
                    @Override
                    public void onSuccess(int code, String msg, CreateShopInfo data) {
                        if (isViewAttached()) {
                            mView.hideLoadingDialog();
                            mView.createShopSuccessView(data);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, CreateShopInfo data) {
                        LogCat.e(TAG, "getSaas  Failed code=" + code + "; msg=" + msg);
                        if (isViewAttached()) {
                            mView.hideLoadingDialog();
                            mView.shortTip(R.string.str_create_store_fail);
                        }
                    }
                });
    }
}
