package com.sunmi.cloudprinter.rpc;

import com.sunmi.cloudprinter.config.PrinterConfig;
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

    private static final String BIND_PRINTER = PrinterConfig.IOT_CLOUD_URL + "Machine.bindMachine";//绑定打印机
    private static final String GET_PRINTER_LIST = PrinterConfig.IOT_CLOUD_URL + "Machine.getMerchantInfo";//获取打印机列表
    private static final String GET_PRINTER_STATUS = PrinterConfig.IOT_CLOUD_URL + "Machine.getMachineIsOnLine";//获取打印机状态
    private static final String UNBIND_PRINTER = PrinterConfig.IOT_CLOUD_URL + "Machine.untiedMachine";//解绑打印机

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
            post(BIND_PRINTER, getSignedParams(params), callback);
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
                    .put("channelId", 1)
                    .put("token", SafeUtils.md5(SpUtils.getUID()))
                    .toString();
            post(UNBIND_PRINTER, getSignedParams(params), callback);
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
                    .put("channelId", 1)
                    .put("token", SafeUtils.md5(SpUtils.getUID()))
                    .toString();
            post(GET_PRINTER_LIST, getSignedParams(params), callback);
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
            post(GET_PRINTER_STATUS, getSignedParams(params), callback);
        } catch (JSONException e) {
            LogCat.e(TAG, "register -> get params error,", e);
        }
    }

}
