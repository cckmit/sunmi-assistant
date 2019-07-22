package com.sunmi.ipc.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Description:
 * Created by bruce on 2019/3/15.
 */
public class JsonUtils {

    public static String getMultiRequest(String msgId, List<JSONObject> params) {
        String jsonResult = "";
        try {
            JSONObject object = new JSONObject();
            object.put("msg_id", msgId);
            JSONArray jsonarray = new JSONArray(params);
            object.put("params", jsonarray);
            jsonResult = object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonResult;
    }

    public static JSONObject getRequest(String code, JSONObject params) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("opcode", code);
            jsonObject.put("param", params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
