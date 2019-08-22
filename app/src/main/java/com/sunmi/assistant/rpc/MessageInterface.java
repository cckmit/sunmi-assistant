package com.sunmi.assistant.rpc;

import com.sunmi.assistant.mine.model.MessageCountBean;
import com.sunmi.assistant.mine.model.MessageListBean;
import com.sunmi.assistant.mine.model.MsgSettingListBean;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-14.
 */
public interface MessageInterface {

    String path = "/api/notification/";

    /**
     * 获取APP消息中心类型统计数量列表
     *
     * @param request
     * @return
     */
    @POST(path + "mailbox/app/getMessageCount")
    Call<BaseResponse<MessageCountBean>> getMessageCount(@Body BaseRequest request);

    /**
     * 获取消息列表
     *
     * @param request
     * @return
     */
    @POST(path + "mailbox/getMessageList")
    Call<BaseResponse<MessageListBean>> getMessageList(@Body BaseRequest request);

    /**
     * 在接收列表中删除消息
     *
     * @param request
     * @return
     */
    @POST(path + "mailbox/deleteMessage")
    Call<BaseResponse<Object>> deleteMessage(@Body BaseRequest request);

    /**
     * 接收消息后对消息进行处理，如查看、操作或标记为已读/未读
     *
     * @param request
     * @return
     */
    @POST(path + "mailbox/updateReceiveStatusByModel")
    Call<BaseResponse<Object>> updateReceiveStatusByModel(@Body BaseRequest request);

    /**
     * 获取app消息中心提醒状态列表
     *
     * @param request
     * @return
     */
    @POST(path + "remindersetting/app/getList")
    Call<BaseResponse<MsgSettingListBean>> getSettingList(@Body BaseRequest request);

    /**
     * 修改app消息中心提醒状态
     *
     * @param request
     * @return
     */
    @POST(path + "remindersetting/app/update")
    Call<BaseResponse<Object>> updateSettingStatus(@Body BaseRequest request);
}
