package com.sunmi.assistant.ui.activity.presenter;

import com.sunmi.assistant.rpc.CloudCall;
import com.sunmi.assistant.ui.activity.contract.PlatformMobileContract;
import com.sunmi.assistant.ui.activity.model.AuthStoreInfo;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * Created by YangShiJie on 2019/7/3.
 */
public class PlatformMobilePresenter extends BasePresenter<PlatformMobileContract.View>
        implements PlatformMobileContract.Presenter {
    //发送验证码
    @Override
    public void sendMobileCode(String mobile) {
        CloudCall.sendSaasVerifyCode(mobile, new RetrofitCallback<Object>() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.sendMobileCodeSuccess(data);
                }
            }

            @Override
            public void onFail(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.sendMobileCodeFail(code, msg);
                }
            }
        });
    }

    //验证码
    @Override
    public void checkMobileCode(String mobile, String code) {
        CloudCall.confirmSaasVerifyCode(mobile, code, new RetrofitCallback() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.checkMobileCodeSuccess(data);
                }
            }

            @Override
            public void onFail(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.checkMobileCodeFail(code, msg);
                }
            }
        });
    }

    //通过手机号获取saas信息
    @Override
    public void getSaasInfo(String mobile) {
        CloudCall.getSaasUserInfo(mobile, new RetrofitCallback<AuthStoreInfo>() {
            @Override
            public void onSuccess(int code, String msg, AuthStoreInfo data) {
                if (isViewAttached()) {
                    mView.getSaasInfoSuccess(data);
                }
            }

            @Override
            public void onFail(int code, String msg, AuthStoreInfo data) {
                if (isViewAttached()) {
                    mView.getSaasInfoFail(code, msg);
                }
            }
        });
    }

//    //默认创建门店
//    @Override
//    public void createStore(String shopName) {
//        CloudCall.createShop(SpUtils.getCompanyId() + "", shopName, new RetrofitCallback<CreateShopInfo>() {
//            @Override
//            public void onSuccess(int code, String msg, CreateShopInfo data) {
//                if (isViewAttached())
//                    mView.createStoreSuccess(data);
//            }
//
//            @Override
//            public void onFail(int code, String msg, CreateShopInfo data) {
//                if (isViewAttached())
//                    mView.createStoreFail(code, msg);
//            }
//        });
//    }
}
