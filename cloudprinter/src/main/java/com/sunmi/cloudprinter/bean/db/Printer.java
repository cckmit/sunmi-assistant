package com.sunmi.cloudprinter.bean.db;

import org.litepal.crud.DataSupport;

public class Printer extends DataSupport {
    private String shop_id;
    private String sn;
    private String status;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
