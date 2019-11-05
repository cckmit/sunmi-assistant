package com.sunmi.ipc.rpc.api;

import sunmi.common.router.model.IpcListResp;
import com.sunmi.ipc.model.IpcNewFirmwareResp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * Description:
 * Created by bruce on 2019/5/7.
 */
public interface DeviceInterface {
    String path = "ipc/api/device/";

    /**
     * ipc绑定
     */
    @POST(path + "bind")
    Call<BaseResponse<Object>> bind(@Body BaseRequest request);

    /**
     * ipc解绑
     */
    @POST(path + "unbind")
    Call<BaseResponse<Object>> unbind(@Body BaseRequest request);

    /**
     * 获取用户指定店铺下的摄像头首页列表
     */
    @POST(path + "getDetailList")
    Call<BaseResponse<IpcListResp>> getDetailList(@Body BaseRequest request);

    /**
     * 用户更新摄像头基本信息
     */
    @POST(path + "updateBaseInfo")
    Call<BaseResponse<Object>> updateBaseInfo(@Body BaseRequest request);

    /**
     * 获取最新版本
     */
    @POST(path + "firmware/detect")
    Call<BaseResponse<IpcNewFirmwareResp>> newFirmware(@Body BaseRequest request);

}
