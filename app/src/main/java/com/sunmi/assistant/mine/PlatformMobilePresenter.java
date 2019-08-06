package com.sunmi.assistant.mine;

import com.sunmi.assistant.rpc.CloudCall;
import com.sunmi.assistant.ui.activity.model.AuthStoreInfo;

import java.util.ArrayList;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.log.LogCat;

/**
 *
 * @author YangShiJie
 * @date 2019/7/3
 */
public class PlatformMobilePresenter extends BasePresenter<PlatformMobileContract.View>
        implements PlatformMobileContract.Presenter {

    private static final String TAG = PlatformMobilePresenter.class.getSimpleName();

    @Override
    public void sendMobileCode(String mobile) {
        CloudCall.sendSaasVerifyCode(mobile, new RetrofitCallback<Object>() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
            }

            @Override
            public void onFail(int code, String msg, Object data) {
                LogCat.e(TAG, "Send sms code Failed. " + msg);
                if (isViewAttached()) {
                    mView.onFailSendMobileCode();
                }
            }
        });
    }

    @Override
    public void checkMobileCode(String mobile, String code, int saas) {
        CloudCall.confirmSaasVerifyCode(mobile, code, new RetrofitCallback<Object>() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                getSaasInfo(mobile, saas);
            }

            @Override
            public void onFail(int code, String msg, Object data) {
                LogCat.e(TAG, "Check sms code Failed. " + msg);
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.onFailCheckMobileCode();
                }
            }
        });
    }

    private void getSaasInfo(String mobile, int saas) {
        CloudCall.getSaasUserInfo(mobile, new RetrofitCallback<AuthStoreInfo>() {
            @Override
            public void onSuccess(int code, String msg, AuthStoreInfo data) {
                ArrayList<AuthStoreInfo.SaasUserInfoListBean> target = new ArrayList<>();
                // 匹配平台
                for (AuthStoreInfo.SaasUserInfoListBean info : data.getSaas_user_info_list()) {
                    if (info.getSaas_source() == saas) {
                        target.add(info);
                    }
                }
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.showAuthDialog(target);
                }
            }

            @Override
            public void onFail(int code, String msg, AuthStoreInfo data) {
                LogCat.e(TAG, "Get SAAS info Failed. " + msg);
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                }
            }
        });
    }

}
