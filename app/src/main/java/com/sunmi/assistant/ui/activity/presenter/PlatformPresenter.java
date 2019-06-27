package com.sunmi.assistant.ui.activity.presenter;

import com.sunmi.apmanager.rpc.merchant.MerchantApi;
import com.sunmi.assistant.ui.activity.contract.SelectPlatformContract;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.http.HttpCallback;


/**
 * 授权平台数据
 * Created by YangShiJie on 2019/6/26.
 */
public class PlatformPresenter extends BasePresenter<SelectPlatformContract.View>
        implements SelectPlatformContract.Presenter {
    @Override
    public void getPlatformInfo() {
        MerchantApi.getUserInfo(new HttpCallback<String>(mView) {

            @Override
            public void onFail(int code, String msg, String data) {
                mView.getPlatformInfoFail(code, msg);
            }

            @Override
            public void onSuccess(int code, String msg, String data) {
                mView.getPlatformInfoSuccess(data);
            }
        });
    }
}
