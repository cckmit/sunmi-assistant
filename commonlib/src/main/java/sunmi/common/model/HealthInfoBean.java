package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * @author yangShiJie
 * @date 2019-12-24
 */
public class HealthInfoBean implements Serializable {

    /**
     * net_inter_inten : {"2.4g":1,"5g":2}
     * net_delay_list : [{"domain_type":1,"domain_id":1,"domain_name":"公网","delay":1.21,"quality":1},{"domain_type":3,"domain_id":3,"domain_name":"银豹","delay":10.11,"quality":2},{"domain_type":3,"domain_id":4,"domain_name":"客无忧","delay":10.11,"quality":2}]
     */

    @SerializedName("net_inter_inten")
    private NetInterIntenBean netInterInten;
    @SerializedName("net_delay_list")
    private List<NetDelayListBean> netDelayList;

    public NetInterIntenBean getNetInterInten() {
        return netInterInten;
    }

    public void setNetInterInten(NetInterIntenBean netInterInten) {
        this.netInterInten = netInterInten;
    }

    public List<NetDelayListBean> getNetDelayList() {
        return netDelayList;
    }

    public void setNetDelayList(List<NetDelayListBean> netDelayList) {
        this.netDelayList = netDelayList;
    }

    public static class NetInterIntenBean implements Serializable {
        @SerializedName("2.4g")
        private int wifi2g;
        @SerializedName("5g")
        private int wifi5g;

        public int getWifi2g() {
            return wifi2g;
        }

        public void setWifi2g(int wifi2g) {
            this.wifi2g = wifi2g;
        }

        public int getWifi5g() {
            return wifi5g;
        }

        public void setWifi5g(int wifi5g) {
            this.wifi5g = wifi5g;
        }
    }

    public static class NetDelayListBean implements Serializable {
        /**
         * domain_type : 1
         * domain_id : 1
         * domain_name : 公网
         * delay : 1.21
         * quality : 1
         */

        @SerializedName("domain_type")
        private int domainType;
        @SerializedName("domain_id")
        private int domainId;
        @SerializedName("domain_name")
        private String domainName;
        @SerializedName("delay")
        private double delay;
        @SerializedName("quality")
        private int quality;

        public int getDomainType() {
            return domainType;
        }

        public void setDomainType(int domainType) {
            this.domainType = domainType;
        }

        public int getDomainId() {
            return domainId;
        }

        public void setDomainId(int domainId) {
            this.domainId = domainId;
        }

        public String getDomainName() {
            return domainName;
        }

        public void setDomainName(String domainName) {
            this.domainName = domainName;
        }

        public double getDelay() {
            return delay;
        }

        public void setDelay(double delay) {
            this.delay = delay;
        }

        public int getQuality() {
            return quality;
        }

        public void setQuality(int quality) {
            this.quality = quality;
        }
    }
}
