package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.support.annotation.NonNull;

import com.sunmi.assistant.R;

import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class TitleCard extends BaseRefreshCard<TitleCard.Model> {

    public TitleCard(Context context) {
        super(context);
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
    protected void load(int companyId, int shopId, int period, Model o) {
    }

    private static class TitleType extends ItemType<Model, BaseViewHolder<Model>> {

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
        }

    }

    public static class Model {
    }
}
