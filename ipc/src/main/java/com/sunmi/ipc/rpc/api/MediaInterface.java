package com.sunmi.ipc.rpc.api;

import com.sunmi.ipc.model.VideoListResp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * Description:
 * Created by bruce on 2019/5/10.
 */
public interface MediaInterface {

    String path = "ipc/api/media/";

    /**
     * 创建emq token去连mqtt
     */
    @POST(path + "getVideoList")
    Call<BaseResponse<VideoListResp>> getVideoList(@Body BaseRequest request);

}
