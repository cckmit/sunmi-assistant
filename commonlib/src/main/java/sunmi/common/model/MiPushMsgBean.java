package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

/**
 * Description:
 * Created by bruce on 2019/8/26.
 */
public class MiPushMsgBean {
    /**
     * company_id : 285
     * model_id : 23
     * model_name : notif-model-device-ipc-motion-detect-video
     * msg_id : 145962
     * shop_id : 8316
     */

    @SerializedName("company_id")
    private int companyId;
    @SerializedName("model_id")
    private int modelId;
    @SerializedName("model_name")
    private String modelName;
    @SerializedName("msg_id")
    private int msgId;
    @SerializedName("shop_id")
    private int shopId;

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
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

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

}
