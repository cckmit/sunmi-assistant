package com.sunmi.assistant.data;

import com.sunmi.assistant.data.response.CompanyInfoResp;
import com.sunmi.assistant.data.response.CompanyListResp;

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

    String companyPath = "api/company/";

    /**
     * 或取商户列表
     */
    @POST(companyPath + "getList")
    Call<BaseResponse<CompanyListResp>> getList(@Body BaseRequest request);

    /**
     * 查看商户
     */
    @POST("/api/company/getInfo")
    Call<BaseResponse<CompanyInfoResp>> getInfo(@Body BaseRequest request);

}
