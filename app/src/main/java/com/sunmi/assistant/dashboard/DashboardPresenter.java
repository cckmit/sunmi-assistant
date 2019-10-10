package com.sunmi.assistant.dashboard;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.newcard.BaseRefreshCard;
import com.sunmi.assistant.dashboard.newcard.DataCard;
import com.sunmi.assistant.dashboard.newcard.DistributionChartCard;
import com.sunmi.assistant.dashboard.newcard.EmptyDataCard;
import com.sunmi.assistant.dashboard.newcard.EmptyGapCard;
import com.sunmi.assistant.dashboard.newcard.NoFsCard;
import com.sunmi.assistant.dashboard.newcard.NoOrderCard;
import com.sunmi.assistant.dashboard.newcard.PeriodTabCard;
import com.sunmi.assistant.dashboard.newcard.TrendChartCard;
import com.sunmi.ipc.model.IpcListResp;
import com.sunmi.ipc.rpc.IpcCloudApi;

import java.util.ArrayList;
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

    private Context mContext;

    private ShopItem mShop;
    private int mCompanyId;
    private int mDataSource = 0;


    private List<ShopItem> mShopList;
    private List<BaseRefreshCard> mList = new ArrayList<>();

    private RefreshTask mTask;

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public void init(Context context) {
        if (!isViewAttached()) {
            return;
        }
        mContext = context;
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
        for (BaseRefreshCard card : mList) {
            card.setPeriod(period, false);
        }
        if (isViewAttached()) {
            mView.updateTab(period);
        }
    }

    @Override
    public void refresh(boolean showLoading) {
        for (BaseRefreshCard card : mList) {
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
                    for (BaseRefreshCard card : mList) {
                        card.setShop(mCompanyId, mShop.getShopId(), true);
                    }
                    if (isViewAttached()) {
                        mView.hideLoadingDialog();
                    }
                    return;
                }
//                mDataSource = 3;
                // 根据数据来源，变更卡片
                initList(mDataSource);
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
        mList.clear();
        switch (mDataSource) {
            case 0x3:
                mList.add(PeriodTabCard.init(this, source));
                mList.add(DataCard.init(this, source));
                mList.add(TrendChartCard.init(this, source));
                mList.add(DistributionChartCard.init(this, source));
                mList.add(EmptyGapCard.init(this, source));
                break;
            case 0x2:
                mList.add(PeriodTabCard.init(this, source));
                mList.add(DataCard.init(this, source));
                mList.add(TrendChartCard.init(this, source));
                mList.add(NoFsCard.init(this, source));
                mList.add(EmptyGapCard.init(this, source));
                break;
            case 0x1:
                mList.add(PeriodTabCard.init(this, source));
                mList.add(DataCard.init(this, source));
                mList.add(TrendChartCard.init(this, source));
                mList.add(DistributionChartCard.init(this, source));
                mList.add(NoOrderCard.init(this, source));
                mList.add(EmptyGapCard.init(this, source));
                break;
            default:
                mList.add(EmptyDataCard.init(this, source));
                mList.add(NoFsCard.init(this, source));
                mList.add(NoOrderCard.init(this, source));
                mList.add(EmptyGapCard.init(this, source));
        }
    }

    @Override
    public void detachView() {
        super.detachView();
        mContext = null;
        WORK_HANDLER.removeCallbacks(mTask);
        for (BaseRefreshCard card : mList) {
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
