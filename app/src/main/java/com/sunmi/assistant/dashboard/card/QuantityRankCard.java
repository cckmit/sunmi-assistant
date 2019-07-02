package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.BaseRefreshCard;
import com.sunmi.assistant.data.SunmiStoreRemote;
import com.sunmi.assistant.data.response.OrderQuantityRankResp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sunmi.common.base.adapter.CommonAdapter;
import sunmi.common.base.adapter.ViewHolder;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class QuantityRankCard extends BaseRefreshCard<QuantityRankCard.Model> {

    private static final String TAG = "QuantityRankCard";

    public QuantityRankCard(Context context, int companyId, int shopId, int period) {
        super(context, companyId, shopId, period);
    }

    @Override
    protected Model createData() {
        return new Model(mContext.getString(R.string.dashboard_quantity_rank));
    }

    @Override
    protected ItemType<Model, BaseViewHolder<Model>> createType() {
        return new QuantityRankType();
    }

    @Override
    protected void onPeriodChange(int period) {
    }

    @Override
    protected void load(int companyId, int shopId, int period, Pair<Long, Long> periodTimestamp, Model model) {
        SunmiStoreRemote.get().getOrderQuantityRank(companyId, shopId,
                periodTimestamp.first, periodTimestamp.second,
                new CardCallback<OrderQuantityRankResp>() {
                    @Override
                    public void success(OrderQuantityRankResp data) {
                        Log.d(TAG, "HTTP request quantity rank success.");
                        List<OrderQuantityRankResp.QuantityRankItem> list = data.getQuantity_rank();
                        Collections.sort(list, (o1, o2) -> o2.getQuantity() - o1.getQuantity());
                        int size = list.size();
                        model.list = new ArrayList<>(10);
                        for (int i = 0; i < 10; i++) {
                            if (i < size) {
                                OrderQuantityRankResp.QuantityRankItem item = list.get(i);
                                model.list.add(new Item(i + 1, item.getName(),
                                        String.valueOf(item.getQuantity())));
                            } else {
                                model.list.add(new Item(i + 1, "--", "--"));
                            }
                        }
                    }
                });
    }

    public class QuantityRankType extends ItemType<Model, BaseViewHolder<Model>> {

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
        public BaseViewHolder<Model> onCreateViewHolder(@NonNull View view,
                                                        @NonNull ItemType<Model, BaseViewHolder<Model>> type) {
            RankListAdapter adapter = new RankListAdapter(view.getContext());
            BaseViewHolder<Model> holder = new BaseViewHolder<>(view, type);
            ListView listView = holder.getView(R.id.lv_dashboard_list);
            listView.setAdapter(adapter);
            listView.setDividerHeight(0);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
            mHolder = holder;
            ListView listView = holder.getView(R.id.lv_dashboard_list);
            TextView title = holder.getView(R.id.tv_dashboard_title);
            title.setText(model.title);

            if (mState == STATE_INIT) {
                Log.d(TAG, "Card data setup view skip.");
                return;
            }

            if (model.list == null || model.list.size() == 0) {
                return;
            }
            RankListAdapter adapter = (RankListAdapter) listView.getAdapter();
            adapter.setDatas(model.list);
            adapter.notifyDataSetChanged();
        }

    }

    private static class RankListAdapter extends CommonAdapter<Item> {

        private RankListAdapter(Context context) {
            super(context, R.layout.dashboard_recycle_item_list_item);
        }

        @Override
        public void convert(ViewHolder holder, Item item) {
            TextView rank = holder.getView(R.id.tv_dashboard_rank);
            TextView name = holder.getView(R.id.tv_dashboard_name);
            TextView count = holder.getView(R.id.tv_dashboard_count);
            View divider = holder.getView(R.id.v_dashboard_divider);

            rank.setText(String.valueOf(item.rank));
            Drawable drawable = rank.getResources()
                    .getDrawable(R.drawable.dashboard_rank_bg_circle);
            drawable = DrawableCompat.wrap(drawable);
            int position = holder.getPosition();
            if (position == 0) {
                DrawableCompat.setTint(drawable, mContext.getResources().getColor(R.color.color_FC5656));
                rank.setBackground(drawable);
                rank.setTextColor(Color.WHITE);
            } else if (position == 1) {
                DrawableCompat.setTint(drawable, mContext.getResources().getColor(R.color.color_F57F45));
                rank.setBackground(drawable);
                rank.setTextColor(Color.WHITE);
            } else if (position == 2) {
                DrawableCompat.setTint(drawable, mContext.getResources().getColor(R.color.color_FAB641));
                rank.setBackground(drawable);
                rank.setTextColor(Color.WHITE);
            } else {
                rank.setBackground(null);
                rank.setTextColor(mContext.getResources().getColor(R.color.color_85858A));
            }
            name.setText(item.name);
            count.setText(item.count);
            divider.setVisibility(position == getCount() - 1 ? View.GONE : View.VISIBLE);
        }
    }

    static class Model {
        public String title;
        public List<Item> list;

        public Model(String title) {
            this.title = title;
        }
    }

    static class Item {
        public int rank;
        public String name;
        public String count;

        public Item(int rank, String name, String count) {
            this.rank = rank;
            this.name = name;
            this.count = count;
        }
    }

}
