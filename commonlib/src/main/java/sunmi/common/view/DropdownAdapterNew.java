package sunmi.common.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.commonlibrary.R;

import java.util.List;

import sunmi.common.model.FilterItem;

/**
 * @author yinhui
 * @date 18-1-17
 */
public class DropdownAdapterNew extends DropdownMenuNew.Adapter<FilterItem> {

    public DropdownAdapterNew(Context context) {
        super(context, R.layout.dropdown_title_new, R.layout.dropdown_item_new);
    }

    @Override
    protected void setupTitle(@NonNull DropdownMenuNew.ViewHolder<FilterItem> holder, List<FilterItem> models) {
        if (models == null || models.isEmpty()) {
            return;
        }
        FilterItem model = models.get(0);
        TextView title = holder.getView(R.id.dropdown_title);
        title.setText(model.getTitleName());
        title.setSelected(model.getId() != 0);
    }

    @Override
    protected void setupItem(@NonNull DropdownMenuNew.ViewHolder<FilterItem> holder, FilterItem model, int position) {
        SettingItemLayout item = holder.getView(R.id.dropdown_item);
        item.setTitle(model.getItemName());
        item.setChecked(model.isChecked());
    }

}
