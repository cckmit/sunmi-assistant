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

    private String returnData;
    private String msgId;
    private String errCode;

    private String opcode;
    private int dataErrCode;
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

    public String getReturnData() {
        return returnData;
    }

    public void setReturnData(String returnData) {
        this.returnData = returnData;
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

    public int getDataErrCode() {
        return dataErrCode;
    }

    public void setDataErrCode(int dataErrCode) {
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
            returnData = jsonString;
            JSONObject object = new JSONObject(jsonString);
            if (object.has("msg_id")) {
                msgId = object.getString("msg_id");
            }
            try {
                if (object.has("errcode")) {
                    errCode = object.getInt("errcode") + "";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray = object.getJSONArray("data");
            opcode = ((JSONObject) jsonArray.opt(0)).getString("opcode");
            try {
                dataErrCode = ((JSONObject) jsonArray.opt(0)).getInt("errcode");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (((JSONObject) jsonArray.opt(0)).has("result")) {
                try {
                    result = ((JSONObject) jsonArray.opt(0)).getJSONObject("result");
                } catch (Exception e) {
                    result = new JSONObject();
                }

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
