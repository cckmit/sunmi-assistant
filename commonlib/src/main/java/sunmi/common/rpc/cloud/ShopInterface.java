package sunmi.common.rpc.cloud;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import sunmi.common.model.AuthStoreInfo;
import sunmi.common.model.CreateShopInfo;
import sunmi.common.model.PlatformInfo;
import sunmi.common.model.ShopAuthorizeInfoResp;
import sunmi.common.model.ShopCategoryResp;
import sunmi.common.model.ShopInfo;
import sunmi.common.model.ShopListResp;
import sunmi.common.model.ShopRegionResp;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * 门店管理Http接口
 *
 * @author yinhui
 * @since 2019-06-20
 */
public interface ShopInterface {

    String shopPath = "/api/shop/";
    String saasPath = "/api/shop/saas/";

    /**
     * 根据companyId获取所有的门店列表
     */
    @POST(shopPath + "getList")
    Call<BaseResponse<ShopListResp>> getList(@Body BaseRequest request);

    /**
     * 根据shopId获取门店信息
     */
    @POST(shopPath + "getInfo")
    Call<BaseResponse<ShopInfo>> getInfo(@Body BaseRequest request);

    //创建门店
    @POST(shopPath + "create")
    Call<BaseResponse<CreateShopInfo>> createShop(@Body BaseRequest request);

    //编辑门店
    @POST(shopPath + "update")
    Call<BaseResponse<Object>> editShop(@Body BaseRequest request);

    @POST(shopPath + "getShopTypeList")
    Call<BaseResponse<ShopCategoryResp>> getShopCategory(@Body BaseRequest request);

    @POST(shopPath + "getRegionList")
    Call<BaseResponse<ShopRegionResp>> getShopRegion(@Body BaseRequest request);


    //saas信息
    @POST(saasPath + "getUserInfo")
    Call<BaseResponse<AuthStoreInfo>> getSaasUserInfo(@Body BaseRequest request);

    //米商引擎所支持的Saas平台信息l
    @POST(saasPath + "getList")
    Call<BaseResponse<PlatformInfo>> getPlatformList(@Body BaseRequest request);

    //米商引擎手机发送验证码
    @POST(saasPath + "sendVerifyCode")
    Call<BaseResponse<Object>> sendSaasVerifyCode(@Body BaseRequest request);

    //米商引擎手机校验验证码
    @POST(saasPath + "confirmVerifyCode")
    Call<BaseResponse<Object>> confirmSaasVerifyCode(@Body BaseRequest request);

    //用户授权获取Saas平台数据
    @POST(saasPath + "authorize")
    Call<BaseResponse<Object>> authorizeSaas(@Body BaseRequest request);

    /**
     * 获取门店Saas对接信息，包括授权状态，授权时间，数据导入状态
     */
    @POST(saasPath + "getAuthorizeInfo")
    Call<BaseResponse<ShopAuthorizeInfoResp>> getAuthorizeInfo(@Body BaseRequest request);

    /**
     * 门店导入Saas历史数据
     */
    @POST(saasPath + "importPaymentHistory")
    Call<BaseResponse<Object>> importSaas(@Body BaseRequest request);

}
