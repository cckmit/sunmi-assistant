package com.sunmi.assistant.mine.shop;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.contract.ShopCategoryContract;
import com.sunmi.assistant.mine.presenter.ShopCategoryPresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.model.ShopCategoryResp;
import sunmi.common.model.ShopInfo;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.ViewHolder;

/**
 * @author yinhui
 * @date 2019-08-08
 */
@EActivity(R.layout.activity_mine_category)
public class ShopCategoryActivity extends BaseMvpActivity<ShopCategoryPresenter>
        implements ShopCategoryContract.View {

    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.recyclerViewLeft)
    RecyclerView recyclerViewLeft;
    @ViewById(R.id.recyclerViewRight)
    RecyclerView recyclerViewRight;

    private LeftCategoryAdapter mLeftAdapter;
    private RightCategoryAdapter mRightAdapter;

    @Extra
    ShopInfo mInfo;

    private int mCategory1;
    private int mCategory2;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        titleBar.getRightText().setOnClickListener(this::save);

        mCategory1 = mInfo.getTypeOne();
        mCategory2 = mInfo.getTypeTwo();

        mLeftAdapter = new LeftCategoryAdapter(this);
        recyclerViewLeft.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewLeft.setAdapter(mLeftAdapter);

        mRightAdapter = new RightCategoryAdapter(this);
        recyclerViewRight.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewRight.setAdapter(mRightAdapter);

        mPresenter = new ShopCategoryPresenter(mInfo);
        mPresenter.attachView(this);
        mPresenter.getCategory();
        showLoadingDialog();
    }

    @Override
    public void showCategoryList(List<ShopCategoryResp.ShopTypeListBean> list) {
        hideLoadingDialog();
        mLeftAdapter.setData(list);
        for (int i = 0, size1 = list.size(); i < size1; i++) {
            ShopCategoryResp.ShopTypeListBean type1 = list.get(i);
            if (mCategory1 == type1.getId()) {
                recyclerViewLeft.scrollToPosition(i);
                List<ShopCategoryResp.ShopTypeListBean.ChildBean> subList = type1.getChild();
                for (int j = 0, size2 = subList.size(); j < size2; j++) {
                    ShopCategoryResp.ShopTypeListBean.ChildBean type2 = subList.get(j);
                    if (mCategory2 == type2.getId()) {
                        mRightAdapter.setData(subList);
                        recyclerViewRight.scrollToPosition(j);
                    }
                }
            }
        }
    }

    @Override
    public void complete() {
        hideLoadingDialog();
        setResult(RESULT_OK);
        finish();
    }

    private void save(View v) {
        if (mCategory1 <= 0 || mCategory2 <= 0) {
            shortTip(R.string.str_selected);
        } else if (mInfo.getTypeOne() == mCategory1 && mInfo.getTypeTwo() == mCategory2) {
            setResult(RESULT_CANCELED);
            finish();
        } else {
            mPresenter.updateCategory(mCategory1, mCategory2);
        }
    }

    @Override
    public void getCategoryFailed() {
        hideLoadingDialog();
        shortTip(R.string.toast_network_Exception);
    }

    @Override
    public void updateCategoryFailed() {
        hideLoadingDialog();
        shortTip(R.string.tip_save_fail);
    }

    private class LeftCategoryAdapter extends CommonListAdapter<ShopCategoryResp.ShopTypeListBean> {

        /**
         * @param context 上下文
         */
        LeftCategoryAdapter(Context context) {
            super(context, R.layout.item_mine_category_left, null);
        }

        @Override
        public void convert(final ViewHolder holder, final ShopCategoryResp.ShopTypeListBean model) {
            TextView textView = holder.getView(R.id.tvName);
            RelativeLayout rlLeft = holder.getView(R.id.rlLeft);
            final String name = model.getName();
            textView.setText(name);
            holder.itemView.setOnClickListener(v -> {
                mCategory1 = model.getId();
                mCategory2 = -1;
                notifyDataSetChanged();
                mRightAdapter.setData(model.getChild());
            });
            if (mCategory1 == model.getId()) {
                textView.setTextColor(ContextCompat.getColor(mContext, R.color.common_orange));
                rlLeft.setBackgroundColor(ContextCompat.getColor(mContext, R.color.c_white));
            } else {
                rlLeft.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_F9F9F9));
                textView.setTextColor(ContextCompat.getColor(mContext, R.color.colorText));
            }
        }
    }

    private class RightCategoryAdapter extends CommonListAdapter<ShopCategoryResp.ShopTypeListBean.ChildBean> {

        /**
         * @param context 上下文
         */
        RightCategoryAdapter(Context context) {
            super(context, R.layout.item_mine_category_left, null);
        }

        @Override
        public void convert(ViewHolder holder, ShopCategoryResp.ShopTypeListBean.ChildBean model) {
            TextView textView = holder.getView(R.id.tvName);
            RelativeLayout rlLeft = holder.getView(R.id.rlLeft);
            rlLeft.setBackgroundColor(ContextCompat.getColor(mContext, R.color.c_white));
            final String nameRight = model.getName();
            textView.setText(nameRight);
            holder.itemView.setOnClickListener(v -> {
                mCategory2 = model.getId();
                notifyDataSetChanged();
            });
            if (mCategory2 == model.getId()) {
                textView.setTextColor(ContextCompat.getColor(mContext, R.color.common_orange));
            } else {
                textView.setTextColor(ContextCompat.getColor(mContext, R.color.colorText));
            }
        }
    }

}
