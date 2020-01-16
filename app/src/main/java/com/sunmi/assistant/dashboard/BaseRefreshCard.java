package com.sunmi.assistant.dashboard;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

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

    protected static final CharSequence DATE_FORMAT = "yyyy-MM-dd";
    protected static final String DATA_NONE = "--";
    protected static final String DATA_ZERO = "0";
    protected static final String DATA_ZERO_RATIO = "0%";
    protected static final String FORMAT_FLOAT_NO_DECIMAL = "%.0f";
    protected static final String FORMAT_FLOAT_DOUBLE_DECIMAL = "%.2f";
    protected static final String FORMAT_FLOAT_SINGLE_DECIMAL = "%.1f";
    protected static final String FORMAT_FLOAT_DOUBLE_PERCENT = "%.2f%%";
    protected static final DecimalFormat FORMAT_MAX_DOUBLE_DECIMAL = new DecimalFormat("#.##");
    protected static final DecimalFormat FORMAT_THOUSANDS_DOUBLE_DECIMAL = new DecimalFormat(",###,##0.00");
    protected static final DecimalFormat FORMAT_THOUSANDS = new DecimalFormat(",###,###");

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private RequestCall<Resp> mCall = new RequestCall<>();
    private Model mModel;
    private int mPositionMin = -1;
    private int mPositionMax = -1;

    protected Presenter mPresenter;
    protected int mState;
    protected int mSource;
    protected int mPeriod;

    protected BaseRefreshCard(Presenter presenter, int source) {
        this.mModel = createModel();
        if (this.mModel == null) {
            throw new RuntimeException("createModel() must return NON-NULL model!");
        }
        reset(presenter, source);
    }

    public void reset(Presenter presenter, int source) {
        mModel.valid = false;
        mModel.init(source);
        this.mPresenter = presenter;
        this.mState = STATE_INIT;
        this.mPeriod = Constants.TIME_PERIOD_INIT;
        this.mSource = source;
    }

    public boolean hasAuth() {
        return Utils.hasAuth(mSource);
    }

    public boolean hasImport() {
        return Utils.hasImport(mSource);
    }

    public boolean hasFs() {
        return Utils.hasFs(mSource);
    }

    public boolean hasCustomer() {
        return Utils.hasCustomer(mSource);
    }

    public boolean hasFloating() {
        return Utils.hasFloating(mSource);
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
//
//    protected void updateModels() {
//        if (Looper.myLooper() != Looper.getMainLooper()) {
//            mHandler.post(this::updateModels);
//            return;
//        }
//        mModels.clear();
//        mPositionMin = -1;
//        mPositionMax = -1;
//        BaseRecyclerAdapter adapter = getAdapter();
//        int count = adapter.getItemCount();
//        int min = count;
//        int max = -1;
//        for (int i = count - 1; i >= 0; i--) {
//            if (adapter.getItemType(i) == this) {
//                min = Math.min(min, i);
//                max = Math.max(max, i);
//                //noinspection unchecked
//                mModels.add((Model) adapter.getItem(i));
//            }
//        }
//        if (min <= max) {
//            mPositionMin = min;
//            mPositionMax = max;
//        }
//    }
//
//    protected void clearModels() {
//        if (Looper.myLooper() != Looper.getMainLooper()) {
//            mHandler.post(this::clearModels);
//            return;
//        }
//        mModels.clear();
//        mPositionMin = -1;
//        mPositionMax = -1;
//        BaseRecyclerAdapter adapter = getAdapter();
//        int count = adapter.getItemCount();
//        for (int i = count - 1; i >= 0; i--) {
//            if (adapter.getItemType(i) == this) {
//                adapter.remove(i);
//            }
//        }
//    }

    public void setPeriod(int period, boolean forceLoad) {
        if (!forceLoad && this.mPeriod == period) {
            return;
        }
        this.mPeriod = period;
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
        if (mCall.isLoading()) {
            mCall.cancel();
        }
    }

    private void requestLoad(boolean showLoading) {
        if (mPeriod == Constants.TIME_PERIOD_INIT) {
            LogCat.d(TAG, "Period is not initialized, skip.");
            return;
        }
        if (mCall.isLoading() && mCall.isRequestSame(SpUtils.getCompanyId(), SpUtils.getShopId(), mPeriod)) {
            LogCat.d(TAG, "Data is loading, skip.");
            return;
        }
        LogCat.d(TAG, "Start to load data.");
        mState = STATE_LOADING;
        if (showLoading) {
            updateViews();
        }
        CardCallback callback = new CardCallback(mSource, mPeriod);
        Call<BaseResponse<Resp>> call = load(SpUtils.getCompanyId(), SpUtils.getShopId(), mPeriod, callback);
        mCall.set(call, SpUtils.getCompanyId(), SpUtils.getShopId(), mPeriod);
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
        if (margin != null && margin.length >= VIEW_BOUNDARY_SIZE) {
            ((ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams())
                    .setMargins(margin[0], margin[1], margin[2], margin[3]);
            model.margin = null;
        }
        if (padding != null && padding.length >= VIEW_BOUNDARY_SIZE) {
            holder.itemView.setPadding(padding[0], padding[1], padding[2], padding[3]);
            model.padding = null;
        }
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
    }

    public abstract void init(Context context);

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

        private int source;
        private int period;

        public CardCallback(int source, int period) {
            this.source = source;
            this.period = period;
        }

        @MainThread
        public void onSuccess() {
            LogCat.d(TAG, "Dashboard card load data pass. ");
            mState = STATE_SUCCESS;
            mModel.valid = true;
            mModel.source = this.source;
            mModel.period = this.period;
            setupModel(mModel, null);
            updateViews();
        }

        @MainThread
        @Override
        public void onSuccess(int code, String msg, Resp data) {
            LogCat.d(TAG, "Dashboard card load Success. " + msg);
            mState = STATE_SUCCESS;
            mModel.valid = true;
            mModel.source = this.source;
            mModel.period = this.period;
            setupModel(mModel, data);
            updateViews();
        }

        @MainThread
        @Override
        public void onFail(int code, String msg, Resp data) {
            LogCat.e(TAG, "Dashboard card load Failed. " + msg);
            mState = STATE_FAILED;
            mModel.source = this.source;
            mModel.period = this.period;
            mPresenter.showFailedTip();
            updateViews();
        }
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

    public static abstract class BaseModel {
        public boolean valid = false;
        public int source;
        public int period;

        public int[] padding;
        public int[] margin;

        public void init(int source) {
        }

        public void setMargin(int left, int top, int right, int bottom) {
            this.margin = new int[]{left, top, right, bottom};
        }

        public void setPadding(int left, int top, int right, int bottom) {
            this.padding = new int[]{left, top, right, bottom};
        }

    }

    public interface Presenter {

        void setPeriod(int period);

        void refresh(boolean showLoading);

        void showLoading();

        void hideLoading();

        void showFailedTip();
    }
}
