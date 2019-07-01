package com.sunmi.assistant.rpc;

import com.sunmi.assistant.rpc.api.AdInterface;
import com.sunmi.ipc.rpc.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import sunmi.common.constant.CommonConfig;
import sunmi.common.rpc.http.BaseHttpApi;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.DateTimeUtils;
import sunmi.common.utils.SafeUtils;

/**
 * Description:
 * Created by bruce on 2019/6/27.
 */
public class CloudCall extends BaseHttpApi {

    public static void getAdList(int companyId, int shopId, RetrofitCallback callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .toString();
            RetrofitClient.getInstance().create(AdInterface.class)
                    .getAdList(getSignedRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 参数加签
     */
    private static BaseRequest getSignedRequest(String params) {
        String timeStamp = DateTimeUtils.currentTimeSecond() + "";
        String randomNum = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String isEncrypted = "0";
        String sign = SafeUtils.md5(params + isEncrypted +
                timeStamp + randomNum + SafeUtils.md5(CommonConfig.CLOUD_TOKEN));
        return new BaseRequest.Builder()
                .setTimeStamp(timeStamp)
                .setRandomNum(randomNum)
                .setIsEncrypted(isEncrypted)
                .setParams(params)
                .setSign(sign)
                .setLang("zh").createBaseRequest();
    }

}
