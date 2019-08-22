package com.sunmi.ipc.face;

import android.content.DialogInterface;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sunmi.ipc.R;
import com.sunmi.ipc.face.contract.FaceListContract;
import com.sunmi.ipc.face.model.Face;
import com.sunmi.ipc.face.model.FaceAge;
import com.sunmi.ipc.face.model.FaceGroup;
import com.sunmi.ipc.face.presenter.FaceListPresenter;
import com.sunmi.ipc.face.util.GlideRoundCrop;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import sunmi.common.base.BaseMvpActivity;
import sunmi.common.base.recycle.BaseRecyclerAdapter;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.SimpleArrayAdapter;
import sunmi.common.base.recycle.listener.OnViewClickListener;
import sunmi.common.view.ClearableEditText;
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
        implements FaceListContract.View {

    private static final int STATE_NORMAL = 0;
    private static final int STATE_CHOOSE = 1;

    @ViewById(resName = "title_bar")
    TitleBarView mTitleBar;
    @ViewById(resName = "cet_face_search")
    ClearableEditText mEtSearch;
    @ViewById(resName = "tv_face_search_hint")
    TextView mTvSearchHint;
    @ViewById(resName = "dm_face_filter_gender")
    TextView mDmFilterGender;
    @ViewById(resName = "dm_face_filter_age")
    DropdownMenu mDmFilterAge;
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

    private CommonDialog mDeleteDialog;
    private BottomDialog mMoveDialog;
    private boolean mMovePending = false;

    @AfterViews
    void init() {
        initViews();
        initFilters();
        initRecyclerView();
        mPresenter = new FaceListPresenter(mShopId, mFaceGroup);
        mPresenter.attachView(this);
        mPresenter.init();
    }

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
                mTvSearchHint.setVisibility(hasFocus ? View.INVISIBLE : View.VISIBLE);
            }
        });
        updateTitle();
    }

    private void initFilters() {
        mDmFilterGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
        mLayoutSelected.setVisibility(View.INVISIBLE);
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
            mPresenter.loadGroup();
            mMovePending = true;
            return;
        }
        if (mMoveDialog == null) {
            // TODO: Create dialog.
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
    public void updateFilter(List<FaceAge> list) {
        // TODO: Fill filter list.
    }

    @Override
    public void updateGroupList(List<FaceGroup> list) {
        this.mFaceGroupList = list;
        if (mMovePending) {
            mMovePending = false;
            clickMove();
        }
    }

    @Override
    public void updateList(List<Face> list, boolean hasMore) {
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
        mAdapter.add(list);
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
        mPresenter.init();
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
