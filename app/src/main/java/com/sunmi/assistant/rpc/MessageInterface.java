package com.sunmi.assistant.rpc;

import com.sunmi.assistant.mine.model.MessageCountBean;

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

    @POST(path+"mailbox/app/getMessageCount")
    Call<BaseResponse<MessageCountBean>> getMessageCount(@Body BaseRequest request);
}
