package com.sunmi.assistant.order;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.order.model.FilterItem;

import sunmi.common.view.DropdownMenu;

/**
 * Created by yinhui on 18-1-17.
 */

public class DropdownAdapter extends DropdownMenu.BaseAdapter<FilterItem> {

    DropdownAdapter(Context context) {
        super(context, R.layout.order_dropdown_title, R.layout.order_dropdown_item);
    }

    @Override
    protected DropdownMenu.BaseTitleViewHolder<FilterItem> createTitle(View view) {
        return new TitleHolder(view);
    }

    @Override
    protected DropdownMenu.BaseItemViewHolder<FilterItem> createItem(View view) {
        return new ItemHolder(view, this);
    }

    public static class TitleHolder extends DropdownMenu.BaseTitleViewHolder<FilterItem> {

        TitleHolder(View v) {
            super(v);
        }

        @Override
        public void setUpView(FilterItem model, int position) {
            TextView title = getView(R.id.order_filter_title);
            title.setText(model.getName());
        }
    }

    public static class ItemHolder extends DropdownMenu.BaseItemViewHolder<FilterItem> {

        ItemHolder(View v, DropdownMenu.BaseAdapter<FilterItem> adapter) {
            super(v, adapter);
        }

        @Override
        public void setUpView(FilterItem model, int position) {
            Resources res = mContext.getResources();
            TextView name = getView(R.id.order_filter_name);
            name.setText(model.getName());
            name.setTextColor(model.isChecked() ?
                    res.getColor(R.color.color_FF6000) : res.getColor(R.color.color_333338));
            getView(R.id.order_filter_checkbox).setVisibility(model.isChecked() ? View.VISIBLE : View.INVISIBLE);
        }
    }
}
