package com.sunmi.ipc.rpc;

import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import sunmi.common.rpc.http.BaseHttpApi;
import sunmi.common.utils.SpUtils;
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
        Map<String, String> map = new HashMap<>();
        try {
            map.put("user_id",SpUtils.getUID());
            map.put("sn", sn);
            map.put("shop_id", shopId + "");
            map.put("bind_token", bindToken);
            map.put("longitude", longitude + "");
            map.put("latitude", latitude + "");
        } catch (Exception e) {
            LogCat.e(TAG, "getSignedParams error,", e);
        }

        post(IPCInterface.BIND_IPC, map, callback);
    }

    //创建APP用户登录EMQ的token
    public static void createEmqToken(StringCallback callback) {
        try {
            String params = new JSONObject()
                    .put("sso_token", SpUtils.getToken())
                    .put("user_id", SpUtils.getUID())
                    .toString();
            post(IPCInterface.CREATE_EMQ_TOKEN, getSignedParams(params), callback);
        } catch (JSONException e) {
            LogCat.e(TAG, "createEmqToken -> get params error,", e);
        }
    }

}
