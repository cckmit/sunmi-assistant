package sunmi.common.mediapicker.picker;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.mediapicker.Config;
import sunmi.common.mediapicker.data.MediaRepository;
import sunmi.common.mediapicker.data.QueryConfig;
import sunmi.common.mediapicker.data.loader.MediaItemLoader;
import sunmi.common.mediapicker.data.model.Album;
import sunmi.common.mediapicker.data.model.Image;
import sunmi.common.mediapicker.data.model.Result;

/**
 * @author Jacob
 * @date 17-11-28
 */
public class PickerPresenter extends BasePresenter<PickerContract.View> implements PickerContract.Presenter {

    private static final String TAG = "PickerPresenter";

    private MediaRepository mRepo;

    private Config mConfig;
    private List<Album> mAlbums;
    private Album mAlbum;
    private Image mLastPhoto;

    private LinkedHashSet<Image> mSelectImages = new LinkedHashSet<>(9);

    private LoadAlbumCallbacks mAlbumCallbacks = new LoadAlbumCallbacks();
    private LoadMediaCallbacks mMediaCallbacks = new LoadMediaCallbacks(false);
    private LoadMediaCallbacks mMediaCallbacksWithCapture = new LoadMediaCallbacks(true);

    PickerPresenter(FragmentActivity activity, Config config,
                    List<Image> selected) {
        mConfig = config;
        mRepo = new MediaRepository(activity, config.makeQueryConfig());
        setSelectItems(selected);
    }

    @Override
    public void start() {
        if (isViewAttached()) {
            mView.showLoading();
        }
        if (mConfig.isEnableAlbumSelect()) {
            mRepo.loadAlbums(mAlbumCallbacks);
        } else {
            mRepo.loadMedia(Album.BUCKET_ID_ALL,
                    mConfig.isEnableCapture() ? mMediaCallbacksWithCapture : mMediaCallbacks);
        }
    }

    @Override
    public void loadMedia(long bucketId) {
        if (isViewAttached()) {
            mView.showLoading();
        }
        mRepo.loadMedia(bucketId, mAlbum.isContainCapture() ? mMediaCallbacksWithCapture : mMediaCallbacks);
    }

    @Override
    public void updateMedia(Image media) {
        mLastPhoto = media;
        if (!mConfig.isEnableAlbumSelect()) {
            return;
        }
        String path = new File(media.getPath()).getParent();
        long bucketId = Album.BUCKET_ID_ALL;
        for (Album album : mAlbums) {
            if (album.getId() == Album.BUCKET_ID_ALL) {
                continue;
            }
            if (TextUtils.equals(path, album.getPath())) {
                bucketId = album.getId();
                break;
            }
        }
        if (bucketId == Album.BUCKET_ID_ALL) {
            // Create new album for photo.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRepo.loadAlbums(new LoadAlbumCallbacks());
                }
            }, 3000);
        }
    }

    @Override
    public Album getAlbum() {
        return mAlbum;
    }

    @Override
    public void setAlbum(Album album) {
        this.mAlbum = album;
    }

    @Override
    public int getSelectSize() {
        return mSelectImages.size();
    }

    @Override
    public ArrayList<Image> getSelectItems() {
        return new ArrayList<>(mSelectImages);
    }

    @Override
    public void setSelectItems(List<Image> items) {
        mSelectImages.clear();
        mSelectImages.addAll(items);
    }

    @Override
    public boolean containSelectItem(Image item) {
        return mSelectImages.contains(item);
    }

    @Override
    public void addSelectItem(Image item) {
        mSelectImages.add(item);
    }

    @Override
    public void removeSelectItem(Image item) {
        mSelectImages.remove(item);
    }

    @Override
    public void clearSelectItem() {
        mSelectImages.clear();
    }

    @Override
    public void completePick() {
        if (isViewAttached()) {
            mView.returnResult(new Result(mSelectImages));
        }
    }

    private boolean isInAlbum(Image image, Album album) {
        String path = new File(image.getPath()).getParent();
        return album.getId() == Album.BUCKET_ID_ALL || TextUtils.equals(path, album.getPath());
    }

    private class LoadAlbumCallbacks implements MediaRepository.AlbumCallbacks {

        @Override
        public void onAlbumLoaded(List<Album> albums) {
            if (albums == null || albums.isEmpty()) {
                // TODO: Show empty message.
                if (isViewAttached()) {
                    mView.showContent();
                }
                return;
            }
            mAlbums = albums;
            int index = 0;
            if (mAlbum == null) {
                mAlbum = mAlbums.get(0);
            } else {
                index = mAlbums.indexOf(mAlbum);
            }
            if (isViewAttached()) {
                mView.updateAlbums(mAlbums, index);
            }
        }
    }

    private class LoadMediaCallbacks implements MediaRepository.MediaCallbacks {
        private final boolean mContainCapture;

        LoadMediaCallbacks(boolean containCapture) {
            this.mContainCapture = containCapture;
        }

        @Override
        public void onMediaLoaded(long bucketId, Cursor cursor) {
            if (!mConfig.isEnableAlbumSelect()) {
                if (!cursor.moveToFirst()) {
                    mAlbum = Album.fromAll(null, 0);
                } else {
                    String coverAll = cursor.getString(cursor.getColumnIndexOrThrow(QueryConfig.COLUMN_PATH));
                    mAlbum = Album.fromAll(coverAll, cursor.getCount());
                }
                mAlbum.setContainCapture(mContainCapture);
            }
            if (mLastPhoto != null && isInAlbum(mLastPhoto, mAlbum)) {
                cursor.moveToFirst();
                Image image = Image.fromCursor(cursor);
                if (!image.equals(mLastPhoto)) {
                    cursor = new MergeCursor(new Cursor[]{mLastPhoto.toCursor(), cursor});
                }
            }
            if (mContainCapture) {
                MatrixCursor dummy = new MatrixCursor(MediaItemLoader.PROJECTION);
                dummy.addRow(new Object[]{
                        QueryConfig.ITEM_CAPTURE_ID,
                        QueryConfig.ITEM_CAPTURE_NAME,
                        "", 0, "", 0, 0});
                cursor = new MergeCursor(new Cursor[]{dummy, cursor});
            }
            if (isViewAttached()) {
                mView.showContent();
                mView.updateMedia(cursor);
            }
        }

        @Override
        public void onMediaReset(long bucketId) {
            if (isViewAttached()) {
                mView.showContent();
                mView.updateMedia(null);
            }
        }
    }

}
