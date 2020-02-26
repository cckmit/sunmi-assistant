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

    private OnPerspectiveSwitchListener listener;

    private boolean isTotalPerspective = true;
    private FilterItem last;

    public DashboardShopAdapter(Context context) {
        super(context, R.layout.dashboard_dropdown_shop_title, R.layout.dropdown_item_new);
    }

    public void init() {
        DropdownMenuNew.ViewHolder<FilterItem> content = getContent();
        if (content == null) {
            throw new RuntimeException("Init must after setAdapter called.");
        }
        SettingItemLayout silName = content.getView(R.id.sil_company);
        silName.setTitle(SpUtils.getCompanyName());
        silName.setChecked(true);

        setOnItemClickListener((adapter, model, position) -> {
            if (last != null && last.getId() == model.getId()) {
                // 重复选择已选中的项
                return;
            }
            last = model;

            // 切换视角
            if (isTotalPerspective) {
                switchToShopPerspective();
            }

            // 将选中的项移到第一个位置
            if (position == 0) {
                return;
            }
            List<FilterItem> data = getData();
            data.remove(position);
            data.add(0, model);
            adapter.notifyItemRangeChanged(0, position + 1);
        });

        silName.setOnClickListener(v -> {
            if (!isTotalPerspective) {
                switchToTotalPerspective();
            }
        });
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
        if (isTotalPerspective) {
            TextView title = getTitle().getView(R.id.dropdown_item_title);
            title.setText(name);
        }
    }

    /**
     * 切换到总部视角
     */
    private void switchToTotalPerspective() {
        isTotalPerspective = true;
        getSelected().clear();
        List<FilterItem> data = getData();
        for (FilterItem item : data) {
            item.setChecked(false);
        }
        notifyDataSetChanged();

        TextView title = getTitle().getView(R.id.dropdown_item_title);
        title.setText(SpUtils.getCompanyName());
        DropdownMenuNew.ViewHolder<FilterItem> content = getContent();
        if (content != null) {
            SettingItemLayout silName = content.getView(R.id.sil_company);
            silName.setChecked(true);
        }
        if (listener != null) {
            listener.onSwitchToTotal();
        }
    }

    /**
     * 切换到门店视角
     */
    private void switchToShopPerspective() {
        isTotalPerspective = false;
        DropdownMenuNew.ViewHolder<FilterItem> content = getContent();
        if (content != null) {
            SettingItemLayout silName = content.getView(R.id.sil_company);
            silName.setChecked(false);
        }
        if (listener != null) {
            listener.onSwitchToShop();
        }
    }

    public void setOnSwitchListener(OnPerspectiveSwitchListener listener) {
        this.listener = listener;
    }

    public interface OnPerspectiveSwitchListener {

        void onSwitchToTotal();

        void onSwitchToShop();
    }

}
