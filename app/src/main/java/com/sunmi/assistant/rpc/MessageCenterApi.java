package com.sunmi.assistant.rpc;

import com.sunmi.assistant.mine.model.MessageCountBean;
import com.sunmi.assistant.mine.model.MessageListBean;

import org.json.JSONException;
import org.json.JSONObject;

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

    private MessageCenterApi() {

    }

    /**
     * 获取APP消息中心类型统计数量列表
     *
     * @param callback
     */
    public static void getMessageCount(RetrofitCallback<MessageCountBean> callback) {
        SunmiStoreRetrofitClient.getInstance().create(MessageInterface.class)
                .getMessageCount(new BaseRequest(""))
                .enqueue(callback);
    }

    /**
     * 获取消息列表
     *
     * @param modelIdList
     * @param pageNum
     * @param pageSize
     * @param callback
     */
    public static void getMessageList(List modelIdList, int pageNum, int pageSize,
                                      RetrofitCallback<MessageListBean> callback) {
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
     * @param msgIdList
     * @param callback
     */
    public static void deleteMessage(List msgIdList,RetrofitCallback<Object> callback){
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
    public static void updateReceiveStatusByModel(List modelId,RetrofitCallback<Object> callback){
        try {
            String params = new JSONObject()
                    .put("model_id", modelId)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(MessageInterface.class)
                    .updateReceiveStatusByModel(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
