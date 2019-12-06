package com.sunmi.ipc.face;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.sunmi.ipc.R;
import com.sunmi.ipc.face.contract.FaceListContract;
import com.sunmi.ipc.face.model.Face;
import com.sunmi.ipc.face.model.FaceGroup;
import com.sunmi.ipc.face.model.UploadImage;
import com.sunmi.ipc.face.presenter.FaceListPresenter;
import com.sunmi.ipc.face.util.BottomAnimation;
import com.sunmi.ipc.face.util.Constants;
import com.sunmi.ipc.face.util.GlideRoundCrop;
import com.sunmi.ipc.face.util.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import sunmi.common.base.BaseMvpActivity;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.SimpleArrayAdapter;
import sunmi.common.base.recycle.listener.OnViewClickListener;
import sunmi.common.mediapicker.TakePhoto;
import sunmi.common.mediapicker.TakePhotoAgent;
import sunmi.common.mediapicker.data.model.Image;
import sunmi.common.mediapicker.data.model.Result;
import sunmi.common.model.FilterItem;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.ClearableEditText;
import sunmi.common.view.DropdownAdapter;
import sunmi.common.view.DropdownAnimation;
import sunmi.common.view.DropdownMenu;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.bottompopmenu.BottomPopMenu;
import sunmi.common.view.bottompopmenu.PopItemAction;
import sunmi.common.view.dialog.BottomListDialog;
import sunmi.common.view.dialog.CommonDialog;

/**
 * @author yinhui
 * @date 2019-08-20
 */
