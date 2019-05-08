package com.sunmi.ipc.rpc;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sunmi.common.rpc.sunmicall.BaseApi;
import sunmi.common.rpc.sunmicall.RequestBean;

/**
 * Description:
 * Created by bruce on 2019/3/29.
 */
public class IPCCall extends BaseApi {

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

    //获取摄像头搜索到的wifi列表
    public void getWifiList(Context context) {
        post(context, "", IpcConstants.getWifiList, new JSONObject());
    }

    public void setIPCWifi(Context context, String ssid, String keyMgmt, String key) {
        try {
            JSONObject object = new JSONObject();
            object.put("ssid", ssid);
            object.put("key_mgmt", keyMgmt);
            object.put("key", key);
            post(context, "", IpcConstants.setIPCWifi, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //获取IPC无线关联前端AP的状态
    public void getApStatus(Context context) {
        post(context, "", IpcConstants.getApStatus, new JSONObject());
    }

    //获取IPC无线还是有线
    public void getIsWire(Context context) {
        post(context, "", IpcConstants.getIsWire, new JSONObject());
    }

    //获取token,绑定ipc使用
    public void getToken(Context context) {
        post(context, "", IpcConstants.getIpcToken, new JSONObject());
    }

    //获取token,绑定ipc使用
    public void getToken(Context context, String url) {
        int opCode = IpcConstants.getIpcToken;
        RequestBean requestBean = new RequestBean("11111",
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

    private void post(Context context, String sn, int opCode, JSONObject jsonObject) {
        RequestBean requestBean = new RequestBean("11111", "0x" + Integer.toHexString(opCode), jsonObject);
        post(context, sn, requestBean.getMsgId(), opCode, requestBean.serialize());
    }

    //    public static boolean isRemoteCall(String sn) {
//        return !TextUtils.equals(sn, MyNetworkCallback.CURRENT_ROUTER);
//    }
    @Override
    public void post(Context context, String sn, String msgId, int opCode, String json) {
        new IPCLocalApi().post(context, sn, msgId, opCode, json);
//        if (isRemoteCall(sn)) {
//            new RemoteApApi().post(context, sn, msgId, opCode, json);
//        } else {
//            new IPCLocalApi().post(context, sn, msgId, opCode, json);
//        }
    }

}
