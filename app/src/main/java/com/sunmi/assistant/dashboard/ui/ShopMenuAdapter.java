package com.sunmi.assistant.dashboard.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.sunmi.assistant.R;

import java.util.List;

import sunmi.common.constant.CommonConstants;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.FilterItem;
import sunmi.common.notification.BaseNotification;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.DropdownMenuNew;
import sunmi.common.view.SettingItemLayout;

public class ShopMenuAdapter extends DropdownMenuNew.Adapter<FilterItem> {

    private boolean isTotalPerspective = true;
    private FilterItem last;

    public ShopMenuAdapter(Context context) {
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
            SpUtils.setShopId(model.getId());
            SpUtils.setShopName(model.getItemName());

            if (isTotalPerspective) {
                // 切换视角
                SpUtils.setPerspective(CommonConstants.PERSPECTIVE_SHOP);
                BaseNotification.newInstance().postNotificationName(CommonNotifications.perspectiveSwitch);
            } else {
                // 切换门店
                BaseNotification.newInstance().postNotificationName(CommonNotifications.shopSwitched);
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
            last = null;
            if (!isTotalPerspective) {
                getMenu().dismiss(true);
                SpUtils.setPerspective(CommonConstants.PERSPECTIVE_TOTAL);
                BaseNotification.newInstance().postNotificationName(CommonNotifications.perspectiveSwitch);
            }
        });

        switchToTotalPerspective();
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

    public void switchPerspective(int perspective) {
        if (perspective == CommonConstants.PERSPECTIVE_TOTAL) {
            switchToTotalPerspective();
        } else if (perspective == CommonConstants.PERSPECTIVE_SHOP) {
            switchToShopPerspective();
        }
    }

    /**
     * 切换到总部视角
     */
    private void switchToTotalPerspective() {
        LogCat.d("yinhui", "adapter: switch to total.");
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
    }

    /**
     * 切换到门店视角
     */
    private void switchToShopPerspective() {
        LogCat.d("yinhui", "adapter: switch to shop.");
        isTotalPerspective = false;
        DropdownMenuNew.ViewHolder<FilterItem> content = getContent();
        if (content != null) {
            SettingItemLayout silName = content.getView(R.id.sil_company);
            silName.setChecked(false);
        }
    }

}
