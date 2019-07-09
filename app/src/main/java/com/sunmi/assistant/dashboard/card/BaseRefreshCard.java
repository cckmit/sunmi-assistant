package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.support.annotation.IdRes;
import android.util.Log;

import com.sunmi.assistant.dashboard.DashboardContract;

import sunmi.common.base.recycle.BaseRecyclerAdapter;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.base.recycle.listener.OnItemClickListener;
import sunmi.common.base.recycle.listener.OnItemLongClickListener;
import sunmi.common.base.recycle.listener.OnViewClickListener;
import sunmi.common.base.recycle.listener.OnViewLongClickListener;
import sunmi.common.rpc.retrofit.RetrofitCallback;

public abstract class BaseRefreshCard<Model> {

    private static final String TAG = "BaseRefreshCard";

    static final int STATE_INIT = 0;
    static final int STATE_LOADING = 1;
    static final int STATE_SUCCESS = 2;
    static final int STATE_FAILED = 3;

    private Context mContext;

    private Model mModel;
    private ItemType<Model, BaseViewHolder<Model>> mType;
    private BaseViewHolder<Model> mHolder;

    private int mCompanyId;
    private int mShopId;
    private int mPeriod;
    private int mState = STATE_INIT;

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
        this.mPeriod = period;
        mModel = createData();
        mType = createType();
    }

    Context getContext() {
        return mContext;
    }

    int getState() {
        return mState;
    }

    void setState(int state) {
        mState = state;
    }

    public int getPeriod() {
        return mPeriod;
    }

    public Model getModel() {
        return mModel;
    }

    public ItemType<Model, BaseViewHolder<Model>> getType() {
        return mType;
    }

    public void registerIntoAdapter(BaseRecyclerAdapter<Object> adapter) {
        //noinspection unchecked
        adapter.register((Class<Model>) mModel.getClass(), mType);
    }

    void setHolder(BaseViewHolder<Model> holder) {
        mHolder = holder;
    }

    public void clearHolder() {
        mHolder = null;
    }

    public void setCompanyId(int companyId, int shopId) {
        if (this.mCompanyId == companyId) {
            return;
        }
        this.mCompanyId = companyId;
        this.mShopId = shopId;
        onCompanyChange(mModel, companyId, shopId);
        if (mCompanyId > 0 && mShopId > 0 && mPeriod != DashboardContract.TIME_PERIOD_INIT) {
            load(mCompanyId, mShopId, mPeriod, mModel);
        }
    }

    public void setShopId(int shopId) {
        if (this.mShopId == shopId || shopId < 0) {
            return;
        }
        this.mShopId = shopId;
        onShopChange(mModel, shopId);
        if (mCompanyId > 0 && mShopId > 0 && mPeriod != DashboardContract.TIME_PERIOD_INIT) {
            load(mCompanyId, mShopId, mPeriod, mModel);
        }
    }

    public void setPeriod(int period) {
        if (this.mPeriod == period || period == DashboardContract.TIME_PERIOD_INIT) {
            return;
        }
        this.mPeriod = period;
        onPeriodChange(mModel, period);
        if (mCompanyId > 0 && mShopId > 0 && mPeriod != DashboardContract.TIME_PERIOD_INIT) {
            load(mCompanyId, mShopId, mPeriod, mModel);
        }
    }

    public void refresh() {
        onRefresh(mModel, mPeriod);
        if (mCompanyId > 0 && mShopId > 0 && mPeriod != DashboardContract.TIME_PERIOD_INIT) {
            load(mCompanyId, mShopId, mPeriod, mModel);
        }
    }

    void updateView() {
        if (mHolder != null) {
            mType.onBindViewHolder(mHolder, mModel, mHolder.getAdapterPosition());
        }
    }

    protected abstract Model createData();

    protected abstract ItemType<Model, BaseViewHolder<Model>> createType();

    protected void onCompanyChange(Model model, int companyId, int shopId) {
    }

    protected void onShopChange(Model model, int shopId) {
    }

    protected void onPeriodChange(Model model, int period) {
    }

    protected void onRefresh(Model model, int period) {
    }

    protected abstract void load(int companyId, int shopId,
                                 int period, Model model);

    public void setOnItemClickListener(OnItemClickListener<Model> l) {
        if (mType != null) {
            mType.setOnItemClickListener(l);
        }
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<Model> l) {
        if (mType != null) {
            mType.setOnItemLongClickListener(l);
        }
    }

    public void addOnViewClickListener(@IdRes int id, OnViewClickListener<Model> l) {
        if (mType != null && l != null) {
            mType.addOnViewClickListener(id, l);
        }
    }

    public void addOnViewLongClickListener(@IdRes int id, OnViewLongClickListener<Model> l) {
        if (mType != null && l != null) {
            mType.addOnViewLongClickListener(id, l);
        }
    }

    public abstract class CardCallback<Response> extends RetrofitCallback<Response> {

        public abstract void success(Response data);

        @Override
        public void onSuccess(int code, String msg, Response data) {
            mState = STATE_SUCCESS;
            success(data);
            updateView();
        }

        @Override
        public void onFail(int code, String msg, Response data) {
            mState = STATE_FAILED;
            Log.e(TAG, "HTTP request Failed. " + msg);
        }
    }

}
