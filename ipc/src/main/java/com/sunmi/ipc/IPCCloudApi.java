package com.sunmi.ipc;

import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import sunmi.common.rpc.http.BaseHttpApi;
import sunmi.common.utils.log.LogCat;

/**
 * Description: IPCCloudApi
 * Created by Bruce on 2019/3/31.
 */
public class IPCCloudApi extends BaseHttpApi {

    public static void bindIPC(String sn, StringCallback callback) {
        try {
            String params = new JSONObject()
                    .put("sn", sn)
//                    .put("sso_token", SpUtils.getToken())
                    .toString();
            post(IPCInterface.BIND_IPC, getSignedParams(params), callback);
        } catch (JSONException e) {
            LogCat.e(TAG, "getBindList -> get params error,", e);
        }
    }

}
