package com.sunmi.ipc;

import android.content.Context;

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
        post(context, "", 0x3118, new JSONObject());
    } //获取摄像头搜索到的wifi列表


    //    {
//        "msg_id": "1",
//            "params":[{
//        "opcode":"0x3116",
//                "param":{
//            "ssid": "SUNMI_WBU",
//                    "key_mgmt": "WPA-PSK",
//                    "key": "sunmi388"
//        }
//    }]
//    }
    public void setIPCWifi(Context context, String ssid, String keyMgmt, String key) {
        post(context, "", IpcConstants.getWifiList, new JSONObject());
    }

    private void post(Context context, String sn, int opCode, JSONObject jsonObject) {
        RequestBean requestBean = new RequestBean("", opCode + "", jsonObject);
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
