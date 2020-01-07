package com.sunmi.assistant.mine.platform;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.contract.SelectStoreContract;
import com.sunmi.assistant.mine.model.SelectShopModel;
import com.sunmi.assistant.mine.presenter.SelectStorePresenter;
import com.sunmi.assistant.ui.activity.MainActivity_;
import com.sunmi.assistant.utils.GetUserInfoUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.AuthStoreInfo;
import sunmi.common.notification.BaseNotification;
import sunmi.common.utils.CommonHelper;
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
    int companyId;
    @Extra
    String companyName;
    @Extra
    int saasExist;
    @Extra
    ArrayList<AuthStoreInfo.SaasUserInfoListBean> list;
    @Extra
    boolean isLoginSuccessSwitchCompany;

    private ShopListAdapter mAdapter;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        mPresenter = new SelectStorePresenter(list, companyId);
        mPresenter.attachView(this);
        mAdapter = new ShopListAdapter(this, mPresenter.getList());
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mAdapter);
    }

    @Click({R.id.btnComplete})
    void btnComplete() {
        mPresenter.createShops(mAdapter.getData());
    }

    @Override
    public void complete(int saasExist, int shopId, String shopName) {
        if (SpUtils.isLoginSuccess()) {
            SpUtils.setSaasExist(1);
            BaseNotification.newInstance().postNotificationName(CommonNotifications.importShop);
            if (isLoginSuccessSwitchCompany) {
                CommonHelper.saveCompanyShopInfo(companyId, companyName, 1, shopId, shopName);
                BaseNotification.newInstance().postNotificationName(CommonNotifications.companySwitch);
                MainActivity_.intent(this).start();
                //GotoActivityUtils.gotoMainActivityClearTask(context);
            } else {
                setResult(RESULT_OK);
            }
            finish();
        } else {
            GetUserInfoUtils.userInfo(this, companyId, companyName, 1, shopId, shopName);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private class ShopListAdapter extends CommonListAdapter<SelectShopModel> {

        private int selectedCount = 0;

        private ShopListAdapter(Context context, List<SelectShopModel> list) {
            super(context, R.layout.item_merchant_auth_store, list);
            btnComplete.setEnabled(false);
            for (SelectShopModel info : list) {
                if (info.isChecked()) {
                    selectedCount++;
                    btnComplete.setEnabled(true);
                }
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ViewHolder holder = super.onCreateViewHolder(parent, viewType);
            holder.getView(R.id.CBox).setOnClickListener(v -> {
                SelectShopModel info = getData().get(holder.getAdapterPosition());
                info.setChecked(!info.isChecked());
                selectedCount = info.isChecked() ? selectedCount + 1 : selectedCount - 1;
                btnComplete.setEnabled(selectedCount > 0);
            });
            return holder;
        }

        @Override
        public void convert(ViewHolder holder, SelectShopModel info) {
            holder.setText(R.id.tvName, info.getShopName());
            holder.setText(R.id.tvPlatform, getString(R.string.str_shop_platform_from, info.getSaasName()));
            CheckBox checkBox = holder.getView(R.id.CBox);
            checkBox.setChecked(info.isChecked());
        }
    }

}
