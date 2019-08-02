package com.sunmi.assistant.ui.activity.presenter;

import com.sunmi.assistant.rpc.CloudCall;
import com.sunmi.assistant.ui.activity.contract.AuthStoreCompleteContract;
import sunmi.common.model.CreateStoreInfo;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;


/**
 * 授权门店完成
 * Created by YangShiJie on 2019/6/26.
 */
public class AuthStoreCompletePresenter extends BasePresenter<AuthStoreCompleteContract.View>
        implements AuthStoreCompleteContract.Presenter {


    @Override
    public void authStoreCompleteInfo(int shop_id, int saas_source, String shop_no, String saas_name) {
        CloudCall.authorizeSaas(SpUtils.getCompanyId() , shop_id, saas_source, shop_no, saas_name, new RetrofitCallback<Object>() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.authStoreCompleteSuccess(data);
                }
            }

            @Override
            public void onFail(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.authStoreCompleteFail(code, msg);
                }
            }
        });
    }

    @Override
    public void createStore(String shopName) {
        CloudCall.createShop(SpUtils.getCompanyId() + "", shopName, new RetrofitCallback<CreateStoreInfo>() {
            @Override
            public void onSuccess(int code, String msg, CreateStoreInfo data) {
                if (isViewAttached()) {
                    mView.createStoreSuccess(data);
                }
            }

            @Override
            public void onFail(int code, String msg, CreateStoreInfo data) {
                if (isViewAttached()) {
                    mView.createStoreFail(code, msg);
                }
            }
        });
    }

    @Override
    public void editStore(String shopName) {
        CloudCall.editShop(shopName, new RetrofitCallback<Object>() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.editStoreSuccess(data);
                }
            }

            @Override
            public void onFail(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.editStoreFail(code, msg);
                }
            }
        });
    }
}
