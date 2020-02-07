package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ServiceListResp {

    @SerializedName("service_list")
    private List<ServiceListBean> serviceList;

    public List<ServiceListBean> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<ServiceListBean> serviceList) {
        this.serviceList = serviceList;
    }

    public static class ServiceListBean {
        /**
         * service_type : 1
         * active_status : 1
         * promote_status : 1
         */

        @SerializedName("service_type")
        private int serviceType;
        @SerializedName("active_status")
        private int activeStatus;
        @SerializedName("promote_status")
        private int promoteStatus;

        public int getServiceType() {
            return serviceType;
        }

        public void setServiceType(int serviceType) {
            this.serviceType = serviceType;
        }

        public int getActiveStatus() {
            return activeStatus;
        }

        public void setActiveStatus(int activeStatus) {
            this.activeStatus = activeStatus;
        }

        public int getPromoteStatus() {
            return promoteStatus;
        }

        public void setPromoteStatus(int promoteStatus) {
            this.promoteStatus = promoteStatus;
        }
    }
}
