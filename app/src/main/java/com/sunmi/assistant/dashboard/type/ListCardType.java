package com.sunmi.assistant.dashboard.type;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.DataRefreshCallback;
import com.sunmi.assistant.dashboard.model.ListCard;

import sunmi.common.base.adapter.CommonAdapter;
import sunmi.common.base.adapter.ViewHolder;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.utils.CommonHelper;

/**
 * @author yinhui
 * @since 2019-06-14
 */
public class ListCardType extends ItemType<ListCard, BaseViewHolder<ListCard>> {

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_recycle_item_list;
    }

    @Override
    public int getSpanSize() {
        return 2;
    }

    @NonNull
    @Override
    public BaseViewHolder<ListCard> onCreateViewHolder(@NonNull View view, @NonNull ItemType<ListCard, BaseViewHolder<ListCard>> type) {
        RankListAdapter adapter = new RankListAdapter(view.getContext());
        BaseViewHolder<ListCard> holder = new BaseViewHolder<>(view, type);
        ListView listView = holder.getView(R.id.lv_dashboard_list);
        listView.setAdapter(adapter);
        listView.setDividerHeight(0);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<ListCard> holder, ListCard model, int position) {
        model.setCallback(new DataRefreshCallback() {
            @Override
            public void onSuccess() {
                setupView(holder, model, position);
            }

            @Override
            public void onFail() {
            }
        });
        setupView(holder, model, position);
    }

    private void setupView(BaseViewHolder<ListCard> holder, ListCard model, int position) {
        ListView listView = holder.getView(R.id.lv_dashboard_list);
        TextView title = holder.getView(R.id.tv_dashboard_title);
        title.setText(model.title);
        if (model.list == null || model.list.size() == 0) {
            return;
        }
        RankListAdapter adapter = (RankListAdapter) listView.getAdapter();
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = CommonHelper.dp2px(holder.getContext(), 36.0f * model.list.size() + 2.0f);
        listView.setLayoutParams(params);
        adapter.setDatas(model.list);
        adapter.notifyDataSetChanged();
    }

    private static class RankListAdapter extends CommonAdapter<ListCard.Item> {

        private RankListAdapter(Context context) {
            super(context, R.layout.dashboard_recycle_item_list_item);
        }

        @Override
        public void convert(ViewHolder holder, ListCard.Item item, int position) {
            TextView rank = holder.getView(R.id.tv_dashboard_rank);
            TextView name = holder.getView(R.id.tv_dashboard_name);
            TextView count = holder.getView(R.id.tv_dashboard_count);
            View divider = holder.getView(R.id.v_dashboard_divider);

            rank.setText(String.valueOf(item.rank));
            Drawable drawable = rank.getResources()
                    .getDrawable(R.drawable.dashboard_rank_bg_circle);
            drawable = DrawableCompat.wrap(drawable);
            if (position == 0) {
                DrawableCompat.setTint(drawable, Color.parseColor("#FFFC5656"));
                rank.setBackground(drawable);
                rank.setTextColor(Color.WHITE);
            } else if (position == 1) {
                DrawableCompat.setTint(drawable, Color.parseColor("#FFF57F45"));
                rank.setBackground(drawable);
                rank.setTextColor(Color.WHITE);
            } else if (position == 2) {
                DrawableCompat.setTint(drawable, Color.parseColor("#FFFAB641"));
                rank.setBackground(drawable);
                rank.setTextColor(Color.WHITE);
            } else {
                rank.setBackground(null);
                rank.setTextColor(Color.parseColor("#85858A"));
            }
            name.setText(item.name);
            count.setText(String.valueOf(item.count));
            divider.setVisibility(position == getCount() - 1 ? View.GONE : View.VISIBLE);
        }
    }
}
