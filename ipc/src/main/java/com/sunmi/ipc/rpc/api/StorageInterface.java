package com.sunmi.ipc.rpc.api;

import com.sunmi.ipc.model.StorageListResp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-10-24.
 */
public interface StorageInterface {

    String path = "/ipc/api/storage/";

    /***
     * 获取设备云存储服务信息
     * @param request
     * @return
     */
    @POST(path + "getList")
    Call<BaseResponse<StorageListResp>> getStorageInfo(@Body BaseRequest request);
}
