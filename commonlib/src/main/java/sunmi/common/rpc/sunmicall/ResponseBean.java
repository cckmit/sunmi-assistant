package sunmi.common.rpc.sunmicall;

import android.text.TextUtils;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResponseBean {

    /**
     * data : [{"opcode":"0x2025","result":{"data":[]},"errcode":0}]
     * errcode : 0
     * msg_id : 427211544080387273
     */

    private String msgId;
    private String errCode;

    private String opcode;
    private String dataErrCode;
    private JSONObject result;

    private JSONArray jsonArray;

    public ResponseBean() {
    }

    public ResponseBean(String response) {
        deserializer(response);
    }

    /**
     * 根据json格式类型解析
     *
     * @param response
     * @param type
     */
    public ResponseBean(String response, String type) {
        if (TextUtils.equals(type, "params"))
            deserializerParams(response);
        else if (TextUtils.equals(type, "data"))
            deserializer(response);
    }

    public ResponseBean(MqttMessage message) {
        deserializer(message.toString());
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getOpcode() {
        return opcode;
    }

    public void setOpcode(String opcode) {
        this.opcode = opcode;
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public String getDataErrCode() {
        return dataErrCode;
    }

    public void setDataErrCode(String dataErrCode) {
        this.dataErrCode = dataErrCode;
    }

    public JSONObject getResult() {
        return result;
    }

    public void setResult(JSONObject result) {
        this.result = result;
    }

    private void deserializer(String jsonString) {
        try {
            JSONObject object = new JSONObject(jsonString);
            if (object.has("msg_id")) {
                msgId = object.getString("msg_id");
            }
            try {
                errCode = object.getInt("errcode") + "";
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray = object.getJSONArray("data");
            opcode = ((JSONObject) jsonArray.opt(0)).getString("opcode");
            if (((JSONObject) jsonArray.opt(0)).has("result")) {
                result = ((JSONObject) jsonArray.opt(0)).getJSONObject("result");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void deserializerParams(String jsonString) {
        try {
            JSONObject object = new JSONObject(jsonString);
            this.msgId = object.getString("msg_id");
            JSONArray jsonArray = object.getJSONArray("params");
            opcode = ((JSONObject) jsonArray.opt(0)).getString("opcode");
            result = ((JSONObject) jsonArray.opt(0)).getJSONObject("param");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