@EActivity(resName = "face_activity_list")
public class FaceListActivity extends BaseMvpActivity<FaceListPresenter>
        implements FaceListContract.View, BGARefreshLayout.BGARefreshLayoutDelegate {

    private static final int REQUEST_CODE_UPLOAD = 100;
    private static final int REQUEST_CODE_DETAIL = 101;
    private static final int STATE_NORMAL = 0;
    private static final int STATE_CHOOSE = 1;

    @ViewById(resName = "face_content")
    ConstraintLayout mContent;
    @ViewById(resName = "title_bar")
    TitleBarView mTitleBar;
    @ViewById(resName = "cet_face_search")
    ClearableEditText mEtSearch;
    @ViewById(resName = "tv_face_search_hint")
    TextView mTvSearchHint;
    @ViewById(resName = "dm_face_filter_gender")
    DropdownMenu mDmFilterGender;
    @ViewById(resName = "dm_face_filter_age")
    DropdownMenu mDmFilterAge;
    @ViewById(resName = "v_face_overlay")
    View mOverlay;
    @ViewById(resName = "refresh_face_list")
    BGARefreshLayout mRefreshLayout;
    @ViewById(resName = "rv_face_list")
    RecyclerView mRvFaceList;
    @ViewById(resName = "layout_face_selected")
    ConstraintLayout mLayoutSelected;
    @ViewById(resName = "rv_face_selected_list")
    RecyclerView mRvSelectedList;
    @ViewById(resName = "tv_face_selected_tip")
    TextView mTvSelectedTip;
    @ViewById(resName = "tv_face_selected_move")
    TextView mTvSelectedMove;
    @ViewById(resName = "tv_face_selected_delete")
    TextView mTvSelectedDelete;

    @ViewById(resName = "layout_network_error")
    View mLayoutError;

    @Extra
    int mShopId;
    @Extra
    FaceGroup mFaceGroup;

    private int mState = STATE_NORMAL;

    private SimpleArrayAdapter<Face> mAdapter;
    private SimpleArrayAdapter<Face> mAdapterSelected;
    private FaceGroupDialogAdapter mAdapterFaceGroup = new FaceGroupDialogAdapter();

    private BottomAnimation mBottomAnimator = new BottomAnimation();
    private DropdownAnimation mDropdownAnimator = new DropdownAnimation();
    private DropdownAdapter mFilterAdapterGender;
    private DropdownAdapter mFilterAdapterAge;

    private FilterItem mFilterGenderCurrent;
    private FilterItem mFilterAgeCurrent;

    private Dialog mDeleteDialog;
    private Dialog mMoveDialog;
    private Dialog mUploadDialog;

    private BottomPopMenu mPickerDialog;
    private GlideRoundCrop mRoundTransform;

    private TakePhotoAgent mPickerAgent;

    private boolean mMovePending = false;

    @AfterViews
    void init() {
        initViews();
        initFilters();
        initRecyclerView();
        updateSelectedLayout(false);
        mPresenter = new FaceListPresenter(this, mShopId, mFaceGroup);
        mPresenter.attachView(this);
        mPresenter.init();
        mPickerAgent = TakePhoto.with(this)
                .setTakePhotoListener(new PickerResult())
                .build();
        mRoundTransform = new GlideRoundCrop((int) getResources().getDimension(R.dimen.dp_6));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initViews() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        mTitleBar.getLeftTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchStateTo(STATE_NORMAL);
            }
        });
        mEtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String text = mEtSearch.getText() == null ? null : mEtSearch.getText().toString().trim();
                mTvSearchHint.setVisibility(hasFocus || !TextUtils.isEmpty(text) ? View.INVISIBLE : View.VISIBLE);
            }
        });
        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.filterName(s.toString().trim());
            }
        });
        mOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideDropdownMenu(false);
            }
        });
        mRefreshLayout.setDelegate(this);
        mRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(this, true));
        mRefreshLayout.setPullDownRefreshEnable(true);
        mRefreshLayout.setIsShowLoadingMoreView(true);
        updateTitle();
    }

    private void initFilters() {
        CustomPopupHelper helper = new CustomPopupHelper();
        mFilterAdapterGender = new DropdownAdapter(this);
        mDmFilterGender.setLayoutManager(new FilterMenuLayoutManager(this));
        mDmFilterGender.setPopupHelper(helper);
        mDmFilterGender.setAdapter(mFilterAdapterGender);
        mFilterAdapterAge = new DropdownAdapter(this);
        mDmFilterAge.setLayoutManager(new FilterMenuLayoutManager(this));
        mDmFilterAge.setPopupHelper(helper);
        mDmFilterAge.setAdapter(mFilterAdapterAge);
    }

    private void initRecyclerView() {
        mAdapter = new FaceListAdapter();
        mAdapterSelected = new FaceSelectedListAdapter();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        mRvFaceList.setLayoutManager(layoutManager);
        mRvFaceList.addItemDecoration(new ImageItemDecoration());
        mRvFaceList.setAdapter(mAdapter);
        mRvSelectedList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        mRvSelectedList.setAdapter(mAdapterSelected);
    }

    private void switchStateTo(int state) {
        if (mState == state) {
            return;
        }
        mState = state;
        updateStateView();
    }

    private void updateStateView() {
        updateTitle();
        updateAddIcon();
        updateSelectedLayout(false);
        hideDropdownMenu(true);
        if (mState == STATE_NORMAL) {
            final List<Face> list = mAdapter.getData();
            for (Face face : list) {
                face.setChecked(false);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void updateTitle() {
        boolean isChoose = mState == STATE_CHOOSE;
        mTitleBar.setLeftTextViewShow(isChoose);
        if (isChoose) {
            mTitleBar.setAppTitle(R.string.ipc_face_list_title_select);
            mTitleBar.setRightTextViewText(R.string.ipc_face_select_all);
            mTitleBar.getRightText().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTvSelectedTip.setVisibility(View.GONE);
                    updateBtnEnable(true);
                    List<Face> list = mAdapter.getData();
                    for (Face face : list) {
                        face.setChecked(true);
                    }
                    mAdapter.notifyDataSetChanged();
                    mAdapterSelected.setData(list);
                    mRvSelectedList.scrollToPosition(list.size() - 1);
                    mTitleBar.setAppTitle(getString(R.string.ipc_face_list_title_select_num, list.size()));
                }
            });
        } else {
            mTitleBar.setAppTitle(R.string.ipc_face_group_manage);
            mTitleBar.setRightTextViewText(R.string.ipc_face_select);
            mTitleBar.getRightText().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchStateTo(STATE_CHOOSE);
                }
            });
        }
    }

    private void updateAddIcon() {
        List<Face> list = mAdapter.getData();
        if (list.isEmpty()) {
            return;
        }
        if (mState == STATE_NORMAL && !list.get(0).isAddIcon()) {
            mAdapter.add(0, Face.createCamera());
        } else if (mState == STATE_CHOOSE && list.get(0).isAddIcon()) {
            mAdapter.remove(0);
        }
    }

    private void updateSelectedLayout(boolean animated) {
        boolean isChoose = mState == STATE_CHOOSE;
        if (isChoose) {
            mBottomAnimator.startAnimationToShow(animated, mLayoutSelected);
        } else {
            mAdapterSelected.clear();
            mTvSelectedTip.setVisibility(View.VISIBLE);
            updateBtnEnable(false);
            mBottomAnimator.startAnimationToDismiss(animated, mLayoutSelected);
        }
    }

    private boolean hideDropdownMenu(boolean animated) {
        boolean result = false;
        if (mDmFilterGender.getPopup().isShowing()) {
            mDmFilterGender.getPopup().dismiss(animated);
            result = true;
        }
        if (mDmFilterAge.getPopup().isShowing()) {
            mDmFilterAge.getPopup().dismiss(animated);
            result = true;
        }
        return result;
    }

    private void updateBtnEnable(boolean enable) {
        mTvSelectedMove.setEnabled(enable);
        mTvSelectedMove.setTextColor(enable ? ContextCompat.getColor(this, R.color.text_main)
                : ContextCompat.getColor(this, R.color.text_caption));
        mTvSelectedDelete.setEnabled(enable);
        mTvSelectedDelete.setTextColor(enable ? ContextCompat.getColor(this, R.color.text_main)
                : ContextCompat.getColor(this, R.color.text_caption));
    }

    @Click(resName = "tv_face_selected_move")
    void clickMove() {
        if (mAdapterFaceGroup == null) {
            showLoadingDialog();
            mPresenter.loadGroup();
            mMovePending = true;
            return;
        }
        if (mMoveDialog == null) {
            mMoveDialog = new BottomListDialog.Builder<FaceGroup>(this)
                    .setAdapter(mAdapterFaceGroup)
                    .setOnItemClickListener(new BottomListDialog.OnItemClickListener<FaceGroup>() {
                        @Override
                        public void onClick(DialogInterface dialog, BaseViewHolder holder, FaceGroup model) {
                            List<Face> selected = mAdapterSelected.getData();
                            if (selected.size() > model.getCapacity() - model.getCount()) {
                                shortTip(R.string.ipc_face_error_move);
                            } else {
                                mPresenter.move(selected, model);
                            }
                        }
                    })
                    .setTitle(R.string.ipc_face_move_title)
                    .setBtnBottom(true)
                    .setCancelButton(R.string.sm_cancel)
                    .create();
        }
        mAdapterFaceGroup.setSelectedCount(mAdapterSelected.getItemCount());
        mMoveDialog.show();
    }

    @Click(resName = "tv_face_selected_delete")
    void clickDelete() {
        if (mDeleteDialog == null) {
            mDeleteDialog = new CommonDialog.Builder(this)
                    .setTitle(R.string.ipc_face_tip_delete)
                    .setConfirmButton(R.string.ipc_setting_delete, R.color.common_orange,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    List<Face> list = mAdapterSelected.getData();
                                    mPresenter.delete(list);
                                }
                            })
                    .setCancelButton(R.string.sm_cancel)
                    .create();
        }
        mDeleteDialog.show();
    }

    @Click(resName = "btn_refresh")
    void refresh() {
        mPresenter.init();
    }

    @Override
    public void updateGroup(FaceGroup group) {
        mFaceGroup = group;
        Intent intent = getIntent();
        intent.putExtra(Constants.EXTRA_UPDATE_COUNT, mFaceGroup.getCount());
        setResult(RESULT_OK, intent);
    }

    @Override
    public void updateGroupList(List<FaceGroup> list) {
        if (list != null) {
            mAdapterFaceGroup.setData(list);
        }
        if (mMovePending) {
            mMovePending = false;
            hideLoadingDialog();
            if (mAdapterFaceGroup.getItemCount() == 0) {
                shortTip(R.string.toast_network_error);
            } else {
                clickMove();
            }
        }
    }

    @Override
    public void updateFilter(List<FilterItem> gender, List<FilterItem> age) {
        int selection = -1;
        for (int i = 0, size = gender.size(); i < size; i++) {
            if (gender.get(i).isChecked()) {
                selection = i;
                mFilterGenderCurrent = gender.get(i);
            }
        }
        mFilterAdapterGender.setData(gender, selection);
        mFilterAdapterGender.setOnItemClickListener(new OnFilterItemClickListener(true));
        selection = -1;
        for (int i = 0, size = age.size(); i < size; i++) {
            if (age.get(i).isChecked()) {
                selection = i;
                mFilterAgeCurrent = age.get(i);
            }
        }
        mFilterAdapterAge.setData(age, selection);
        mFilterAdapterAge.setOnItemClickListener(new OnFilterItemClickListener(false));
    }

    @Override
    public void updateList(List<Face> list, boolean hasMore) {
        mLayoutError.setVisibility(View.GONE);
        if (list.isEmpty()) {
            mTitleBar.setRightTextViewColor(R.color.text_caption);
            mTitleBar.setRightTextViewEnable(false);
        } else {
            mTitleBar.setRightTextViewColor(R.color.text_main);
            mTitleBar.setRightTextViewEnable(true);
        }
        if (mState == STATE_NORMAL) {
            list.add(0, Face.createCamera());
        }
        List<Face> selected = mAdapterSelected.getData();
        if (selected.size() > 0) {
            for (Face face : list) {
                if (selected.contains(face)) {
                    face.setChecked(true);
                }
            }
        }
        mAdapter.setData(list);
        mRefreshLayout.endRefreshing();
        hideLoadingDialog();
    }

    @Override
    public void addList(List<Face> list, boolean hasMore) {
        List<Face> selected = mAdapterSelected.getData();
        if (selected.size() > 0) {
            for (Face face : list) {
                if (selected.contains(face)) {
                    face.setChecked(true);
                }
            }
        }
        mAdapter.add(list);
        mRefreshLayout.endLoadingMore();
        hideLoadingDialog();
    }

    @Override
    public void getDataFailed() {
        hideLoadingDialog();
        if (mAdapter.getItemCount() == 0) {
            mLayoutError.setVisibility(View.VISIBLE);
        } else {
            mLayoutError.setVisibility(View.GONE);
            shortTip(R.string.toast_network_error);
        }
    }

    @Override
    public void uploadSuccess() {
        mUploadDialog.dismiss();
        resetView();
        new CommonDialog.Builder(context)
                .setMessage(R.string.ipc_face_tip_album_upload_success)
                .setMessageDrawable(0, R.mipmap.face_ic_ok, 0, 0)
                .setMessageDrawablePadding(R.dimen.dp_8)
                .setConfirmButton(R.string.str_confirm)
                .create().show();
    }

    @Override
    public void uploadFailed(final int code, String file) {
        int size = (int) getResources().getDimension(R.dimen.dp_90);
        Glide.with(FaceListActivity.this)
                .load(file)
                .apply(RequestOptions.bitmapTransform(mRoundTransform))
                .into(new CustomTarget<Drawable>(size, size) {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource,
                                                @Nullable Transition<? super Drawable> transition) {
                        mUploadDialog.dismiss();
                        new CommonDialog.Builder(context)
                                .setTitle(R.string.ipc_face_error_upload)
                                .setMessage(code == 5526 ? R.string.ipc_face_error_photo
                                        : R.string.ipc_face_error_photo_network)
                                .setMessageDrawablePadding(R.dimen.dp_12)
                                .setMessageDrawable(null, null, null, resource)
                                .setCancelButton(R.string.sm_cancel)
                                .setConfirmButton(R.string.ipc_face_tack_photo_again, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mPickerAgent.takePhoto();
                                    }
                                })
                                .create().show();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    @Override
    public void resetView() {
        switchStateTo(STATE_NORMAL);
        mEtSearch.setText("");
        mEtSearch.clearFocus();
        if (mFilterAdapterGender.getItemCount() > 0) {
            mFilterAdapterGender.setCurrent(0);
        }
        if (mFilterAdapterAge.getItemCount() > 0) {
            mFilterAdapterAge.setCurrent(0);
        }
    }

    private void takePhoto() {
        new CommonDialog.Builder(this)
                .setTitle(R.string.ipc_face_tip_album_title)
                .setMessage(R.string.ipc_face_tip_album_content)
                .setMessageDrawable(0, 0, 0, R.mipmap.face_tip_image)
                .setMessageDrawablePadding(R.dimen.dp_12)
                .setConfirmButton(R.string.str_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPickerAgent.takePhoto();
                    }
                })
                .create()
                .show();
    }

    private void openPicker() {
        new CommonDialog.Builder(this)
                .setTitle(R.string.ipc_face_tip_album_title)
                .setMessage(R.string.ipc_face_tip_album_content)
                .setMessageDrawable(0, 0, 0, R.mipmap.face_tip_image)
                .setMessageDrawablePadding(R.dimen.dp_12)
                .setConfirmButton(R.string.str_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int remain = mFaceGroup.getCapacity() - mFaceGroup.getCount();
                        mPickerAgent.setPickLimit(Math.min(remain, Constants.IMAGE_PICKER_LIMIT));
                        mPickerAgent.pickMultiPhotos(null);
                    }
                })
                .create()
                .show();
    }

    @Background
    void compress(File file) {
        file = Utils.imageCompress(this, file);
        if (file == null) {
            mUploadDialog.dismiss();
            shortTip(R.string.toast_network_error);
        } else {
            mPresenter.upload(file);
        }
    }

    @Override
    public void onBackPressed() {
        if (hideDropdownMenu(true)) {
            return;
        }
        if (mState == STATE_CHOOSE) {
            switchStateTo(STATE_NORMAL);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        mPresenter.refresh();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return mPresenter.getMore();
    }

    @CallSuper
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view != null && isShouldHideKeyBord(view, ev)) {
                hideSoftInput(view.getWindowToken());
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 判定当前是否需要隐藏
     */
    protected boolean isShouldHideKeyBord(View v, MotionEvent ev) {
        if (v instanceof EditText) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            return !(ev.getX() > left && ev.getX() < right && ev.getY() > top && ev.getY() < bottom);
        }
        return false;
    }

    /**
     * 隐藏软键盘
     */
    private void hideSoftInput(IBinder token) {
        mEtSearch.clearFocus();
        if (token != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPickerAgent.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_UPLOAD) {
                mPresenter.init();
                new CommonDialog.Builder(context)
                        .setTitle(R.string.ipc_face_tip_album_upload_success)
                        .setMessageDrawable(0, R.mipmap.face_ic_ok, 0, 0)
                        .setMessageDrawablePadding(R.dimen.dp_8)
                        .setConfirmButton(R.string.str_confirm)
                        .create().show();
            } else if (requestCode == REQUEST_CODE_DETAIL) {
                mPresenter.init();
            }
        }
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
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPickerAgent.onRestoreInstanceState(savedInstanceState);
    }

    private class CustomPopupHelper implements DropdownMenu.PopupHelper {

        @Override
        public void initMenu(RecyclerView list) {
            if (list.getAdapter() == null || list.getAdapter().getItemCount() == 0) {
                return;
            }
            // Add view into ConstraintLayout.
            int index = mContent.indexOfChild(mOverlay) + 1;
            if (mContent.indexOfChild(list) == -1) {
                mContent.addView(list, index);
            }
            // Init constraint set of menu list in ConstraintLayout.
            ConstraintSet con = new ConstraintSet();
            con.clone(mContent);
            con.connect(list.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
            con.connect(list.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
            con.connect(list.getId(), ConstraintSet.TOP, R.id.v_face_divider, ConstraintSet.BOTTOM, 0);
            con.constrainHeight(list.getId(), ConstraintSet.MATCH_CONSTRAINT);
            con.constrainWidth(list.getId(), ConstraintSet.MATCH_CONSTRAINT);
            con.applyTo(mContent);
            list.measure(0, 0);
        }

        @Override
        public void show(RecyclerView list, boolean animated) {
            hideDropdownMenu(false);
            mDropdownAnimator.startAnimationToShow(animated, list, mOverlay);
        }

        @Override
        public void dismiss(RecyclerView list, boolean animated) {
            mDropdownAnimator.startAnimationToDismiss(animated, list, mOverlay);
        }
    }

    private static class FilterMenuLayoutManager extends LinearLayoutManager {

        private FilterMenuLayoutManager(Context context) {
            super(context);
        }

        @Override
        public void onMeasure(@NonNull RecyclerView.Recycler recycler, @NonNull RecyclerView.State state, int widthSpec, int heightSpec) {
            if (getChildCount() == 0) {
                super.onMeasure(recycler, state, widthSpec, heightSpec);
                return;
            }
            View firstChildView = recycler.getViewForPosition(0);
            measureChild(firstChildView, widthSpec, heightSpec);
            int itemHeight = firstChildView.getMeasuredHeight();
            setMeasuredDimension(View.MeasureSpec.getSize(widthSpec),
                    getChildCount() > 9 ? (int) (itemHeight * 9f) : itemHeight * getChildCount());
        }
    }

    private class PickerResult implements TakePhoto.TakePhotoListener {

        @Override
        public void onSuccess(int from, Result result) {
            if (from == TakePhotoAgent.FROM_TAKE_PHOTO) {
                final File file = new File(result.getImage().getPath());
                int size = (int) getResources().getDimension(R.dimen.dp_90);
                Glide.with(FaceListActivity.this)
                        .load(file)
                        .apply(RequestOptions.bitmapTransform(mRoundTransform))
                        .into(new CustomTarget<Drawable>(size, size) {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource,
                                                        @Nullable Transition<? super Drawable> transition) {
                                mUploadDialog = new CommonDialog.Builder(context)
                                        .setTitle(R.string.ipc_face_tip_photo_uploading_title)
                                        .setMessage(R.string.ipc_face_tip_photo_uploading_content)
                                        .setMessageDrawablePadding(R.dimen.dp_12)
                                        .setMessageDrawable(null, null, null, resource)
                                        .create();
                                mUploadDialog.show();
                                compress(file);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });
            } else {
                List<Image> images = result.getImages();
                ArrayList<UploadImage> list = new ArrayList<>(images.size());
                for (Image image : images) {
                    list.add(new UploadImage(image));
                }
                FaceUploadActivity_.intent(FaceListActivity.this)
                        .mShopId(mShopId)
                        .mFaceGroup(mFaceGroup)
                        .mImages(list)
                        .mRemain(mFaceGroup.getCapacity() - mFaceGroup.getCount())
                        .startForResult(REQUEST_CODE_UPLOAD);
            }
        }

        @Override
        public void onError(int errorCode, int from, String msg) {
        }

        @Override
        public void onCancel(int from) {
        }
    }

    private class OnFilterItemClickListener implements DropdownMenu.OnItemClickListener<FilterItem> {

        private boolean isGender;

        private OnFilterItemClickListener(boolean isGender) {
            this.isGender = isGender;
        }

        @Override
        public void onItemSelected(DropdownMenu.BaseAdapter<FilterItem> adapter, FilterItem model, int position) {
            model.setChecked(true);
            if (isGender) {
                if (mFilterGenderCurrent != null) {
                    mFilterGenderCurrent.setChecked(false);
                }
                mFilterGenderCurrent = model;
                mPresenter.filterGender(model.getId());
            } else {
                if (mFilterAgeCurrent != null) {
                    mFilterAgeCurrent.setChecked(false);
                }
                mFilterAgeCurrent = model;
                mPresenter.filterAge(model.getId());
            }
            adapter.notifyDataSetChanged();
        }
    }

    private static class FaceGroupDialogAdapter extends SimpleArrayAdapter<FaceGroup> {

        private int selectedCount = 0;

        public void setSelectedCount(int selectedCount) {
            this.selectedCount = selectedCount;
            notifyDataSetChanged();
        }

        @Override
        public int getLayoutId() {
            return R.layout.face_item_dialog_group;
        }

        @Override
        public void setupView(@NonNull BaseViewHolder<FaceGroup> holder, FaceGroup model, int position) {
            TextView name = holder.getView(R.id.tv_face_group_name);
            TextView content = holder.getView(R.id.tv_face_group_remain);
            name.setText(Utils.getGroupName(holder.getContext(), model));
            int remain = model.getCapacity() - model.getCount();
            content.setText(holder.getContext().getString(R.string.ipc_face_remain, remain));
            if (selectedCount > remain) {
                name.setTextColor(ContextCompat.getColor(holder.getContext(), R.color.text_caption));
                content.setTextColor(ContextCompat.getColor(holder.getContext(), R.color.text_caption));
            } else {
                name.setTextColor(ContextCompat.getColor(holder.getContext(), R.color.text_main));
                content.setTextColor(ContextCompat.getColor(holder.getContext(), R.color.text_caption));
            }
        }

    }

    private class FaceListAdapter extends SimpleArrayAdapter<Face> {

        public FaceListAdapter() {
            addOnViewClickListener(R.id.item_image, new OnViewClickListener<Face>() {
                @Override
                public void onClick(BaseViewHolder<Face> holder, Face model, int position) {
                    if (!model.isAddIcon()) {
                        FaceDetailActivity_.intent(context)
                                .mShopId(mShopId)
                                .mFace(model)
                                .mFaceGroup(mFaceGroup)
                                .startForResult(REQUEST_CODE_DETAIL);
                        LogCat.d(TAG, "Face: " + model);
                        return;
                    }

                    if (mFaceGroup.getCount() >= mFaceGroup.getCapacity()) {
                        shortTip(R.string.ipc_face_error_add);
                        return;
                    }
                    if (mPickerDialog == null) {
                        mPickerDialog = new BottomPopMenu.Builder(FaceListActivity.this)
                                .addItemAction(new PopItemAction(R.string.str_take_photo,
                                        PopItemAction.PopItemStyle.Normal, () -> takePhoto()))
                                .addItemAction(new PopItemAction(R.string.str_choose_from_album,
                                        PopItemAction.PopItemStyle.Normal, () -> openPicker()))
                                .addItemAction(new PopItemAction(R.string.sm_cancel,
                                        PopItemAction.PopItemStyle.Cancel))
                                .create();
                    }
                    mPickerDialog.show();
                }
            });
            addOnViewClickListener(R.id.item_check_region, new OnViewClickListener<Face>() {
                @Override
                public void onClick(BaseViewHolder<Face> holder, Face model, int position) {
                    model.setChecked(!model.isChecked());
                    CheckBox checkBox = holder.getView(R.id.item_check_box);
                    checkBox.setChecked(model.isChecked());
                    mTitleBar.setAppTitle(getString(R.string.ipc_face_list_title_select_num,
                            mAdapterSelected.getItemCount()));
                    if (model.isChecked()) {
                        mAdapterSelected.add(model);
                        mRvSelectedList.scrollToPosition(mAdapterSelected.getItemCount() - 1);
                        mTvSelectedTip.setVisibility(View.GONE);
                        updateBtnEnable(true);
                    } else {
                        mAdapterSelected.remove(model);
                        if (mAdapterSelected.getData().isEmpty()) {
                            mTvSelectedTip.setVisibility(View.VISIBLE);
                            updateBtnEnable(false);
                            mTitleBar.setAppTitle(R.string.ipc_face_list_title_select);
                        }
                    }
                }
            });
        }

        @Override
        public int getLayoutId() {
            return R.layout.face_item_image_check;
        }

        @Override
        public void setupView(@NonNull BaseViewHolder<Face> holder, Face model, int position) {
            ImageView image = holder.getView(R.id.item_image);
            CheckBox checkBox = holder.getView(R.id.item_check_box);
            View region = holder.getView(R.id.item_check_region);
            TextView name = holder.getView(R.id.item_name);
            if (mState == STATE_NORMAL || model.isAddIcon()) {
                checkBox.setVisibility(View.GONE);
                region.setVisibility(View.GONE);
            } else {
                checkBox.setVisibility(View.VISIBLE);
                region.setVisibility(View.VISIBLE);
            }
            if (model.isAddIcon()) {
                name.setVisibility(View.GONE);
                image.setActivated(mFaceGroup.getCount() < mFaceGroup.getCapacity());
                image.setScaleType(ImageView.ScaleType.CENTER);
                image.setImageResource(R.drawable.face_ic_add);
            } else {
                name.setVisibility(View.VISIBLE);
                image.setActivated(true);
                checkBox.setChecked(model.isChecked());
                if (TextUtils.isEmpty(model.getName())) {
                    name.setText(R.string.ipc_face_name_default);
                } else {
                    name.setText(model.getName());
                }
                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(holder.itemView)
                        .load(model.getImgUrl())
                        .placeholder(R.mipmap.face_tip_placeholder)
                        .into(image);
            }
        }

        @Override
        public void onViewRecycled(@NonNull BaseViewHolder<Face> holder) {
            Glide.with(holder.itemView).clear((View) holder.getView(R.id.item_image));
        }
    }

    private class FaceSelectedListAdapter extends SimpleArrayAdapter<Face> {

        public FaceSelectedListAdapter() {
            addOnViewClickListener(R.id.item_image, new OnViewClickListener<Face>() {
                @Override
                public void onClick(BaseViewHolder<Face> holder, Face model, int position) {
                    model.setChecked(false);
                    remove(model);
                    List<Face> list = mAdapter.getData();
                    int i = list.indexOf(model);
                    if (i >= 0) {
                        list.get(i).setChecked(false);
                    }
                    mAdapter.notifyDataSetChanged();
                    if (getItemCount() == 0) {
                        mTvSelectedTip.setVisibility(View.VISIBLE);
                        updateBtnEnable(false);
                        mTitleBar.setAppTitle(R.string.ipc_face_list_title_select);
                    }
                }
            });
        }

        @Override
        public int getLayoutId() {
            return R.layout.face_item_image;
        }

        @Override
        public void setupView(@NonNull BaseViewHolder<Face> holder, Face model, int position) {
            ImageView image = holder.getView(R.id.item_image);
            Glide.with(holder.itemView)
                    .load(model.getImgUrl())
                    .apply(RequestOptions.bitmapTransform(mRoundTransform))
                    .placeholder(R.mipmap.face_tip_placeholder)
                    .into(image);
        }

        @Override
        public void onViewRecycled(@NonNull BaseViewHolder<Face> holder) {
            Glide.with(holder.itemView).clear((View) holder.getView(R.id.item_image));
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
