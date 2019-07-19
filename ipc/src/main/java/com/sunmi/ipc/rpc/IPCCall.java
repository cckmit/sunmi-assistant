package com.sunmi.ipc.rpc;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sunmi.common.constant.CommonUdpData;
import sunmi.common.rpc.sunmicall.BaseIpcApi;
import sunmi.common.rpc.sunmicall.RequestBean;
import sunmi.common.utils.Utils;

/**
 * Description:
 * Created by bruce on 2019/3/29.
 */
public class IPCCall extends BaseIpcApi {

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

    //获取IPC无线还是有线
    public void getWifiList(Context context, String url) {
        int opCode = IpcConstants.getWifiList;
        RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                "0x" + Integer.toHexString(opCode), new JSONObject());
        new IPCLocalApi(url).post(context, "", requestBean.getMsgId(), opCode, requestBean.serialize());
    }

    public void setIPCWifi(Context context, String ssid, String keyMgmt, String key, String url) {
        if (keyMgmt.equalsIgnoreCase("NONE")) {
            key = "0";
        }
        try {
            JSONObject object = new JSONObject();
            object.put("ssid", ssid);
            object.put("key_mgmt", keyMgmt);
            object.put("key", key);
            int opCode = IpcConstants.setIPCWifi;
            RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                    "0x" + Integer.toHexString(opCode), object);
            new IPCLocalApi(url).post(context, "", requestBean.getMsgId(), opCode, requestBean.serialize());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //获取IPC无线关联前端AP的状态
    public void getApStatus(Context context, String url) {
        int opCode = IpcConstants.getApStatus;
        RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                "0x" + Integer.toHexString(opCode), new JSONObject());
        new IPCLocalApi(url).post(context, "", requestBean.getMsgId(), opCode, requestBean.serialize());
    }

    //获取ipc当前连接的AP信息
    public void getIpcConnectApMsg(Context context, String url) {
        int opCode = IpcConstants.getIpcConnectApMsg;
        RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                "0x" + Integer.toHexString(opCode), new JSONObject());
        new IPCLocalApi(url).post(context, "", requestBean.getMsgId(), opCode, requestBean.serialize());
    }

    //获取IPC无线还是有线
    public void getIsWire(Context context, String url) {
        int opCode = IpcConstants.getIsWire;
        RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                "0x" + Integer.toHexString(opCode), new JSONObject());
        new IPCLocalApi(url).post(context, "", requestBean.getMsgId(), opCode, requestBean.serialize());
    }

    //获取token,绑定ipc使用
    public void getToken(Context context, String url) {
        int opCode = IpcConstants.getIpcToken;
        RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                "0x" + Integer.toHexString(opCode), new JSONObject());
        new IPCLocalApi(url).post(context, "", requestBean.getMsgId(), opCode, requestBean.serialize());
    }

    /**
     * 设置缩放
     *
     * @param zoom 0-500
     */
    public void fsZoom(int zoom, Context context) {
        try {
            JSONObject object = new JSONObject();
            object.put("set", zoom);
            post(context, "", IpcConstants.fsZoom, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置焦距
     *
     * @param focus 0-780
     */
    public void fsFocus(int focus, Context context) {
        try {
            JSONObject object = new JSONObject();
            object.put("set", focus);
            post(context, "", IpcConstants.fsFocus, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置对焦点
     *
     * @param x 0-100
     * @param y 0-100
     */
    public void fsSetFocusPoint(int x, int y, Context context) {
        try {
            JSONObject object = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(x);
            jsonArray.put(y);
            object.put("area", jsonArray);
            post(context, "", IpcConstants.fsAutoFocus, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 自动对焦
     */
    public void fsAutoFocus(Context context) {
        fsSetFocusPoint(101, 101, context);
    }

    /**
     * 重置
     */
    public void fsReset(Context context) {
        post(context, "", IpcConstants.fsReset, new JSONObject());
    }

    /**
     * 夜间模式
     *
     * @param set 1-夜间
     */
    public void fsIrMode(int set, Context context) {
        try {
            JSONObject object = new JSONObject();
            object.put("set", set);
            post(context, "", IpcConstants.fsIrMode, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取设备信息
     */
    public void fsGetStatus(Context context) {
        post(context, "", IpcConstants.fsGetStatus, new JSONObject());
    }

    /**
     * ipc升级 mqtt
     *
     * @param context
     * @param url
     * @param sn
     * @param version
     */
    public void ipcUpgrade(Context context, String model, String url, String sn, String version) {
        try {
            JSONObject object = new JSONObject();
            object.put("sn", sn);
            object.put("bin_version", version);
            object.put("url", url);
            int opCode = IpcConstants.ipcUpgrade;
            RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                    "0x" + Integer.toHexString(opCode), object);
            new IpcRemoteSettingApi().post(context, sn, requestBean.getMsgId(), opCode, model, requestBean.serialize());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取  夜视模式 指示灯 旋转信息
     *
     * @param context
     * @param model
     * @param sn
     */
    public void getIpcNightIdeRotation(Context context, String model, String sn) {
        try {
            JSONObject object = new JSONObject();
            object.put("sn", sn);
            int opCode = IpcConstants.getIpcNightIdeRotation;
            RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                    "0x" + Integer.toHexString(opCode), object);
            post(context, sn, requestBean.getMsgId(), opCode, model, requestBean.serialize());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param context
     * @param model        SS1 FS1
     * @param sn
     * @param night        夜视模式 0:始终关闭/1:始终开启/2:自动切换
     * @param ledIndicator 指示灯 0:关闭/1:开启
     * @param rotation     旋转 0:关闭/1:开启
     */
    public void setIpcNightIdeRotation(Context context, String model, String sn, int night, int ledIndicator, int rotation) {
        try {
            JSONObject object = new JSONObject();
            object.put("sn", sn);
            object.put("night_mode", night);
            object.put("led_indicator", ledIndicator);
            object.put("rotation", rotation);
            int opCode = IpcConstants.setIpcNightIdeRotation;
            RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                    "0x" + Integer.toHexString(opCode), object);
            post(context, sn, requestBean.getMsgId(), opCode, model, requestBean.serialize());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context
     * @param model       SS1 FS1
     * @param sn
     * @param motionLevel 动作检测级别 0-1-2-3对应关闭-低-中-高
     * @param audioLevel  声音检测级别 0-1-2-3对应关闭-低-中-高
     * @param weekday     每周监测日期，详见备注 按位计算，比如0x1对应周一打开，
     *                    0x3对应周一加周二，0x7f对应周一到周日，特别的，0x80对应7*24小时。
     * @param startTime   从0点开始的时间戳 以从0点开始的秒数表示，如28800对应8点，
     *                    72000对应20点，若开始时间大于结束时间，表示从前一天开始到第二天结束。
     * @param stopTime    从0点开始的时间戳
     */
    public void ipcDetect(Context context, String model, String sn, String motionLevel, String audioLevel,
                          String weekday, String startTime, String stopTime) {
        try {
            JSONObject object = new JSONObject();
            object.put("sn", sn);
            object.put("motion_level", motionLevel);
            object.put("audio_level", audioLevel);
            object.put("weekday", weekday);
            object.put("start_time", startTime);
            object.put("stop_time", stopTime);
            int opCode = IpcConstants.ipcDetect;
            RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                    "0x" + Integer.toHexString(opCode), object);
            post(context, sn, requestBean.getMsgId(), opCode, model, requestBean.serialize());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void post(Context context, String sn, int opCode, JSONObject jsonObject) {
        RequestBean requestBean = new RequestBean(Utils.getMsgId(), "0x" + Integer.toHexString(opCode), jsonObject);
        post(context, sn, requestBean.getMsgId(), opCode, requestBean.serialize());
    }

    /**
     * 是否局域网
     *
     * @param sn
     * @return
     */
    public static boolean isRemoteCall(String sn) {
        return TextUtils.isEmpty(CommonUdpData.currentIpcDeviceId(sn));
    }

    @Override
    public void post(Context context, String sn, String msgId, int opCode, String json) {
        if (isRemoteCall(sn)) {
            new IpcRemoteApApi().post(context, sn, msgId, opCode, json);
        } else {
            new IPCLocalApi(CommonUdpData.currentIpcIp(sn)).post(context, sn, msgId, opCode, json);
        }
    }

    /**
     * IPC setting
     *
     * @param context
     * @param sn
     * @param msgId
     * @param opCode
     * @param model
     * @param json
     */
    @Override
    public void post(Context context, String sn, String msgId, int opCode, String model, String json) {
        if (isRemoteCall(sn)) {
            new IpcRemoteSettingApi().post(context, sn, msgId, opCode, model, json);
        } else {
            new IPCLocalApi(CommonUdpData.currentIpcIp(sn)).post(context, sn, msgId, opCode, json);
        }
    }

}
