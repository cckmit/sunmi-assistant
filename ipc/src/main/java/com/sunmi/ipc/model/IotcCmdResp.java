package com.sunmi.ipc.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Description:
 * Created by bruce on 2019/8/14.
 */
public class IotcCmdResp<T> {
    /**
     * errCode : 0
     * msg_id : 427211565774728491
     * data : [{"errCode":0,"cmd":32,"result":[{"start_time":1565601921,"end_time":1565696219},{"start_time":1565696279,"end_time":1565762289},{"start_time":1565764722,"end_time":1565771679},{"start_time":1565771681,"end_time":1565774718}]}]
     */

    @SerializedName("errCode")
    private int errCode;
    @SerializedName("msg_id")
    private String msgId;
    @SerializedName("data")
    private List<DataBean<T>> data;

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public List<DataBean<T>> getData() {
        return data;
    }

    public void setData(List<DataBean<T>> data) {
        this.data = data;
    }

    public static class DataBean<T> {
        /**
         * errCode : 0
         * cmd : 32
         * result : [{"start_time":1565601921,"end_time":1565696219},{"start_time":1565696279,"end_time":1565762289},{"start_time":1565764722,"end_time":1565771679},{"start_time":1565771681,"end_time":1565774718}]
         */

        @SerializedName("errCode")
        private int errCode;
        @SerializedName("cmd")
        private int cmd;
        @SerializedName("result")
        private T result;

        public int getErrCode() {
            return errCode;
        }

        public void setErrCode(int errCode) {
            this.errCode = errCode;
        }

        public int getCmd() {
            return cmd;
        }

        public void setCmd(int cmd) {
            this.cmd = cmd;
        }

        public T getResult() {
            return result;
        }

        public void setResult(T result) {
            this.result = result;
        }
    }

}
