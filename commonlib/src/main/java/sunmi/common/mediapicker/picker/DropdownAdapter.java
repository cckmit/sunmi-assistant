package sunmi.common.mediapicker.picker;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.commonlibrary.R;

import sunmi.common.mediapicker.data.model.Album;
import sunmi.common.view.DropdownMenu;

/**
 * @author Jacob
 * @date 18-1-17
 */
public class DropdownAdapter extends DropdownMenu.BaseAdapter<Album> {

    protected DropdownAdapter(Context context) {
        super(context, R.layout.picker_album_dropdown_title, R.layout.picker_album_dropdown_item);
    }

    @Override
    protected DropdownMenu.BaseTitleViewHolder<Album> createTitle(View view) {
        return new TitleHolder(view);
    }

    @Override
    protected DropdownMenu.BaseItemViewHolder<Album> createItem(View view) {
        return new ItemHolder(view, this);
    }

    public static class TitleHolder extends DropdownMenu.BaseTitleViewHolder<Album> {

        TitleHolder(View v) {
            super(v);
        }

        @Override
        public void setUpView(Album model, int position) {
            TextView title = getView(R.id.tv_picker_album_title);
            if (model.getId() == Album.BUCKET_ID_ALL) {
                title.setText("All Media");
            } else {
                title.setText(model.getName());
            }
        }

    }

    public static class ItemHolder extends DropdownMenu.BaseItemViewHolder<Album> {

        ItemHolder(View v, DropdownMenu.BaseAdapter<Album> adapter) {
            super(v, adapter);
        }

        @Override
        public void setUpView(Album model, int position) {
            ImageView thumbnail = getView(R.id.iv_picker_album_thumbnail);
            TextView title = getView(R.id.tv_picker_album_title);
            TextView count = getView(R.id.tv_picker_album_num);
            CheckBox check = getView(R.id.cb_picker_album_check);
            if (model.getCover() == null) {
//                thumbnail.setImageResource(R.drawable.placeholder);
            } else {
                Glide.with(mContext)
                        .load(model.getCover())
                        .into(thumbnail);
            }
            if (model.getId() == Album.BUCKET_ID_ALL) {
                title.setText("All Media");
            } else {
                title.setText(model.getName());
            }
            count.setText(model.getCount());
            check.setVisibility(model.isChecked() ? View.VISIBLE : View.GONE);
        }
    }
}
