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
import java.util.HashMap;
import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.ShopListResp;
import sunmi.common.notification.BaseNotification;
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
    private ShopItem mShop;
    private boolean mSaasExist;

    private int mDataSource = 0;

    private HashMap<Class<?>, BaseRefreshItem> mCardMap = new HashMap<>(8);
    private List<ShopItem> mShopList;
    private List<BaseRefreshItem> mList = new ArrayList<>();

    private RefreshTask mTask;

    @Override
    public void init() {
        if (!isViewAttached()) {
            return;
        }
        mCompanyId = SpUtils.getCompanyId();
        loadShopList();
        if (mTask == null) {
            mTask = new RefreshTask();
            WORK_HANDLER.postDelayed(mTask, REFRESH_TIME_PERIOD);
        }
    }

    @Override
    public void reload() {
        loadDataSource(mShop.isSaasExist());
    }

    @Override
    public boolean switchShopTo(ShopItem shop) {
        if (mShop.getShopId() != shop.getShopId()) {
            mShop.setChecked(false);
            mShop = shop;
            shop.setChecked(true);
            SpUtils.setShopId(shop.getShopId());
            SpUtils.setShopName(shop.getShopName());
            SpUtils.setSaasExist(shop.isSaasExist() ? 1 : 0);
            BaseNotification.newInstance().postNotificationName(CommonNotifications.shopSwitched);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void switchPeriodTo(int period) {
        for (BaseRefreshItem card : mList) {
            card.setPeriod(period);
        }
        if (isViewAttached()) {
            mView.updateTab(period);
        }
    }

    @Override
    public void refresh(boolean showLoading) {
        for (BaseRefreshItem card : mList) {
            card.refresh(showLoading);
        }
    }

    @Override
    public void refresh(int position, boolean showLoading) {
        if (mList.size() > position) {
            mList.get(position).refresh(showLoading);
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
                if (data == null) {
                    onFail(code, msg, null);
                    return;
                }
                List<ShopListResp.ShopInfo> shops = data.getShop_list();
                if (shops == null) {
                    onFail(code, msg, data);
                    return;
                }
                List<ShopItem> result = new ArrayList<>(shops.size());
                int shopId = SpUtils.getShopId();
                for (ShopListResp.ShopInfo shop : shops) {
                    if (shop.getShop_id() == shopId) {
                        ShopItem item = new ShopItem(shop.getShop_id(), shop.getShop_name(),
                                shop.getSaas_exist() == 1);
                        item.setChecked(true);
                        mShop = item;
                        result.add(0, item);
                    } else {
                        result.add(new ShopItem(shop.getShop_id(), shop.getShop_name(),
                                shop.getSaas_exist() == 1));
                    }
                }
                mShopList = result;
                if (isViewAttached()) {
                    mView.setShopList(result);
                }
                loadDataSource(mShop.isSaasExist());
            }

            @Override
            public void onFail(int code, String msg, ShopListResp data) {
                LogCat.e(TAG, "Load shop list Failed. " + code + ":" + msg);
                if (isViewAttached()) {
                    mView.loadDataFailed();
                }
            }
        });
    }

    private void loadDataSource(boolean saasExist) {
        int old = mDataSource;
        if (saasExist) {
            mDataSource |= Constants.DATA_SOURCE_SAAS;
        } else {
            mDataSource &= ~Constants.DATA_SOURCE_SAAS;
        }
        IpcCloudApi.getDetailList(mCompanyId, mShop.getShopId(), new RetrofitCallback<IpcListResp>() {
            @Override
            public void onSuccess(int code, String msg, IpcListResp data) {
                if (data == null) {
                    onFail(code, msg, null);
                    return;
                }
                if (data.getFs_list() != null && data.getFs_list().size() > 0) {
                    mDataSource |= Constants.DATA_SOURCE_FS;
                } else {
                    mDataSource &= ~Constants.DATA_SOURCE_FS;
                }
                int changed = mDataSource ^ old;
                // 如果数据来源无变化（是否有FS以及是否有订单数据），那么直接更新卡片商户和门店
                if (changed == 0 && !mList.isEmpty()) {
                    for (BaseRefreshItem card : mList) {
                        card.setCompanyId(mCompanyId, mShop.getShopId());
                    }
                    refresh(true);
                    if (isViewAttached()) {
                        mView.hideLoadingDialog();
                    }
                    return;
                }
                // 初始化列表
                mList.clear();
//                mDataSource = 3;
                // 根据数据来源，变更卡片
                switch (mDataSource) {
                    case 0x3:
                        initList(mDataSource);
                        break;
                    case 0x1:
                        initNoFsList(mDataSource);
                        break;
                    case 0x2:
                        initNoOrderList(mDataSource);
                        break;
                    default:
                        initNoDataList(mDataSource);
                }
                if (isViewAttached()) {
                    mView.setCards(mList, mDataSource);
                    switchPeriodTo(Constants.TIME_PERIOD_TODAY);
                    refresh(true);
                }
            }

            @Override
            public void onFail(int code, String msg, IpcListResp data) {
                LogCat.e(TAG, "Load data source Failed. " + code + ":" + msg);
                mView.loadDataFailed();
            }
        });
    }

    private void initList(int source) {
        if (!isViewAttached()) {
            return;
        }
        Context context = mView.getContext();
        BaseRefreshItem card;

        card = mCardMap.get(PeriodTabCard.class);
        if (card == null) {
            card = new PeriodTabCard(context, this);
            mCardMap.put(PeriodTabCard.class, card);
        }
        card.initConfig(source);
        mList.add(card);

        card = mCardMap.get(DataCard.class);
        if (card == null) {
            card = new DataCard(context, this);
            mCardMap.put(DataCard.class, card);
        }
        card.initConfig(source);
        mList.add(card);

        card = mCardMap.get(TrendChartCard.class);
        if (card == null) {
            card = new TrendChartCard(context, this);
            mCardMap.put(TrendChartCard.class, card);
        }
        card.initConfig(source);
        mList.add(card);

        card = mCardMap.get(DistributionChartCard.class);
        if (card == null) {
            card = new DistributionChartCard(context, this);
            mCardMap.put(DistributionChartCard.class, card);
        }
        card.initConfig(source);
        mList.add(card);

        card = mCardMap.get(EmptyGapCard.class);
        if (card == null) {
            card = new EmptyGapCard();
            mCardMap.put(EmptyGapCard.class, card);
        }
        ((EmptyGapCard) card).setHeightAndColor((int) context.getResources().getDimension(R.dimen.dp_24),
                ContextCompat.getColor(context, R.color.color_F5F7FA));
        mList.add(card);
    }

    private void initNoOrderList(int source) {
        if (!isViewAttached()) {
            return;
        }
        Context context = mView.getContext();
        BaseRefreshItem card;

        card = mCardMap.get(PeriodTabCard.class);
        if (card == null) {
            card = new PeriodTabCard(context, this);
            mCardMap.put(PeriodTabCard.class, card);
        }
        card.initConfig(source);
        mList.add(card);

        card = mCardMap.get(DataCard.class);
        if (card == null) {
            card = new DataCard(context, this);
            mCardMap.put(DataCard.class, card);
        }
        card.initConfig(source);
        mList.add(card);

        card = mCardMap.get(TrendChartCard.class);
        if (card == null) {
            card = new TrendChartCard(context, this);
            mCardMap.put(TrendChartCard.class, card);
        }
        card.initConfig(source);
        mList.add(card);

        card = mCardMap.get(DistributionChartCard.class);
        if (card == null) {
            card = new DistributionChartCard(context, this);
            mCardMap.put(DistributionChartCard.class, card);
        }
        card.initConfig(source);
        mList.add(card);

        card = mCardMap.get(NoOrderCard.class);
        if (card == null) {
            card = new NoOrderCard(context, this);
            mCardMap.put(NoOrderCard.class, card);
        }
        ((NoOrderCard) card).setIsAllEmpty(false);
        mList.add(card);

        card = mCardMap.get(EmptyGapCard.class);
        if (card == null) {
            card = new EmptyGapCard();
            mCardMap.put(EmptyGapCard.class, card);
        }
        ((EmptyGapCard) card).setHeightAndColor((int) context.getResources().getDimension(R.dimen.dp_32),
                ContextCompat.getColor(context, R.color.color_F5F7FA));
        mList.add(card);
    }

    private void initNoFsList(int source) {
        if (!isViewAttached()) {
            return;
        }
        Context context = mView.getContext();
        BaseRefreshItem card;

        card = mCardMap.get(PeriodTabCard.class);
        if (card == null) {
            card = new PeriodTabCard(context, this);
            mCardMap.put(PeriodTabCard.class, card);
        }
        card.initConfig(source);
        mList.add(card);

        card = mCardMap.get(DataCard.class);
        if (card == null) {
            card = new DataCard(context, this);
            mCardMap.put(DataCard.class, card);
        }
        card.initConfig(source);
        mList.add(card);

        card = mCardMap.get(TrendChartCard.class);
        if (card == null) {
            card = new TrendChartCard(context, this);
            mCardMap.put(TrendChartCard.class, card);
        }
        card.initConfig(source);
        mList.add(card);

        card = mCardMap.get(NoFsCard.class);
        if (card == null) {
            card = new NoFsCard(context, this);
            mCardMap.put(NoFsCard.class, card);
        }
        ((NoFsCard) card).setIsAllEmpty(false);
        mList.add(card);

        card = mCardMap.get(EmptyGapCard.class);
        if (card == null) {
            card = new EmptyGapCard();
            mCardMap.put(EmptyGapCard.class, card);
        }
        ((EmptyGapCard) card).setHeightAndColor((int) context.getResources().getDimension(R.dimen.dp_32),
                ContextCompat.getColor(context, R.color.color_F5F7FA));
        mList.add(card);
    }

    private void initNoDataList(int source) {
        if (!isViewAttached()) {
            return;
        }
        Context context = mView.getContext();
        BaseRefreshItem card;

        card = mCardMap.get(EmptyDataCard.class);
        if (card == null) {
            card = new EmptyDataCard();
            mCardMap.put(EmptyDataCard.class, card);
        }
        mList.add(card);

        card = mCardMap.get(NoFsCard.class);
        if (card == null) {
            card = new NoFsCard(context, this);
            mCardMap.put(NoFsCard.class, card);
        }
        ((NoFsCard) card).setIsAllEmpty(true);
        mList.add(card);

        card = mCardMap.get(NoOrderCard.class);
        if (card == null) {
            card = new NoOrderCard(context, this);
            mCardMap.put(NoOrderCard.class, card);
        }
        ((NoOrderCard) card).setIsAllEmpty(true);
        mList.add(card);

        card = mCardMap.get(EmptyGapCard.class);
        if (card == null) {
            card = new EmptyGapCard();
            mCardMap.put(EmptyGapCard.class, card);
        }
        ((EmptyGapCard) card).setHeightAndColor((int) context.getResources().getDimension(R.dimen.dp_32), 0xFFFFFFFF);
        mList.add(card);
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
            refresh(false);
            WORK_HANDLER.postDelayed(this, REFRESH_TIME_PERIOD);
        }
    }

}
