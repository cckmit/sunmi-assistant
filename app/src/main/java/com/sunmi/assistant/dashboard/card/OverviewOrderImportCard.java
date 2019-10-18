package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.BaseRefreshCard;
import com.sunmi.assistant.dashboard.Constants;

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
public class OverviewOrderImportCard extends BaseRefreshCard<OverviewOrderImportCard.Model, ShopAuthorizeInfoResp> {

    private static OverviewOrderImportCard sInstance;

    private int mColorGray;
    private int mColorWhite;
    private GradientDrawable mContentBg;
    private int mRequestCount;

    private OverviewOrderImportCard(Presenter presenter, int source) {
        super(presenter, source);
    }

    public static OverviewOrderImportCard get(Presenter presenter, int source) {
        if (sInstance == null) {
            sInstance = new OverviewOrderImportCard(presenter, source);
        } else {
            sInstance.reset(source);
        }
        return sInstance;
    }

    @Override
    public void init(Context context) {
    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_recycle_item_order_import;
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

    @NonNull
    @Override
    public BaseViewHolder<Model> onCreateViewHolder(@NonNull View view, @NonNull ItemType<Model, BaseViewHolder<Model>> type) {
        BaseViewHolder<Model> holder = super.onCreateViewHolder(view, type);
        holder.addOnClickListener(R.id.btn_dashboard_import, (h, model, position) -> {
            if (model.state == Constants.IMPORT_NONE || model.state == Constants.IMPORT_FAIL) {
                // Import
                mRequestCount = 0;
                model.state = Constants.IMPORT_DOING;
                updateViews();
                int companyId = SpUtils.getCompanyId();
                int shopId = SpUtils.getShopId();
                for (Model.Item item : model.saasList) {
                    SunmiStoreApi.getInstance().importSaas(companyId, shopId,
                            item.shopNo, item.saasSource, new RetrofitCallback<Object>() {
                                @Override
                                public void onSuccess(int code, String msg, Object data) {
                                    mRequestCount++;
                                }

                                @Override
                                public void onFail(int code, String msg, Object data) {
                                    mRequestCount++;
                                    if (mRequestCount >= model.saasCount
                                            && model.state != Constants.IMPORT_SUCCESS) {
                                        model.state = Constants.IMPORT_FAIL;
                                        updateViews();
                                    }
                                }
                            });
                }
            } else if (model.state == Constants.IMPORT_SUCCESS) {
                model.state = Constants.IMPORT_COMPLETE;
                updateViews();
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
            tip.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
            tip.setCompoundDrawablePadding(0);
            btn.setEnabled(true);
            btn.setText(R.string.dashboard_card_import_btn);

        } else if (model.state == Constants.IMPORT_DOING) {
            tip.setText(R.string.dashboard_card_import_tip_loading);
            tip.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
            tip.setCompoundDrawablePadding(0);
            btn.setEnabled(false);
            btn.setText(R.string.dashboard_card_import_btn);

        } else if (model.state == Constants.IMPORT_SUCCESS) {
            tip.setText(R.string.dashboard_card_import_tip_ok);
            tip.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.dashboard_import_ok,
                    0, 0, 0);
            tip.setCompoundDrawablePadding((int) context.getResources().getDimension(R.dimen.dp_4));
            btn.setEnabled(true);
            btn.setText(R.string.str_confirm);

        } else if (model.state == Constants.IMPORT_FAIL) {
            tip.setText(R.string.dashboard_card_import_tip_error);
            tip.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.dashboard_import_error,
                    0, 0, 0);
            tip.setCompoundDrawablePadding((int) context.getResources().getDimension(R.dimen.dp_4));
            btn.setEnabled(true);
            btn.setText(R.string.str_retry);

        } else {
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.getParent().requestLayout();
        }
    }

    public static class Model extends BaseRefreshCard.BaseModel {
        private int state = Constants.IMPORT_NONE;
        private long authTime;
        private List<Item> saasList = new ArrayList<>();
        private int saasCount;

        public static class Item {
            private String shopNo;
            private int saasSource;

            public Item(String shopNo, int saasSource) {
                this.shopNo = shopNo;
                this.saasSource = saasSource;
            }
        }
    }
}
