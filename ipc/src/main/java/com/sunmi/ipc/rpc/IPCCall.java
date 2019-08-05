package com.sunmi.ipc.rpc;

import android.content.Context;

import com.sunmi.ipc.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.constant.CommonConstants;
import sunmi.common.model.SunmiDevice;
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
     * 获取SD卡状态信息
     */
    public void getSdState(String ip) {
        int opCode = IpcConstants.getSdState;
        RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                "0x" + Integer.toHexString(opCode), new JSONObject());
        new IPCLocalApi(ip).postRouterTimeout("", opCode, requestBean.serialize(), 20);
    }

    /**
     * 设置缩放
     *
     * @param zoom 0-500
     */
    public void fsZoom(String ip, int zoom) {
        try {
            JSONObject object = new JSONObject();
            object.put("zoom", zoom);
            int opCode = IpcConstants.fsZoom;
            RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                    "0x" + Integer.toHexString(opCode), object);
            new IPCLocalApi(ip).postRouterTimeout("", opCode, requestBean.serialize(), 20);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置焦距
     *
     * @param focus 0-780
     */
    public void fsFocus(String ip, int focus) {
        try {
            JSONObject object = new JSONObject();
            object.put("focus", focus);
            int opCode = IpcConstants.fsFocus;
            RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                    "0x" + Integer.toHexString(opCode), object);
            new IPCLocalApi(ip).postRouterTimeout("", opCode, requestBean.serialize(), 20);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 自动对焦（中心区域）
     */
    public void fsAutoFocus(String ip) {
        fsAutoFocus(ip, 101, 101);
    }

    /**
     * 自动对焦
     *
     * @param ip IPC的局域网IP地址
     * @param x  0-100
     * @param y  0-100
     */
    public void fsAutoFocus(String ip, int x, int y) {
        try {
            JSONObject object = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(x);
            jsonArray.put(y);
            object.put("area", jsonArray);
            int opCode = IpcConstants.fsAutoFocus;
            RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                    "0x" + Integer.toHexString(opCode), object);
            new IPCLocalApi(ip).postRouterTimeout("", opCode, requestBean.serialize(), 20);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 重置变焦和对焦
     *
     * @param ip     IPC的局域网IP地址
     * @param target 重置对象
     *               target = 0, 缩放电机和对焦电机均复位，画面可能会很模糊
     *               target = 1, 缩放电机复位，同时自动对焦(若缩放电机本来在复位位置，则直接退出)
     */
    public void fsReset(String ip, int target) {
        try {
            JSONObject object = new JSONObject();
            object.put("target", target);
            int opCode = IpcConstants.fsReset;
            RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                    "0x" + Integer.toHexString(opCode), object);
            new IPCLocalApi(ip).postRouterTimeout("", opCode, requestBean.serialize(), 20);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取设备信息
     */
    public void fsGetStatus(String ip) {
        int opCode = IpcConstants.fsGetStatus;
        RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                "0x" + Integer.toHexString(opCode), new JSONObject());
        new IPCLocalApi(ip).postRouterTimeout("", opCode, requestBean.serialize(), 20);
    }

    /**
     * 设置进店绊线
     *
     * @param ip    IPC的局域网IP地址
     * @param start 起始点坐标，统一按1920*1080范围定义
     * @param end   终止点坐标，统一按1920*1080范围定义
     */
    public void fsLine(String ip, int[] start, int[] end) {
        try {
            JSONObject object = new JSONObject();
            object.put("start_x", start[0]);
            object.put("start_y", start[1]);
            object.put("end_x", end[0]);
            object.put("end_y", end[1]);
            object.put("resolution", 0);
            int opCode = IpcConstants.fsSetLine;
            RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                    "0x" + Integer.toHexString(opCode), object);
            new IPCLocalApi(ip).postRouterTimeout("", opCode, requestBean.serialize(), 20);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
     * ipc升级 mqtt
     *
     * @param context
     * @param url
     * @param sn
     * @param version
     */
    public void ipcUpgrade(Context context, String model, String sn, String url, String version) {
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
     * 获取IPC声音侦测和动态侦测相关配置信息
     *
     * @param context 上下文
     * @param model   型号
     * @param sn      SN
     */
    public void getIpcDetection(Context context, String model, String sn) {
        try {
            JSONObject object = new JSONObject();
            object.put("sn", sn);
            int opCode = IpcConstants.getIpcDetection;
            RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                    "0x" + Integer.toHexString(opCode), object);
            post(context, sn, requestBean.getMsgId(), opCode, model, requestBean.serialize());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置IPC声音侦测和动态侦测相关配置
     *
     * @param context     上下文
     * @param model       型号（SS1、FS1）
     * @param sn          SN
     * @param motionLevel 动态侦测设置，0：关闭，1～3：低中高
     * @param audioLevel  声音侦测设置，0：关闭，1～3：低中高
     * @param weekday     侦测时间中每周的开启日，按位表示，从低位到高位依次表示周一到周日，0：关闭，1：开启。
     *                    特别的，0x80表示7*24小时开启侦测。
     * @param startTime   时间戳，表示从当天00:00开始到现在经历过的秒数，如28800表示08:00，72000表示20:00。
     * @param stopTime    时间戳，表示从当天00:00开始到现在经历过的秒数，如果比{@param startTime}小，则表示次日。
     */
    public void setIpcDetection(Context context, String model, String sn, int motionLevel, int audioLevel,
                                int weekday, int startTime, int stopTime) {
        try {
            JSONObject object = new JSONObject();
            object.put("sn", sn);
            object.put("motion_level", motionLevel);
            object.put("audio_level", audioLevel);
            object.put("weekday", weekday);
            object.put("start_time", startTime);
            object.put("stop_time", stopTime);
            int opCode = IpcConstants.setIpcDetection;
            RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                    "0x" + Integer.toHexString(opCode), object);
            post(context, sn, requestBean.getMsgId(), opCode, model, requestBean.serialize());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取 IPC声音侦测和动态侦测相关配置信息
     * 获取 夜视模式 指示灯 旋转信息
     *
     * @param context
     * @param model
     * @param sn
     */
    public void getIpcSettingMessage(Context context, String model, String sn) {
        String msgId = Utils.getMsgId();
        try {
            JSONObject object = new JSONObject();
            object.put("sn", sn);
            List<JSONObject> list = new ArrayList<>();
            list.add(JsonUtils.getRequest("0x" + Integer.toHexString(IpcConstants.getIpcDetection), object));
            list.add(JsonUtils.getRequest("0x" + Integer.toHexString(IpcConstants.getIpcNightIdeRotation), object));
            post(context, sn, msgId, IpcConstants.getIpcSettingMessage, model, JsonUtils.getMultiRequest(msgId, list));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void post(Context context, String sn, int opCode, JSONObject jsonObject) {
        RequestBean requestBean = new RequestBean(Utils.getMsgId(), "0x" + Integer.toHexString(opCode), jsonObject);
        post(context, sn, requestBean.getMsgId(), opCode, requestBean.serialize());
    }

    @Override
    public void post(Context context, String sn, String msgId, int opCode, String json) {
        SunmiDevice device = CommonConstants.SUNMI_DEVICE_MAP.get(sn);
        if (device != null) {
            new IPCLocalApi(device.getIp()).post(context, sn, msgId, opCode, json);
        } else {
            new IpcRemoteApApi().post(context, sn, msgId, opCode, json);
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
        SunmiDevice device = CommonConstants.SUNMI_DEVICE_MAP.get(sn);
        if (device != null) {
            new IPCLocalApi(device.getIp()).post(context, sn, msgId, opCode, json);
        } else {
            new IpcRemoteSettingApi().post(context, sn, msgId, opCode, model, json);
        }
    }

}
