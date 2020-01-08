package com.sunmi.ipc.model;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-12-30.
 */
public class CashTag {

    private int id;
    private String name;
    private String tip;

    public CashTag(int id, String name, String tip) {
        this.id = id;
        this.name = name;
        this.tip = tip;
    }

    public int getTag() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTip() {
        return tip;
    }
}
