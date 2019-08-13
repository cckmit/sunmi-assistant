package com.sunmi.assistant.mine.presenter;

import com.sunmi.apmanager.utils.CommonUtils;
import com.sunmi.assistant.mine.contract.MineContract;

import sunmi.common.base.BasePresenter;
import sunmi.common.model.UserInfoBean;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.log.LogCat;

/**
 * @author bruce
 * @date 2019/1/29
 */
public class MinePresenter extends BasePresenter<MineContract.View> implements MineContract.Presenter {

    private static final String TAG = MinePresenter.class.getSimpleName();

    @Override
    public void getUserInfo() {
        SunmiStoreApi.getUserInfo(new RetrofitCallback<UserInfoBean>() {
            @Override
            public void onSuccess(int code, String msg, UserInfoBean data) {
                LogCat.d(TAG, data.toString());
                saveUserInfo(data);
                if (isViewAttached()) {
                    mView.updateUserInfo();
                }
            }

            @Override
            public void onFail(int code, String msg, UserInfoBean data) {
                LogCat.e(TAG, "Get user info failed. " + msg);
            }
        });
    }

    private void saveUserInfo(UserInfoBean bean) {
        LogCat.d(TAG, "Avatar: " + bean.getOrigin_icon());
        CommonUtils.saveLoginInfo(bean);
    }

}
