package sunmi.common.rpc.mqtt;

import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import sunmi.common.notification.BaseNotification;
import sunmi.common.rpc.sunmicall.RequestBean;
import sunmi.common.rpc.sunmicall.ResponseBean;

public class SMMqttCallback implements MqttCallbackExtended {
    private String TAG = "IPC" + getClass().getSimpleName();

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        Log.e(TAG, "mqtt connectComplete reconnect = " + reconnect);
        if (reconnect) {//如果是mqtt自动重连，重新订阅一次
            Log.e(TAG, "mqtt connectComplete thread id  = " + Process.myTid());
            MqttManager.getInstance().subscribeToTopic();
//            BaseNotification.newInstance().postNotificationName(NotificationConstant.updateConnectComplete, "connectComplete");
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.e(TAG, "mqtt connectionLost");
//        checkToken();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        Log.e(TAG, "mqtt messageArrived topic = " + topic + ", message = " + message.toString());
        if (TextUtils.equals(topic, MqttManager.tokenRequestSub)) {
            ResponseBean res = new ResponseBean(message.toString(), "params");
            MessageManager.newInstance().notice(res, MessageManager.REQUEST_CHANNEL);
        }
        if (TextUtils.equals(topic, MqttManager.tokenWEBSS1EventSub) ||
                TextUtils.equals(topic, MqttManager.tokenWEBFS1EventSub) ||
                TextUtils.equals(topic, MqttManager.tokenWEBFM010EventSub) ||
                TextUtils.equals(topic, MqttManager.tokenWEBFM020EventSub)) {
            ResponseBean res = new ResponseBean(message.toString(), "params");
            BaseNotification.newInstance().postNotificationName(
                    Integer.parseInt(res.getOpcode().substring(2)), res, topic);
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
                        Integer.parseInt(res.getOpcode().substring(2
                        ), 16), res, topic);
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

}
