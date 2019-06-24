package com.sunmi.assistant.dashboard;

import android.util.Log;
import android.util.Pair;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.data.CompanyManagementRemote;
import com.sunmi.assistant.dashboard.data.ShopManagementRemote;
import com.sunmi.assistant.dashboard.data.response.CompanyInfoResponse;
import com.sunmi.assistant.dashboard.data.response.ShopListResponse;
import com.sunmi.assistant.dashboard.model.BarChartCard;
import com.sunmi.assistant.dashboard.model.BaseRefreshCard;
import com.sunmi.assistant.dashboard.model.DataCard;
import com.sunmi.assistant.dashboard.model.ListCard;
import com.sunmi.assistant.dashboard.model.PieChartCard;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;


public class DashboardPresenter extends BasePresenter<DashboardContract.View>
        implements DashboardContract.Presenter {

    private static final String TAG = "DashboardPresenter";

    private int mCompanyId;
    private String mCompanyName;
    private int mShopId;
    private String mShopName;

    private int mTimeSpan = DashboardContract.TIME_SPAN_INIT;
    private Pair<Long, Long> mTimeSpanPair;

    private List<BaseRefreshCard> mList;

    @Override
    public void loadConfig() {
        mCompanyId = SpUtils.getCompanyId();
        CompanyManagementRemote.get().getShopList(mCompanyId, new RetrofitCallback<CompanyInfoResponse>() {
            @Override
            public void onSuccess(int code, String msg, CompanyInfoResponse data) {
                mCompanyName = data.getCompany_name();
                mView.updateCompanyName(mCompanyName);
            }

            @Override
            public void onFail(int code, String msg, CompanyInfoResponse data) {
                Log.e(TAG, "Get company info FAILED. code=" + code + "; msg=" + msg);
            }
        });
        ShopManagementRemote.get().getShopList(mCompanyId, new RetrofitCallback<ShopListResponse>() {
            @Override
            public void onSuccess(int code, String msg, ShopListResponse data) {
                List<ShopListResponse.ShopInfo> shopList = data.getShop_list();
                if (shopList == null || shopList.size() == 0) {
                    Log.e(TAG, "Get shop list EMPTY!");
                } else {
                    mShopId = shopList.get(1).getShop_id();
                    mShopName = shopList.get(1).getShop_name();
                    mView.updateShopName(mShopName);
                    initList();
                }
            }

            @Override
            public void onFail(int code, String msg, ShopListResponse data) {
                Log.e(TAG, "Get shop list FAILED. code=" + code + "; msg=" + msg);
            }
        });
    }

    @Override
    public void timeSpanSwitchTo(int timeSpan) {
        Log.d(TAG, "Switch time span to: " + timeSpan);
        if (mTimeSpan == timeSpan) {
            Log.d(TAG, "Switch time span skip.");
            return;
        }
        this.mTimeSpan = timeSpan;
        this.mTimeSpanPair = Utils.calcTimeSpan(timeSpan);
        if (mList != null) {
            for (BaseRefreshCard card : mList) {
                card.setTimeSpan(timeSpan, mTimeSpanPair);
            }
        }
    }

    private void initList() {
        mList = new ArrayList<>(7);
        DataCard totalSalesAmount = new DataCard(mView.getContext().getString(
                R.string.dashboard_total_sales_amount), "%.2f",
                new DataRefreshHelper.TotalSalesAmountRefresh(mCompanyId, mShopId));
        DataCard customerPrice = new DataCard(mView.getContext().getString(
                R.string.dashboard_customer_price), "%.2f",
                new DataRefreshHelper.CustomerPriceRefresh(mCompanyId, mShopId));
        DataCard totalSalesVolume = new DataCard(mView.getContext().getString(
                R.string.dashboard_total_sales_volume), "%.0f",
                new DataRefreshHelper.TotalSalesVolumeRefresh(mCompanyId, mShopId));
        DataCard totalRefunds = new DataCard(mView.getContext().getString(
                R.string.dashboard_total_refunds), "%.0f",
                new DataRefreshHelper.TotalRefundsRefresh(mCompanyId, mShopId));
        BarChartCard timeDistribution = new BarChartCard(mView.getContext().getString(
                R.string.dashboard_time_distribution), DashboardContract.DATA_MODE_SALES,
                new DataRefreshHelper.TimeDistributionRefresh(mCompanyId, mShopId));
        PieChartCard purchaseRank = new PieChartCard(mView.getContext().getString(
                R.string.dashboard_purchase_rank), DashboardContract.DATA_MODE_SALES,
                new DataRefreshHelper.PurchaseTypeRankRefresh(mCompanyId, mShopId));
        ListCard quantityRank = new ListCard(mView.getContext().getString(
                R.string.dashboard_quantity_rank),
                new DataRefreshHelper.QuantityRankRefresh(mCompanyId, mShopId));

        totalSalesAmount.setTimeSpan(mTimeSpan, mTimeSpanPair);
        customerPrice.setTimeSpan(mTimeSpan, mTimeSpanPair);
        totalSalesVolume.setTimeSpan(mTimeSpan, mTimeSpanPair);
        totalRefunds.setTimeSpan(mTimeSpan, mTimeSpanPair);
        timeDistribution.setTimeSpan(mTimeSpan, mTimeSpanPair);
        purchaseRank.setTimeSpan(mTimeSpan, mTimeSpanPair);
        quantityRank.setTimeSpan(mTimeSpan, mTimeSpanPair);

//        mList.add(totalSalesAmount);
//        mList.add(customerPrice);
//        mList.add(totalSalesVolume);
//        mList.add(totalRefunds);
//        mList.add(timeDistribution);
//        mList.add(purchaseRank);
//        mList.add(quantityRank);
        mView.updateData(mList);
    }

}
