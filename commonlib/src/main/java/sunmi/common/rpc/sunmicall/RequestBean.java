package sunmi.common.rpc.sunmicall;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RequestBean {

    /**
     * msg_id : 427211544076275181
     * params : [{"opcode":"0x2025","param":{}}]
     */
    private String msgId;

    private String opcode;

    private JSONObject param;

    public RequestBean(String msgId, String opcode, JSONObject param) {
        this.msgId = msgId;
        this.opcode = opcode;
        this.param = param;
    }

    public RequestBean(MqttMessage message) {
        deserializer(message.toString());
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getOpcode() {
        return opcode;
    }

    public void setOpcode(String opcode) {
        this.opcode = opcode;
    }

    public JSONObject getParam() {
        return param;
    }

    public void setParam(JSONObject param) {
        this.param = param;
    }

    private void deserializer(String jsonString) {
        try {
            JSONObject object = new JSONObject(jsonString);
            this.msgId = object.getString("msg_id");
            JSONArray jsonArray = object.getJSONArray("params");
            opcode = ((JSONObject) jsonArray.opt(0)).getString("opcode");
            param = ((JSONObject) jsonArray.opt(0)).getJSONObject("param");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //封装json信息
    public String serialize() {
        String jsonResult = "";
        try {
            JSONObject object = new JSONObject();
            object.put("msg_id", getMsgId());
            JSONArray jsonarray = new JSONArray();
            //jsonObj0
            JSONObject jsonObj0 = new JSONObject();
            jsonObj0.put("opcode", opcode);
            jsonObj0.put("param", param);
            jsonarray.put(jsonObj0);
            object.put("params", jsonarray);
            jsonResult = object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonResult;
    }

}
