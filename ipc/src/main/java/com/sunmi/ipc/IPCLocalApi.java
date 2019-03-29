package com.sunmi.ipc;

import java.util.Map;

import sunmi.common.rpc.sunmicall.BaseLocalApi;
import sunmi.common.rpc.sunmicall.ResponseBean;

/**
 * Description:
 * Created by bruce on 2019/3/29.
 */
public class IPCLocalApi extends BaseLocalApi {
    @Override
    public String getBaseUrl() {
        return "";
    }

    @Override
    public Map<String, String> getHeader() {
        return null;
    }

    @Override
    public void onFail(ResponseBean res) {

    }

    @Override
    public void onSuccess(String result, String sn) {

    }
}
