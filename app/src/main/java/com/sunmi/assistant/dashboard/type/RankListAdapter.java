package com.sunmi.assistant.dashboard.type;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
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
    public void convert(ViewHolder holder, ListCard.Item item, int position) {
        TextView rank = holder.getView(R.id.tv_dashboard_rank);
        TextView name = holder.getView(R.id.tv_dashboard_name);
        TextView count = holder.getView(R.id.tv_dashboard_count);
        View divider = holder.getView(R.id.v_dashboard_divider);

        rank.setText(String.valueOf(item.rank));
        if (position == 0) {
            Drawable drawable = rank.getResources()
                    .getDrawable(R.drawable.dashboard_rank_bg_circle).mutate();
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, Color.parseColor("#FFFC5656"));
            rank.setBackground(drawable);
            rank.setTextColor(Color.WHITE);
        } else if (position == 1) {
            Drawable drawable = rank.getResources()
                    .getDrawable(R.drawable.dashboard_rank_bg_circle).mutate();
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, Color.parseColor("#FFF57F45"));
            rank.setBackground(drawable);
            rank.setTextColor(Color.WHITE);
        } else if (position == 2) {
            Drawable drawable = rank.getResources()
                    .getDrawable(R.drawable.dashboard_rank_bg_circle).mutate();
            drawable = DrawableCompat.wrap(drawable);
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
