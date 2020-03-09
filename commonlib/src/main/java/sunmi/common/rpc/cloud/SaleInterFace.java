package sunmi.common.rpc.cloud;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import sunmi.common.model.CustomerShopDataResp;
import sunmi.common.model.SaleDataResp;
import sunmi.common.model.TotalRealTimeShopSalesResp;
import sunmi.common.model.TotalRealtimeSalesTrendResp;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.BaseResponse;

public interface SaleInterFace {
    String companyPath = "api/order/company/statistic/";

    /**
     * 获取总部实时经营概况
     * @param request
     * @return
     */
    @POST(companyPath+"getLatest")
    Call<BaseResponse<SaleDataResp>> getTotalSaleData(@Body BaseRequest request);

    /**
     * 获取总部实时销售额趋势
     * @param request
     * @return
     */
    @POST(companyPath +"trend/getLatest")
    Call<BaseResponse<TotalRealtimeSalesTrendResp>> getTotalSaleRealtimeTrend(@Body BaseRequest request);

    /**
     * 获取总部实时分店销售额列表
     * @param request
     * @return
     */
    @POST(companyPath +"branch/getLatest")
    Call<BaseResponse<TotalRealTimeShopSalesResp>> getTotalSaleShopData(@Body BaseRequest request);

    /**
     * 获取总部T+1销售概况
     * @param request
     * @return
     */
    @POST(companyPath +"history/getByDate")
    Call<BaseResponse<SaleDataResp>> getTotalHistorySaleData(@Body BaseRequest request);
}
