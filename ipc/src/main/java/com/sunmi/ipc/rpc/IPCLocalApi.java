package com.sunmi.ipc.rpc;

import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

import sunmi.common.rpc.sunmicall.BaseLocalApi;
import sunmi.common.rpc.sunmicall.ResponseBean;

/**
 * Description:
 * Created by bruce on 2019/3/29.
 */
public class IPCLocalApi extends BaseLocalApi {
    @Override
    public String getBaseUrl() {
        return IpcConstants.IPC_IP;
    }

    @Override
    protected SSLSocketFactory getSSLSocketFactory() {
        return new SSLSocketFactoryGenerator().generate();
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
