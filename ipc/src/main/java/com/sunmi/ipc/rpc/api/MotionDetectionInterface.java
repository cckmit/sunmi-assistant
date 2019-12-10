package com.sunmi.ipc.rpc.api;

import com.sunmi.ipc.model.MotionVideoListResp;
import com.sunmi.ipc.model.MotionVideoTimeSlotsResp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * @author yinhui
 * @date 2019-12-09
 */
public interface MotionDetectionInterface {

    String URL = "/ipc/api/device/motion/";

    @POST(URL + "getTimeSlots")
    Call<BaseResponse<MotionVideoTimeSlotsResp>> getTimeSlots(@Body BaseRequest request);

    @POST(URL + "getList")
    Call<BaseResponse<MotionVideoListResp>> getVideoList(@Body BaseRequest request);
}
