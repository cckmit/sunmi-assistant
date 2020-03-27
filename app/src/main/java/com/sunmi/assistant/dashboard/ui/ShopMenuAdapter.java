package com.sunmi.assistant.dashboard.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.utils.AppConstants;

import java.util.Collection;
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

    private static final String TAG = ShopMenuAdapter.class.getSimpleName();

    private int authority;
    private int perspective = CommonConstants.PERSPECTIVE_NONE;
    private int shopId;
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

            if (perspective == CommonConstants.PERSPECTIVE_TOTAL) {
                // 切换视角
                SpUtils.setPerspective(CommonConstants.PERSPECTIVE_SHOP);
                BaseNotification.newInstance().postNotificationName(CommonNotifications.perspectiveSwitch);
            } else if (perspective == CommonConstants.PERSPECTIVE_SHOP) {
                // 切换门店
                BaseNotification.newInstance().postNotificationName(CommonNotifications.shopSwitched);
            }
        });

        silName.setOnClickListener(v -> {
            if (this.authority != AppConstants.ACCOUNT_AUTH_COMPANY) {
                return;
            }
            last = null;
            if (perspective == CommonConstants.PERSPECTIVE_SHOP) {
                getMenu().dismiss(true);
                SpUtils.setPerspective(CommonConstants.PERSPECTIVE_TOTAL);
                BaseNotification.newInstance().postNotificationName(CommonNotifications.perspectiveSwitch);
            }
        });

        switchToTotalPerspective();
    }

    @Override
    public void setData(@NonNull Collection<? extends FilterItem> items) {
        super.setData(items);
        if (perspective != CommonConstants.PERSPECTIVE_NONE) {
            switchPerspective(perspective, shopId);
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

    public void setAuthority(int authority) {
        this.authority = authority;
        DropdownMenuNew.ViewHolder<FilterItem> content = getContent();
        if (content != null) {
            SettingItemLayout silName = content.getView(R.id.sil_company);
            silName.setEnabled(authority == AppConstants.ACCOUNT_AUTH_COMPANY);
        }
    }

    public void setCompanyName(String name) {
        DropdownMenuNew.ViewHolder<FilterItem> content = getContent();
        if (content != null) {
            SettingItemLayout silName = content.getView(R.id.sil_company);
            silName.setTitle(name);
        }
        if (perspective == CommonConstants.PERSPECTIVE_TOTAL) {
            TextView title = getTitle().getView(R.id.dropdown_item_title);
            title.setText(name);
        }
    }

    public void switchPerspective(int perspective, int shopId) {
        if (perspective == CommonConstants.PERSPECTIVE_TOTAL) {
            switchToTotalPerspective();
        } else if (perspective == CommonConstants.PERSPECTIVE_SHOP) {
            switchToShopPerspective(shopId);
        } else {
            LogCat.e(TAG, "Switch perspective:" + perspective + " is Invalid.");
        }
    }

    public void switchShop(int shopId) {
        updateShopSelected(shopId);
    }

    /**
     * 切换到总部视角
     */
    private void switchToTotalPerspective() {
        LogCat.i(TAG, "Shop menu adapter: switch to company.");
        this.perspective = CommonConstants.PERSPECTIVE_TOTAL;
        this.shopId = -1;
        updateShopSelected(-1);
        updateCompanySelected(true);
        TextView title = getTitle().getView(R.id.dropdown_item_title);
        title.setText(SpUtils.getCompanyName());
    }

    /**
     * 切换到门店视角
     *
     * @param shopId 门店ID
     */
    private void switchToShopPerspective(int shopId) {
        LogCat.i(TAG, "Shop menu adapter: switch to shop.");
        LogCat.d(TAG, "Shop id : " + shopId);
        this.perspective = CommonConstants.PERSPECTIVE_SHOP;
        this.shopId = shopId;
        updateShopSelected(shopId);
        updateCompanySelected(false);
    }

    private void updateCompanySelected(boolean isSelected) {
        DropdownMenuNew.ViewHolder<FilterItem> content = getContent();
        if (content != null) {
            SettingItemLayout silName = content.getView(R.id.sil_company);
            silName.setChecked(isSelected);
        }
    }

    private void updateShopSelected(int shopId) {
        List<FilterItem> list = getData();
        int index = -1;
        for (int i = 0, size = list.size(); i < size; i++) {
            FilterItem item = list.get(i);
            item.setChecked(false);
            if (item.getId() == shopId) {
                index = i;
                break;
            }
        }
        if (index < 0) {
            getSelected().clear();
        } else if (index == 0) {
            setSelected(0);
        } else {
            // 将选中的项移到第一个位置
            FilterItem target = list.remove(index);
            list.add(0, target);
            setSelected(0);
        }
        notifyDataSetChanged();
    }

}
