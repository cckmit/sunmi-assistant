package com.sunmi.assistant.utils;

import android.content.Context;

import com.sunmi.apmanager.rpc.cloud.CloudApi;
import com.xiaojinzi.component.impl.Router;

import sunmi.common.model.SsoTokenResp;
import sunmi.common.model.UserInfoBean;
import sunmi.common.router.AppApi;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.http.RpcCallback;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.GotoActivityUtils;
import sunmi.common.utils.SpUtils;

/**
 * @author yangShiJie
 * @date 2019/8/13
 */
public class GetUserInfoUtils {

    public static void userInfo(Context context, int companyId, String companyName,
                                int saasExist, int shopId, String shopName) {
        SunmiStoreApi.getInstance().getUserInfo(new RetrofitCallback<UserInfoBean>() {
            @Override
            public void onSuccess(int code, String msg, UserInfoBean data) {
                ssoToken(context, data, companyId, companyName, saasExist, shopId, shopName);
            }

            @Override
            public void onFail(int code, String msg, UserInfoBean data) {
            }
        });
    }

    private static void ssoToken(Context context, UserInfoBean userInfoBean, int companyId, String companyName,
                                 int saasExist, int shopId, String shopName) {
        SunmiStoreApi.getInstance().getSsoToken(new RetrofitCallback<SsoTokenResp>() {
            @Override
            public void onSuccess(int code, String msg, SsoTokenResp resp) {
                SpUtils.setSsoToken(resp.getSsoToken());
                CommonHelper.saveLoginInfo(userInfoBean);
                CommonHelper.saveCompanyShopInfo(companyId, companyName, saasExist, shopId, shopName);
                checkSession(context);
            }

            @Override
            public void onFail(int code, String msg, SsoTokenResp data) {
            }
        });
    }

    private static void checkSession(Context context) {
        CloudApi.checkSession(new RpcCallback(context, false) {
            @Override
            public void onSuccess(int code, String msg, String data) {
                Router.withApi(AppApi.class).goToMainClearTask(context);
            }

            @Override
            public void onError(int code, String msg, String data) {
                Router.withApi(AppApi.class).goToMainClearTask(context);
            }
        });
    }

}
