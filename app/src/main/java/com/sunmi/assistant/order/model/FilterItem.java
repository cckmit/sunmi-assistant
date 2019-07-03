package com.sunmi.assistant.order.model;

/**
 * @author yinhui
 * @since 2019-06-27
 */
public class FilterItem {
    private int id;
    private String titleName;
    private String itemName;
    private boolean isChecked;

    public FilterItem(int id, String name) {
        this.id = id;
        this.titleName = name;
        this.itemName = name;
    }

    public FilterItem(int id, String titleName, String itemName) {
        this.id = id;
        this.titleName = titleName;
        this.itemName = itemName;
    }

    public int getId() {
        return id;
    }

    public String getTitleName() {
        return titleName;
    }

    public String getItemName() {
        return itemName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
