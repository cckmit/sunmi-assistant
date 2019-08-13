package com.sunmi.assistant.utils;

import android.content.Context;

import com.sunmi.apmanager.utils.CommonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import sunmi.common.model.UserInfoBean;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.GotoActivityUtils;
import sunmi.common.utils.SpUtils;

/**
 * @author yangShiJie
 * @date 2019/8/13
 */
public class GetUserInfo {


    public static void userInfo(Context context) {
        SunmiStoreApi.getUserInfo(new RetrofitCallback<UserInfoBean>() {
            @Override
            public void onSuccess(int code, String msg, UserInfoBean data) {
                CommonUtils.saveLoginInfo(data);
                ssoToken(context);
            }

            @Override
            public void onFail(int code, String msg, UserInfoBean data) {
            }
        });
    }


    private static void ssoToken(Context context) {
        SunmiStoreApi.getSsoToken(new RetrofitCallback<Object>() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                try {
                    JSONObject jsonObject = new JSONObject(data.toString());
                    String ssoToken = jsonObject.getString("sso_token");
                    SpUtils.setSsoToken(ssoToken);

                    GotoActivityUtils.gotoMainActivity(context);
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
