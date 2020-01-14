package com.sunmi.ipc.cash.model;

import java.util.Objects;

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
    private String name;
    private String desc;
    private boolean checked;

    public CashTagFilter(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public CashTagFilter(int id, String name, boolean checked) {
        this.id = id;
        this.name = name;
        this.checked = checked;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CashTagFilter that = (CashTagFilter) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
