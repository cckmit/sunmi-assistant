package com.sunmi.assistant.dashboard.type;

import android.content.Context;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.model.ListCard;

import sunmi.common.base.adapter.CommonAdapter;
import sunmi.common.base.adapter.ViewHolder;

/**
 * @author yinhui
 * @since 2019-06-17
 */
public class RankListAdapter extends CommonAdapter<ListCard.Item> {

    public RankListAdapter(Context context) {
        super(context, R.layout.dashboard_recycle_item_list_item);
    }

    @Override
    public void convert(ViewHolder holder, ListCard.Item item) {
        TextView rank = holder.getView(R.id.tv_dashboard_rank);
        TextView name = holder.getView(R.id.tv_dashboard_name);
        TextView count = holder.getView(R.id.tv_dashboard_count);
        rank.setText(String.valueOf(item.rank));
        name.setText(item.name);
        count.setText(String.valueOf(item.count));
    }
}
