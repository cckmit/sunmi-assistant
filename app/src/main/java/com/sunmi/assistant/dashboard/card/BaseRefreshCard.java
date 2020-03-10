package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.sunmi.assistant.dashboard.data.DashboardCondition;

import java.text.DecimalFormat;
import java.util.Objects;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseRecyclerAdapter;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.model.Interval;
import sunmi.common.rpc.retrofit.BaseResponse;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.log.LogCat;

/**
 * @author yinhui
 * @date 2019-10-08
 */
public abstract class BaseRefreshCard<Model extends BaseRefreshCard.BaseModel, Resp>
        extends ItemType<Model, BaseViewHolder<Model>> {

    protected final String TAG = this.getClass().getSimpleName();

    private static final int VIEW_BOUNDARY_SIZE = 4;

    private static final int STATE_INIT = 0;
    private static final int STATE_LOADING = 1;
    private static final int STATE_SUCCESS = 10;
    private static final int STATE_FAILED = 11;
    private static final int STATE_CANCEL = 12;

    protected static final String FORMAT_FLOAT_NO_DECIMAL = "%.0f";
    protected static final String FORMAT_FLOAT_DOUBLE_DECIMAL = "%.2f";
    protected static final String FORMAT_FLOAT_SINGLE_DECIMAL = "%.1f";
    protected static final String FORMAT_FLOAT_DOUBLE_PERCENT = "%.2f%%";
    protected static final DecimalFormat FORMAT_MAX_DOUBLE_DECIMAL = new DecimalFormat("#.##");
    protected static final DecimalFormat FORMAT_THOUSANDS_DOUBLE_DECIMAL = new DecimalFormat(",###,##0.00");
    protected static final DecimalFormat FORMAT_THOUSANDS = new DecimalFormat(",###,###");

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private Model mModel;
    private int mPositionMin = -1;
    private int mPositionMax = -1;

    protected Presenter mPresenter;
    protected DashboardCondition mCondition;
    protected int mState;
    protected int mPeriod;
    protected Interval mPeriodTime;

    protected BaseRefreshCard(Presenter presenter, DashboardCondition condition, int period, Interval periodTime) {
        this.mModel = createModel();
        if (this.mModel == null) {
            throw new RuntimeException("createModel() must return NON-NULL model!");
        }
        reset(presenter, condition, period, periodTime);
    }

    public void reset(Presenter presenter, DashboardCondition condition, int period, Interval periodTime) {
        mModel.valid = false;
        mModel.init(condition);
        this.mState = STATE_INIT;
        this.mPresenter = presenter;
        this.mPeriod = period;
        this.mPeriodTime = periodTime;
        this.mCondition = condition;
    }

    public Model getModel() {
        return mModel;
    }

    public void registerIntoAdapter(BaseRecyclerAdapter<Object> adapter, int position) {
        this.mPositionMin = position;
        this.mPositionMax = position;
        //noinspection unchecked
        adapter.register((Class<Model>) mModel.getClass(), this);
    }

    public void setPeriod(int period, Interval periodTime, boolean forceLoad) {
        if (!forceLoad && this.mPeriod == period && Objects.equals(mPeriodTime, periodTime)) {
            return;
        }
        this.mPeriod = period;
        this.mPeriodTime = periodTime;
        mModel.valid = false;
        requestLoad(true);
    }

    public void refresh(boolean showLoading) {
        if (showLoading) {
            mModel.valid = false;
        }
        requestLoad(showLoading);
    }

    public void cancelLoad() {
        // TODO: Cancel.
    }

    private void requestLoad(boolean showLoading) {
//        if (mCall.isLoading() && mCall.isRequestSame(SpUtils.getCompanyId(), SpUtils.getShopId(), mPeriod)) {
//            LogCat.d(TAG, "Data is loading, skip.");
//            return;
//        }
        LogCat.d(TAG, "Start to load data.");
        mState = STATE_LOADING;
        if (showLoading) {
            updateViews();
        }
        CardCallback callback = new CardCallback(mPeriod, mPeriodTime);
        Call<BaseResponse<Resp>> call = load(SpUtils.getCompanyId(), SpUtils.getShopId(), mPeriod, mPeriodTime, callback);
//        mCall.set(call, SpUtils.getCompanyId(), SpUtils.getShopId(), mPeriod);
    }

    protected void updateViews() {
        LogCat.d(TAG, "Post update views of card.");
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mHandler.post(this::updateViews);
            return;
        }
        if (mPositionMin < 0 || mPositionMax < 0) {
            return;
        }
        BaseRecyclerAdapter adapter = getAdapter();
        if (mPositionMin == mPositionMax) {
            adapter.notifyItemChanged(mPositionMin);
        } else {
            adapter.notifyItemRangeChanged(mPositionMin, mPositionMax);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        LogCat.d(TAG, "Set up views.");
        boolean isLoading = (mState == STATE_INIT || mState == STATE_LOADING);
        int[] margin = model.margin;
        int[] padding = model.padding;
        if (model.valid) {
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
        if (margin != null && margin.length >= VIEW_BOUNDARY_SIZE) {
            ((ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams())
                    .setMargins(margin[0], margin[1], margin[2], margin[3]);
            model.margin = null;
        }
        if (padding != null && padding.length >= VIEW_BOUNDARY_SIZE) {
            holder.itemView.setPadding(padding[0], padding[1], padding[2], padding[3]);
            model.padding = null;
        }
    }

    /**
     * 初始化
     *
     * @param context 上下文
     */
    public abstract void init(Context context);

    /**
     * 加载数据，如果有API请求，请使用callback回调；如果无需网络请求，直接使用callback.success();
     *
     * @param companyId  商户ID
     * @param shopId     店铺ID
     * @param period     时间维度
     * @param periodTime 时间段
     * @param callback   接口回调
     * @return API请求Call，用于取消
     */
    protected abstract Call<BaseResponse<Resp>> load(int companyId, int shopId, int period, Interval periodTime,
                                                     CardCallback callback);

    /**
     * 创建ViewModel数据
     *
     * @return ViewModel
     */
    protected abstract Model createModel();

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
        setupView(holder, model, position);
    }

    /**
     * 显示Error状态，在第一次数据加载失败时调用
     *
     * @param holder   ViewHolder
     * @param model    ViewModel
     * @param position 在列表中的位置index
     */
    protected void showError(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        setupView(holder, model, position);
    }

    protected class CardCallback extends RetrofitCallback<Resp> {

        private int period;
        private Interval periodTime;

        public CardCallback(int period, Interval periodTime) {
            this.period = period;
            this.periodTime = periodTime;
        }

        public int getPeriod() {
            return period;
        }

        public Interval getPeriodTime() {
            return periodTime;
        }

        @MainThread
        public void onSuccess() {
            LogCat.d(TAG, "Dashboard card load data pass. ");
            mState = STATE_SUCCESS;
            mModel.valid = true;
            mModel.period = this.period;
            mModel.periodTime = this.periodTime;
            setupModel(mModel, null);
            updateViews();
        }

        @MainThread
        @Override
        public void onSuccess(int code, String msg, Resp data) {
            LogCat.d(TAG, "Dashboard card load Success. " + msg);
            mState = STATE_SUCCESS;
            mModel.valid = true;
            mModel.period = this.period;
            mModel.periodTime = this.periodTime;
            setupModel(mModel, data);
            updateViews();
        }

        @MainThread
        @Override
        public void onFail(int code, String msg, Resp data) {
            LogCat.e(TAG, "Dashboard card load Failed. " + msg);
            mState = STATE_FAILED;
            mModel.period = this.period;
            mModel.periodTime = this.periodTime;
            mPresenter.showFailedTip();
            updateViews();
        }
    }

    public static abstract class BaseModel {
        public boolean valid = false;
        public int period;
        public Interval periodTime;

        public int[] padding;
        public int[] margin;

        public void init(DashboardCondition condition) {
        }

        public void setMargin(int left, int top, int right, int bottom) {
            this.margin = new int[]{left, top, right, bottom};
        }

        public void setPadding(int left, int top, int right, int bottom) {
            this.padding = new int[]{left, top, right, bottom};
        }

    }

    public interface Presenter {

        void pullToRefresh(boolean showLoading);

        void setPeriod(int period, Interval periodTime);

        void showLoading();

        void hideLoading();

        void showFailedTip();
    }
}
