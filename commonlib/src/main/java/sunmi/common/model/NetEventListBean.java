package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * @author yangShiJie
 * @date 2019-12-24
 */
public class NetEventListBean implements Serializable {

    @SerializedName("event_list")
    private List<EventListBean> eventList;

    public List<EventListBean> getEventList() {
        return eventList;
    }

    public void setEventList(List<EventListBean> eventList) {
        this.eventList = eventList;
    }

    public static class EventListBean implements Serializable {
        /**
         * event_type : 20
         * params : {"model":"","alias":"","domain_name":"银豹"}
         * time : 1576143854
         */

        @SerializedName("event_type")
        private int eventType;
        @SerializedName("params")
        private ParamsBean params;
        @SerializedName("time")
        private int time;

        public int getEventType() {
            return eventType;
        }

        public void setEventType(int eventType) {
            this.eventType = eventType;
        }

        public ParamsBean getParams() {
            return params;
        }

        public void setParams(ParamsBean params) {
            this.params = params;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public static class ParamsBean implements Serializable {
            /**
             * model :
             * alias :
             * domain_name : 银豹
             */

            @SerializedName("model")
            private String model;
            @SerializedName("alias")
            private String alias;
            @SerializedName("domain_name")
            private String domainName;

            public String getModel() {
                return model;
            }

            public void setModel(String model) {
                this.model = model;
            }

            public String getAlias() {
                return alias;
            }

            public void setAlias(String alias) {
                this.alias = alias;
            }

            public String getDomainName() {
                return domainName;
            }

            public void setDomainName(String domainName) {
                this.domainName = domainName;
            }
        }
    }
}
