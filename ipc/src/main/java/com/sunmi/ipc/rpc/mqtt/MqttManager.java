package com.sunmi.ipc.rpc.mqtt;

import android.text.TextUtils;

import com.sunmi.ipc.rpc.IPCCloudApi;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import sunmi.common.base.BaseApplication;
import sunmi.common.rpc.RpcErrorCode;
import sunmi.common.rpc.SSLSocketFactoryGenerator;
import sunmi.common.rpc.mqtt.EmqTokenResp;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.ToastUtils;
import sunmi.common.utils.log.LogCat;

public class MqttManager {
    private String TAG = "IPC" + getClass().getSimpleName();

    private static MqttManager instance = null;

    private static String clientId;//由客户端生成，连接后唯一，重连更换
    static String tokenRequestSub;//订阅的request token
    public static String tokenSS1EventSub;//订阅的event ipc

    private static Map<String, Integer> messages = new ConcurrentHashMap<>(2);

    private boolean isRegister;
    private boolean isConnecting;

    private MqttAndroidClient mqttClient;
    private MqttConnectOptions options;
    private SMMqttCallback smMqttCallback;

    public static MqttManager getInstance() {
        MqttManager mqttManager = instance;
        if (mqttManager == null) {
            synchronized (MqttManager.class) {
                mqttManager = instance;
                if (mqttManager == null) {
                    instance = mqttManager = new MqttManager();
                }
            }
        }
        return mqttManager;
    }

    public static String getClientId() {
        return clientId;
    }

    public int getCode(String msgID) {
        Integer code = messages.get(msgID);
        return code != null ? code : -1;
    }

    public void removeMessage(String msgID) {
        messages.remove(msgID);
    }

    public void createEmqToken(final boolean isInit) {
//        if (isInit) {
//            MQttBean.DataBean bean = new MQttBean.DataBean();
//            if (TextUtils.isEmpty(clientId))
//                clientId = SpUtils.getUID() + "_" + System.currentTimeMillis();
//            bean.setClientID(clientId);
//            bean.setUsername("APP_42721");
//            bean.setPassword("123456");
//            bean.setServerAddress(IpcConfig.MQTT_HOST);
//            bean.setPort(IpcConfig.MQTT_PORT);
//            initMQTT(bean);
//        } else {
//            mqttConnect();
//        }
        LogCat.e(TAG, "mqtt createEmqToken start");
        if (mqttClient != null) return;
//        IPCCloudApi.createEmqToken(new StringCallback() {
//            @Override
//            public void onError(Call call, Response response, Exception e, int id) {
//                LogCat.e(TAG, "mqtt createEmqToken " + response + e.getMessage());
//            }
//
//            @Override
//            public void onResponse(String response, int id) {
//                LogCat.e(TAG, "mqtt checkToken sso token = " + response);
//                if (TextUtils.isEmpty(response)) {
//                    ToastUtils.toastForShort(BaseApplication.getContext(), "network error");
//                }
//                try {
//                    MQttBean bean = new GsonBuilder().create().fromJson(response, MQttBean.class);
//                    if (bean != null && bean.getData() != null) {
//                        LogCat.e(TAG, "mqtt createEmqToken success");
////                        if (mqttClient != null) mqttClient.disconnect();//todo 重连之前先断连，云端先考虑主动断连
//                        if (isInit) {
//                            initMQTT(bean.getData());
//                        } else {
//                            mqttConnect();
//                        }
//                    }
//                } catch (Exception e) {
//                    if (response.contains("code")) {//{"code":-1,"data":[],"msg":"invalid user token"}
////                        CommonUtils.logout();
//                    }
////                    ToastUtils.toastForShort(BaseApplication.getContext(),
////                            BaseApplication.getContext().getString(R.string.login_error));
//                }
//            }
//        });
        IPCCloudApi.createEmqToken(new RetrofitCallback<EmqTokenResp>() {
            @Override
            public void onSuccess(int code, String msg, EmqTokenResp response) {
                LogCat.e(TAG, "mqtt checkToken sso token = " + response);
                if (response == null) {
                    ToastUtils.toastForShort(BaseApplication.getContext(), "network error");
                }
                LogCat.e(TAG, "mqtt createEmqToken success");
//                if (mqttClient != null) mqttClient.disconnect();//todo 重连之前先断连，云端先考虑主动断连
                if (isInit) {
                    initMQTT(response);
                } else {
                    mqttConnect();
                }
            }

            @Override
            public void onFail(int code, String msg, EmqTokenResp data) {
//                LogCat.e(TAG, "mqtt createEmqToken " + response + e.getMessage());
            }
        });
    }

    /**
     * MQtt设置及连接
     */
    private void initMQTT(EmqTokenResp resp) {
        //clientId客户端生成，每次建连重新生成
        String host = resp.getServer_address();
        String serverURL = new StringBuilder().append("ssl://")
                .append(host).toString();  //需要证书
        if (TextUtils.isEmpty(clientId))
            clientId = resp.getUsername() + "_" + System.currentTimeMillis();

        mqttClient = new MqttAndroidClient(BaseApplication.getContext(), serverURL, clientId);
        options = new MqttConnectOptions();
        options.setUserName(resp.getUsername());
        options.setPassword(resp.getPassword().toCharArray());//解密
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(20);
        //setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知消息
//        options.setWill(topic, "close".getBytes(), 2, true);
        options.setSocketFactory(new SSLSocketFactoryGenerator().generate());//设置证书校验
        mqttClient.setCallback(getSMMqttCallback()); //发布订阅回调
        LogCat.e(TAG, "mqtt initMQTT");
        mqttConnect();//开始连接
    }

