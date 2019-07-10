package com.sunmi.assistant.dashboard;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Pair;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.card.BaseRefreshCard;
import com.sunmi.assistant.dashboard.card.CustomerPriceCard;
import com.sunmi.assistant.dashboard.card.PayMethodCard;
import com.sunmi.assistant.dashboard.card.QuantityRankCard;
import com.sunmi.assistant.dashboard.card.TimeDistributionCard;
import com.sunmi.assistant.dashboard.card.TitleCard;
import com.sunmi.assistant.dashboard.card.TopTabCard;
import com.sunmi.assistant.dashboard.card.TotalCountCard;
import com.sunmi.assistant.dashboard.card.TotalRefundsCard;
import com.sunmi.assistant.dashboard.card.TotalSalesCard;
import com.sunmi.assistant.order.OrderListActivity_;
import com.sunmi.assistant.order.model.OrderInfo;
import com.sunmi.assistant.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.log.LogCat;


class DashboardPresenter extends BasePresenter<DashboardContract.View>
        implements DashboardContract.Presenter {

    private static final String TAG = "DashboardPresenter";

    private static final int REFRESH_TIME_PERIOD = 120_000;
    private static final HandlerThread sThread = new HandlerThread("RefreshTask");
    private static final Handler sHandler;

    static {
        sThread.start();
        sHandler = new Handler(sThread.getLooper());
    }

    private int mCompanyId;
    private int mShopId;

    private int mPeriod = DashboardContract.TIME_PERIOD_INIT;

    private List<BaseRefreshCard> mList;

    private RefreshTask mTask;

    @Override
    public void loadConfig() {
        mCompanyId = SpUtils.getCompanyId();
        mShopId = SpUtils.getShopId();
        initList(mCompanyId, mShopId, DashboardContract.TIME_PERIOD_INIT);
    }

    @Override
    public void switchPeriodTo(int period) {
        LogCat.d(TAG, "Switch time span to: " + period);
        if (mPeriod == period || period == DashboardContract.TIME_PERIOD_INIT) {
            LogCat.d(TAG, "Switch time span skip.");
            return;
        }
        this.mPeriod = period;
        if (mList != null) {
            for (BaseRefreshCard card : mList) {
                card.setPeriod(period);
            }
        }
    }

    @Override
    public void switchShopTo(int shopId) {
        mShopId = shopId;
        if (mList != null) {
            for (BaseRefreshCard card : mList) {
                card.setShopId(shopId);
            }
        }
    }

    @Override
    public void refresh() {
        if (mList != null) {
            for (BaseRefreshCard card : mList) {
                card.refresh();
            }
        }
    }

    @Override
    public void refresh(int position) {
        if (mList != null && mList.size() > position) {
            mList.get(position).refresh();
        }
    }

    private void initList(int companyId, int shopId, int period) {
        if (!isViewAttached()) {
            return;
        }
        Context context = mView.getContext();

        TopTabCard tab = new TopTabCard(context, period);
        TotalSalesCard totalSales = new TotalSalesCard(context, companyId, shopId, period);
        CustomerPriceCard customerPrice = new CustomerPriceCard(context, companyId, shopId, period);
        TotalCountCard totalCount = new TotalCountCard(context, companyId, shopId, period);
        TotalRefundsCard totalRefunds = new TotalRefundsCard(context, companyId, shopId, period);
        TimeDistributionCard timeDistribution = new TimeDistributionCard(context, companyId, shopId, period);
        PayMethodCard payMethod = new PayMethodCard(context, companyId, shopId, period);

        tab.addOnViewClickListener(R.id.tv_dashboard_today, (adapter, holder, v, model, position) -> {
            switchPeriodTo(DashboardContract.TIME_PERIOD_TODAY);
            adapter.notifyItemChanged(position);
            mView.updateStickyTab(DashboardContract.TIME_PERIOD_TODAY);
        });
        tab.addOnViewClickListener(R.id.tv_dashboard_week, (adapter, holder, v, model, position) -> {
            switchPeriodTo(DashboardContract.TIME_PERIOD_WEEK);
            adapter.notifyItemChanged(position);
            mView.updateStickyTab(DashboardContract.TIME_PERIOD_WEEK);
        });
        tab.addOnViewClickListener(R.id.tv_dashboard_month, (adapter, holder, v, model, position) -> {
            switchPeriodTo(DashboardContract.TIME_PERIOD_MONTH);
            adapter.notifyItemChanged(position);
            mView.updateStickyTab(DashboardContract.TIME_PERIOD_MONTH);
        });

        totalSales.setOnItemClickListener((adapter, holder, model, position) ->
                goToOrderList(OrderInfo.ORDER_TYPE_ALL));
        customerPrice.setOnItemClickListener((adapter, holder, model, position) ->
                goToOrderList(OrderInfo.ORDER_TYPE_NORMAL));
        totalCount.setOnItemClickListener((adapter, holder, model, position) ->
                goToOrderList(OrderInfo.ORDER_TYPE_NORMAL));
        totalRefunds.setOnItemClickListener((adapter, holder, model, position) ->
                goToOrderList(OrderInfo.ORDER_TYPE_REFUNDS));

        timeDistribution.addOnViewClickListener(R.id.tv_dashboard_radio_by_sales,
                (adapter, holder, v, model, position) -> {
                    model.setDataSource(DashboardContract.DATA_MODE_SALES);
                    adapter.notifyItemChanged(position);
                });
        timeDistribution.addOnViewClickListener(R.id.tv_dashboard_radio_by_order,
                (adapter, holder, v, model, position) -> {
                    model.setDataSource(DashboardContract.DATA_MODE_ORDER);
                    adapter.notifyItemChanged(position);
                });
        payMethod.addOnViewClickListener(R.id.tv_dashboard_radio_by_sales,
                (adapter, holder, v, model, position) -> {
                    model.setDataSource(DashboardContract.DATA_MODE_SALES);
                    adapter.notifyItemChanged(position);
                });
        payMethod.addOnViewClickListener(R.id.tv_dashboard_radio_by_order,
                (adapter, holder, v, model, position) -> {
                    model.setDataSource(DashboardContract.DATA_MODE_ORDER);
                    adapter.notifyItemChanged(position);
                });

        mList = new ArrayList<>(9);
        mList.add(new TitleCard(context, companyId, shopId, period));
        mList.add(tab);
        mList.add(totalSales);
        mList.add(customerPrice);
        mList.add(totalCount);
        mList.add(totalRefunds);
        mList.add(timeDistribution);
        mList.add(payMethod);
        mList.add(new QuantityRankCard(context, companyId, shopId, period));
        mView.initData(mList);
        mTask = new RefreshTask(mList);
        sHandler.postDelayed(mTask, REFRESH_TIME_PERIOD);
    }

    private void goToOrderList(int orderType) {
        Pair<Long, Long> periodTimestamp = Utils.getPeriodTimestamp(mPeriod);
        OrderListActivity_.intent(mView.getContext())
                .mTimeStart(periodTimestamp.first)
                .mTimeEnd(periodTimestamp.second)
                .mInitOrderType(orderType)
                .start();
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
