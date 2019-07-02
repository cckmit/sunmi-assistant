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

    public int mCompanyId = -1;
    public int mShopId = -1;
    public int period = DashboardContract.TIME_SPAN_INIT;
    public int mState = STATE_INIT;

    protected Pair<Long, Long> periodTimestamp;

    public BaseRefreshCard(Context context) {
        this.mContext = context;
    }

    public void registerIntoAdapter(BaseRecyclerAdapter<Object> adapter) {
        if (mModel == null) {
            mModel = createData();
        }
        if (mType == null) {
            mType = createType();
        }
        //noinspection unchecked
        adapter.register((Class<Model>) mModel.getClass(), mType);
    }

    public void setCompanyId(int companyId, int shopId) {
        if (this.mCompanyId == companyId) {
            return;
        }
        this.mCompanyId = companyId;
        this.mShopId = shopId;
        reload(mCompanyId, mShopId, period, periodTimestamp, mModel);
    }

    public void setShopId(int shopId) {
        if (this.mShopId == shopId || shopId < 0) {
            return;
        }
        this.mShopId = shopId;
        reload(mCompanyId, mShopId, period, periodTimestamp, mModel);
    }

    public void setPeriod(int period) {
        if (this.period == period) {
            return;
        }
        this.period = period;
        this.periodTimestamp = Utils.getPeriodTimestamp(this.period);
        onPeriodChange(period);
        updateView();
        reload(mCompanyId, mShopId, this.period, periodTimestamp, mModel);
    }

    private void updateView() {
        if (mHolder != null) {
            mType.onBindViewHolder(mHolder, mModel, mHolder.getAdapterPosition());
        }
    }

    protected abstract Model createData();

    protected abstract ItemType<Model, BaseViewHolder<Model>> createType();

    protected abstract void onPeriodChange(int period);

    public abstract void reload(int companyId, int shopId,
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
