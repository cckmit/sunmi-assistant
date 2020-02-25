package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.util.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.model.ShopAuthorizeInfoResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.BaseResponse;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class RealtimeOrderImportCard extends BaseRefreshCard<RealtimeOrderImportCard.Model, ShopAuthorizeInfoResp> {

    private static RealtimeOrderImportCard sInstance;

    private OnImportStateChangeListener mListener;

    private int mColorNormal;
    private int mColorOk;
    private int mColorError;

    private int mRequestCount;
    private int mFailedCount;

    private RealtimeOrderImportCard(Presenter presenter, int source) {
        super(presenter, source);
    }

    public static RealtimeOrderImportCard get(Presenter presenter, int source) {
        if (sInstance == null) {
            sInstance = new RealtimeOrderImportCard(presenter, source);
        } else {
            sInstance.reset(presenter, source);
        }
        return sInstance;
    }

    @Override
    public void init(Context context) {
        mColorNormal = ContextCompat.getColor(context, R.color.text_caption);
        mColorOk = ContextCompat.getColor(context, R.color.assist_primary);
        mColorError = ContextCompat.getColor(context, R.color.caution_primary);
    }

    public void setListener(OnImportStateChangeListener l) {
        this.mListener = l;
    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_item_realtime_order_import;
    }

    @Override
    protected Call<BaseResponse<ShopAuthorizeInfoResp>> load(int companyId, int shopId, int period, CardCallback callback) {
        if (getModel().state != Constants.IMPORT_COMPLETE) {
            SunmiStoreApi.getInstance().getAuthorizeInfo(companyId, shopId, callback);
        }
        return null;
    }

    @Override
    protected Model createModel() {
        return new Model();
    }

    private void switchState(Model model, int state) {
        model.state = state;
        if (mListener != null) {
            mListener.onImportStateChange(model.state);
        }
        updateViews();
    }

    @NonNull
    @Override
    public BaseViewHolder<Model> onCreateViewHolder(@NonNull View view, @NonNull ItemType<Model, BaseViewHolder<Model>> type) {
        BaseViewHolder<Model> holder = super.onCreateViewHolder(view, type);
        holder.addOnClickListener(R.id.btn_dashboard_import, (h, model, position) -> {
            if (model.state == Constants.IMPORT_NONE || model.state == Constants.IMPORT_FAIL) {
                // Import
                mRequestCount = 0;
                mFailedCount = 0;
                mPresenter.showLoading();
                switchState(model, Constants.IMPORT_DOING);
                int companyId = SpUtils.getCompanyId();
                int shopId = SpUtils.getShopId();
                for (Model.Item item : model.saasList) {
                    SunmiStoreApi.getInstance().importSaas(companyId, shopId,
                            item.shopNo, item.saasSource, new RetrofitCallback<Object>() {
                                @Override
                                public void onSuccess(int code, String msg, Object data) {
                                    mRequestCount++;
                                    if (mRequestCount >= model.saasCount) {
                                        mPresenter.hideLoading();
                                    }
                                }

                                @Override
                                public void onFail(int code, String msg, Object data) {
                                    mRequestCount++;
                                    if (mRequestCount >= model.saasCount) {
                                        mPresenter.hideLoading();
                                    }

                                    mFailedCount++;
                                    if (mFailedCount >= model.saasCount) {
                                        switchState(model, Constants.IMPORT_FAIL);
                                    }
                                }
                            });
                }
            } else if (model.state == Constants.IMPORT_SUCCESS) {
                switchState(model, Constants.IMPORT_COMPLETE);
            }
        });
        return holder;
    }

    @Override
    protected void setupModel(Model model, ShopAuthorizeInfoResp response) {
        if (response == null
                || response.getAuthorizedList() == null
                || response.getAuthorizedList().isEmpty()) {
            return;
        }
        List<ShopAuthorizeInfoResp.Info> list = response.getAuthorizedList();
        ShopAuthorizeInfoResp.Info info = list.get(0);
        model.state = info.getImportStatus();
        model.authTime = info.getAuthorizedTime();
        model.saasList.clear();
        model.saasCount = list.size();
        for (ShopAuthorizeInfoResp.Info item : list) {
            model.saasList.add(new Model.Item(item.getShopNo(), item.getSaasSource()));
        }
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        Context context = holder.getContext();
        TextView tip = holder.getView(R.id.tv_dashboard_tip);
        Button btn = holder.getView(R.id.btn_dashboard_import);

        if (model.state == Constants.IMPORT_NONE) {
            String time = DateUtils.formatDateTime(context, model.authTime * 1000,
                    DateUtils.FORMAT_SHOW_YEAR);
            tip.setText(context.getString(R.string.dashboard_card_import_tip_import, time));
            tip.setTextColor(mColorNormal);
            tip.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
            tip.setCompoundDrawablePadding(0);
            btn.setVisibility(View.VISIBLE);
            btn.setText(R.string.dashboard_card_import_btn);

        } else if (model.state == Constants.IMPORT_DOING) {
            tip.setText(R.string.dashboard_card_import_tip_loading);
            tip.setTextColor(mColorOk);
            tip.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
            tip.setCompoundDrawablePadding(0);
            btn.setVisibility(View.INVISIBLE);

        } else if (model.state == Constants.IMPORT_SUCCESS) {
            tip.setText(R.string.dashboard_card_import_tip_ok);
            tip.setTextColor(mColorOk);
            tip.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.dashboard_import_ok,
                    0, 0, 0);
            tip.setCompoundDrawablePadding((int) context.getResources().getDimension(R.dimen.dp_4));
            btn.setVisibility(View.VISIBLE);
            btn.setText(R.string.str_confirm);

        } else if (model.state == Constants.IMPORT_FAIL) {
            tip.setText(R.string.dashboard_card_import_tip_error);
            tip.setTextColor(mColorError);
            tip.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.dashboard_import_error,
                    0, 0, 0);
            tip.setCompoundDrawablePadding((int) context.getResources().getDimension(R.dimen.dp_4));
            btn.setVisibility(View.VISIBLE);
            btn.setText(R.string.str_retry);

        } else {
            holder.itemView.postDelayed(() -> {
                getAdapter().remove(position);
                mPresenter.refresh(true);
            }, 200);
        }
    }

    public static class Model extends BaseRefreshCard.BaseModel {
        private int state = Constants.IMPORT_NONE;
        private long authTime;
        private int saasCount;
        private List<Item> saasList = new ArrayList<>();

        @Override
        public void init(int source) {
            this.state = Constants.IMPORT_NONE;
            this.authTime = 0;
            this.saasCount = 0;
            this.saasList.clear();
        }

        public static class Item {
            private String shopNo;
            private int saasSource;

            public Item(String shopNo, int saasSource) {
                this.shopNo = shopNo;
                this.saasSource = saasSource;
            }
        }
    }

    public interface OnImportStateChangeListener {

        void onImportStateChange(int state);
    }
}
