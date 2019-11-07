package sunmi.common.mediapicker.picker;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.commonlibrary.R;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.base.recycle.BaseCursorAdapter;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.base.recycle.listener.OnViewClickListener;
import sunmi.common.mediapicker.Config;
import sunmi.common.mediapicker.data.model.Album;
import sunmi.common.mediapicker.data.model.Image;
import sunmi.common.mediapicker.data.model.Result;
import sunmi.common.mediapicker.util.Constant;
import sunmi.common.mediapicker.util.ContextWrap;
import sunmi.common.mediapicker.util.PermissionManager;
import sunmi.common.mediapicker.util.Utils;
import sunmi.common.view.DropdownAnimation;
import sunmi.common.view.DropdownMenu;
import sunmi.common.view.TitleBarView;

/**
 * Activity for pick pictures, include picture list & checkbox.
 *
 * @author Jacob
 * @date 17-11-23
 */
public class PickerActivity extends BaseMvpActivity<PickerPresenter>
        implements PickerContract.View {

    private static final String TAG = PickerActivity.class.getSimpleName();

    protected Config mConfig;
    private boolean mIsMultiPicker;
    protected Image mCurrentImage;

    private ConstraintLayout mContent;
    private TitleBarView mTitleBar;
    private View mOverlay;

    private DropdownMenu mDropdownMenu;
    private DropdownAdapter mDropdownAdapter;
    private DropdownAnimation mDropdownAnimator;

    private RecyclerView mRecyclerView;
    protected BaseCursorAdapter<Image> mAdapter;

    private Runnable waitRunnable;

    private PermissionManager.PermissionRequestCallback callback =
            new PermissionManager.PermissionRequestCallback() {
                @Override
                public void onPermissionGranted() {
                    if (waitRunnable != null) {
                        waitRunnable.run();
                    }
                }

                @Override
                public void onPermissionDenied(List<String> permissions) {
                    Log.d(TAG, "Permission denied!");
                    Toast.makeText(getApplicationContext(), "Permission denied!",
                            Toast.LENGTH_SHORT).show();
                    for (String per : permissions) {
                        Log.d(TAG, "Permission: " + per);
                    }
                }
            };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init data & create presenter
        List<Image> selected;
        Album album = null;
        Intent intent = getIntent();
        if (savedInstanceState != null) {
            mConfig = savedInstanceState.getParcelable(Constant.STATE_BUNDLE_CONFIG);
            mIsMultiPicker = savedInstanceState.getBoolean(Constant.STATE_BUNDLE_IS_MULTI, true);
            selected = savedInstanceState.getParcelableArrayList(Constant.STATE_BUNDLE_IMAGES_SELECTED);
            album = savedInstanceState.getParcelable(Constant.STATE_BUNDLE_ALBUM);
        } else {
            mConfig = intent.getParcelableExtra(Constant.INTENT_EXTRA_CONFIG);
            mIsMultiPicker = intent.getBooleanExtra(Constant.INTENT_EXTRA_IS_MULTI, true);
            Result picked = intent.getParcelableExtra(Constant.INTENT_EXTRA_IMAGES);
            selected = (picked == null ? new ArrayList<Image>() : picked.getImages());
        }
        if (mConfig.getThemeId() != 0) {
            setTheme(mConfig.getThemeId());
        }
        mPresenter = new PickerPresenter(this, mConfig, selected);
        mPresenter.setAlbum(album);
        mPresenter.attachView(this);

        setContentView(R.layout.picker_activity);

        // init view
        mContent = findViewById(R.id.layout_picker_content);
        mRecyclerView = findViewById(R.id.rv_picker_list);
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.getRightText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.completePick();
            }
        });

        updateTitleBar();
        initRecycler();

        // init dropdown menu
