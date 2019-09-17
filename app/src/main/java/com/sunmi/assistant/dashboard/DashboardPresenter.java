package com.sunmi.assistant.dashboard;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.content.ContextCompat;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.card.BaseRefreshItem;
import com.sunmi.assistant.dashboard.card.DataCard;
import com.sunmi.assistant.dashboard.card.DistributionChartCard;
import com.sunmi.assistant.dashboard.card.EmptyDataCard;
import com.sunmi.assistant.dashboard.card.EmptyGapCard;
import com.sunmi.assistant.dashboard.card.NoFsCard;
import com.sunmi.assistant.dashboard.card.NoOrderCard;
import com.sunmi.assistant.dashboard.card.PeriodTabCard;
import com.sunmi.assistant.dashboard.card.TrendChartCard;
import com.sunmi.ipc.model.IpcListResp;
import com.sunmi.ipc.rpc.IpcCloudApi;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.model.FilterItem;
import sunmi.common.model.ShopListResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.log.LogCat;


class DashboardPresenter extends BasePresenter<DashboardContract.View>
        implements DashboardContract.Presenter {

    private static final String TAG = DashboardPresenter.class.getSimpleName();

    private static final int REFRESH_TIME_PERIOD = 120_000;
    private static final HandlerThread WORK_THREAD = new HandlerThread("RefreshTask");
    private static final Handler WORK_HANDLER;

    static {
        WORK_THREAD.start();
        WORK_HANDLER = new Handler(WORK_THREAD.getLooper());
    }

    private int mCompanyId;
    private int mShopId;

    private int mDataSource = 0;
    private int mPeriod = Constants.TIME_PERIOD_INIT;

    private List<BaseRefreshItem> mList;

    private RefreshTask mTask;

    @Override
    public void init() {
        mCompanyId = SpUtils.getCompanyId();
        mShopId = SpUtils.getShopId();
        loadShopList();
        loadDataSource();
        mTask = new RefreshTask();
        WORK_HANDLER.postDelayed(mTask, REFRESH_TIME_PERIOD);
    }

    @Override
    public void switchShopTo(int companyId, int shopId) {
        mCompanyId = companyId;
        mShopId = shopId;
        loadDataSource();
    }

    @Override
    public void switchPeriodTo(int period) {
        LogCat.d(TAG, "All card switch period to: " + period + "; Current period is " + mPeriod);
        if (mPeriod == period || period == Constants.TIME_PERIOD_INIT) {
            LogCat.d(TAG, "Switch period skip.");
            return;
        }
        this.mPeriod = period;
        if (mList != null) {
            for (BaseRefreshItem card : mList) {
                card.setPeriod(period);
            }
        }
        if (isViewAttached()) {
            mView.updateTab(period);
        }
    }

    @Override
    public void refresh() {
        if (mList != null) {
            for (BaseRefreshItem card : mList) {
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

    @Override
    public void showFailedTip() {
        if (isViewAttached()) {
            mView.shortTip(R.string.toast_network_Exception);
        }
    }

    private void loadShopList() {
        SunmiStoreApi.getInstance().getShopList(mCompanyId, new RetrofitCallback<ShopListResp>() {
            @Override
            public void onSuccess(int code, String msg, ShopListResp data) {
                List<ShopListResp.ShopInfo> shops = data.getShop_list();
                List<FilterItem> result = new ArrayList<>(shops.size());
                for (ShopListResp.ShopInfo shop : shops) {
                    if (shop.getShop_id() == mShopId) {
                        FilterItem item = new FilterItem(shop.getShop_id(), shop.getShop_name());
                        item.setChecked(true);
                        result.add(0, item);
                    } else {
                        result.add(new FilterItem(shop.getShop_id(), shop.getShop_name()));
                    }
                }
                if (isViewAttached()) {
                    mView.setShopList(result);
                }
            }

            @Override
            public void onFail(int code, String msg, ShopListResp data) {
                // TODO
            }
        });
    }

    private void loadDataSource() {
        int old = mDataSource;
        if (SpUtils.getSaasExist() == 1) {
            mDataSource |= Constants.DATA_SOURCE_SAAS;
        } else {
            mDataSource &= ~Constants.DATA_SOURCE_SAAS;
        }
        IpcCloudApi.getDetailList(mCompanyId, mShopId, new RetrofitCallback<IpcListResp>() {
            @Override
            public void onSuccess(int code, String msg, IpcListResp data) {
                if (data.getFs_list() != null && data.getFs_list().size() > 0) {
                    mDataSource |= Constants.DATA_SOURCE_FS;
                } else {
                    mDataSource &= ~Constants.DATA_SOURCE_FS;
                }
                int changed = mDataSource ^ old;
                // 如果数据来源无变化（是否有FS以及是否有订单数据），那么直接更新卡片商户和门店
                if (changed == 0 && mList != null && !mList.isEmpty()) {
                    for (BaseRefreshItem card : mList) {
                        card.setCompanyId(mCompanyId, mShopId);
                    }
                    return;
                }
                // 初始化列表
                if (mList == null) {
                    mList = new ArrayList<>(6);
                } else {
                    mList.clear();
                }
                // 根据数据来源，变更卡片
                switch (mDataSource) {
                    case 0x3:
                        initList(mCompanyId, mShopId);
                        break;
                    case 0x1:
                        initNoFsList(mCompanyId, mShopId);
                        break;
                    case 0x2:
                        initNoOrderList(mCompanyId, mShopId);
                        break;
                    default:
                        initNoDataList(mCompanyId, mShopId);
                }
            }

            @Override
            public void onFail(int code, String msg, IpcListResp data) {
                // TODO
            }
        });

    }

    private void initList(int companyId, int shopId) {
        if (!isViewAttached()) {
            return;
        }
        Context context = mView.getContext();
        mList.add(new PeriodTabCard(context, this, mDataSource));
        mList.add(new DataCard(context, this, mDataSource));
        mList.add(new TrendChartCard(context, this, mDataSource));
        mList.add(new DistributionChartCard(context, this, mDataSource));
        mList.add(new EmptyGapCard(ContextCompat.getColor(context, R.color.color_F5F7FA),
                (int) context.getResources().getDimension(R.dimen.dp_24)));
        mView.setCards(mList);
        switchPeriodTo(Constants.TIME_PERIOD_TODAY);
    }

    private void initNoOrderList(int companyId, int shopId) {
        if (!isViewAttached()) {
            return;
        }
        Context context = mView.getContext();
        mList.add(new PeriodTabCard(context, this, mDataSource));
        mList.add(new DataCard(context, this, mDataSource));
        mList.add(new TrendChartCard(context, this, mDataSource));
        mList.add(new DistributionChartCard(context, this, mDataSource));
        mList.add(new NoOrderCard(context, this, false));
        mList.add(new EmptyGapCard(ContextCompat.getColor(context, R.color.color_F5F7FA),
                (int) context.getResources().getDimension(R.dimen.dp_32)));
        mView.setCards(mList);
        switchPeriodTo(Constants.TIME_PERIOD_TODAY);
    }

    private void initNoFsList(int companyId, int shopId) {
        if (!isViewAttached()) {
            return;
        }
        Context context = mView.getContext();
        mList.add(new PeriodTabCard(context, this, mDataSource));
        mList.add(new DataCard(context, this, mDataSource));
        mList.add(new TrendChartCard(context, this, mDataSource));
        mList.add(new NoFsCard(context, this, false));
        mList.add(new EmptyGapCard(ContextCompat.getColor(context, R.color.color_F5F7FA),
                (int) context.getResources().getDimension(R.dimen.dp_32)));
        mView.setCards(mList);
        switchPeriodTo(Constants.TIME_PERIOD_TODAY);
    }

    private void initNoDataList(int companyId, int shopId) {
        if (!isViewAttached()) {
            return;
        }
        Context context = mView.getContext();
        mList.add(new EmptyDataCard(context, this));
        mList.add(new NoFsCard(context, this, true));
        mList.add(new NoOrderCard(context, this, true));
        mList.add(new EmptyGapCard(0xFFFFFFFF, (int) context.getResources().getDimension(R.dimen.dp_32)));
        mView.setCards(mList);
    }

    @Override
    public void detachView() {
        super.detachView();
        WORK_HANDLER.removeCallbacks(mTask);
        for (BaseRefreshItem card : mList) {
            card.cancelLoad();
        }
    }

    private class RefreshTask implements Runnable {

        @Override
        public void run() {
            refresh();
            WORK_HANDLER.postDelayed(this, REFRESH_TIME_PERIOD);
        }
    }

}
