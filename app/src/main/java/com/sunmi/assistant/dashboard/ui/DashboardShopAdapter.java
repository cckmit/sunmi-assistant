package com.sunmi.assistant.dashboard.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.sunmi.assistant.R;

import java.util.List;

import sunmi.common.model.FilterItem;
import sunmi.common.utils.SpUtils;
import sunmi.common.view.DropdownMenuNew;
import sunmi.common.view.SettingItemLayout;

public class DashboardShopAdapter extends DropdownMenuNew.Adapter<FilterItem> {

    public DashboardShopAdapter(Context context) {
        super(context, R.layout.dashboard_dropdown_shop_title, R.layout.dropdown_item_new);
    }

    public void init() {
        DropdownMenuNew.ViewHolder<FilterItem> content = getContent();
        if (content != null) {
            SettingItemLayout silName = content.getView(R.id.sil_company);
            silName.setTitle(SpUtils.getCompanyName());
            silName.setChecked(true);
        }
    }

    @Override
    protected void setupTitle(@NonNull DropdownMenuNew.ViewHolder<FilterItem> holder, List<FilterItem> models) {
        if (models == null || models.isEmpty()) {
            return;
        }
        FilterItem model = models.get(0);
        TextView title = holder.getView(R.id.dropdown_item_title);
        title.setText(model.getTitleName());
    }

    @Override
    protected void setupItem(@NonNull DropdownMenuNew.ViewHolder<FilterItem> holder, FilterItem model, int position) {
        SettingItemLayout item = holder.getView(R.id.dropdown_item);
        item.setTitle(model.getItemName());
        item.setChecked(model.isChecked());
    }

    public void setCompanyName(String name) {
        DropdownMenuNew.ViewHolder<FilterItem> content = getContent();
        if (content != null) {
            SettingItemLayout silName = content.getView(R.id.sil_company);
            silName.setTitle(name);
        }
    }

    /**
     * 切换到总部视角
     */
    public void selectTotalPerspective() {
        getSelected().clear();
        List<FilterItem> data = getData();
        for (FilterItem item : data) {
            item.setChecked(false);
        }
        notifyDataSetChanged();

        DropdownMenuNew.ViewHolder<FilterItem> content = getContent();
        if (content != null) {
            SettingItemLayout silName = content.getView(R.id.sil_company);
            silName.setChecked(true);
        }
    }

    /**
     * 切换到门店视角
     */
    public void selectShopPerspective() {
        DropdownMenuNew.ViewHolder<FilterItem> content = getContent();
        if (content != null) {
            SettingItemLayout silName = content.getView(R.id.sil_company);
            silName.setChecked(false);
        }
    }

}
