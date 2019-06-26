package com.sunmi.assistant.dashboard;

import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.data.CompanyManagementRemote;
import com.sunmi.assistant.dashboard.data.ShopManagementRemote;
import com.sunmi.assistant.dashboard.data.response.CompanyInfoResponse;
import com.sunmi.assistant.dashboard.data.response.ShopInfoResponse;
import com.sunmi.assistant.dashboard.data.response.ShopListResponse;
import com.sunmi.assistant.dashboard.model.BarChartCard;
import com.sunmi.assistant.dashboard.model.BaseRefreshCard;
import com.sunmi.assistant.dashboard.model.DataCard;
import com.sunmi.assistant.dashboard.model.ListCard;
import com.sunmi.assistant.dashboard.model.PieChartCard;
import com.sunmi.assistant.dashboard.model.Tab;
import com.sunmi.assistant.dashboard.model.Title;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;


public class DashboardPresenter extends BasePresenter<DashboardContract.View>
        implements DashboardContract.Presenter {

    private static final String TAG = "DashboardPresenter";

    private static final int REFRESH_TIME_PERIOD = 12000;
    private static final HandlerThread sThread = new HandlerThread("RefreshTask");
    private static final Handler sHandler;

    static {
        sThread.start();
        sHandler = new Handler(sThread.getLooper());
    }

    private int mCompanyId;
    private String mCompanyName;
    private int mShopId;
    private String mShopName;

    private int mTimeSpan = DashboardContract.TIME_SPAN_INIT;
    private Pair<Long, Long> mTimeSpanPair;

    private List<BaseRefreshCard> mList;
    private Title mTitle;

    private RefreshTask mTask;

    @Override
    public void loadConfig() {
        initList();
        mCompanyId = SpUtils.getCompanyId();
        mCompanyName = SpUtils.getCompanyName();
        mShopId = SpUtils.getShopId();
        mShopName = SpUtils.getShopName();

        for (BaseRefreshCard card : mList) {
            card.setCompanyId(mCompanyId);
        }
        if (mShopId > 0) {
            for (BaseRefreshCard card : mList) {
                card.setShopId(mShopId);
            }
        }

        if (TextUtils.isEmpty(mCompanyName)) {
            CompanyManagementRemote.get().getCompanyInfo(mCompanyId, new RetrofitCallback<CompanyInfoResponse>() {
                @Override
                public void onSuccess(int code, String msg, CompanyInfoResponse data) {
                    mCompanyName = data.getCompany_name();
                    mTitle.setCompanyName(mCompanyName);
                    SpUtils.setCompanyName(mCompanyName);
                    mView.updateTitle();
                }

                @Override
                public void onFail(int code, String msg, CompanyInfoResponse data) {
                    Log.e(TAG, "Get company info FAILED. code=" + code + "; msg=" + msg);
                }
            });
        } else {
            mTitle.setCompanyName(mCompanyName);
            mView.updateTitle();
        }

        if (mShopId < 0) {
            ShopManagementRemote.get().getShopList(mCompanyId, new RetrofitCallback<ShopListResponse>() {
                @Override
                public void onSuccess(int code, String msg, ShopListResponse data) {
                    List<ShopListResponse.ShopInfo> shopList = data.getShop_list();
                    if (shopList == null || shopList.size() == 0) {
                        Log.e(TAG, "Get shop list EMPTY!");
                    } else {
                        ShopListResponse.ShopInfo shopInfo = shopList.get(0);
                        mShopId = shopInfo.getShop_id();
                        mShopName = shopInfo.getShop_name();
                        SpUtils.setShopId(mShopId);
                        SpUtils.setShopName(mShopName);
                        mTitle.setShopName(mShopName);
                        mView.updateTitle();
                        for (BaseRefreshCard card : mList) {
                            card.setShopId(mShopId);
                        }
                    }
                }

                @Override
                public void onFail(int code, String msg, ShopListResponse data) {
                    Log.e(TAG, "Get shop list FAILED. code=" + code + "; msg=" + msg);
                }
            });
        } else if (TextUtils.isEmpty(mShopName)) {
            ShopManagementRemote.get().getShopInfo(mShopId, new RetrofitCallback<ShopInfoResponse>() {
                @Override
                public void onSuccess(int code, String msg, ShopInfoResponse data) {
                    mShopName = data.getShop_name();
                    SpUtils.setShopName(mShopName);
                    mTitle.setShopName(mShopName);
                    mView.updateTitle();
                }

                @Override
                public void onFail(int code, String msg, ShopInfoResponse data) {
                    Log.e(TAG, "Get shop info FAILED. code=" + code + "; msg=" + msg);
                }
            });
        } else {
            mTitle.setShopName(mShopName);
            mView.updateTitle();
        }

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
        mList = new ArrayList<>(9);
        mTitle = new Title();
        Tab tab = new Tab();
        DataCard totalSalesAmount = new DataCard(mView.getContext().getString(
                R.string.dashboard_total_sales_amount), "%.2f",
                new DataRefreshHelper.TotalSalesAmountRefresh(mView.getContext()));
        DataCard customerPrice = new DataCard(mView.getContext().getString(
                R.string.dashboard_customer_price), "%.2f",
                new DataRefreshHelper.CustomerPriceRefresh(mView.getContext()));
        DataCard totalSalesVolume = new DataCard(mView.getContext().getString(
                R.string.dashboard_total_sales_volume), "%.0f",
                new DataRefreshHelper.TotalSalesVolumeRefresh(mView.getContext()));
        DataCard totalRefunds = new DataCard(mView.getContext().getString(
                R.string.dashboard_total_refunds), "%.0f",
                new DataRefreshHelper.TotalRefundsRefresh(mView.getContext()));
        BarChartCard timeDistribution = new BarChartCard(mView.getContext().getString(
                R.string.dashboard_time_distribution), DashboardContract.DATA_MODE_SALES,
                new DataRefreshHelper.TimeDistributionRefresh());
        PieChartCard purchaseRank = new PieChartCard(mView.getContext().getString(
                R.string.dashboard_purchase_rank), DashboardContract.DATA_MODE_SALES,
                new DataRefreshHelper.PurchaseTypeRankRefresh());
        ListCard quantityRank = new ListCard(mView.getContext().getString(
                R.string.dashboard_quantity_rank),
                new DataRefreshHelper.QuantityRankRefresh());

        totalSalesAmount.setTimeSpan(mTimeSpan, mTimeSpanPair);
        customerPrice.setTimeSpan(mTimeSpan, mTimeSpanPair);
        totalSalesVolume.setTimeSpan(mTimeSpan, mTimeSpanPair);
        totalRefunds.setTimeSpan(mTimeSpan, mTimeSpanPair);
        timeDistribution.setTimeSpan(mTimeSpan, mTimeSpanPair);
        purchaseRank.setTimeSpan(mTimeSpan, mTimeSpanPair);
        quantityRank.setTimeSpan(mTimeSpan, mTimeSpanPair);

        mList.add(mTitle);
        mList.add(tab);
        mList.add(totalSalesAmount);
        mList.add(customerPrice);
        mList.add(totalSalesVolume);
        mList.add(totalRefunds);
        mList.add(timeDistribution);
        mList.add(purchaseRank);
        mList.add(quantityRank);
        mView.updateData(mList);
        mTask = new RefreshTask(mList);
        sHandler.postDelayed(mTask, REFRESH_TIME_PERIOD);
    }

    @Override
    public void detachView() {
        super.detachView();
        sHandler.removeCallbacks(mTask);
    }

    private static class RefreshTask implements Runnable {

        private final List<BaseRefreshCard> mList;

        private RefreshTask(List<BaseRefreshCard> list) {
            this.mList = list;
        }

        @Override
        public void run() {
            for (BaseRefreshCard card : mList) {
                card.refresh();
            }
            sHandler.postDelayed(this, REFRESH_TIME_PERIOD);
        }
    }

}
