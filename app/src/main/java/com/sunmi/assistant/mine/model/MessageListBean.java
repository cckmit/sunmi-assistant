package com.sunmi.assistant.mine.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-14.
 */
public class MessageListBean {

    @SerializedName("total_count")
    private int totalCount;
    @SerializedName("return_count")
    private int returnCount;
    @SerializedName("msg_list")
    private List<MsgListBean> msgList;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getReturnCount() {
        return returnCount;
    }

    public void setReturnCount(int returnCount) {
        this.returnCount = returnCount;
    }

    public List<MsgListBean> getMsgList() {
        return msgList;
    }

    public void setMsgList(List<MsgListBean> msgList) {
        this.msgList = msgList;
    }

    public static class MsgListBean {
        /**
         * msg_id : 111784
         * company_id : 145
         * shop_id : 9474
         * title : notif-device-esl-ap-on/offline-title:shop_name=OpenAPI%E5%BA%97%E9%93%BA&company_name=cloudtest
         * description : notif-device-esl-ap-on/offline-desc:shop_name=OpenAPI%E5%BA%97%E9%93%BA&company_name=cloudtest&device_name=new_struction_ap&disconnect_time=1565753904
         * level : 2
         * model_id : 13
         * model_name : notif-model-device-esl-ap-on/offline
         * content : notif-device-esl-ap-on/offline-content:device_name=new_struction_ap&disconnect_time=1565753904
         * receive_time : 1565754575
         * receive_status : 0
         * is_remind : 1
         * major_button_name :
         * major_button_link :
         * minor_button_name :
         * minor_button_link :
         */

        @SerializedName("msg_id")
        private int msgId;
        @SerializedName("company_id")
        private int companyId;
        @SerializedName("shop_id")
        private int shopId;
        @SerializedName("title")
        private String title;
        @SerializedName("description")
        private String description;
        @SerializedName("level")
        private int level;
        @SerializedName("model_id")
        private int modelId;
        @SerializedName("model_name")
        private String modelName;
        @SerializedName("content")
        private String content;
        @SerializedName("receive_time")
        private int receiveTime;
        @SerializedName("receive_status")
        private int receiveStatus;
        @SerializedName("is_remind")
        private int isRemind;
        @SerializedName("major_button_name")
        private String majorButtonName;
        @SerializedName("major_button_link")
        private String majorButtonLink;
        @SerializedName("minor_button_name")
        private String minorButtonName;
        @SerializedName("minor_button_link")
        private String minorButtonLink;

        public int getMsgId() {
            return msgId;
        }

        public void setMsgId(int msgId) {
            this.msgId = msgId;
        }

        public int getCompanyId() {
            return companyId;
        }

        public void setCompanyId(int companyId) {
            this.companyId = companyId;
        }

        public int getShopId() {
            return shopId;
        }

        public void setShopId(int shopId) {
            this.shopId = shopId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public int getModelId() {
            return modelId;
        }

        public void setModelId(int modelId) {
            this.modelId = modelId;
        }

        public String getModelName() {
            return modelName;
        }

        public void setModelName(String modelName) {
            this.modelName = modelName;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getReceiveTime() {
            return receiveTime;
        }

        public void setReceiveTime(int receiveTime) {
            this.receiveTime = receiveTime;
        }

        public int getReceiveStatus() {
            return receiveStatus;
        }

        public void setReceiveStatus(int receiveStatus) {
            this.receiveStatus = receiveStatus;
        }

        public int getIsRemind() {
            return isRemind;
        }

        public void setIsRemind(int isRemind) {
            this.isRemind = isRemind;
        }

        public String getMajorButtonName() {
            return majorButtonName;
        }

        public void setMajorButtonName(String majorButtonName) {
            this.majorButtonName = majorButtonName;
        }

        public String getMajorButtonLink() {
            return majorButtonLink;
        }

        public void setMajorButtonLink(String majorButtonLink) {
            this.majorButtonLink = majorButtonLink;
        }

        public String getMinorButtonName() {
            return minorButtonName;
        }

        public void setMinorButtonName(String minorButtonName) {
            this.minorButtonName = minorButtonName;
        }

        public String getMinorButtonLink() {
            return minorButtonLink;
        }

        public void setMinorButtonLink(String minorButtonLink) {
            this.minorButtonLink = minorButtonLink;
        }

        public MsgTag getTitleTag() {
            return new MsgTag(getTitle());
        }

        public MsgTag getDetailTag() {
            return new MsgTag(getContent());
        }

        public MsgTag getMajorButtonLinkTag() {
            return new MsgTag(getMajorButtonLink());
        }
    }
}
