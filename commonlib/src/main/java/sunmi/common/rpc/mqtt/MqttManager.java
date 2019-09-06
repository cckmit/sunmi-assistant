package sunmi.common.rpc.mqtt;

import android.os.Build;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import sunmi.common.base.BaseApplication;
import sunmi.common.rpc.RpcErrorCode;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.HttpsUtils;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.rpc.sunmicall.RequestBean;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.ToastUtils;
import sunmi.common.utils.Utils;
import sunmi.common.utils.log.LogCat;

public class MqttManager {
    private String TAG = "IPC" + getClass().getSimpleName();

    private static MqttManager instance = null;

    private static String clientId;//由客户端生成，连接后唯一，重连更换
    static String tokenRequestSub;//订阅的request token
    public static String tokenSS1EventSub;//订阅的event ipc
    public static String tokenFS1EventSub;//订阅的event ipc
    public static String tokenFM010EventSub;//订阅的event ipc
    public static String tokenFM020EventSub;//订阅的event ipc

    public static String tokenWEBSS1EventSub;//订阅的event ipc
    public static String tokenWEBFS1EventSub;//订阅的event ipc
    public static String tokenWEBFM010EventSub;//订阅的event ipc
    public static String tokenWEBFM020EventSub;//订阅的event ipc

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

    String getClientId() {
        return clientId;
    }

    int getOpCode(String msgID) {
        Integer code = messages.get(msgID);
        return code != null ? code : -1;
    }

    void removeMessage(String msgID) {
        messages.remove(msgID);
    }

