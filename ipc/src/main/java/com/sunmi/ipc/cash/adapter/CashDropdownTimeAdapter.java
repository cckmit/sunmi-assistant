package com.sunmi.ipc.cash.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.model.DropdownTime;

import java.util.List;

import sunmi.common.view.DropdownMenuNew;

/**
 * @author yinhui
 * @date 2019-12-05
 */
public class CashDropdownTimeAdapter extends DropdownMenuNew.Adapter<DropdownTime> {

    public CashDropdownTimeAdapter(Context context) {
        super(context, R.layout.dropdown_title_new, R.layout.cash_video_dropdown_time_item);
    }

    @Override
    protected void setupTitle(@NonNull DropdownMenuNew.ViewHolder<DropdownTime> holder,
                              List<DropdownTime> models) {
        if (models == null || models.isEmpty()) {
            return;
        }
        DropdownTime model = models.get(0);
        TextView title = holder.getView(R.id.dropdown_title);
        title.setText(model.getTitle());
        title.setSelected(model.getId() != 0);
    }

    @Override
    protected void setupContent(@NonNull DropdownMenuNew.ViewHolder<DropdownTime> holder,
                                List<DropdownTime> models) {
        // TODO:
    }

    @Override
    protected void setupItem(@NonNull DropdownMenuNew.ViewHolder<DropdownTime> holder,
                             DropdownTime model, int position) {
        TextView item = holder.getView(R.id.tv_item);
        item.setText(model.getItemName());
        item.setSelected(model.isChecked());
    }
}
