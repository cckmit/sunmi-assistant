package com.sunmi.assistant.dashboard;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.sunmi.assistant.utils.Utils;

import sunmi.common.base.recycle.BaseRecyclerAdapter;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.rpc.retrofit.RetrofitCallback;

public abstract class BaseRefreshCard<Model> {

    private static final String TAG = "BaseRefreshCard";

    public static final int STATE_INIT = 0;
    public static final int STATE_SUCCESS = 2;
    public static final int STATE_FAILED = 3;

    protected Context mContext;

    protected Model mModel;
    protected ItemType<Model, BaseViewHolder<Model>> mType;
    protected BaseViewHolder<Model> mHolder;

    public int mCompanyId;
    public int mShopId;
    public int period;
    public int mState = STATE_INIT;

    protected Pair<Long, Long> periodTimestamp;

    public BaseRefreshCard(Context context) {
        this(context, -1, -1);
    }

    public BaseRefreshCard(Context context, int companyId, int shopId) {
        this(context, companyId, shopId, DashboardContract.TIME_PERIOD_INIT);
    }

    public BaseRefreshCard(Context context, int companyId, int shopId, int period) {
        this.mContext = context;
        this.mCompanyId = companyId;
        this.mShopId = shopId;
        this.period = period;
        if (this.period != DashboardContract.TIME_PERIOD_INIT) {
            this.periodTimestamp = Utils.getPeriodTimestamp(this.period);
        }
        mModel = createData();
        mType = createType();
    }

    public void registerIntoAdapter(BaseRecyclerAdapter<Object> adapter) {
        //noinspection unchecked
        adapter.register((Class<Model>) mModel.getClass(), mType);
    }

    public void setCompanyId(int companyId, int shopId) {
        if (this.mCompanyId == companyId) {
            return;
        }
        this.mCompanyId = companyId;
        this.mShopId = shopId;
        refresh();
    }

    public void setShopId(int shopId) {
        if (this.mShopId == shopId || shopId < 0) {
            return;
        }
        this.mShopId = shopId;
        refresh();
    }

    public void setPeriod(int period) {
        if (this.period == period || period == DashboardContract.TIME_PERIOD_INIT) {
            return;
        }
        this.period = period;
        this.periodTimestamp = Utils.getPeriodTimestamp(this.period);
        onPeriodChange(period);
        updateView();
        refresh();
    }

    public void refresh() {
        if (mCompanyId > 0 && mShopId > 0 && period != DashboardContract.TIME_PERIOD_INIT) {
            load(mCompanyId, mShopId, period, periodTimestamp, mModel);
        }
    }

    private void updateView() {
        if (mHolder != null) {
            mType.onBindViewHolder(mHolder, mModel, mHolder.getAdapterPosition());
        }
    }

    protected abstract Model createData();

    protected abstract ItemType<Model, BaseViewHolder<Model>> createType();

    protected abstract void onPeriodChange(int period);

    protected abstract void load(int companyId, int shopId,
                                 int period, Pair<Long, Long> periodTimestamp, Model model);

    public abstract class CardCallback<Response> extends RetrofitCallback<Response> {

        public abstract void success(Response data);

        @Override
        public void onSuccess(int code, String msg, Response data) {
            success(data);
            updateView();
            mState = STATE_SUCCESS;
        }

        @Override
        public void onFail(int code, String msg, Response data) {
            Log.e(TAG, "HTTP request Failed. " + msg);
            mState = STATE_FAILED;
        }
    }

}
