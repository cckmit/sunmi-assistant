package com.sunmi.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-10-22.
 */
public class SubscriptionListBean {

    /**
     * service_list : [{"id":1,"service_type":1,"service_no":"1571709544753362","service_name":"7天云存储服务","order_no":"1571709544093362","product_no":"YCC0001","device_id":2224,"device_sn":"SS101D8BS00088","device_name":"","device_model_id":15,"device_model":"FM010","service_duration":2592000,"create_time":1571709544,"subscribe_time":1571709544,"expire_time":1574301544,"valid_time":2571415,"status":2,"renew_status":1,"renew_error_code":5420}]
     * total_count : 11
     */

    @SerializedName("total_count")
    private int totalCount;
    @SerializedName("service_list")
    private List<ServiceDetailBean> serviceList;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<ServiceDetailBean> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<ServiceDetailBean> serviceList) {
        this.serviceList = serviceList;
    }

}
