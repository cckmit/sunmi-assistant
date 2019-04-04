package sunmi.common.rpc.mqtt;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Description:
 * Created by bruce on 2019/4/4.
 */
public class BaseMqttCallback implements MqttCallbackExtended {
    private String TAG = getClass().getSimpleName();

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        Log.e(TAG, "mqtt connectComplete reconnect = " + reconnect);
        if (reconnect) {//如果是mqtt自动重连，重新订阅一次
//            Log.e(TAG, "mqtt connectComplete clientId = " + MqttManager.getClientId());
//            Log.e(TAG, "mqtt connectComplete thread id  = " + Process.myTid());
//            MqttManager.getInstance().subscribeToTopic();
//            MQTTManager.getInstance().mqttConnect();
//            BaseNotification.newInstance().postNotificationName(NotificationConstant.updateConnectComplete, "connectComplete");
        }
//        else {
//            MQTTManager.getInstance().mqttConnect();
//        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.e(TAG, "mqtt connectionLost");
//        BaseNotification.newInstance().postNotificationName(
//                NotificationConstant.apStatusException, "-1");
//        checkToken();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        Log.e(TAG, "mqtt messageArrived topic = " + topic);
//        if (TextUtils.equals(topic, MqttManager.tokenRequestSub)) {
//            ResponseBean res = new ResponseBean(message.toString(), "params");
//            MessageManager.newInstance().notice(res, MessageManager.REQUEST_CHANNEL);
//        } else if (topic.contains("event/sub")) {//在线 离线 设备状态
//            BaseNotification.newInstance().postNotificationName(
//                    NotificationConstant.apPostStatus, message.toString());
//        } else {
//            ResponseBean res = new ResponseBean(message);
//            Log.e(TAG, "mqtt messageArrived " + ", thread id  = " + Process.myTid()
//                    + ",message id = " + res.getMsgId() + ", opcode = " + res.getOpcode());
//            MessageCache.getInstance().removeMessage(res.getMsgId());
//            if (TextUtils.equals(MSG_AP_BIND, res.getOpcode())
//                    || TextUtils.equals(MSG_AP_UNBIND, res.getOpcode())) {
//                MessageManager.newInstance().notice(res, MessageManager.BYPASS_CHANNEL);
//            } else {
//                BaseNotification.newInstance().postNotificationName(
//                        MqttManager.getInstance().getCode(res.getMsgId()), res, topic);
//            }
//            MqttManager.getInstance().removeMessage(res.getMsgId());
//        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
//        try {
//            RequestBean req = new RequestBean(token.getMessage());
//            MessageCache.getInstance().addMessage(req.getMsgId(), req);
//            Log.e(TAG, "mqtt deliveryComplete msg = " + token.getMessage().toString());
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
    }

    private void checkToken() {
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
    }

}
