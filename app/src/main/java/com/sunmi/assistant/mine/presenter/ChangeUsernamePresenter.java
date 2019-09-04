package com.sunmi.assistant.mine.presenter;

import com.sunmi.apmanager.R;
import com.sunmi.apmanager.constant.NotificationConstant;
import com.sunmi.assistant.mine.contract.ChangeUsernameContract;

import sunmi.common.base.BasePresenter;
import sunmi.common.model.UserInfoBean;
import sunmi.common.notification.BaseNotification;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.log.LogCat;

/**
 * @author bruce
 * @date 2019/1/29
 */
public class ChangeUsernamePresenter extends BasePresenter<ChangeUsernameContract.View>
        implements ChangeUsernameContract.Presenter {

    private static final String TAG = ChangeUsernamePresenter.class.getSimpleName();

    @Override
    public void getUsername() {
        SunmiStoreApi.getInstance().getUserInfo(new RetrofitCallback<UserInfoBean>() {
            @Override
            public void onSuccess(int code, String msg, UserInfoBean data) {
                CommonHelper.saveLoginInfo(data);
                if (isViewAttached()) {
                    mView.updateUsernameView(data.getUsername());
                }
            }

            @Override
            public void onFail(int code, String msg, UserInfoBean data) {
                LogCat.e(TAG, "Get user info Failed. " + msg);
                if (isViewAttached()) {
                    mView.getNameFailed();
                }
            }
        });
    }

    @Override
    public void updateUsername(final String name) {
        SunmiStoreApi.getInstance().updateUsername(name, new RetrofitCallback<Object>() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                SpUtils.setUsername(name);
                BaseNotification.newInstance().postNotificationName(
                        NotificationConstant.updateUsernameSuccess, name);
                if (isViewAttached()) {
                    mView.updateSuccess();
                }
            }

            @Override
            public void onFail(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.shortTip(R.string.tip_set_fail);
                }
            }
        });
    }
}
