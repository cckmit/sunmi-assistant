package com.sunmi.assistant.rpc;

import com.sunmi.assistant.mine.model.MessageCountBean;
import com.sunmi.assistant.mine.model.MessageListBean;
import com.sunmi.assistant.mine.model.MsgSettingListBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.rpc.cloud.SunmiStoreRetrofitClient;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * Description: 消息中心接口
 *
 * @author linyuanpeng on 2019-08-14.
 */
public class MessageCenterApi {

    private static final String TAG = "MessageCenterApi";

    private static final class Singleton {
        private static final MessageCenterApi INSTANCE = new MessageCenterApi();
    }


    public static MessageCenterApi getInstance() {
        return Singleton.INSTANCE;
    }

    private MessageCenterApi() {

    }

    /**
     * 获取APP消息中心类型统计数量列表
     *
     * @param callback
     */
    public void getMessageCount(RetrofitCallback<MessageCountBean> callback) {
        SunmiStoreRetrofitClient.getInstance().create(MessageInterface.class)
                .getMessageCount(new BaseRequest(""))
                .enqueue(callback);
    }

    /**
     * 获取消息列表
     *
     * @param modelId
     * @param pageNum
     * @param pageSize
     * @param callback
     */
    public void getMessageList(int modelId, int pageNum, int pageSize,
                               RetrofitCallback<MessageListBean> callback) {
        List<Integer> modelIdList = new ArrayList<>();
        modelIdList.add(modelId);
        try {
            String params = new JSONObject()
                    .put("model_id_list", modelIdList)
                    .put("page_num", pageNum)
                    .put("page_size", pageSize)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(MessageInterface.class)
                    .getMessageList(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 在接收列表中删除消息
     *
     * @param msgId
     * @param callback
     */
    public void deleteMessage(int msgId, RetrofitCallback<Object> callback) {
        List<Integer> msgIdList = new ArrayList<>();
        msgIdList.add(msgId);
        try {
            String params = new JSONObject()
                    .put("msg_id_list", msgIdList)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(MessageInterface.class)
                    .deleteMessage(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 接收消息后对消息进行处理，如查看、操作或标记为已读/未读
     *
     * @param modelId
     * @param callback
     */
    public void updateReceiveStatusByModel(int modelId, int statusCode, RetrofitCallback<Object> callback) {
        try {
            String params = new JSONObject()
                    .put("model_id", modelId)
                    .put("status_code", statusCode)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(MessageInterface.class)
                    .updateReceiveStatusByModel(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取app消息中心提醒状态列表
     *
     * @param callback
     */
    public void getSettingList(RetrofitCallback<MsgSettingListBean> callback) {
        SunmiStoreRetrofitClient.getInstance().create(MessageInterface.class)
                .getSettingList(new BaseRequest(""))
                .enqueue(callback);
    }

    /**
     * 修改app消息中心提醒状态
     *
     * @param settingId
     * @param status
     * @param callback
     */
    public void updateSettingStatus(int settingId, int status, RetrofitCallback<Object> callback) {
        try {
            String params = new JSONObject()
                    .put("setting_id", settingId)
                    .put("status", status)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(MessageInterface.class)
                    .updateSettingStatus(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
