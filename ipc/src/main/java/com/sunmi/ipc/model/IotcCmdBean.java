package com.sunmi.ipc.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * Created by bruce on 2019/5/25.
 */
public class IotcCmdBean {
    /**
     * msg_id : 1000
     * params : [{"cmd":22,"channel ":1,"param":{}}]
     */

    private String msg_id;
    private List<Params> params;

    private IotcCmdBean(String msg_id, List<Params> params) {
        this.msg_id = msg_id;
        this.params = params;
    }

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public List<Params> getParams() {
        return params;
    }

    public void setParams(List<Params> params) {
        this.params = params;
    }

    public void setParams(Params paramsItem) {
        if (params == null)
            params = new ArrayList<>();
        params.add(paramsItem);
    }

    public static class Params {

        /**
         * cmd : 22
         * channel  : 1
         * param : {}
         */
        private int cmd;
        private int channel;
        private Object param;

        Params(int cmd, int channel, Object param) {
            this.cmd = cmd;
            this.channel = channel;
            this.param = param;
        }

        public int getCmd() {
            return cmd;
        }

        public void setCmd(int cmd) {
            this.cmd = cmd;
        }

        public int getChannel() {
            return channel;
        }

        public void setChannel(int channel) {
            this.channel = channel;
        }

        public Object getParam() {
            return param;
        }

        public void setParam(Object param) {
            this.param = param;
        }
    }

    public static final class Builder {
        private String msg_id;
        private int cmd;
        private int channel;
        private Map<String, Object> param = new HashMap<>();

        public Builder setMsg_id(String msg_id) {
            this.msg_id = msg_id;
            return this;
        }

        public Builder setCmd(int cmd) {
            this.cmd = cmd;
            return this;
        }

        public Builder setChannel(int channel) {
            this.channel = channel;
            return this;
        }

        public Builder setParam(String key, Object value) {
            this.param.put(key, value);
            return this;
        }

        public IotcCmdBean builder() {
            Params params1 = new Params(cmd, channel, param);
            List<Params> params = new ArrayList<>();
            params.add(params1);
            return new IotcCmdBean(msg_id, params);
        }
    }

}
