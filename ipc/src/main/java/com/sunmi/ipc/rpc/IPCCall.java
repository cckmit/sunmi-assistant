package com.sunmi.ipc.rpc;

import android.content.Context;

import com.sunmi.ipc.config.IpcConstants;
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
        int opCode = OpcodeConstants.getWifiList;
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
            int opCode = OpcodeConstants.setIPCWifi;
            RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                    "0x" + Integer.toHexString(opCode), object);
            new IPCLocalApi(url).post(context, "", requestBean.getMsgId(), opCode, requestBean.serialize());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //获取IPC无线关联前端AP的状态
    public void getApStatus(Context context, String url) {
        int opCode = OpcodeConstants.getApStatus;
        RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                "0x" + Integer.toHexString(opCode), new JSONObject());
        new IPCLocalApi(url).post(context, "", requestBean.getMsgId(), opCode, requestBean.serialize());
    }

    //获取ipc当前连接的AP信息
    public void getIpcConnectApMsg(Context context, String url) {
        int opCode = OpcodeConstants.getIpcConnectApMsg;
        RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                "0x" + Integer.toHexString(opCode), new JSONObject());
        new IPCLocalApi(url).post(context, "", requestBean.getMsgId(), opCode, requestBean.serialize());
    }

    //获取IPC无线还是有线
    public void getIsWire(Context context, String url) {
        int opCode = OpcodeConstants.getIsWire;
        RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                "0x" + Integer.toHexString(opCode), new JSONObject());
        new IPCLocalApi(url).post(context, "", requestBean.getMsgId(), opCode, requestBean.serialize());
    }

    //获取token,绑定ipc使用
    public void getToken(Context context, String url) {
        int opCode = OpcodeConstants.getIpcToken;
        RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                "0x" + Integer.toHexString(opCode), new JSONObject());
        new IPCLocalApi(url).post(context, "", requestBean.getMsgId(), opCode, requestBean.serialize());
    }

    /**
     * 设置缩放
     *
     * @param model IPC的型号
     * @param sn    IPC的SN
     * @param zoom  0-500
     */
    public void fsZoom(String model, String sn, int zoom) {
        try {
            JSONObject object = new JSONObject();
            object.put("sn", sn);
            object.put("zoom", zoom);
            int opCode = OpcodeConstants.fsZoom;
            RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                    "0x" + Integer.toHexString(opCode), object);
            post(null, sn, requestBean.getMsgId(), opCode, model, requestBean.serialize(), 20);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置焦距
     *
     * @param model IPC的型号
     * @param sn    IPC的SN
     * @param focus 0-780
     */
    public void fsFocus(String model, String sn, int focus) {
        try {
            JSONObject object = new JSONObject();
            object.put("sn", sn);
            object.put("focus", focus);
            int opCode = OpcodeConstants.fsFocus;
            RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                    "0x" + Integer.toHexString(opCode), object);
            post(null, sn, requestBean.getMsgId(), opCode, model, requestBean.serialize(), 15);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 自动对焦（中心区域）
     */
    public void fsAutoFocus(String model, String sn) {
        fsAutoFocus(model, sn, 101, 101);
    }

    /**
     * 自动对焦
     *
     * @param model IPC的型号
     * @param sn    IPC的SN
     * @param x     0-100
     * @param y     0-100
     */
    public void fsAutoFocus(String model, String sn, int x, int y) {
        try {
            JSONObject object = new JSONObject();
            object.put("sn", sn);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(x);
            jsonArray.put(y);
            object.put("area", jsonArray);
            int opCode = OpcodeConstants.fsAutoFocus;
            RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                    "0x" + Integer.toHexString(opCode), object);
            post(null, sn, requestBean.getMsgId(), opCode, model, requestBean.serialize(), 15);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重置变焦和对焦
     *
     * @param model  IPC的型号
     * @param sn     IPC的SN
     * @param target 重置对象
     *               target = 0, 缩放电机和对焦电机均复位，画面可能会很模糊
     *               target = 1, 缩放电机复位，同时自动对焦(若缩放电机本来在复位位置，则直接退出)
     */
    public void fsReset(String model, String sn, int target) {
        try {
            JSONObject object = new JSONObject();
            object.put("sn", sn);
            object.put("target", target);
            int opCode = OpcodeConstants.fsReset;
            RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                    "0x" + Integer.toHexString(opCode), object);
            post(null, sn, requestBean.getMsgId(), opCode, model, requestBean.serialize(), 20);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取设备信息
     *
     * @param model IPC的型号
     * @param sn    IPC的SN
     */
    public void fsGetStatus(String model, String sn) {
        try {
            int opCode = OpcodeConstants.fsGetStatus;
            JSONObject object = new JSONObject();
            object.put("sn", sn);
            RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                    "0x" + Integer.toHexString(opCode), object);
            post(null, sn, requestBean.getMsgId(), opCode, model, requestBean.serialize(), 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置进店绊线
     *
     * @param model IPC的型号
     * @param sn    IPC的SN
     * @param start 起始点坐标，统一按1920*1080范围定义
     * @param end   终止点坐标，统一按1920*1080范围定义
     */
    public void fsLine(String model, String sn, int[] start, int[] end) {
        try {
            JSONObject object = new JSONObject();
            object.put("sn", sn);
            object.put("start_x", start[0]);
            object.put("start_y", start[1]);
            object.put("end_x", end[0]);
            object.put("end_y", end[1]);
            object.put("resolution", 0);
            int opCode = OpcodeConstants.fsSetLine;
            RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                    "0x" + Integer.toHexString(opCode), object);
            post(null, sn, requestBean.getMsgId(), opCode, model, requestBean.serialize(), 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 画面调整之前获取SD卡状态信息
     */
    public void getSdState(Context context, String model, String sn) {
        try {
            int opCode = OpcodeConstants.getSdStatus;
            JSONObject object = new JSONObject();
            object.put("sn", sn);
            RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                    "0x" + Integer.toHexString(opCode), object);
            post(context, sn, requestBean.getMsgId(), IpcConstants.getSdcardStatus, model, requestBean.serialize());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 进sd卡管理获取SD卡状态状态
     */
    public void getSdStatus(Context context, String model, String sn) {
        try {
            int opCode = OpcodeConstants.getSdStatus;
            JSONObject object = new JSONObject();
            object.put("sn", sn);
            RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                    "0x" + Integer.toHexString(opCode), object);
            post(context, sn, requestBean.getMsgId(), opCode, model, requestBean.serialize());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * SD卡格式化
     */
    public void sdcardFormat(Context context, String model, String sn) {
        try {
            int opCode = OpcodeConstants.sdcardFormat;
            JSONObject object = new JSONObject();
            object.put("sn", sn);
            RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                    "0x" + Integer.toHexString(opCode), object);
            post(context, sn, requestBean.getMsgId(), opCode, model, requestBean.serialize());
        } catch (Exception e) {
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
            int opCode = OpcodeConstants.ipcUpgrade;
            RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                    "0x" + Integer.toHexString(opCode), object);
            new IpcRemoteSettingApi().post(context, sn, requestBean.getMsgId(), opCode, model, requestBean.serialize());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * ipc升级 查询升级状态
     *
     * @param context
     * @param sn
     */
    public void ipcQueryUpgradeStatus(Context context, String model, String sn) {
        try {
            JSONObject object = new JSONObject();
            object.put("sn", sn);
            int opCode = OpcodeConstants.ipcQueryUpgradeStatus;
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
            int opCode = OpcodeConstants.getIpcNightIdeRotation;
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
    public void setIpcNightIdeRotation(Context context, String model, String sn, int night,
                                       int wdrMode, int ledIndicator, int rotation) {
        try {
            JSONObject object = new JSONObject();
            object.put("sn", sn);
            object.put("night_mode", night);
            object.put("wdr_mode", wdrMode);
            object.put("led_indicator", ledIndicator);
            object.put("rotation", rotation);
            int opCode = OpcodeConstants.setIpcNightIdeRotation;
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
            int opCode = OpcodeConstants.getIpcDetection;
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
            int opCode = OpcodeConstants.setIpcDetection;
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
            list.add(JsonUtils.getRequest("0x" + Integer.toHexString(OpcodeConstants.getIpcDetection), object));
            list.add(JsonUtils.getRequest("0x" + Integer.toHexString(OpcodeConstants.getIpcNightIdeRotation), object));
            post(context, sn, msgId, IpcConstants.getIpcSettingMessage, model, JsonUtils.getMultiRequest(msgId, list));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * ipc重启
     * action--reset:恢复出厂设置，reboot:重启设备
     *
     * @param context
     * @param sn
     */
    public void ipcRelaunch(Context context, String sn, String model) {
        try {
            JSONObject object = new JSONObject();
            object.put("sn", sn);
            object.put("action", "reboot");
            int opCode = OpcodeConstants.ipcRelaunch;
            RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                    "0x" + Integer.toHexString(opCode), object);
            new IpcRemoteSettingApi().post(context, sn, requestBean.getMsgId(), opCode, model, requestBean.serialize());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置图像亮度参数接口
     */
    public void fsAdjustBrightness(Context context, String model, String sn, int compensation) {
        try {
            JSONObject object = new JSONObject();
            object.put("sn", sn);
            object.put("compensation", compensation);
            int opCode = OpcodeConstants.fsAdjustBrightness;
            RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                    "0x" + Integer.toHexString(opCode), object);
            post(context, sn, requestBean.getMsgId(), opCode, model, requestBean.serialize());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置图像对比度参数接口
     */
    public void fsAdjustContrast(Context context, String model, String sn, int contrast) {
        try {
            JSONObject object = new JSONObject();
            object.put("sn", sn);
            object.put("contrast", contrast);
            int opCode = OpcodeConstants.fsAdjustContrast;
            RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                    "0x" + Integer.toHexString(opCode), object);
            post(context, sn, requestBean.getMsgId(), opCode, model, requestBean.serialize());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置图像饱和度参数接口
     */
    public void fsAdjustSaturation(Context context, String model, String sn, int saturation) {
        try {
            JSONObject object = new JSONObject();
            object.put("sn", sn);
            object.put("saturation", saturation);
            int opCode = OpcodeConstants.fsAdjustSaturation;
            RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                    "0x" + Integer.toHexString(opCode), object);
            post(context, sn, requestBean.getMsgId(), opCode, model, requestBean.serialize());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取图像配置当前参数请求
     */
    public void getVideoParams(Context context, String model, String sn) {
        try {
            JSONObject object = new JSONObject();
            object.put("sn", sn);
            int opCode = OpcodeConstants.getVideoParams;
            RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                    "0x" + Integer.toHexString(opCode), object);
            post(context, sn, requestBean.getMsgId(), opCode, model, requestBean.serialize());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置聚焦微调加（+）的接口
     */
    public void fsAdjustFocusAdd(Context context, String model, String sn) {
        try {
            JSONObject object = new JSONObject();
            object.put("sn", sn);
            object.put("param", "add");
            int opCode = OpcodeConstants.fsAdjustFocusAdd;
            RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                    "0x" + Integer.toHexString(opCode), object);
            post(context, sn, requestBean.getMsgId(), opCode, model, requestBean.serialize());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置聚焦微调加（+）的接口
     */
    public void fsAdjustFocusMinus(Context context, String model, String sn) {
        try {
            JSONObject object = new JSONObject();
            object.put("sn", sn);
            object.put("param", "minus");
            int opCode = OpcodeConstants.fsAdjustFocusMinus;
            RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                    "0x" + Integer.toHexString(opCode), object);
            post(context, sn, requestBean.getMsgId(), opCode, model, requestBean.serialize());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置聚焦微复位
     */
    public void fsAdjustFocusReset(Context context, String model, String sn) {
        try {
            JSONObject object = new JSONObject();
            object.put("sn", sn);
            object.put("param", "reset");
            int opCode = OpcodeConstants.fsAdjustFocusReset;
            RequestBean requestBean = new RequestBean(Utils.getMsgId(),
                    "0x" + Integer.toHexString(opCode), object);
            post(context, sn, requestBean.getMsgId(), opCode, model, requestBean.serialize());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void post(Context context, String sn, String msgId, int opCode, String json) {

    }

    /**
     * IPC setting
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

    public void post(Context context, String sn, String msgId, int opCode, String model, String json, int timeout) {
        SunmiDevice device = CommonConstants.SUNMI_DEVICE_MAP.get(sn);
        if (device != null) {
            new IPCLocalApi(device.getIp()).post(context, sn, msgId, opCode, json, timeout);
        } else {
            new IpcRemoteSettingApi().post(context, sn, msgId, opCode, model, json);
        }
    }

}
