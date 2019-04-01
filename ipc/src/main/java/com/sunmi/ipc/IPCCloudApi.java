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

    /**
     * @param shopId    是	integer	店铺id
     * @param sn        是	string	设备序列号
     * @param bindToken 是	string	bind认证token
     * @param longitude 是	float	经度
     * @param latitude  是	float	纬度
     */
    public static void bindIPC(int shopId, String sn, String bindToken, float longitude,
                               float latitude, StringCallback callback) {
        try {
            String params = new JSONObject()
                    .put("user_id", sn)
                    .put("sn", sn)
                    .put("shop_id", shopId)
                    .put("bind_token", bindToken)
                    .put("longitude", longitude)
                    .put("latitude", latitude)
                    .toString();
            post(IPCInterface.BIND_IPC, getSignedParams(params), callback);
        } catch (JSONException e) {
            LogCat.e(TAG, "bindIPC -> get params error,", e);
        }
    }

}
