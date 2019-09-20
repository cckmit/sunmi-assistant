package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.sunmi.assistant.dashboard.Constants;
import com.sunmi.assistant.dashboard.DashboardContract;

import java.text.DecimalFormat;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseRecyclerAdapter;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.rpc.retrofit.BaseResponse;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.log.LogCat;

/**
 * @author yinhui
 * @date 2019-07-22
 */
public abstract class BaseRefreshItem<Model extends BaseRefreshItem.BaseModel, Resp>
        extends ItemType<Model, BaseViewHolder<Model>> {

    protected final String TAG = this.getClass().getSimpleName();

    private static final int STATE_INIT = 0;
    private static final int STATE_LOADING = 1;
    private static final int STATE_SUCCESS = 10;
    private static final int STATE_FAILED = 11;
    private static final int STATE_CANCEL = 12;

    protected static final CharSequence DATE_FORMAT = "yyyy-MM-dd";
    protected static final String DATA_NONE = "--";
    protected static final String DATA_ZERO = "0";
    protected static final String FORMAT_FLOAT_NO_DECIMAL = "%.0f";
    protected static final String FORMAT_FLOAT_DOUBLE_DECIMAL = "%.2f";
    protected static final String FORMAT_FLOAT_DOUBLE_PERCENT = "%.2f%%";
    protected static final DecimalFormat FORMAT_MAX_DOUBLE_DECIMAL = new DecimalFormat("#.##");

    protected Context mContext;
    protected DashboardContract.Presenter mPresenter;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private RequestCall<Resp> mCall = new RequestCall<>();

    private BaseRecyclerAdapter<Object> mAdapter;
    private int mPosition;

    private Model mModel;

    protected boolean isInit = false;
    private int mCompanyId;
    private int mShopId;
    private int mDataSource;
    private int mPeriod = Constants.TIME_PERIOD_TODAY;
    private int mState = STATE_INIT;

    protected BaseRefreshItem(Context context, DashboardContract.Presenter presenter) {
        this.mContext = context;
        this.mPresenter = presenter;
        mModel = createModel(context);
    }

    public void initConfig(int source) {
        this.mDataSource = source;
        this.mCompanyId = SpUtils.getCompanyId();
        this.mShopId = SpUtils.getShopId();
        this.isInit = true;
    }

    public boolean showTransactionData() {
        return (mDataSource & Constants.DATA_SOURCE_SAAS) != 0;
    }

    public boolean showConsumerData() {
        return (mDataSource & Constants.DATA_SOURCE_FS) != 0;
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

    public void setDataSource(int source) {
        if (this.mDataSource == source) {
            return;
        }
        this.mDataSource = source;
        this.mModel.skipLoad = false;
        this.mModel.isValid = false;
        requestLoad(true, true);
    }

    public void setCompanyId(int companyId, int shopId) {
        if (this.mCompanyId == companyId && this.mShopId == shopId) {
            return;
        }
        this.mCompanyId = companyId;
        this.mShopId = shopId;
        this.mModel.skipLoad = false;
        this.mModel.isValid = false;
        onPreShopChange(mModel, shopId);
        requestLoad(true, true);
    }

    public void setShopId(int shopId) {
        if (this.mShopId == shopId || shopId < 0) {
            return;
        }
        this.mShopId = shopId;
        this.mModel.skipLoad = false;
        this.mModel.isValid = false;
        onPreShopChange(mModel, shopId);
        requestLoad(true, true);
    }

    public void setPeriod(int period) {
        if (this.mPeriod == period) {
            return;
        }
        this.mPeriod = period;
        this.mModel.skipLoad = false;
        this.mModel.isValid = false;
        onPrePeriodChange(mModel, period);
        requestLoad(false, true);
    }

    public void refresh(boolean showLoading) {
        if (showLoading) {
            this.mModel.isValid = false;
        }
        requestLoad(true, showLoading);
    }

    public void cancelLoad() {
        if (mCall.isLoading()) {
            mCall.cancel();
        }
    }

    public void updateView() {
        if (mAdapter != null) {
            LogCat.d(TAG, "Update view.");
            mHandler.post(() -> mAdapter.notifyItemChanged(mPosition));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        if (!isInit) {
            return;
        }
        boolean isLoading = (mState == STATE_INIT || mState == STATE_LOADING);
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

    private void requestLoad(boolean forceLoad, boolean showLoading) {
        if (!isInit) {
            return;
        }
        if (forceLoad || !mModel.skipLoad) {
            if (mCall.isLoading() && mCall.isRequestSame(mCompanyId, mShopId, mPeriod)) {
                LogCat.d(TAG, "Data is loading, skip.");
                return;
            }
            LogCat.d(TAG, "Start to load data.");
            mState = STATE_LOADING;
            if (showLoading) {
                updateView();
            }
            Call<BaseResponse<Resp>> call = load(mCompanyId, mShopId, mPeriod,
                    new CardCallback(mPeriod));
            mCall.set(call, mCompanyId, mShopId, mPeriod);
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
     * @return API请求Call，用于取消
     */
    protected abstract Call<BaseResponse<Resp>> load(int companyId, int shopId, int period, CardCallback callback);

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

    private static class RequestCall<Resp> {
        @Nullable
        private Call<BaseResponse<Resp>> call;
        private int companyId;
        private int shopId;
        private int period;

        public void set(Call<BaseResponse<Resp>> call, int companyId, int shopId, int period) {
            if (this.call != null) {
                this.call.cancel();
            }
            this.call = call;
            this.companyId = companyId;
            this.shopId = shopId;
            this.period = period;
        }

        public void clear() {
            this.call = null;
            this.companyId = -1;
            this.shopId = -1;
            this.period = 0;
        }

        public Call<BaseResponse<Resp>> get() {
            return this.call;
        }

        public void cancel() {
            if (this.call != null) {
                this.call.cancel();
                clear();
            }
        }

        public boolean isRequestSame(int companyId, int shopId, int period) {
            return this.companyId == companyId && this.shopId == shopId && this.period == period;
        }

        public boolean isLoading() {
            return this.call != null && !this.call.isCanceled();
        }

        public boolean isCanceled() {
            return this.call != null && this.call.isCanceled();
        }

    }

    protected class CardCallback extends RetrofitCallback<Resp> {

        private int period;

        public CardCallback(int period) {
            this.period = period;
        }

        public void onSuccess() {
            LogCat.d(TAG, "Dashboard card load data pass. ");
            mState = STATE_SUCCESS;
            mCall.clear();
            mModel.isValid = true;
            mModel.period = this.period;
            setupModel(mModel, null);
            updateView();
        }

        @Override
        public void onSuccess(int code, String msg, Resp data) {
            LogCat.d(TAG, "Dashboard card load Success. " + msg);
            mState = STATE_SUCCESS;
            mCall.clear();
            mModel.isValid = true;
            mModel.period = this.period;
            setupModel(mModel, data);
            updateView();
        }

        @Override
        public void onFail(int code, String msg, Resp data) {
            LogCat.e(TAG, "Dashboard card load Failed. " + msg);
            if (mCall.isCanceled()) {
                mState = STATE_CANCEL;
            } else {
                mState = STATE_FAILED;
                mPresenter.showFailedTip();
            }
            mCall.clear();
            updateView();
        }
    }

    static abstract class BaseModel {
        boolean isValid = false;
        boolean skipLoad = false;
        int period = Constants.TIME_PERIOD_TODAY;
    }
}
