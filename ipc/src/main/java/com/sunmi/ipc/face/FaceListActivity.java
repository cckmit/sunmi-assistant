package com.sunmi.ipc.face;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.IBinder;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sunmi.ipc.R;
import com.sunmi.ipc.face.contract.FaceListContract;
import com.sunmi.ipc.face.model.Face;
import com.sunmi.ipc.face.model.FaceGroup;
import com.sunmi.ipc.face.presenter.FaceListPresenter;
import com.sunmi.ipc.face.util.GlideRoundCrop;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import sunmi.common.base.BaseMvpActivity;
import sunmi.common.base.recycle.BaseRecyclerAdapter;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.SimpleArrayAdapter;
import sunmi.common.base.recycle.listener.OnViewClickListener;
import sunmi.common.model.FilterItem;
import sunmi.common.view.ClearableEditText;
import sunmi.common.view.DropdownAdapter;
import sunmi.common.view.DropdownAnimation;
import sunmi.common.view.DropdownMenu;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.dialog.BottomDialog;
import sunmi.common.view.dialog.CommonDialog;

/**
 * @author yinhui
 * @date 2019-08-20
 */
@EActivity(resName = "face_activity_list")
public class FaceListActivity extends BaseMvpActivity<FaceListPresenter>
        implements FaceListContract.View, BGARefreshLayout.BGARefreshLayoutDelegate {

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
    @ViewById(resName = "layout_empty")
    View mLayoutEmpty;

    @Extra
    int mShopId;
    @Extra
    FaceGroup mFaceGroup;

    private int mState = STATE_NORMAL;

    private SimpleArrayAdapter<Face> mAdapter;
    private SimpleArrayAdapter<Face> mAdapterSelected;
    private List<FaceGroup> mFaceGroupList;

    private DropdownAnimation mDropdownAnimator = new DropdownAnimation();
    private DropdownAdapter mFilterAdapterGender;
    private DropdownAdapter mFilterAdapterAge;

    private FilterItem mFilterGenderCurrent;
    private FilterItem mFilterAgeCurrent;

    private CommonDialog mDeleteDialog;
    private BottomDialog mMoveDialog;
    private boolean mMovePending = false;

    @AfterViews
    void init() {
        initViews();
        initFilters();
        initDropdown();
        initRecyclerView();
        mPresenter = new FaceListPresenter(mShopId, mFaceGroup);
        mPresenter.attachView(this);
        mPresenter.init(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initViews() {
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
        mOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDmFilterGender.getPopup().isShowing()) {
                    mDmFilterGender.getPopup().dismiss(true);
                } else if (mDmFilterAge.getPopup().isShowing()) {
                    mDmFilterAge.getPopup().dismiss(true);
                }
            }
        });
        mRefreshLayout.setDelegate(this);
        mRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(this, true));
        mRefreshLayout.setPullDownRefreshEnable(true);
        mRefreshLayout.setIsShowLoadingMoreView(true);
        updateTitle();
    }

    private void initFilters() {
        mDmFilterGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initDropdown() {
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

    private void initSelectedLayout() {
        mAdapterSelected.clear();
        mTvSelectedTip.setVisibility(View.VISIBLE);
        updateBtnEnable(false);
        mLayoutSelected.setVisibility(View.GONE);
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
                }
            });
        } else {
            mTitleBar.setAppTitle(R.string.ipc_face_list_title);
            mTitleBar.setRightTextViewText(R.string.ipc_face_select);
            mTitleBar.getRightText().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchStateTo(STATE_CHOOSE);
                }
            });
        }
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
        hideDropdownMenu();
        if (mState == STATE_NORMAL) {
            final List<Face> list = mAdapter.getData();
            for (Face face : list) {
                face.setChecked(false);
            }
            initSelectedLayout();
        } else {
            mLayoutSelected.setVisibility(View.VISIBLE);
        }
        mAdapter.notifyDataSetChanged();
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

    private void updateBtnEnable(boolean enable) {
        mTvSelectedMove.setEnabled(enable);
        mTvSelectedMove.setTextColor(enable ? ContextCompat.getColor(this, R.color.colorText)
                : ContextCompat.getColor(this, R.color.colorText_60));
        mTvSelectedDelete.setEnabled(enable);
        mTvSelectedDelete.setTextColor(enable ? ContextCompat.getColor(this, R.color.colorText)
                : ContextCompat.getColor(this, R.color.colorText_60));
    }

    @Click(resName = "tv_face_selected_move")
    void clickMove() {
        if (mFaceGroupList == null) {
            showLoadingDialog();
            mPresenter.loadGroup();
            mMovePending = true;
            return;
        }
        if (mMoveDialog == null) {
            mMoveDialog = new BottomDialog.Builder(this)
                    .setBtnBottom(true)
                    .setCancelButton(R.string.sm_cancel)
                    // TODO: Group dialog content
                    .create();
        }
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
    public void updateGroupList(List<FaceGroup> list) {
        this.mFaceGroupList = list;
        if (mMovePending) {
            mMovePending = false;
            hideLoadingDialog();
            if (list == null) {
                shortTip(R.string.toast_network_error);
            } else {
                clickMove();
            }
        }
    }

    @Override
    public void updateList(List<Face> list, boolean hasMore) {
        mRefreshLayout.endRefreshing();
        hideLoadingDialog();
        mLayoutError.setVisibility(View.GONE);
        if (list.isEmpty()) {
            mLayoutEmpty.setVisibility(View.VISIBLE);
            mTitleBar.setRightTextViewColor(R.color.colorText_60);
            mTitleBar.setRightTextViewEnable(false);
        } else {
            mLayoutEmpty.setVisibility(View.GONE);
            mTitleBar.setRightTextViewColor(R.color.colorText);
            mTitleBar.setRightTextViewEnable(true);
            if (mState == STATE_NORMAL) {
                list.add(0, Face.createCamera());
            }
        }
        mAdapter.setData(list);
    }

    @Override
    public void addList(List<Face> list, boolean hasMore) {
        mRefreshLayout.endLoadingMore();
        hideLoadingDialog();
        mAdapter.add(list);
    }

    @Override
    public void resetView() {
        switchStateTo(STATE_NORMAL);
        mFilterAdapterGender.setCurrent(0);
        mFilterAdapterAge.setCurrent(0);
    }

    @Override
    public void getDataFailed() {
        hideLoadingDialog();
        if (mAdapter.getItemCount() == 0) {
            mLayoutEmpty.setVisibility(View.GONE);
            mLayoutError.setVisibility(View.VISIBLE);
        } else {
            mLayoutError.setVisibility(View.GONE);
            shortTip(R.string.toast_network_error);
        }
    }

    @Click(resName = "btn_refresh")
    void refresh() {
        mPresenter.init(this);
        mEtSearch.setText("");
        mEtSearch.clearFocus();
        resetView();
    }

    @Override
    public void onBackPressed() {
        if (hideDropdownMenu()) {
            return;
        }
        if (mState == STATE_CHOOSE) {
            switchStateTo(STATE_NORMAL);
        } else {
            super.onBackPressed();
        }
    }

    private boolean hideDropdownMenu() {
        boolean result = false;
        if (mDmFilterGender.getPopup().isShowing()) {
            mDmFilterGender.getPopup().dismiss(true);
            result = true;
        }
        if (mDmFilterAge.getPopup().isShowing()) {
            mDmFilterAge.getPopup().dismiss(true);
            result = true;
        }
        return result;
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        mPresenter.loadFace(true, false);
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
            hideDropdownMenu();
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
                    getChildCount() > 6 ? (int) (itemHeight * 6.5f) : itemHeight * getChildCount());
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

    private class FaceListAdapter extends SimpleArrayAdapter<Face> {

        public FaceListAdapter() {
            addOnViewClickListener(R.id.item_image, new OnViewClickListener<Face>() {
                @Override
                public void onClick(BaseRecyclerAdapter<Face> adapter, BaseViewHolder<Face> holder,
                                    View v, Face model, int position) {
                    if (model.isAddIcon()) {
                        // TODO: Go to take photo or pick image.
                    } else {
                        // TODO: Go to detail.
                    }
                }
            });
            addOnViewClickListener(R.id.item_check_region, new OnViewClickListener<Face>() {
                @Override
                public void onClick(BaseRecyclerAdapter<Face> adapter, BaseViewHolder<Face> holder,
                                    View v, Face model, int position) {
                    model.setChecked(!model.isChecked());
                    CheckBox checkBox = holder.getView(R.id.item_check_box);
                    checkBox.setChecked(model.isChecked());
                    if (model.isChecked()) {
                        mAdapterSelected.add(model);
                        mRvSelectedList.scrollToPosition(mAdapterSelected.getItemCount() - 1);
                        mTvSelectedTip.setVisibility(View.GONE);
                        updateBtnEnable(true);
                        mTitleBar.setAppTitle(getString(R.string.ipc_face_list_title_select_num,
                                mAdapterSelected.getItemCount()));
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
            if (mState == STATE_NORMAL || model.isAddIcon()) {
                checkBox.setVisibility(View.GONE);
                region.setVisibility(View.GONE);
            } else {
                checkBox.setVisibility(View.VISIBLE);
                region.setVisibility(View.VISIBLE);
            }
            if (model.isAddIcon()) {
                image.setScaleType(ImageView.ScaleType.CENTER);
                image.setImageResource(R.mipmap.face_ic_add);
            } else {
                checkBox.setChecked(model.isChecked());
                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(holder.itemView).load(model.getImgUrl()).into(image);
            }
        }

        @Override
        public void onViewRecycled(@NonNull BaseViewHolder<Face> holder) {
            Glide.with(holder.itemView).clear(holder.getView(R.id.item_image));
        }
    }

    private class FaceSelectedListAdapter extends SimpleArrayAdapter<Face> {

        private GlideRoundCrop round = new GlideRoundCrop((int) getResources().getDimension(R.dimen.dp_6));

        public FaceSelectedListAdapter() {
            addOnViewClickListener(R.id.item_image, new OnViewClickListener<Face>() {
                @Override
                public void onClick(BaseRecyclerAdapter<Face> adapter, BaseViewHolder<Face> holder,
                                    View v, Face model, int position) {
                    model.setChecked(false);
                    remove(model);
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
                    .apply(RequestOptions.bitmapTransform(round))
                    .into(image);
        }

        @Override
        public void onViewRecycled(@NonNull BaseViewHolder<Face> holder) {
            Glide.with(holder.itemView).clear(holder.getView(R.id.item_image));
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
