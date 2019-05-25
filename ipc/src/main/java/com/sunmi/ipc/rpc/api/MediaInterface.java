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
    String videoPath = "ipc/api/media/video/";

    /**
     * 获取视频列表
     */
    @POST(path + "getVideoList")
    Call<BaseResponse<VideoListResp>> getVideoList(@Body BaseRequest request);
//    Call<BaseResponse<Object>> getVideoList(@Body BaseRequest request);

    /**
     * 获取时间轴
     */
    @POST(videoPath + "getTimeSlots")
    Call<BaseResponse<Object>> getTimeSlots(@Body BaseRequest request);


}
