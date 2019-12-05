package com.sunmi.ipc.model;

import sunmi.common.view.DropdownMenuNew;

/**
 * @author yinhui
 * @date 2019-12-05
 */
public class DropdownTime extends DropdownMenuNew.Model {
    private int id;
    private String title;
    private String itemName;
    private long timeStart;
    private long timeEnd;

    public DropdownTime(int id, String name, long timeStart, long timeEnd) {
        this.id = id;
        this.title = name;
        this.itemName = name;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public DropdownTime(int id, String title, String itemName, long timeStart, long timeEnd) {
        this.id = id;
        this.title = title;
        this.itemName = itemName;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public DropdownTime(int id, String title, String itemName, long timeStart, long timeEnd, boolean isChecked) {
        super(isChecked);
        this.id = id;
        this.title = title;
        this.itemName = itemName;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getItemName() {
        return itemName;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }
}
