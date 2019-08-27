package com.sunmi.ipc.face;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sunmi.ipc.R;
import com.sunmi.ipc.face.contract.FaceUploadContract;
import com.sunmi.ipc.face.model.UploadImage;
import com.sunmi.ipc.face.presenter.FaceUploadPresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.base.recycle.BaseRecyclerAdapter;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.SimpleArrayAdapter;
import sunmi.common.base.recycle.listener.OnViewClickListener;
import sunmi.common.luban.Luban;
import sunmi.common.mediapicker.TakePhoto;
import sunmi.common.mediapicker.TakePhotoAgent;
import sunmi.common.mediapicker.data.model.Result;
import sunmi.common.utils.FileHelper;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.bottompopmenu.BottomPopMenu;
import sunmi.common.view.bottompopmenu.PopItemAction;

import static com.sunmi.ipc.face.contract.FaceUploadContract.FILE_SIZE_1M;

/**
 * @author yinhui
 * @date 2019-08-27
 */
@EActivity(resName = "face_activity_upload_list")
public class FaceUploadActivity extends BaseMvpActivity<FaceUploadPresenter>
        implements FaceUploadContract.View {

    private static final String STATE_IMAGES = "state_images";
    private static final int IMAGE_COUNT_LIMIT = 20;

    @ViewById(resName = "title_bar")
    TitleBarView mTitleBar;
    @ViewById(resName = "rv_face_list")
    RecyclerView mRvFaceList;

    @Extra
    ArrayList<UploadImage> mImages;
    @Extra
    int mRemain;

    List<UploadImage> mValidImages = new ArrayList<>();
    private int mUploadCount;
    private int mCompleteCount;

    private BottomPopMenu mPickerDialog;
    private ImageAdapter mAdapter;
    private TakePhotoAgent mPickerAgent;
    private int mPickerLimit;

    @AfterViews
    void init() {
        initTitle();
        initRecycler();
        mPickerLimit = Math.min(mRemain, IMAGE_COUNT_LIMIT);
        mPickerAgent = TakePhoto.with(this)
                .setTakePhotoListener(new PickerResult())
                .build();
    }

    private void initTitle() {
        mTitleBar.getRightText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });
    }

    private void initRecycler() {
        mAdapter = new ImageAdapter(mImages);
        mAdapter.add(0, new UploadImage());
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        mRvFaceList.setLayoutManager(layoutManager);
        mRvFaceList.addItemDecoration(new ImageItemDecoration());
        mRvFaceList.setAdapter(mAdapter);
    }

    private void takePhoto() {
        mPickerAgent.takePhoto();
    }

    private void pickImage(int limit, Result result) {
        mPickerAgent.setPickLimit(limit);
        mPickerAgent.pickMultiPhotos(result);
    }

    private void upload() {
        mTitleBar.getRightText().setEnabled(false);
        mTitleBar.setRightTextViewColor(R.color.colorText_40);
        List<UploadImage> list = mAdapter.getData();
        if (list.size() > 0 && list.get(0).getState() == UploadImage.STATE_ADD) {
            list.remove(0);
        }
        for (UploadImage image : list) {
            image.setState(UploadImage.STATE_UPLOADING);
        }
        mAdapter.notifyDataSetChanged();
        mUploadCount = list.size();
        mCompleteCount = 0;
        for (UploadImage image : list) {
            compress(image);
        }
    }

    private void save() {
        showLoadingDialog();
        mPresenter.save(mValidImages);
    }

    @Background
    void compress(UploadImage image) {
        try {
            File file = new File(image.getFile());
            while (file.length() > FILE_SIZE_1M) {
                List<File> files = Luban.with(this)
                        .setTargetDir(FileHelper.SDCARD_CACHE_IMAGE_PATH)
                        .load(image.getFile())
                        .get();
                file = files.get(0);
            }
            image.setCompressed(file.getAbsolutePath());
            mPresenter.upload(image);
        } catch (IOException e) {
            e.printStackTrace();
            LogCat.e(TAG, "Compress face image Failed. " + e.getLocalizedMessage());
            uploadFailed(image);
        }
    }

    private void uploadComplete() {
        mTitleBar.setRightTextViewText(R.string.ipc_setting_save);
        if (mValidImages.isEmpty()) {
            mTitleBar.getRightText().setEnabled(false);
            mTitleBar.setRightTextViewColor(R.color.colorText_40);
            pickImage(mPickerLimit, null);
            mAdapter.clear();
        } else {
            mTitleBar.getRightText().setEnabled(true);
            mTitleBar.setRightTextViewColor(R.color.colorText);
            mTitleBar.getRightText().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    save();
                }
            });
        }
    }

    @Override
    public void saveComplete() {
        hideLoadingDialog();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void saveFailed() {
        hideLoadingDialog();
        shortTip(R.string.toast_network_error);
    }

    @Override
    public void uploadSuccess(UploadImage image) {
        mValidImages.add(image);
        image.setState(UploadImage.STATE_SUCCESS);
        mAdapter.notifyDataSetChanged();
        mCompleteCount++;
        if (mCompleteCount >= mUploadCount) {
            uploadComplete();
        }
    }

    @UiThread
    @Override
    public void uploadFailed(UploadImage image) {
        image.setState(UploadImage.STATE_FAILED);
        mAdapter.notifyDataSetChanged();
        mCompleteCount++;
        if (mCompleteCount >= mUploadCount) {
            uploadComplete();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPickerAgent.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPickerAgent.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPickerAgent.onSaveInstanceState(outState);
        ArrayList<UploadImage> list = new ArrayList<>(mAdapter.getData());
        outState.putParcelableArrayList(STATE_IMAGES, list);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPickerAgent.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            ArrayList<UploadImage> list = savedInstanceState.getParcelableArrayList(STATE_IMAGES);
            if (list != null) {
                mAdapter.setData(list);
            }
        }
    }

    private class PickerResult implements TakePhoto.TakePhotoListener {
        @Override
        public void onSuccess(int from, Result result) {
            // TODO:
        }

        @Override
        public void onError(int errorCode, int from, String msg) {
        }

        @Override
        public void onCancel(int from) {
        }
    }

    private class ImageAdapter extends SimpleArrayAdapter<UploadImage> {

        public ImageAdapter(List<UploadImage> data) {
            super(data);
            addOnViewClickListener(R.id.iv_face_item_image, new OnViewClickListener<UploadImage>() {
                @Override
                public void onClick(BaseRecyclerAdapter<UploadImage> adapter, BaseViewHolder<UploadImage> holder,
                                    View v, UploadImage model, int position) {
                    if (model.getState() == UploadImage.STATE_ADD) {
                        if (mPickerDialog == null) {
                            mPickerDialog = new BottomPopMenu.Builder(FaceUploadActivity.this)
                                    .addItemAction(new PopItemAction(R.string.ipc_face_take_photo,
                                            PopItemAction.PopItemStyle.Normal, new PopItemAction.OnClickListener() {
                                        @Override
                                        public void onClick() {
                                            takePhoto();
                                        }
                                    }))
                                    .addItemAction(new PopItemAction(R.string.ipc_face_album_choose,
                                            PopItemAction.PopItemStyle.Normal, new PopItemAction.OnClickListener() {
                                        @Override
                                        public void onClick() {
                                            // TODO:
                                        }
                                    }))
                                    .addItemAction(new PopItemAction(R.string.sm_cancel,
                                            PopItemAction.PopItemStyle.Cancel))
                                    .create();
                        }
                        mPickerDialog.show();
                    }
                }
            });
            addOnViewClickListener(R.id.v_face_item_region, new OnViewClickListener<UploadImage>() {
                @Override
                public void onClick(BaseRecyclerAdapter<UploadImage> adapter, BaseViewHolder<UploadImage> holder,
                                    View v, UploadImage model, int position) {
                    remove(position);
                }
            });
        }

        @Override
        public int getLayoutId() {
            return R.layout.face_item_upload_image;
        }

        @Override
        public void setupView(@NonNull BaseViewHolder<UploadImage> holder, UploadImage model, int position) {
            updateVisible(holder, model.getState());
            ImageView image = holder.getView(R.id.iv_face_item_image);
            Glide.with(holder.getContext()).load(model.getFile()).into(image);
        }

        private void updateVisible(@NonNull BaseViewHolder<UploadImage> holder, int state) {
            ImageView delete = holder.getView(R.id.iv_face_item_delete);
            View region = holder.getView(R.id.v_face_item_region);
            TextView tip = holder.getView(R.id.tv_face_item_tip);
            ProgressBar loading = holder.getView(R.id.pb_face_item_loading);
            switch (state) {
                case UploadImage.STATE_INIT:
                    delete.setVisibility(View.VISIBLE);
                    region.setVisibility(View.VISIBLE);
                    tip.setVisibility(View.GONE);
                    loading.setVisibility(View.GONE);
                    break;
                case UploadImage.STATE_UPLOADING:
                    delete.setVisibility(View.GONE);
                    region.setVisibility(View.GONE);
                    tip.setVisibility(View.GONE);
                    loading.setVisibility(View.VISIBLE);
                    break;
                case UploadImage.STATE_SUCCESS:
                case UploadImage.STATE_ADD:
                    delete.setVisibility(View.GONE);
                    region.setVisibility(View.GONE);
                    tip.setVisibility(View.GONE);
                    loading.setVisibility(View.GONE);
                    break;
                case UploadImage.STATE_FAILED:
                    delete.setVisibility(View.GONE);
                    region.setVisibility(View.GONE);
                    tip.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.GONE);
                    break;
                default:
            }
        }
    }

    private static class ImageItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.set(4, 4, 4, 4);
        }
    }

}