    public void createEmqToken(final boolean isInit) {
        LogCat.e(TAG, "mqtt createEmqToken start");
        SunmiStoreApi.getInstance().createEmqToken(new RetrofitCallback<EmqTokenResp>() {
            @Override
            public void onSuccess(int code, String msg, EmqTokenResp response) {
                LogCat.e(TAG, "mqtt createEmqToken success");
                if (response == null) {
                    ToastUtils.toastForShort(BaseApplication.getContext(), "network error");
                }
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
//        if (TextUtils.isEmpty(clientId))
        clientId = resp.getUsername() + "_" + System.currentTimeMillis();

        mqttClient = new MqttAndroidClient(BaseApplication.getContext(), serverURL, clientId);
        options = new MqttConnectOptions();
        options.setUserName(resp.getUsername());
        options.setPassword(resp.getPassword().toCharArray());//解密
        options.setAutomaticReconnect(false);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(10);
        //setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知消息
//        options.setWill(topic, "close".getBytes(), 2, true);
        options.setSocketFactory(HttpsUtils.getSslSocketFactory().sSLSocketFactory);//设置证书校验 //todo ssl
        mqttClient.setCallback(getSMMqttCallback()); //发布订阅回调
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
        if (mqttClient == null) {
            return;
        }
        if (isConnecting) return;
        isConnecting = true;
        try {
            mqttClient.connect(options, BaseApplication.getContext(), new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    isConnecting = false;
                    LogCat.e(TAG, "mqtt Connect SUCCESS");
                    subscribeToTopic(); //订阅
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    isConnecting = false;
                    LogCat.e(TAG, "mqtt Connect FAIL");
                    if (!NetworkUtils.isNetworkAvailable(BaseApplication.getContext())) {
                        LogCat.e(TAG, "mqtt Connect fail no net");
                        mqttClient = null;
                        isRegister = false;
                        return;
                    }
                    if (asyncActionToken.getException() != null
                            && asyncActionToken.getException().getReasonCode()
                            == MqttException.REASON_CODE_FAILED_AUTHENTICATION
                            || asyncActionToken.getException().getReasonCode()
                            == MqttException.REASON_CODE_CLIENT_EXCEPTION) {
                        LogCat.e(TAG, "mqtt Connect fail,code = " + asyncActionToken.getException().getReasonCode()
                                + ", cause = " + asyncActionToken.getException().getCause());
                        isRegister = false;
                        createEmqToken(true);
                    }
                    if (exception != null)
                        exception.printStackTrace();
                }
            });
        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }

    private String[] getTokens() {
        return new String[]{tokenSS1EventSub, tokenFS1EventSub, tokenFM010EventSub, tokenFM020EventSub,
                tokenWEBSS1EventSub, tokenWEBFS1EventSub, tokenWEBFM010EventSub, tokenWEBFM020EventSub};//初始化所有门店下的设备dev状态
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

            int[] qoss = new int[]{2, 2, 2, 2, 2, 2, 2, 2};
            //订阅1
            mqttClient.subscribe(getTokens(), qoss, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    LogCat.e(TAG, "mqtt Subscribed token success");
//                    if (!isRegister)
                    pubRegister(clientId);
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
     * 注册
     */
    public void pubRegister(String clientId) {
        Map<String, String> map = new HashMap<>();
        map.put("client_id", clientId);
        map.put("bin_version", CommonHelper.getAppVersionName(BaseApplication.getContext()));
        map.put("os_version", Build.VERSION.RELEASE);
        map.put("country_code", 0 + "");
        JSONObject param = new JSONObject();

        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                param.put(entry.getKey(), entry.getValue());
            }
            RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                    "0x0056", param);
            MqttManager.getInstance().pubRegisterMessage(requestBean.serialize());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发布request消息
     */
    public void pubRegisterMessage(String publishMessage) {
        if (!NetworkUtils.isNetworkAvailable(BaseApplication.getContext())
                || mqttClient == null || !mqttClient.isConnected())
            return;
        try {
            final MqttMessage message = new MqttMessage();
            message.setPayload(publishMessage.getBytes());
            message.setQos(2);
            message.setRetained(false);
            mqttClient.publish(getPubTopic(clientId, "register"),
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
            res.setErrCode(RpcErrorCode.RPC_COMMON_ERROR + "");
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


    /**
     * 发布固件升级消息
     */
    public void pubIpcMessage(final String msgId, final int opCode, String model, String publishMessage) {
        LogCat.e(TAG, "pubIpcMessage : publishMessage = " + publishMessage);
        LogCat.e(TAG, "pubIpcMessage : publishMessage topic= " + getPubIpcTopic(clientId, model));
        if (!NetworkUtils.isNetworkAvailable(BaseApplication.getContext())
                || mqttClient == null || !mqttClient.isConnected()) {
            ResponseBean res = new ResponseBean();
            res.setErrCode(RpcErrorCode.RPC_COMMON_ERROR + "");
            return;
        }
        try {
            final MqttMessage message = new MqttMessage();
            message.setPayload(publishMessage.getBytes());
            message.setQos(2);
            message.setRetained(false);
            mqttClient.publish(getPubIpcTopic(clientId, model),
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

    ///WEB/userId/clientId/model/request/pub
    private String getPubIpcTopic(String clientId, String model) {
        return String.format("/WEB/%s/%s/%s/request/pub", SpUtils.getUID(), clientId, model);
    }

    /**
     * 订阅发布消息
     */
    private void initSubToken() {// /APP/userid/client_id/SS1/response/sub
        tokenSS1EventSub = String.format("/APP/%s/%s/SS1/response/sub", SpUtils.getUID(), clientId);
        tokenFS1EventSub = String.format("/APP/%s/%s/FS1/response/sub", SpUtils.getUID(), clientId);
        tokenFM010EventSub = String.format("/APP/%s/%s/FM010/response/sub", SpUtils.getUID(), clientId);
        tokenFM020EventSub = String.format("/APP/%s/%s/FM020/response/sub", SpUtils.getUID(), clientId);
        tokenWEBSS1EventSub = String.format("/WEB/%s/%s/SS1/response/sub", SpUtils.getUID(), clientId);
        tokenWEBFS1EventSub = String.format("/WEB/%s/%s/FS1/response/sub", SpUtils.getUID(), clientId);
        tokenWEBFM010EventSub = String.format("/WEB/%s/%s/FM010/response/sub", SpUtils.getUID(), clientId);
        tokenWEBFM020EventSub = String.format("/WEB/%s/%s/FM020/response/sub", SpUtils.getUID(), clientId);
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

    public boolean isConnected() {
        return mqttClient != null && mqttClient.isConnected();
    }

    public void reconnect() {
        if (!isConnected()) {
            mqttConnect();
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
