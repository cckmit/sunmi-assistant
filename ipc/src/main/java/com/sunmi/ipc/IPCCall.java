package com.sunmi.ipc;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import sunmi.common.rpc.sunmicall.BaseApi;
import sunmi.common.rpc.sunmicall.RequestBean;

/**
 * Description:
 * Created by bruce on 2019/3/29.
 */
public class IPCCall extends BaseApi {

    private static IPCCall mInstance;

    public static IPCCall getInstance() {
        if (mInstance == null) {
            synchronized (IPCCall.class) {
                if (mInstance == null) {
                    mInstance = new IPCCall();
                }
            }
        }
        return mInstance;
    }

    //获取摄像头搜索到的wifi列表
    public void getWifiList(Context context) {
        post(context, "", IpcConstants.getWifiList, new JSONObject());
    }

    public void setIPCWifi(Context context, String ssid, String keyMgmt, String key) {
        try {
            JSONObject object = new JSONObject();
            object.put("ssid", ssid);
            object.put("key_mgmt", keyMgmt);
            object.put("key", key);
            post(context, "", IpcConstants.setIPCWifi, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //获取IPC无线关联前端AP的状态
    public void getApStatus(Context context) {
        post(context, "", IpcConstants.getApStatus, new JSONObject());
    }

    //获取token,绑定ipc使用
    public void getToken(Context context) {
        post(context, "", IpcConstants.getIpcToken, new JSONObject());
    }

    private void post(Context context, String sn, int opCode, JSONObject jsonObject) {
        RequestBean requestBean = new RequestBean("11111", "0x" + Integer.toHexString(opCode), jsonObject);
        post(context, sn, requestBean.getMsgId(), opCode, requestBean.serialize());
    }

    //    public static boolean isRemoteCall(String sn) {
//        return !TextUtils.equals(sn, MyNetworkCallback.CURRENT_ROUTER);
//    }
    @Override
    public void post(Context context, String sn, String msgId, int opCode, String json) {
        new IPCLocalApi().post(context, sn, msgId, opCode, json);
//        if (isRemoteCall(sn)) {
//            new RemoteApApi().post(context, sn, msgId, opCode, json);
//        } else {
//            new IPCLocalApi().post(context, sn, msgId, opCode, json);
//        }
    }

}
