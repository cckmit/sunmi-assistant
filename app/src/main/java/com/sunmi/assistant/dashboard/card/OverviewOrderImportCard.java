package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.view.View;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.BaseRefreshCard;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.model.ShopAuthorizeInfoResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class OverviewOrderImportCard extends BaseRefreshCard<OverviewOrderImportCard.Model, ShopAuthorizeInfoResp> {

    private static OverviewOrderImportCard sInstance;

    private int mColorGray;
    private int mColorWhite;
    private GradientDrawable mContentBg;

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
        SunmiStoreApi.getInstance().getAuthorizeInfo(companyId, shopId, callback);
        return null;
    }

    @Override
    protected Model createModel() {
        return new Model();
    }

    @Override
    protected void setupModel(Model model, ShopAuthorizeInfoResp response) {
        if (response == null
                || response.getAuthorizedList() == null
                || response.getAuthorizedList().isEmpty()) {
            return;
        }
        ShopAuthorizeInfoResp.Info info = response.getAuthorizedList().get(0);
        model.state = info.getImportStatus();
        model.authTime = info.getAuthorizedTime();
    }

    @NonNull
    @Override
    public BaseViewHolder<Model> onCreateViewHolder(@NonNull View view, @NonNull ItemType<Model, BaseViewHolder<Model>> type) {
        BaseViewHolder<Model> holder = super.onCreateViewHolder(view, type);
        Context context = view.getContext();
        // TODO: Init view
        return holder;
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        // TODO: Update view
    }

    public static class Model extends BaseRefreshCard.BaseModel {
        private int state;
        private long authTime;
    }
}
