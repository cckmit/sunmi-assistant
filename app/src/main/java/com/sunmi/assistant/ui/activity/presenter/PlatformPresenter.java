package com.sunmi.assistant.ui.activity.presenter;

import com.sunmi.assistant.rpc.CloudCall;
import com.sunmi.assistant.ui.activity.contract.SelectPlatformContract;
import com.sunmi.assistant.ui.activity.model.PlatformInfo;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.retrofit.RetrofitCallback;


/**
 * 授权平台数据
 * Created by YangShiJie on 2019/6/26.
 */
public class PlatformPresenter extends BasePresenter<SelectPlatformContract.View>
        implements SelectPlatformContract.Presenter {
    @Override
    public void getPlatformInfo() {
        CloudCall.getPlatformList(new RetrofitCallback<PlatformInfo>() {
            @Override
            public void onSuccess(int code, String msg, PlatformInfo data) {
                if (isViewAttached())
                    mView.getPlatformInfoSuccess(data);
            }

            @Override
            public void onFail(int code, String msg, PlatformInfo data) {
                if (isViewAttached())
                    mView.getPlatformInfoFail(code, msg);
            }
        });
    }
}
