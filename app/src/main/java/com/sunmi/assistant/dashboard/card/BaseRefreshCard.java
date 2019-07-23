package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.sunmi.assistant.dashboard.DashboardContract;

import java.text.DecimalFormat;

import sunmi.common.base.recycle.BaseRecyclerAdapter;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.log.LogCat;

/**
 * @author yinhui
 * @date 2019-07-22
 */
public abstract class BaseRefreshCard<Model extends BaseRefreshCard.BaseModel, Resp>
        extends ItemType<Model, BaseViewHolder<Model>> {

    protected final String TAG = this.getClass().getSimpleName();

    private static final int STATE_INIT = 0;
    private static final int STATE_LOADING = 1;
    private static final int STATE_SUCCESS = 10;
    private static final int STATE_FAILED = 11;

    protected static final String DATA_NONE = "--";
    protected static final String DATA_ZERO = "0";
    protected static final String FORMAT_FLOAT_NO_DECIMAL = "%.0f";
    protected static final String FORMAT_FLOAT_DOUBLE_DECIMAL = "%.2f";
    protected static final DecimalFormat FORMAT_MAX_DOUBLE_DECIMAL = new DecimalFormat("#.##");

    protected Context mContext;
    protected DashboardContract.Presenter mPresenter;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private CardCallback mCallback = new CardCallback();

    private BaseRecyclerAdapter<Object> mAdapter;
    private int mPosition;

    private Model mModel;

    private int mCompanyId;
    private int mShopId;
    private int mPeriod = DashboardContract.TIME_PERIOD_INIT;
    private int mState = STATE_INIT;

    protected BaseRefreshCard(Context context, DashboardContract.Presenter presenter,
                              int companyId, int shopId) {
        this.mContext = context;
        this.mPresenter = presenter;
        this.mCompanyId = companyId;
        this.mShopId = shopId;
        mModel = createModel(context);
    }

    public Model getModel() {
        return mModel;
    }

    public int getPeriod() {
        return mPeriod;
    }

    public void registerIntoAdapter(BaseRecyclerAdapter<Object> adapter, int position) {
        this.mAdapter = adapter;
        this.mPosition = position;
        //noinspection unchecked
        adapter.register((Class<Model>) mModel.getClass(), this);
    }

    public void setShopId(int shopId) {
        if (this.mShopId == shopId || shopId < 0) {
            return;
        }
        this.mShopId = shopId;
        mModel.skipLoad = false;
        onPreShopChange(mModel, shopId);
        if (!mModel.skipLoad) {
            load(mCompanyId, mShopId, mPeriod, mCallback);
        }
    }

    public void setPeriod(int period) {
        if (this.mPeriod == period || period == DashboardContract.TIME_PERIOD_INIT) {
            return;
        }
        this.mPeriod = period;
        this.mModel.period = period;
        mModel.skipLoad = false;
        onPrePeriodChange(mModel, period);
        if (!mModel.skipLoad) {
            load(mCompanyId, mShopId, mPeriod, mCallback);
        }
    }

    public void refresh() {
        mModel.skipLoad = false;
        load(mCompanyId, mShopId, mPeriod, mCallback);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        boolean isLoading = (mState == STATE_INIT || mState == STATE_LOADING);
        if (mState == STATE_FAILED) {
            mPresenter.showFailedTip();
        }
        if (model.isValid) {
            setupView(holder, model, position);
        } else {
            if (isLoading) {
                LogCat.d(TAG, "Skip set up view before first loading.");
                showLoading(holder, model, position);
            } else {
                LogCat.e(TAG, "Load data Failed.");
                showError(holder, model, position);
            }
        }
    }

    void updateView() {
        if (mAdapter != null) {
            mHandler.post(() -> mAdapter.notifyItemChanged(mPosition));
        }
    }

    /**
     * 创建ViewModel数据
     *
     * @param context 上下文
     * @return ViewModel
     */
    protected abstract Model createModel(Context context);

    /**
     * 切换店铺时，于加载数据前调用
     *
     * @param model  ViewModel
     * @param shopId 店铺ID
     */
    protected void onPreShopChange(Model model, int shopId) {
    }

    /**
     * 切换日期时，于加载数据前调用
     *
     * @param model  ViewModel
     * @param period 时间枚举
     */
    protected void onPrePeriodChange(Model model, int period) {
    }

    /**
     * 加载数据，如果有API请求，请使用callback回调；如果无需网络请求，直接使用callback.success();
     *
     * @param companyId 商户ID
     * @param shopId    店铺ID
     * @param period    时间枚举
     * @param callback  接口回调
     */
    protected abstract void load(int companyId, int shopId, int period, CardCallback callback);

    /**
     * 对ViewModel进行更新，一般用于接口回调成功时
     *
     * @param model    ViewModel
     * @param response 接口响应数据
     */
    protected abstract void setupModel(Model model, Resp response);

    /**
     * 对View进行更新，一般在ViewModel更新后被调用
     *
     * @param holder   ViewHolder
     * @param model    ViewModel
     * @param position 在列表中的位置index
     */
    protected abstract void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position);

    /**
     * 显示Loading状态，在第一次ViewModel更新前接口请求时调用
     *
     * @param holder   ViewHolder
     * @param model    ViewModel
     * @param position 在列表中的位置index
     */
    protected void showLoading(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
    }

    /**
     * 显示Error状态，在第一次数据加载失败时调用
     *
     * @param holder   ViewHolder
     * @param model    ViewModel
     * @param position 在列表中的位置index
     */
    protected void showError(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
    }

    protected class CardCallback extends RetrofitCallback<Resp> {

        public void onSuccess() {
            LogCat.d(TAG, "Dashboard card load data pass. ");
            mState = STATE_SUCCESS;
            mModel.isValid = true;
            setupModel(mModel, null);
            updateView();
        }

        @Override
        public void onSuccess(int code, String msg, Resp data) {
            LogCat.d(TAG, "Dashboard card load Success. " + msg);
            mState = STATE_SUCCESS;
            mModel.isValid = true;
            setupModel(mModel, data);
            updateView();
        }

        @Override
        public void onFail(int code, String msg, Resp data) {
            LogCat.e(TAG, "Dashboard card load Failed. " + msg);
            mState = STATE_FAILED;
            updateView();
        }
    }

    static abstract class BaseModel {
        boolean isValid = false;
        boolean skipLoad = false;
        int period = DashboardContract.TIME_PERIOD_INIT;
    }
}
