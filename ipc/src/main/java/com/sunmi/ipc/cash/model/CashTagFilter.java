package com.sunmi.ipc.cash.model;

/**
 * @author yinhui
 * @date 2019-12-25
 */
public class CashTagFilter {

    /**
     * 正常视频Tag
     */
    public static final int TAG_ID_NORMAL = 0;
    /**
     * 自定义异常视频Tag
     */
    public static final int TAG_ID_CUSTOM = 1;

    private int id;
    private String desc;
    private boolean checked;

    public CashTagFilter(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public CashTagFilter(int id, String desc, boolean checked) {
        this.id = id;
        this.desc = desc;
        this.checked = checked;
    }

    public int getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
