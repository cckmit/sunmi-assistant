package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.support.annotation.NonNull;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.DashboardContract;
import com.sunmi.assistant.ui.MainTopBar;

import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.utils.SpUtils;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class TitleCard extends BaseRefreshCard<TitleCard.Model, Object> {

    public TitleCard(Context context, DashboardContract.Presenter presenter, int companyId, int shopId) {
        super(context, presenter, companyId, shopId);
    }

    @Override
    protected Model createModel(Context context) {
        return new Model();
    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_recycle_item_title;
    }

    @Override
    public int getSpanSize() {
        return 2;
    }

    @Override
    protected void load(int companyId, int shopId, int period, CardCallback callback) {
        callback.onSuccess();
    }

    @Override
    protected void setupModel(Model model, Object response) {
        model.companyName = SpUtils.getCompanyName();
        model.shopName = SpUtils.getShopName();
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        MainTopBar bar = holder.getView(R.id.dashboard_title);
        bar.setCompanyName(model.companyName);
        bar.setShopName(model.shopName);
    }

    public static class Model extends BaseRefreshCard.BaseModel {
        private String companyName;
        private String shopName;

        private Model() {
            this.companyName = SpUtils.getCompanyName();
            this.shopName = SpUtils.getShopName();
        }
    }
}
