package sunmi.common.rpc.cloud;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import sunmi.common.model.CreateStoreInfo;
import sunmi.common.model.ShopInfoResp;
import sunmi.common.model.ShopListResp;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * 门店管理Http接口
 *
 * @author yinhui
 * @since 2019-06-20
 */
public interface ShopInterface {
    String shopPath = "api/shop/";

    /**
     * 根据companyId获取所有的门店列表
     */
    @POST(shopPath + "getList")
    Call<BaseResponse<ShopListResp>> getList(@Body BaseRequest request);

    /**
     * 根据shopId获取门店信息
     */
    @POST(shopPath + "getInfo")
    Call<BaseResponse<ShopInfoResp>> getInfo(@Body BaseRequest request);

    //创建门店
    @POST(shopPath + "create")
    Call<BaseResponse<CreateStoreInfo>> createShop(@Body BaseRequest request);

    //编辑门店
    @POST(shopPath + "update")
    Call<BaseResponse<Object>> editShop(@Body BaseRequest request);

}
