package com.sunmi.assistant.mine.presenter;

import com.sunmi.assistant.mine.contract.UserInfoContract;

import java.io.File;

import sunmi.common.base.BasePresenter;
import sunmi.common.model.UserAvatarResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.log.LogCat;

/**
 * @author bruce
 * @date 2019/1/24
 */
public class UserInfoPresenter extends BasePresenter<UserInfoContract.View>
        implements UserInfoContract.Presenter {

    private static final String TAG = UserInfoPresenter.class.getSimpleName();

    @Override
    public void updateAvatar(File file) {
        SunmiStoreApi.updateIcon("t.png", file, new RetrofitCallback<UserAvatarResp>() {
            @Override
            public void onSuccess(int code, String msg, UserAvatarResp data) {
                if (isViewAttached()) {
                    mView.updateAvatarView(data.getOriginIcon());
                }
            }

            @Override
            public void onFail(int code, String msg, UserAvatarResp data) {
                LogCat.e(TAG, "Update avatar Failed. " + msg);
            }
        });
    }
}
