package com.sunmi.assistant.rpc;

import com.sunmi.assistant.rpc.api.AdInterface;
import com.sunmi.assistant.rpc.api.SaasInterface;
import com.sunmi.assistant.ui.activity.model.PlatformInfo;

import org.json.JSONException;
import org.json.JSONObject;

import sunmi.common.constant.CommonConfig;
import sunmi.common.model.CreateShopInfo;
import sunmi.common.rpc.cloud.CompanyInterface;
import sunmi.common.rpc.cloud.ShopInterface;
import sunmi.common.rpc.cloud.SunmiStoreRetrofitClient;
import sunmi.common.rpc.http.BaseHttpApi;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.DateTimeUtils;
import sunmi.common.utils.SafeUtils;
import sunmi.common.utils.SpUtils;

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
            SunmiStoreRetrofitClient.getInstance().create(AdInterface.class)
                    .getAdList(getSignedRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取商户列表
     */
    public static void getCompanyList(RetrofitCallback callback) {
        try {
            String params = new JSONObject()
                    .put("page_num", 1)
                    .put("page_size", 999)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(CompanyInterface.class)
                    .getList(getSignedRequest(params))
                    .enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建门店
     *
     * @param companyId 是
     * @param shopName  是
     * @param person     否
     * @param tel       否
     * @param callback 回调
     */
    public static void createShop(int companyId, String shopName, String person, String tel,
                                  RetrofitCallback<CreateShopInfo> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_name", shopName)
                    .put("contact_person", person)
                    .put("contact_tel", tel)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(ShopInterface.class)
                    .createShop(getSignedRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 编辑门店
     */
    public static void editShop(String shopName, RetrofitCallback callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", SpUtils.getCompanyId())
                    .put("shop_id", SpUtils.getShopId())
                    .put("shop_name", shopName)
                    //营业状态 0:营业 1:停业
                    .put("business_status", 0)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(ShopInterface.class)
                    .editShop(getSignedRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * saas信息
     */
    public static void getSaasUserInfo(String phone, RetrofitCallback callback) {
        try {
            String params = new JSONObject()
                    .put("phone", phone)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(SaasInterface.class)
                    .getSaasUserInfo(getSignedRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 米商引擎所支持的Saas平台信息
     */
    public static void getPlatformList(RetrofitCallback<PlatformInfo> callback) {
        try {
            SunmiStoreRetrofitClient.getInstance().create(SaasInterface.class)
                    .getPlatformList(getSignedRequest(""))
                    .enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 米商引擎手机发送验证码
     */
    public static void sendSaasVerifyCode(String phone, RetrofitCallback callback) {
        try {
            String params = new JSONObject()
                    .put("phone", phone)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(SaasInterface.class)
                    .sendSaasVerifyCode(getSignedRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 米商引擎手机校验验证码
     */
    public static void confirmSaasVerifyCode(String phone, String code, RetrofitCallback<Object> callback) {
        try {
            String params = new JSONObject()
                    .put("phone", phone)
                    .put("code", code)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(SaasInterface.class)
                    .confirmSaasVerifyCode(getSignedRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用户授权获取Saas平台数据
     *
     * @param companyId
     * @param shopId
     * @param saasSource Saas来源标识id
     * @param shopNo     店铺在商米引擎的唯一标识
     * @param saasName   Saas软件商名称
     * @param callback
     */
    public static void authorizeSaas(int companyId, int shopId, int saasSource,
                                     String shopNo, String saasName, RetrofitCallback<Object> callback) {
        try {
            String params = new JSONObject()
                    .put("companyId", companyId)
                    .put("shopId", shopId)
                    .put("saasSource", saasSource)
                    .put("shopNo", shopNo)
                    .put("saasName", saasName)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(SaasInterface.class)
                    .authorizeSaas(getSignedRequest(params))
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