    public SMMqttCallback getSMMqttCallback() {
        if (smMqttCallback == null)
            smMqttCallback = new SMMqttCallback();
        return smMqttCallback;
    }

    /**
     * 建立连接
     */
    public void mqttConnect() {
        if (isConnecting) return;
        isConnecting = true;
        if (!NetworkUtils.isNetworkAvailable(BaseApplication.getContext()) || mqttClient == null) {
            return;
        }
        try {
            mqttClient.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    isConnecting = false;
                    LogCat.e(TAG, "mqtt Connect SUCCESS");
                    subscribeToTopic(); //订阅
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                    LogCat.e(TAG, "mqtt Connect fail, asyncActionToken code = "
//                            + asyncActionToken.getException().getReasonCode());
                    if (!NetworkUtils.isNetworkAvailable(BaseApplication.getContext())) {
                        LogCat.e(TAG, "mqtt Connect fail no net");
                        mqttClient = null;
                        isRegister = false;
                        disconnect();
                        isConnecting = false;
                        return;
                    }
                    if (asyncActionToken.getException() != null
                            && asyncActionToken.getException().getReasonCode()
                            == MqttException.REASON_CODE_FAILED_AUTHENTICATION
                            || asyncActionToken.getException().getReasonCode()
                            == MqttException.REASON_CODE_CLIENT_EXCEPTION
                            || asyncActionToken.getException().getReasonCode()
                            == MqttException.REASON_CODE_CONNECT_IN_PROGRESS) {
                        LogCat.e(TAG, "mqtt Connect fail,code = " + asyncActionToken.getException().getReasonCode()
                                + ", cause = " + asyncActionToken.getException().getCause());
                        disconnect();
                        isConnecting = false;
                        isRegister = false;
                        createEmqToken(true);
                    }
                    exception.printStackTrace();
                }
            });
        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }

    private String[] getTokens() {
        return new String[]{tokenSS1EventSub};//初始化所有门店下的设备dev状态
    }

    /**
     * 订阅
     */
    public void subscribeToTopic() {
        initSubToken();
        try {
            if (!NetworkUtils.isNetworkAvailable(BaseApplication.getContext())
                    || mqttClient == null || !mqttClient.isConnected()) {
                return;
            }

            int[] qoss = new int[]{2};
            //订阅1
            mqttClient.subscribe(getTokens(), qoss, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    LogCat.e(TAG, "mqtt Subscribed token success");
//                    if (!isRegister)
//                    APCall.getInstance().pubRegister();//todo
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    LogCat.e(TAG, "mqtt Subscribed token Failed ");
                }
            });
        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 发布request消息
     */
    public void pubRequestMessage(String publishMessage) {
        if (!NetworkUtils.isNetworkAvailable(BaseApplication.getContext())
                || mqttClient == null || !mqttClient.isConnected())
            return;
        try {
            final MqttMessage message = new MqttMessage();
            message.setPayload(publishMessage.getBytes());
            message.setQos(2);
            message.setRetained(false);
            mqttClient.publish(getPubTopic(clientId, "request"),
                    message, null, new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            isRegister = true;
                            LogCat.e(TAG, "mqtt pubRequestMessage onSuccess");
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable ex) {
                            LogCat.e(TAG, "mqtt pubRequestMessage fail, ", ex);
                        }
                    });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发布ByPass消息
     */
    public void pubByPassMessage(String sn, final String msgId, final int opCode, String publishMessage) {
        LogCat.e(TAG, "pubByPassMessage : publishMessage = " + publishMessage);
        if (!NetworkUtils.isNetworkAvailable(BaseApplication.getContext())
                || mqttClient == null || !mqttClient.isConnected()) {
            LogCat.e(TAG, "pubByPassMessage  no net or client is null ");
            ResponseBean res = new ResponseBean();
            res.setErrCode(RpcErrorCode.WHAT_ERROR + "");
//            BaseNotification.newInstance().postNotificationName(
//                    NotificationConstant.dismissDialogException, res);
            return;
        }
        try {
            final MqttMessage message = new MqttMessage();
            message.setPayload(publishMessage.getBytes());
            message.setQos(2);
            message.setRetained(false);
            mqttClient.publish(getPubTopic(sn, "bypass"),
                    message, null, new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            if (messages.size() > 2000) messages.clear();
                            messages.put(msgId, opCode);
                            LogCat.e(TAG, "pubByPassMessage onSuccess");
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable ex) {
                            LogCat.e(TAG, "pubByPassMessage fail, ", ex);
                        }
                    });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private String getPubTopic(String clientId, String type) {
        return String.format("/APP/%s/%s/%s/pub", SpUtils.getUID(), clientId, type);
    }

    /**
     * 订阅发布消息
     */
    private void initSubToken() {
        tokenSS1EventSub = String.format("/APP/%s/%s/SS1/response/sub", SpUtils.getUID(), clientId);
        tokenSS1EventSub = String.format("/APP/%s/%s/FS1/response/sub", SpUtils.getUID(), clientId);
    }

    /**
     * 取消订阅
     */
    private void unsubscribe(String[] topic) {
        try {
            IMqttToken iMqttToken = mqttClient.unsubscribe(topic);
            iMqttToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    LogCat.e(TAG, "unsubscribe onSuccess");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    LogCat.e(TAG, "unsubscribe onFailure ");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        LogCat.e(TAG, "disconnect start");
        clientId = "";
        if (mqttClient == null || !mqttClient.isConnected()) return;
        unsubscribe(getTokens());
        try {
            IMqttToken iMqttToken = mqttClient.disconnect();
            iMqttToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    LogCat.e(TAG, "disconnect Success");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    LogCat.e(TAG, "disconnect fail");
                }
            });
            mqttClient = null;
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}
