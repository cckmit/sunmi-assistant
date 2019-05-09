package com.sunmi.ipc.rpc;

import android.text.TextUtils;

import com.sunmi.ipc.config.IpcConfig;

import java.util.HashMap;
import java.util.Map;

import sunmi.common.rpc.retrofit.BaseRetrofitClient;
import sunmi.common.utils.SpUtils;

/**
 * Description:
 * Created by bruce on 2019/5/7.
 */
public class RetrofitClient  extends BaseRetrofitClient {

    private static class SingletonHolder {
        private static RetrofitClient INSTANCE = new RetrofitClient();
    }

    public static RetrofitClient getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public static RetrofitClient createInstance() {
        return new RetrofitClient();
    }

    public RetrofitClient() {
        init(IpcConfig.IPC_CLOUD_URL, getHeaders());
    }

    private Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        if (!TextUtils.isEmpty(SpUtils.getToken())) {
            headers.put("Authorization", "Bearer " + SpUtils.getSsoToken());
        }
        return headers;
    }

}