//        if (mConfig.isEnableAlbumSelect()) {
//            mOverlay = findViewById(R.id.v_picker_overlay);
//            mOverlay.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mDropdownMenu.getPopup().dismiss(true);
//                }
//            });
//            mDropdownAnimator = new DropdownAnimation();
//            mDropdownAdapter = new DropdownAdapter(getApplicationContext());
//            mDropdownAdapter.setOnItemClickListener(new OnAlbumClickListener());
//            mDropdownMenu.setVisibility(View.VISIBLE);
//            mDropdownMenu.setPopupHelper(new CustomPopupHelper());
//            mDropdownMenu.setAdapter(mDropdownAdapter);
//        } else {
//            findViewById(R.id.title_bar).setVisibility(View.VISIBLE);
//        }


        mPresenter.start();
    }

    private void initRecycler() {
        mAdapter = new ImageAdapter();
        ImageType type = new ImageType(mIsMultiPicker);
        mAdapter.register(Image.class, type);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new ImageItemDecoration());
        mRecyclerView.setAdapter(mAdapter);
    }

    private void updateTitleBar() {
        TextView down = mTitleBar.getRightText();
        if (mPresenter.getSelectSize() == 0) {
            down.setEnabled(false);
            down.setTextColor(ContextCompat.getColor(this, R.color.text_caption));
        } else {
            down.setEnabled(true);
            down.setTextColor(ContextCompat.getColor(this, R.color.text_main));
        }
    }

    @Override
    public void showContent() {
        hideLoadingDialog();
    }

    @Override
    public void showLoading() {
        showLoadingDialog();
    }

    @Override
    public void updateAlbums(List<Album> albums, int currentAlbumIndex) {
        mDropdownAdapter.setData(albums, currentAlbumIndex);
    }

    @Override
    public void updateMedia(Cursor cursor) {
        mAdapter.setCursor(cursor);
    }

    @Override
    public void returnResult(Result result) {
        Intent intent = new Intent();
        intent.putExtra(Constant.INTENT_EXTRA_IMAGES, result);
        setResult(RESULT_OK, intent);
        finish();
    }

    protected void takePhoto() {
        ContextWrap context = new ContextWrap(this);
        int result = PermissionManager.checkPermission(context,
                new String[]{Constant.PER_STORAGE, Constant.PER_CAMERA});
        if (result == PermissionManager.STATE_REQUEST) {
            waitRunnable = new Runnable() {
                @Override
                public void run() {
                    takePhoto();
                }
            };
            return;
        }
        try {
            File file = Utils.getNewImage(getApplicationContext(), mConfig.isPhotoSave());
            mCurrentImage = Image.fromPhoto(file);
            Utils.takePhoto(context, file);
        } catch (IOException e) {
            Intent intent = new Intent();
            intent.putExtra(Constant.INTENT_EXTRA_CODE, Constant.ERROR_CODE_CREATE_FAIL);
            intent.putExtra(Constant.INTENT_EXTRA_MESSAGE, e.getMessage());
            setResult(Constant.RESULT_ERROR, intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            if (mConfig.isPhotoSave()) {
                Utils.galleryAddPic(this, mCurrentImage.getUri());
            }
            mCurrentImage.setChecked(true);
            // Return directly from take photo in photo picker.
            mPresenter.updateMedia(mCurrentImage);
            if (mConfig.isEnableCaptureInPickReturn()) {
                mPresenter.addSelectItem(mCurrentImage);
                mPresenter.completePick();
                return;
            }
            mPresenter.addSelectItem(mCurrentImage);
//            startPreviewActivity(mCurrentImage, 0);

            // Add photo into album list.
            Album album = mPresenter.getAlbum();
            String path = new File(mCurrentImage.getPath()).getParent();
            if (album.getId() == Album.BUCKET_ID_ALL || TextUtils.equals(album.getPath(), path)) {
                mAdapter.add(album.isContainCapture() ? 1 : 0, mCurrentImage);
            }
        }
    }

//    @Override
//    public void onBackPressed() {
//        if (mDropdownMenu != null && mDropdownMenu.getPopup().isShowing()) {
//            mDropdownMenu.getPopup().dismiss(true);
//        } else {
//            super.onBackPressed();
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults, callback);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constant.STATE_BUNDLE_CONFIG, mConfig);
        outState.putBoolean(Constant.STATE_BUNDLE_IS_MULTI, mIsMultiPicker);
        outState.putParcelable(Constant.STATE_BUNDLE_ALBUM, mPresenter.getAlbum());
        outState.putParcelable(Constant.STATE_BUNDLE_IMAGE, mCurrentImage);
        outState.putParcelableArrayList(Constant.STATE_BUNDLE_IMAGES_SELECTED, mPresenter.getSelectItems());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrentImage = savedInstanceState.getParcelable(Constant.STATE_BUNDLE_IMAGE);
    }

    private static class ImageAdapter extends BaseCursorAdapter<Image> {

        @Override
        public Image fromCursor(Cursor current) {
            return Image.fromCursor(current);
        }
    }

    private class ImageType extends ItemType<Image, BaseViewHolder<Image>> {

        private boolean isMultiPicker;

        public ImageType(final boolean isMultiPicker) {
            this.isMultiPicker = isMultiPicker;
            addOnViewClickListener(R.id.v_picker_item_check_region, new OnViewClickListener<Image>() {
                @Override
                public void onClick(BaseViewHolder<Image> holder, Image model, int position) {
                    CheckBox check = holder.getView(R.id.cb_picker_item_check);
                    if (!model.isChecked()
                            && mConfig.getPickLimit() != 0
                            && mPresenter.getSelectSize() >= mConfig.getPickLimit()) {
                        Log.w(TAG, "Image selection reached the limit! Choose up to "
                                + mConfig.getPickLimit() + " pictures.");
                        shortTip(R.string.picker_toast_limit);
                    } else {
                        model.setChecked(!model.isChecked());
                        check.setChecked(model.isChecked());
                        if (model.isChecked()) {
                            mPresenter.addSelectItem(model);
                        } else {
                            mPresenter.removeSelectItem(model);
                        }
                        updateTitleBar();
                    }
                }
            });
            addOnViewClickListener(R.id.iv_picker_item_image, new OnViewClickListener<Image>() {
                @Override
                public void onClick(BaseViewHolder<Image> holder, Image model, int position) {
                    if (!isMultiPicker) {
                        model.setChecked(true);
                        mPresenter.clearSelectItem();
                        mPresenter.addSelectItem(model);
                        mPresenter.completePick();
                    }
                }
            });
        }

        @Override
        public int getLayoutId(int type) {
            return R.layout.picker_item_image_check;
        }

        @NonNull
        @Override
        public BaseViewHolder<Image> onCreateViewHolder(@NonNull View view, @NonNull ItemType<Image, BaseViewHolder<Image>> type) {
            BaseViewHolder<Image> holder = super.onCreateViewHolder(view, type);
            if (!isMultiPicker) {
                holder.getView(R.id.cb_picker_item_check).setVisibility(View.GONE);
                holder.getView(R.id.v_picker_item_check_region).setVisibility(View.GONE);
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder<Image> holder, Image model, int position) {
            model.setChecked(mPresenter.containSelectItem(model));
            ImageView image = holder.getView(R.id.iv_picker_item_image);
            CheckBox check = holder.getView(R.id.cb_picker_item_check);
            check.setChecked(model.isChecked());
            Glide.with(holder.getContext()).load(model.getUri()).into(image);
        }
    }

    private static class ImageItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NotNull Rect outRect, @NotNull View view,
                                   @NotNull RecyclerView parent, @NotNull RecyclerView.State state) {
            outRect.set(4, 4, 4, 4);
        }
    }

    private class OnAlbumClickListener implements DropdownMenu.OnItemClickListener<Album> {

        @Override
        public void onItemSelected(DropdownMenu.BaseAdapter<Album> adapter, Album model, int position) {
            mPresenter.getAlbum().setChecked(false);
            mPresenter.setAlbum(model);
            model.setChecked(true);
            mPresenter.loadMedia(model.getId());
            adapter.notifyDataSetChanged();
        }
    }
}
