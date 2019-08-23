package sunmi.common.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.commonlibrary.R;

import sunmi.common.model.FilterItem;

/**
 *
 * @author yinhui
 * @date 18-1-17
 */
public class DropdownAdapter extends DropdownMenu.BaseAdapter<FilterItem> {

    public DropdownAdapter(Context context) {
        super(context, R.layout.dropdown_title, R.layout.dropdown_item);
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
            TextView title = getView(R.id.dropdown_item_title);
            title.setText(model.getTitleName());
            Drawable drawable = title.getCompoundDrawablesRelative()[2].mutate();
            if (model.getId() != -1) {
                title.setTextColor(ContextCompat.getColor(mContext, R.color.color_F35000));
                drawable.setTint(ContextCompat.getColor(mContext, R.color.color_F35000));
            } else {
                title.setTextColor(ContextCompat.getColor(mContext, R.color.color_333338));
                drawable.setTint(ContextCompat.getColor(mContext, R.color.color_333338));
            }
        }
    }

    public static class ItemHolder extends DropdownMenu.BaseItemViewHolder<FilterItem> {

        ItemHolder(View v, DropdownMenu.BaseAdapter<FilterItem> adapter) {
            super(v, adapter);
        }

        @Override
        public void setUpView(FilterItem model, int position) {
            TextView name = getView(R.id.dropdown_item_name);
            name.setText(model.getItemName());
            name.setTextColor(model.isChecked() ? ContextCompat.getColor(mContext, R.color.color_FF6000)
                    : ContextCompat.getColor(mContext, R.color.color_333338));
            getView(R.id.dropdown_item_checkbox).setVisibility(model.isChecked() ?
                    View.VISIBLE : View.INVISIBLE);
        }
    }
}
