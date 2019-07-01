package com.sunmi.assistant.order.model;

/**
 * @author yinhui
 * @since 2019-06-27
 */
public class FilterItem {
    private int id;
    private String name;
    private boolean isChecked;

    public FilterItem(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
