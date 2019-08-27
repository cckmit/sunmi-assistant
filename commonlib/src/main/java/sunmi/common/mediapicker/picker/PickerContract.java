package sunmi.common.mediapicker.picker;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseView;
import sunmi.common.mediapicker.data.model.Album;
import sunmi.common.mediapicker.data.model.Image;
import sunmi.common.mediapicker.data.model.Result;

/**
 * Created by Jacob on 17-11-28.
 */

public interface PickerContract {

    interface View extends BaseView {

        void showContent();

        void showLoading();

        void updateAlbums(List<Album> albums, int currentAlbumIndex);

        void updateMedia(Cursor cursor);

        void returnResult(Result result);
    }

    interface Presenter {

        void start();

        void loadMedia(long bucketId);

        void updateMedia(Image media);

        Album getAlbum();

        void setAlbum(Album album);

        int getSelectSize();

        ArrayList<Image> getSelectItems();

        void setSelectItems(List<Image> items);

        boolean containSelectItem(Image item);

        void addSelectItem(Image item);

        void removeSelectItem(Image item);

        void clearSelectItem();

        void completePick();
    }
}
