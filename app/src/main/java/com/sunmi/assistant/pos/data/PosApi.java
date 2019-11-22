package com.sunmi.assistant.pos.data;

import com.sunmi.assistant.pos.response.PosDetailsResp;
import com.sunmi.assistant.pos.response.PosListResp;
import com.sunmi.assistant.pos.response.PosWarrantyResp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import sunmi.common.constant.CommonConfig;
import sunmi.common.rpc.cloud.SunmiStoreRetrofitClient;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.DateTimeUtils;
import sunmi.common.utils.SafeUtils;
import sunmi.common.utils.SpUtils;

/**
 * @author yangShiJie
 * @date 2019-11-20
 */
public class PosApi {

    public static final String TAG = "SunmiStoreApi";

    private PosApi() {
    }

    public static PosApi getInstance() {
        return Singleton.INSTANCE;
    }

    /**
     * 获取门店下绑定pos设备列表
     */
    public void getPosList(RetrofitCallback<PosListResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", SpUtils.getCompanyId())
                    .put("shop_id", SpUtils.getShopId())
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(PosInterface.class)
                    .getList(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取设备基本信息
     */
    public void getBaseInfo(String deviceId, RetrofitCallback<PosDetailsResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", SpUtils.getCompanyId())
                    .put("shop_id", SpUtils.getShopId())
                    .put("device_sn", deviceId)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(PosInterface.class)
                    .getBaseInfo(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取设备保修信息
     */
    public void getWarrantyInfo(String deviceId, RetrofitCallback<PosWarrantyResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", SpUtils.getCompanyId())
                    .put("shop_id", SpUtils.getShopId())
                    .put("device_sn", deviceId)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(PosInterface.class)
                    .getWarrantyInfo(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取POS设备类别
     */
    public void getCategoryByModel(String[] arrays, RetrofitCallback<Object> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", SpUtils.getCompanyId())
                    .put("shop_id", SpUtils.getShopId())
                    .put("model_list", Arrays.toString(arrays)) //array[string]
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(PosInterface.class)
                    .getCategoryByModel(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private BaseRequest createRequestBody(String params) {
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

    private static final class Singleton {
        private static final PosApi INSTANCE = new PosApi();
    }
}
