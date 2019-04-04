package sunmi.common.rpc.mqtt;

import java.io.Serializable;

public class MQttBean {

    /**
     * code : 1
     * data : {"clientID":"SN003","username":"APP_SN003","password":"shz+8TEvdKrPjRARajjZNd/EFlXpzs1FL/czepSytfiGttNUlEeA0A==","serverAddress":"dev.wap.sunmi.com","port":"30412"}
     * msg :
     */

    private int code;
    private DataBean data;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean implements Serializable {
        /**
         * clientID : SN003
         * username : APP_SN003
         * password : shz+8TEvdKrPjRARajjZNd/EFlXpzs1FL/czepSytfiGttNUlEeA0A==
         * serverAddress : dev.wap.sunmi.com
         * port : 30412
         */

        private String clientID;
        private String username;
        private String password;
        private String serverAddress;
        private String port;

        public String getClientID() {
            return clientID;
        }

        public void setClientID(String clientID) {
            this.clientID = clientID;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getServerAddress() {
            return serverAddress;
        }

        public void setServerAddress(String serverAddress) {
            this.serverAddress = serverAddress;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }
    }
}
