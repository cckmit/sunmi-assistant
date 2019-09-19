package com.sunmi.assistant.dashboard.oldcard;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Pair;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.DashboardContract;
import com.sunmi.assistant.dashboard.Utils;
import com.sunmi.assistant.data.PaymentApi;
import com.sunmi.assistant.data.response.OrderQuantityRankResp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import sunmi.common.base.adapter.CommonAdapter;
import sunmi.common.base.adapter.ViewHolder;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.rpc.retrofit.BaseResponse;
import sunmi.common.utils.log.LogCat;

/**
 * @author yinhui
 * @date 2019-07-22
 */
public class QuantityRankCard extends BaseRefreshCard<QuantityRankCard.Model, OrderQuantityRankResp> {

    private static final int MAX_RANK_COUNT = 10;

    private static final int RANK_FIRST = 0;
    private static final int RANK_SECOND = 1;
    private static final int RANK_THIRD = 2;

    public QuantityRankCard(Context context, DashboardContract.Presenter presenter, int companyId, int shopId) {
        super(context, presenter, companyId, shopId);
    }

    @Override
    protected Model createModel(Context context) {
        return new Model(context.getString(R.string.dashboard_quantity_rank));
    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_recycle_item_old_list;
    }

    @Override
    public int getSpanSize() {
        return 2;
    }

    @NonNull
    @Override
    public BaseViewHolder<Model> onCreateViewHolder(@NonNull View view, @NonNull ItemType<Model, BaseViewHolder<Model>> type) {
        QuantityRankCard.RankListAdapter adapter = new QuantityRankCard.RankListAdapter(view.getContext());
        BaseViewHolder<QuantityRankCard.Model> holder = new BaseViewHolder<>(view, type);
        ListView listView = holder.getView(R.id.lv_dashboard_list);
        listView.setAdapter(adapter);
        listView.setDividerHeight(0);
        return holder;
    }

    @Override
    protected Call<BaseResponse<OrderQuantityRankResp>> load(int companyId, int shopId, int period, CardCallback callback) {
        Pair<Long, Long> periodTimestamp = Utils.getPeriodTimestamp(period);
        return PaymentApi.get().getOrderQuantityRank(companyId, shopId,
                periodTimestamp.first, periodTimestamp.second, callback);
    }

    @Override
    protected void setupModel(Model model, OrderQuantityRankResp response) {
        List<OrderQuantityRankResp.QuantityRankItem> list = response.getQuantity_rank();
        Collections.sort(list, (o1, o2) -> o2.getQuantity() - o1.getQuantity());
        int size = list.size();
        model.list = new ArrayList<>(MAX_RANK_COUNT);
        for (int i = 0; i < MAX_RANK_COUNT; i++) {
            if (i < size) {
                OrderQuantityRankResp.QuantityRankItem item = list.get(i);
                model.list.add(new QuantityRankCard.Item(i + 1, item.getName(),
                        String.valueOf(item.getQuantity())));
            } else {
                model.list.add(new QuantityRankCard.Item(i + 1, DATA_NONE, DATA_NONE));
            }
        }
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        holder.getView(R.id.layout_dashboard_content).setVisibility(View.VISIBLE);
        holder.getView(R.id.pb_dashboard_loading).setVisibility(View.GONE);

        if (model.list == null) {
            model.list = new ArrayList<>(10);
        }
        int size = model.list.size();
        if (model.list.isEmpty()) {
            for (int i = 0; i < MAX_RANK_COUNT; i++) {
                model.list.add(new QuantityRankCard.Item(i + 1, DATA_NONE, DATA_NONE));
            }
        } else if (size < MAX_RANK_COUNT) {
            for (int i = size; i < MAX_RANK_COUNT; i++) {
                model.list.add(new QuantityRankCard.Item(i + 1, DATA_NONE, DATA_NONE));
            }
        }

        TextView title = holder.getView(R.id.tv_dashboard_title);
        title.setText(model.title);

        ListView listView = holder.getView(R.id.lv_dashboard_list);
        QuantityRankCard.RankListAdapter adapter = (QuantityRankCard.RankListAdapter) listView.getAdapter();
        adapter.setDatas(model.list);
        adapter.notifyDataSetChanged();
        listView.requestLayout();
    }

    @Override
    protected void showLoading(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        holder.getView(R.id.layout_dashboard_content).setVisibility(View.GONE);
        holder.getView(R.id.pb_dashboard_loading).setVisibility(View.VISIBLE);
    }

    @Override
    protected void showError(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        setupView(holder, model, position);
    }

    private static class RankListAdapter extends CommonAdapter<QuantityRankCard.Item> {

        private static final String TAG = QuantityRankCard.class.getSimpleName();

        private RankListAdapter(Context context) {
            super(context, R.layout.dashboard_recycle_item_old_list_item);
        }

        @Override
        public void convert(ViewHolder holder, QuantityRankCard.Item item) {
            LogCat.d(TAG, "Quantity rank list view setup.");
            TextView rank = holder.getView(R.id.tv_dashboard_rank);
            TextView name = holder.getView(R.id.tv_dashboard_name);
            TextView count = holder.getView(R.id.tv_dashboard_count);
            View divider = holder.getView(R.id.v_dashboard_divider);

            rank.setText(String.valueOf(item.rank));
            name.setText(item.name);
            count.setText(item.count);
            int position = holder.getPosition();
            divider.setVisibility(position == getCount() - 1 ? View.GONE : View.VISIBLE);

            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.dashboard_rank_bg_circle);
            if (drawable == null) {
                return;
            }
            drawable = DrawableCompat.wrap(drawable);
            if (position == RANK_FIRST) {
                DrawableCompat.setTint(drawable, ContextCompat.getColor(mContext, R.color.color_FC5656));
                rank.setBackground(drawable);
                rank.setTextColor(Color.WHITE);
            } else if (position == RANK_SECOND) {
                DrawableCompat.setTint(drawable, ContextCompat.getColor(mContext, R.color.color_F57F45));
                rank.setBackground(drawable);
                rank.setTextColor(Color.WHITE);
            } else if (position == RANK_THIRD) {
                DrawableCompat.setTint(drawable, ContextCompat.getColor(mContext, R.color.color_FAB641));
                rank.setBackground(drawable);
                rank.setTextColor(Color.WHITE);
            } else {
                rank.setBackground(null);
                rank.setTextColor(ContextCompat.getColor(mContext, R.color.color_85858A));
            }
        }
    }

    public static class Model extends BaseRefreshCard.BaseModel {
        public String title;
        public List<QuantityRankCard.Item> list;

        public Model(String title) {
            this.title = title;
        }
    }

    private static class Item {
        private int rank;
        private String name;
        private String count;

        private Item(int rank, String name, String count) {
            this.rank = rank;
            this.name = name;
            this.count = count;
        }
    }
}
