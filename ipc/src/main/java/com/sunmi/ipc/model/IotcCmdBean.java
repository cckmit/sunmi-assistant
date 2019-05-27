package com.sunmi.ipc.model;

import java.util.ArrayList;
import java.util.List;

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

    public IotcCmdBean() {
    }

    public IotcCmdBean(String msg_id) {
        this.msg_id = msg_id;
    }

    public IotcCmdBean(String msg_id, int cmd, int channel, Object param) {
        this.msg_id = msg_id;
        Params params1 = new Params(cmd, channel, param);
        params = new ArrayList<>();
        params.add(params1);
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
        public Params() {
        }

        public Params(int cmd, int channel, Object param) {
            this.cmd = cmd;
            this.channel = channel;
            this.param = param;
        }

        /**
         * cmd : 22
         * channel  : 1
         * param : {}
         */

        private int cmd;
        private int channel;
        private Object param;

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

}
