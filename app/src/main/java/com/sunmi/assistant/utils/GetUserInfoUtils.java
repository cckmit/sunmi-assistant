package com.sunmi.assistant.utils;

import android.app.Activity;
import android.content.Context;

import com.sunmi.apmanager.utils.CommonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import sunmi.common.model.UserInfoBean;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;

/**
 * @author yangShiJie
 * @date 2019/8/13
 */
public class GetUserInfoUtils {

    public static void userInfo(Context context, int companyId, String companyName, int saasExist, int shopId, String shopName) {
        SunmiStoreApi.getUserInfo(new RetrofitCallback<UserInfoBean>() {
            @Override
            public void onSuccess(int code, String msg, UserInfoBean data) {
                CommonUtils.saveLoginInfo(data);
                ssoToken(context, companyId, companyName, saasExist, shopId, shopName);
            }

            @Override
            public void onFail(int code, String msg, UserInfoBean data) {
            }
        });
    }

    private static void ssoToken(Context context, int companyId, String companyName, int saasExist, int shopId, String shopName) {
        SunmiStoreApi.getSsoToken(new RetrofitCallback<Object>() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                try {
                    JSONObject jsonObject = new JSONObject(data.toString());
                    String ssoToken = jsonObject.getString("sso_token");
                    SpUtils.setSsoToken(ssoToken);

                    CommonUtils.gotoMainActivity((Activity) context, companyId, companyName, saasExist, shopId, shopName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(int code, String msg, Object data) {
            }
        });
    }

}
