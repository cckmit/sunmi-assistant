package com.sunmi.assistant.dashboard;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.sunmi.assistant.R;

import sunmi.common.view.DropdownMenu;

/**
 * @author yinhui
 * @date 18-1-17
 */
public class ShopMenuAdapter extends DropdownMenu.BaseAdapter<ShopItem> {

    public ShopMenuAdapter(Context context) {
        super(context, R.layout.dashboard_dropdown_title, R.layout.dropdown_item);
    }

    @Override
    protected DropdownMenu.BaseTitleViewHolder<ShopItem> createTitle(View view) {
        return new TitleHolder(view);
    }

    @Override
    protected DropdownMenu.BaseItemViewHolder<ShopItem> createItem(View view) {
        return new ItemHolder(view, this);
    }

    public static class TitleHolder extends DropdownMenu.BaseTitleViewHolder<ShopItem> {

        TitleHolder(View v) {
            super(v);
        }

        @Override
        public void setUpView(ShopItem model, int position) {
            TextView title = getView(R.id.dropdown_item_title);
            title.setText(model.getShopName());
            title.setSelected(model.getShopId() != -1);
        }
    }

    public static class ItemHolder extends DropdownMenu.BaseItemViewHolder<ShopItem> {

        ItemHolder(View v, DropdownMenu.BaseAdapter<ShopItem> adapter) {
            super(v, adapter);
        }

        @Override
        public void setUpView(ShopItem model, int position) {
            TextView name = getView(R.id.dropdown_item_name);
            name.setText(model.getShopName());
            name.setSelected(model.isChecked());
            getView(R.id.dropdown_item_checkbox).setVisibility(model.isChecked() ?
                    View.VISIBLE : View.INVISIBLE);
        }
    }
}
