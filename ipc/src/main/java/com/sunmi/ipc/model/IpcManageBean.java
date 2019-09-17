package com.sunmi.ipc.model;

/**
 * Description:
 * Created by bruce on 2019/9/17.
 */
public class IpcManageBean {

    private int leftImageResId;
    private String title;
    private String summary;
    private String rightText;

    public IpcManageBean(int leftImageResId, String title, String summary, String rightText) {
        this.leftImageResId = leftImageResId;
        this.title = title;
        this.summary = summary;
        this.rightText = rightText;
    }

    public int getLeftImageResId() {
        return leftImageResId;
    }

    public void setLeftImageResId(int leftImageResId) {
        this.leftImageResId = leftImageResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getRightText() {
        return rightText;
    }

    public void setRightText(String rightText) {
        this.rightText = rightText;
    }

}
