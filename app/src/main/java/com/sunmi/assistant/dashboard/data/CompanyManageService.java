package com.sunmi.assistant.dashboard.data;

import com.sunmi.assistant.dashboard.data.response.CompanyInfoResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * 订单管理Http接口
 *
 * @author yinhui
 * @since 2019-06-20
 */
public interface CompanyManageService {

    @POST("/api/company/getInfo")
    Call<BaseResponse<CompanyInfoResponse>> getInfo(@Body BaseRequest request);

}
