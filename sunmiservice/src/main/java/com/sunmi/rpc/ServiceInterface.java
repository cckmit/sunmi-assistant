package com.sunmi.rpc;

import com.sunmi.bean.BundleServiceMsg;
import com.sunmi.bean.ServiceDetailBean;
import com.sunmi.bean.SubscriptionListBean;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import sunmi.common.router.model.IpcListResp;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-10-22.
 */
public interface ServiceInterface {

    String path = "/api/service/";

    /**
     * 查看用户订阅服务记录列表
     *
     * @param request
     * @return
     */
    @POST(path + "subscription/getList")
    Call<BaseResponse<SubscriptionListBean>> getSubscriptionList(@Body BaseRequest request);

    /**
     * 查看用户订阅服务记录详情
     *
     * @param request
     * @return
     */
    @POST(path + "subscription/getInfoByDevice")
    Call<BaseResponse<ServiceDetailBean>> getServiceDetailByDevice(@Body BaseRequest request);


    /**
     * 查询当前门店捆绑服务信息
     * @param request
     * @return
     */
    @POST(path+"storage/getBundledList")
    Call<BaseResponse<BundleServiceMsg>> getBundledList(@Body BaseRequest request);
    
}
