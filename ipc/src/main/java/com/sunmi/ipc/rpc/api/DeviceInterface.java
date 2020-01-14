package com.sunmi.ipc.rpc.api;

import com.sunmi.ipc.model.IpcNewFirmwareResp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import sunmi.common.model.ServiceResp;
import sunmi.common.router.model.IpcListResp;
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

    /**
     * 通过设备sn查询ipc设备捆绑云存储服务信息
     *
     * @param request
     * @return
     */
    @POST(path + "getStorageList")
    Call<BaseResponse<ServiceResp>> getStorageList(@Body BaseRequest request);

    /**
     * 通过设备sn或shopId查询ipc设备收银视频服务信息
     *
     * @param request
     * @return
     */
    @POST(path + "getAuditVideoServiceList")
    Call<BaseResponse<ServiceResp>> getAuditVideoServiceList(@Body BaseRequest request);

    /**
     * 指定设备是收银防损开通状态
     * @param request
     * @return
     */
    @POST(path + "getAuditSecurityPolicyList")
    Call<BaseResponse<ServiceResp>> getAuditSecurityPolicyList(@Body BaseRequest request);

}
