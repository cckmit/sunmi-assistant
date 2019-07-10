package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.support.annotation.NonNull;

import com.sunmi.assistant.R;
import com.sunmi.assistant.ui.MainTopBar;

import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.utils.SpUtils;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class TitleCard extends BaseRefreshCard<TitleCard.Model> {

    public TitleCard(Context context, int companyId, int shopId, int period) {
        super(context, companyId, shopId, period);
    }

    @Override
    protected Model createData() {
        return new Model();
    }

    @Override
    protected ItemType<Model, BaseViewHolder<Model>> createType() {
        return new TitleType();
    }

    @Override
    protected void load(int companyId, int shopId, int period, Model model) {
        model.companyName = SpUtils.getCompanyName();
        model.shopName = SpUtils.getShopName();
        updateView();
    }

    private class TitleType extends ItemType<Model, BaseViewHolder<Model>> {

        @Override
        public int getLayoutId(int type) {
            return R.layout.dashboard_recycle_item_title;
        }

        @Override
        public int getSpanSize() {
            return 2;
        }

        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
            MainTopBar bar = holder.getView(R.id.dashboard_title);
            bar.setCompanyName(model.companyName);
            bar.setShopName(model.shopName);
        }

    }

    public static class Model {
        private String companyName;
        private String shopName;

        private Model() {
            this.companyName = SpUtils.getCompanyName();
            this.shopName = SpUtils.getShopName();
        }
    }
}
