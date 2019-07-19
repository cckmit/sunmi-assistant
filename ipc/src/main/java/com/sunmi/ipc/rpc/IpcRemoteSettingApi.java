package com.sunmi.ipc.rpc;

import android.content.Context;

import com.sunmi.ipc.rpc.mqtt.MqttManager;

import sunmi.common.rpc.sunmicall.BaseIpcApi;

class IpcRemoteSettingApi extends BaseIpcApi {

    /**
     * 远程调用
     */

    @Override
    public void post(Context context, String sn, String msgId, int opCode, String json) {
        MqttManager.getInstance().pubByPassMessage(sn, msgId, opCode, json);
    }

    @Override
    public void post(Context context, String sn, String msgId, int opCode, String model, String json) {
        MqttManager.getInstance().pubIpcMessage(msgId, opCode, model, json);
    }
}
