package com.sunmi.assistant.ui.activity.login;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.contract.ChooseShopContract;
import com.sunmi.assistant.presenter.ChooseShopPresenter;
import com.sunmi.assistant.ui.activity.MainActivity_;
import com.sunmi.assistant.ui.activity.merchant.CommonSaasUtils;
import com.sunmi.assistant.ui.activity.merchant.CreateCompanyActivity_;
import com.sunmi.assistant.ui.activity.model.AuthStoreInfo;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

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

    private int shopId;
    private String shopName;

    @AfterViews
    void init() {
        mPresenter = new ChooseShopPresenter();
        mPresenter.attachView(this);
        mRefreshLayout.setDelegate(this);
        mRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(context, true));
        mRefreshLayout.setPullDownRefreshEnable(false);
        rvChoose.init(R.drawable.shap_line_divider);

        if (action == CommonConstants.ACTION_LOGIN_CHOOSE_COMPANY) {
            titleBar.setAppTitle(R.string.str_select_company);
            tvSelectType.setText(R.string.company_select);
            titleBar.setRightTextViewText(R.string.company_create);
            titleBar.getRightText().setOnClickListener(this);
            btnEnterMain.setVisibility(View.GONE);
            mPresenter.getCompanyList();
            mPresenter.getUserInfo();
            mPresenter.getSsoToken();
        } else if (action == CommonConstants.ACTION_LOGIN_CHOOSE_SHOP) {
            CommonHelper.isCanClick(btnEnterMain, false);
            titleBar.setAppTitle(R.string.str_select_store);
            tvSelectType.setText(R.string.company_shop_select);
            btnEnterMain.setVisibility(View.VISIBLE);
            mPresenter.getShopList(companyId);
        }
    }

    @Override
    protected boolean needLandscape() {
        return true;
    }

    @Override
    public void onClick(View v) {
        CreateCompanyActivity_.intent(context).start();
    }

    @Click(R.id.btn_enter_main)
    void enterMainClick() {
        gotoMainActivity(shopId, shopName);
    }

    @Click(R.id.btn_refresh)
    void refreshClick() {
        setNoDataVisible(View.GONE);
        if (action == CommonConstants.ACTION_LOGIN_CHOOSE_COMPANY) {
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
    public void getSaasSuccessView(AuthStoreInfo data) {
        CommonSaasUtils.getSaasData(context, data.getSaas_user_info_list());
    }

    @Override
    public void getSaasFailView(int code, String msg) {

    }

    @Override
    public void getShopListSuccess(List<ShopListResp.ShopInfo> shopList) {
        int shopSize = shopList.size();
        if (shopSize == 1) {
            gotoMainActivity(shopList.get(0).getShop_id(), shopList.get(0).getShop_name());
        } else {
            initShopList(shopList);
        }
    }

    @Override
    public void getShopListFail(int code, String msg, ShopListResp data) {
        if (action == CommonConstants.ACTION_LOGIN_CHOOSE_COMPANY) {
            shortTip(R.string.tip_get_shop_list_fail);
        } else if (action == CommonConstants.ACTION_LOGIN_CHOOSE_SHOP) {
            setNoDataVisible(View.VISIBLE);
        }
    }

    @UiThread
    void initCompanyList(List<CompanyInfoResp> companyList) {
        activityVisible();
        if (companyList.size() == 0) {
            setNoDataVisible(View.VISIBLE);
            return;
        }
        rvChoose.setAdapter(new CommonListAdapter<CompanyInfoResp>(context,
                R.layout.item_shop_company, companyList) {
            @Override
            public void convert(ViewHolder holder, final CompanyInfoResp item) {
                ((SettingItemLayout) holder.getView(R.id.sil_item)).setLeftText(item.getCompany_name());
                holder.itemView.setOnClickListener(
                        v -> LoginChooseShopActivity_.intent(context)
                                .companyId(item.getCompany_id()).companyName(item.getCompany_name())
                                .saasExist(item.getSaas_exist())
                                .action(CommonConstants.ACTION_LOGIN_CHOOSE_SHOP).start());
            }
        });
    }

    @UiThread
    void initShopList(final List<ShopListResp.ShopInfo> shopList) {
        activityVisible();
        if (shopList.size() == 0) {
            //setNoDataVisible(View.VISIBLE);
            //当商户下没有门店检测是否有saas门店数据
            mPresenter.getSaas(SpUtils.getMobile());
            return;
        }
        rvChoose.setAdapter(new CommonListAdapter<ShopListResp.ShopInfo>(context,
                R.layout.item_shop_company, shopList) {
            int selectedIndex = -1;

            @Override
            public void convert(ViewHolder holder, final ShopListResp.ShopInfo item) {
                SettingItemLayout shopItem = holder.getView(R.id.sil_item);
                shopItem.setLeftText(item.getShop_name());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedIndex = holder.getAdapterPosition();
                        shopId = item.getShop_id();
                        shopName = item.getShop_name();
                        notifyDataSetChanged();
                        CommonHelper.isCanClick(btnEnterMain, true);
                    }
                });
                if (selectedIndex == holder.getAdapterPosition()) {
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

    private void gotoMainActivity(int shopId, String shopName) {
        SpUtils.setCompanyId(companyId);
        SpUtils.setCompanyName(companyName);
        SpUtils.setShopId(shopId);
        SpUtils.setShopName(shopName);
        SpUtils.setSaasExist(saasExist);
        MainActivity_.intent(context)
                .flags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK).start();
        finish();
    }

}
