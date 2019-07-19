package com.sunmi.cloudprinter.rpc;

import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import sunmi.common.rpc.http.BaseHttpApi;
import sunmi.common.utils.SafeUtils;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.log.LogCat;

/**
 * Description: iot云接口
 * Created by Bruce on 2019/3/31.
 */
public class IOTCloudApi extends BaseHttpApi {

    /**
     * 绑定打印机到iot云端
     *
     * @param sn 是	string	设备序列号
     */
    public static void bindPrinter(int shopId, String sn, StringCallback callback) {
        try {
            String params = new JSONObject()
                    .put("userId", SpUtils.getUID())
                    .put("merchantId", shopId)
                    .put("sn", sn)
                    .put("channelId", 1)
                    .put("token", SafeUtils.md5(SpUtils.getUID()))
                    .toString();
            post(MerchantInterface.BIND_PRINTER, getSignedParams(params), callback);
        } catch (JSONException e) {
            LogCat.e(TAG, "register -> get params error,", e);
        }
    }

    /**
     * 解绑
     *
     * @param sn 是	string	设备序列号
     */
    public static void unbindPrinter(int shopId, String sn, StringCallback callback) {
        try {
            String params = new JSONObject()
                    .put("userId", SpUtils.getUID())
                    .put("merchantId", shopId)
                    .put("sn", sn)
                    .put("token", SafeUtils.md5(SpUtils.getUID()))
                    .toString();
            post(MerchantInterface.UNBIND_PRINTER, getSignedParams(params), callback);
        } catch (JSONException e) {
            LogCat.e(TAG, "register -> get params error,", e);
        }
    }

    /**
     * 绑定打印机到iot云端
     */
    public static void getPrinterList(int shopId, StringCallback callback) {
        try {
            String params = new JSONObject()
                    .put("userId", SpUtils.getUID())
                    .put("merchantId", shopId)
                    .put("token", SafeUtils.md5(SpUtils.getUID()))
                    .toString();
            post(MerchantInterface.GET_PRINTER_LIST, getSignedParams(params), callback);
        } catch (JSONException e) {
            LogCat.e(TAG, "register -> get params error,", e);
        }
    }

    /**
     * 获取打印机的在线状态
     *
     * @param sn 是	string	设备序列号
     */
    public static void getPrinterStatus(String sn, StringCallback callback) {
        try {
            String params = new JSONObject()
                    .put("sn", sn)
                    .toString();
            post(MerchantInterface.GET_PRINTER_STATUS, getSignedParams(params), callback);
        } catch (JSONException e) {
            LogCat.e(TAG, "register -> get params error,", e);
        }
    }

//    /**
//     * 绑定打印机到iot云端
//     *
//     * @param sn 是	string	设备序列号
//     */
//    public static void bindPrinter(String sn, RetrofitCallback callback) {
//        try {
//            String params = new JSONObject()
//                    .put("userId", SpUtils.getUID())
//                    .put("merchantId", SpUtils.getMerchantUid())
//                    .put("sn", sn)
//                    .put("token", SafeUtils.md5(SpUtils.getUID()))
//                    .toString();
//            PrinterRetrofitClient.getInstance().create(MerchantInterface.class)
//                    .bind(getSignedRequest(params))
//                    .enqueue(callback);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 参数加签
//     */
//    private static BaseRequest getSignedRequest(String params) {
//        String timeStamp = DateTimeUtils.currentTimeSecond() + "";
//        String randomNum = (int) ((Math.random() * 9 + 1) * 100000) + "";
//        String isEncrypted = "0";
//        String sign = SafeUtils.md5(params + isEncrypted +
//                timeStamp + randomNum + SafeUtils.md5(CommonConfig.CLOUD_TOKEN));
//        return new BaseRequest.Builder()
//                .setTimeStamp(timeStamp)
//                .setRandomNum(randomNum)
//                .setIsEncrypted(isEncrypted)
//                .setParams(URLEncoder.encode(params))
//                .setSign(sign)
//                .setLang("zh").createBaseRequest();
//    }

}
