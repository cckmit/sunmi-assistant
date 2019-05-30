package com.sunmi.ipc.rpc.mqtt;

import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import sunmi.common.notification.BaseNotification;
import sunmi.common.rpc.mqtt.MessageCache;
import sunmi.common.rpc.mqtt.MessageManager;
import sunmi.common.rpc.sunmicall.RequestBean;
import sunmi.common.rpc.sunmicall.ResponseBean;

public class SMMqttCallback implements MqttCallbackExtended {
    private String TAG = "IPC" + getClass().getSimpleName();

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        Log.e(TAG, "mqtt connectComplete reconnect = " + reconnect);
        if (reconnect) {//如果是mqtt自动重连，重新订阅一次
            Log.e(TAG, "mqtt connectComplete clientId = " + MqttManager.getInstance().getClientId());
            Log.e(TAG, "mqtt connectComplete thread id  = " + Process.myTid());
            MqttManager.getInstance().subscribeToTopic();
//            BaseNotification.newInstance().postNotificationName(NotificationConstant.updateConnectComplete, "connectComplete");
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.e(TAG, "mqtt connectionLost");
//        checkToken();
        MqttManager.getInstance().createEmqToken(true);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        Log.e(TAG, "mqtt messageArrived topic = " + topic + ", message = " + message.toString());
        if (TextUtils.equals(topic, MqttManager.tokenRequestSub)) {
            ResponseBean res = new ResponseBean(message.toString(), "params");
            MessageManager.newInstance().notice(res, MessageManager.REQUEST_CHANNEL);
        } else {
            ResponseBean res = new ResponseBean(message);
            Log.e(TAG, "mqtt messageArrived " + ", thread id  = " + Process.myTid()
                    + ",message id = " + res.getMsgId() + ", opcode = " + res.getOpcode());
            if (MqttManager.getInstance().getOpCode(res.getMsgId()) > 0) {
                BaseNotification.newInstance().postNotificationName(
                        MqttManager.getInstance().getOpCode(res.getMsgId()), res, topic);
                MqttManager.getInstance().removeMessage(res.getMsgId());
            } else {
                BaseNotification.newInstance().postNotificationName(
                        Integer.parseInt(res.getOpcode().substring(2,
                                res.getOpcode().length()), 16), res, topic);
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        try {
            RequestBean req = new RequestBean(token.getMessage());
            MessageCache.getInstance().addMessage(req.getMsgId(), req);
            Log.e(TAG, "mqtt deliveryComplete msg = " + token.getMessage().toString());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

//    private void checkToken() {
//        if (!NetworkUtils.isNetworkAvailable(BaseApplication.getContext())) return;
//        if (TextUtils.isEmpty(SpUtils.getLoginStatus())) return;
//        CloudApi.checkToken(new StringCallback() {
//            @Override
//            public void onError(Call call, Response response, Exception e, int id) {
////                CommonUtils.gotoLoginActivity("");
//                Log.e(TAG, "mqtt checkToken onError, response = " + response);
//            }
//
//            @Override
//            public void onResponse(String response, int id) {
//                try {
//                    Log.e(TAG, "mqtt checkToken onResponse, response = " + response);
//                    if (!TextUtils.isEmpty(response)) {
//                        JSONObject jsonObject = new JSONObject(response);
//                        int code = jsonObject.getInt("code");
//                        if (code != 1) {//token失效
//                            CommonUtils.gotoLoginActivity(BaseApplication.getContext(), "");
//                        }
//                    }
//                } catch (Exception e) {
//                    Log.e(TAG, "mqtt checkToken handle response exception, ", e);
////                    CommonUtils.gotoLoginActivity("");
//                }
//            }
//        });
//    }

}
