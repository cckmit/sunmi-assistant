package com.sunmi.cloudprinter.rpc;

import android.text.TextUtils;

import com.sunmi.cloudprinter.config.PrinterConfig;

import java.util.HashMap;
import java.util.Map;

import sunmi.common.rpc.retrofit.BaseRetrofitClient;
import sunmi.common.utils.SpUtils;

/**
 * Description:
 * Created by bruce on 2019/5/31.
 */
public class PrinterRetrofitClient extends BaseRetrofitClient {

    private static class SingletonHolder {
        private static PrinterRetrofitClient INSTANCE = new PrinterRetrofitClient();
    }

    public static PrinterRetrofitClient getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public static PrinterRetrofitClient createInstance() {
        return new PrinterRetrofitClient();
    }

    public PrinterRetrofitClient() {
        init(PrinterConfig.IOT_CLOUD_URL, getHeaders());
    }

    private Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        if (!TextUtils.isEmpty(SpUtils.getStoreToken())) {
//            headers.put("Authorization", "Bearer " + SpUtils.getStoreToken());
        }
        return headers;
    }

}
