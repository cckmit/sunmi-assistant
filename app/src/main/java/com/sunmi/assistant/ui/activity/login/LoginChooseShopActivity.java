package com.sunmi.assistant.ui.activity.login;

import android.annotation.SuppressLint;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.contract.ChooseShopContract;
import com.sunmi.assistant.mine.shop.CreateShopPreviewActivity_;
import com.sunmi.assistant.presenter.ChooseShopPresenter;
import com.sunmi.assistant.ui.activity.merchant.CreateCompanyActivity_;
import com.sunmi.assistant.utils.GetUserInfoUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import sunmi.common.base.BaseMvpActivity;
import sunmi.common.constant.CommonConstants;
import sunmi.common.model.CompanyInfoResp;
import sunmi.common.model.CompanyListResp;
import sunmi.common.model.ShopListResp;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.SettingItemLayout;
import sunmi.common.view.SmRecyclerView;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.ViewHolder;

/**
 * Description:登录选择商户和门店
 * Created by bruce on 2019/7/2.
 */
@SuppressLint("Registered")
@EActivity(R.layout.activity_login_choose_shop)
public class LoginChooseShopActivity extends BaseMvpActivity<ChooseShopPresenter>
        implements ChooseShopContract.View, BGARefreshLayout.BGARefreshLayoutDelegate,
        View.OnClickListener {

    @ViewById(R.id.rl_root)
    RelativeLayout rlRoot;
    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.bgarl_choose)
    BGARefreshLayout mRefreshLayout;
    @ViewById(R.id.rv_choose)
    SmRecyclerView rvChoose;
    @ViewById(R.id.ll_no_data)
    LinearLayout rlNoData;
    @ViewById(R.id.tv_select_type)
    TextView tvSelectType;
    @ViewById(R.id.tv_selected_company)
    TextView tvSelectedCompany;
    @ViewById(R.id.sil_selected_company)
    SettingItemLayout silSelectedCompany;
    @ViewById(R.id.btn_enter_main)
    Button btnEnterMain;

    @Extra
    int action;
    @Extra
    int companyId;
    @Extra
    String companyName;
    @Extra
    int saasExist;
    @Extra
    ArrayList<ShopListResp.ShopInfo> shopList;
    @Extra
    boolean isRegisterEnterCompany;
    @Extra
    boolean isLoginSuccessSwitchCompany;

    private int shopId;
    private String shopName;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        mPresenter = new ChooseShopPresenter();
        mPresenter.attachView(this);
        mRefreshLayout.setDelegate(this);
        mRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(context, true));
        mRefreshLayout.setPullDownRefreshEnable(false);
        rvChoose.init(R.drawable.shap_line_divider);

        if (action == CommonConstants.ACTION_LOGIN_CHOOSE_COMPANY) {
            titleBar.setAppTitle(R.string.str_select_company);
            tvSelectType.setText(R.string.company_select);
            if (isRegisterEnterCompany) {
                titleBar.setRightTextViewText(R.string.company_create);
                titleBar.setLeftImageVisibility(View.GONE);
            }
            titleBar.getRightText().setOnClickListener(this);
            btnEnterMain.setVisibility(View.GONE);
            mPresenter.getCompanyList();
        } else if (action == CommonConstants.ACTION_LOGIN_CHOOSE_SHOP) {
            CommonHelper.isCanClick(btnEnterMain, false);
            titleBar.setAppTitle(R.string.str_select_store);
            tvSelectType.setText(R.string.company_shop_select);
            btnEnterMain.setVisibility(View.VISIBLE);
            initShopList(shopList);
        } else if (action == CommonConstants.ACTION_CHANGE_COMPANY) {
            titleBar.setAppTitle(R.string.company_switch);
            tvSelectedCompany.setVisibility(View.VISIBLE);
            silSelectedCompany.setVisibility(View.VISIBLE);
            tvSelectType.setText(R.string.company_can_switch);
            tvSelectedCompany.setText(R.string.company_now_selected);
            silSelectedCompany.setLeftText(SpUtils.getCompanyName());
            btnEnterMain.setVisibility(View.GONE);
            mPresenter.getCompanyList();
        }
    }

    @Override
    public void onClick(View v) {
        CreateCompanyActivity_.intent(context).start();
    }

    @Override
    public void onBackPressed() {
        if (isRegisterEnterCompany) {
            return;
        }
        super.onBackPressed();
    }

    /**
     * 选择完商户，门店，获取用户信息成功
     */
    @Click(R.id.btn_enter_main)
    void enterMainClick() {
        if (isFastClick(1500)) {
            return;
        }
        GetUserInfoUtils.userInfo(this, companyId, companyName, saasExist, shopId, shopName);
    }

    @Click(R.id.btn_refresh)
    void refreshClick() {
        setNoDataVisible(View.GONE);
        if (action == CommonConstants.ACTION_LOGIN_CHOOSE_COMPANY ||
                action == CommonConstants.ACTION_CHANGE_COMPANY) {
            mPresenter.getCompanyList();
        } else if (action == CommonConstants.ACTION_LOGIN_CHOOSE_SHOP) {
            mPresenter.getShopList(companyId);
        }
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {

    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }

    @Override
    public void getCompanyListSuccess(List<CompanyInfoResp> companyList) {
        initCompanyList(companyList);
    }

    @Override
    public void getCompanyListFail(int code, String msg, CompanyListResp data) {
        setNoDataVisible(View.VISIBLE);
    }

    @Override
    public void getShopListSuccess(List<ShopListResp.ShopInfo> shopList) {
        if (shopList.size() == 0) {
            CreateShopPreviewActivity_.intent(context)
                    .companyId(companyId)
                    .companyName(companyName)
                    .saasExist(saasExist)
                    .isLoginSuccessSwitchCompany(isLoginSuccessSwitchCompany)
                    .start();
        } else {
            LoginChooseShopActivity_.intent(context)
                    .companyId(companyId)
                    .companyName(companyName)
                    .saasExist(saasExist)
                    .shopList((ArrayList<ShopListResp.ShopInfo>) shopList)
                    .action(CommonConstants.ACTION_LOGIN_CHOOSE_SHOP).start();
        }
    }

    @Override
    public void getShopListFail(int code, String msg, ShopListResp data) {
        if (action == CommonConstants.ACTION_LOGIN_CHOOSE_COMPANY ||
                action == CommonConstants.ACTION_CHANGE_COMPANY) {
            shortTip(R.string.tip_get_shop_list_fail);
        } else if (action == CommonConstants.ACTION_LOGIN_CHOOSE_SHOP) {
            setNoDataVisible(View.VISIBLE);
        }
    }

    @UiThread
    void initCompanyList(List<CompanyInfoResp> companyList) {
        activityVisible();
        if (companyList.size() == 0) {
            CreateCompanyActivity_.intent(context).start();
            return;
        }
        rvChoose.setAdapter(new CommonListAdapter<CompanyInfoResp>(context,
                R.layout.item_shop_company, companyList) {
            @Override
            public void convert(ViewHolder holder, final CompanyInfoResp item) {
                SettingItemLayout silItem = holder.getView(R.id.sil_item);
                silItem.setLeftText(item.getCompany_name());
                if (action == CommonConstants.ACTION_CHANGE_COMPANY &&
                        TextUtils.equals(item.getCompany_name(), SpUtils.getCompanyName())) {
                    silItem.setVisibility(View.GONE);
                } else {
                    silItem.setVisibility(View.VISIBLE);
                }
                holder.itemView.setOnClickListener(v -> {
                    companyId = item.getCompany_id();
                    companyName = item.getCompany_name();
                    saasExist = item.getSaas_exist();
                    mPresenter.getShopList(item.getCompany_id());
                });
            }
        });
    }

    @UiThread
    void initShopList(final List<ShopListResp.ShopInfo> shopList) {
        activityVisible();
        rvChoose.setAdapter(new CommonListAdapter<ShopListResp.ShopInfo>(context,
                R.layout.item_shop_company, shopList) {
            int selectedIndex = shopList.size() == 1 ? 0 : -1;

            @Override
            public void convert(ViewHolder holder, final ShopListResp.ShopInfo item) {
                SettingItemLayout shopItem = holder.getView(R.id.sil_item);
                shopItem.setLeftText(item.getShop_name());
                holder.itemView.setOnClickListener(v -> {
                    selectedIndex = holder.getAdapterPosition();
                    shopId = item.getShop_id();
                    shopName = item.getShop_name();
                    notifyDataSetChanged();
                    CommonHelper.isCanClick(btnEnterMain, true);
                });
                if (selectedIndex == holder.getAdapterPosition()) {
                    if (shopList.size() == 1) {
                        shopId = item.getShop_id();
                        shopName = item.getShop_name();
                        CommonHelper.isCanClick(btnEnterMain, true);
                    }
                    shopItem.setRightImage(ContextCompat.getDrawable(context, com.sunmi.ipc.R.mipmap.ic_yes));
                    shopItem.setLeftTextColor(ContextCompat.getColor(context, com.sunmi.ipc.R.color.common_orange));
                } else {
                    shopItem.setLeftTextColor(ContextCompat.getColor(context, com.sunmi.ipc.R.color.colorText));
                    shopItem.setRightImage(null);
                }
            }
        });
    }

    @UiThread
    void setNoDataVisible(int visible) {
        rlNoData.setVisibility(visible);
    }

    @UiThread
    void activityVisible() {
        rlRoot.setVisibility(View.VISIBLE);
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
    }
}
