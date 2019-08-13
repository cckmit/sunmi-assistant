package com.sunmi.assistant.mine.platform;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.model.SelectShopModel;
import com.sunmi.assistant.utils.GetUserInfo;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.model.AuthStoreInfo;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.ViewHolder;

/**
 * 选择门店
 *
 * @author YangShiJie
 * @date 2019/6/26
 */
@SuppressLint("Registered")
@EActivity(R.layout.activity_merchant_select_store)
public class SelectStoreActivity extends BaseMvpActivity<SelectStorePresenter>
        implements SelectStoreContract.View {

    private static final int SHOP_ALREADY_EXIST = 5034;

    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.recyclerView)
    RecyclerView recyclerView;
    @ViewById(R.id.btnComplete)
    Button btnComplete;

    @Extra
    boolean isBack;
    @Extra
    ArrayList<AuthStoreInfo.SaasUserInfoListBean> list;

    private ShopListAdapter mAdapter;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        if (!isBack) {
            titleBar.getLeftLayout().setVisibility(View.GONE);
        }
        mPresenter = new SelectStorePresenter(list);
        mPresenter.attachView(this);
        mAdapter = new ShopListAdapter(this, mPresenter.getList());
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mAdapter);
        enableCompleteBtn(false);
    }

    @Click({R.id.btnComplete})
    void btnComplete() {
        mPresenter.createShops(mAdapter.getData());
    }

    @Override
    public void complete() {
        if (SpUtils.isLoginSuccess()) {
            finish();
        } else {
            GetUserInfo.userInfo(this);
        }
    }

    @Override
    public void onFailedShopCreate(int code) {
        if (code == SHOP_ALREADY_EXIST) {
            shortTip(getString(R.string.str_create_store_alredy_exit));
        } else {
            shortTip(getString(R.string.str_create_store_fail));
        }
    }

    private void enableCompleteBtn(boolean enable) {
        btnComplete.setEnabled(enable);
        if (enable) {
            btnComplete.setAlpha(1f);
        } else {
            btnComplete.setAlpha(0.5f);
        }
    }

    @Override
    public void onBackPressed() {
        if (!isBack) {
            return;
        }
        super.onBackPressed();
    }

    private class ShopListAdapter extends CommonListAdapter<SelectShopModel> {

        private int selectedCount = 0;

        private ShopListAdapter(Context context, List<SelectShopModel> list) {
            super(context, R.layout.item_merchant_auth_store, list);
            if (list.size() == 1 && list.get(0).isChecked()) {
                selectedCount = 1;
                enableCompleteBtn(true);
            }
        }

        @Override
        public void convert(ViewHolder holder, SelectShopModel info) {
            holder.setText(R.id.tvName, info.getShopName());
            holder.setText(R.id.tvPlatform, info.getSaasName());
            CheckBox checkBox = holder.getView(R.id.CBox);
            checkBox.setChecked(info.isChecked());
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                info.setChecked(isChecked);
                selectedCount = isChecked ? selectedCount + 1 : selectedCount - 1;
                enableCompleteBtn(selectedCount > 0);
            });
        }
    }

}
