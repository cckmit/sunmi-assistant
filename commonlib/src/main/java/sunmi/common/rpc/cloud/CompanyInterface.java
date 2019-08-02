package sunmi.common.rpc.cloud;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import sunmi.common.model.CompanyInfoResp;
import sunmi.common.model.CompanyListResp;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * 商户管理Http接口
 *
 * @author yinhui
 * @since 2019-06-20
 */
public interface CompanyInterface {

    String companyPath = "api/company/";

    /**
     * 或取商户列表
     */
    @POST(companyPath + "getList")
    Call<BaseResponse<CompanyListResp>> getList(@Body BaseRequest request);

    /**
     * 查看商户
     */
    @POST(companyPath + "getInfo")
    Call<BaseResponse<CompanyInfoResp>> getInfo(@Body BaseRequest request);

    //创建商户
    @POST(companyPath + "create")
    Call<BaseResponse<CompanyInfoResp>> createCompany(@Body BaseRequest request);

}
