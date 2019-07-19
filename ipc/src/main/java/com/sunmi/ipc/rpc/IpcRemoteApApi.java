package com.sunmi.ipc.rpc;

import android.content.Context;

import com.sunmi.ipc.rpc.mqtt.MqttManager;

import sunmi.common.rpc.sunmicall.BaseApi;

class IpcRemoteApApi extends BaseApi {

    /**
     * 远程调用
     */
    @Override
    public void post(Context context, String sn, String msgId, int opCode, String json) {
        MqttManager.getInstance().pubByPassMessage(sn, msgId, opCode, json);
    }

}
