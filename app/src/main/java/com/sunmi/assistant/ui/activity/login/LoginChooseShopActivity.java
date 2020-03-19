package com.sunmi.assistant.ui.activity.login;

import android.annotation.SuppressLint;
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
import com.sunmi.assistant.utils.AppConstants;
import com.sunmi.assistant.utils.GetUserInfoUtils;
import com.xiaojinzi.component.impl.Router;

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
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.CompanyInfoResp;
import sunmi.common.model.CompanyListResp;
import sunmi.common.model.ShopInfo;
import sunmi.common.notification.BaseNotification;
import sunmi.common.router.AppApi;
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
    @ViewById(R.id.llBottomBtn)
    LinearLayout llBottomBtn;
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
    ArrayList<ShopInfo> shopList;
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
            llBottomBtn.setVisibility(View.GONE);
            mPresenter.getCompanyList();
        } else if (action == CommonConstants.ACTION_LOGIN_CHOOSE_SHOP) {
            btnEnterMain.setEnabled(false);
            titleBar.setAppTitle(R.string.str_select_store);
            tvSelectType.setText(R.string.company_shop_select);
            llBottomBtn.setVisibility(View.VISIBLE);
            initShopList(shopList);
        } else if (action == CommonConstants.ACTION_CHANGE_COMPANY) {
            titleBar.setAppTitle(R.string.company_switch);
            tvSelectedCompany.setVisibility(View.VISIBLE);
            silSelectedCompany.setVisibility(View.VISIBLE);
            tvSelectType.setText(R.string.company_can_switch);
            tvSelectedCompany.setText(R.string.company_now_selected);
            silSelectedCompany.setTitle(SpUtils.getCompanyName());
            llBottomBtn.setVisibility(View.GONE);
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
        //切换商户保存信息直接跳转MainActivity
        if (isLoginSuccessSwitchCompany) {
            CommonHelper.saveCompanyShopInfo(companyId, companyName, saasExist, shopId, shopName);
            BaseNotification.newInstance().postNotificationName(CommonNotifications.companySwitch);
            Router.withApi(AppApi.class).goToMainClearTask(context);
            return;
        }
        showLoadingDialog();
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
        if (companyList.size() == 0) {
            CreateCompanyActivity_.intent(context).start();
            return;
        }
        if (action == CommonConstants.ACTION_CHANGE_COMPANY) {
            List<CompanyInfoResp> list = new ArrayList<>();
            for (CompanyInfoResp resp : companyList) {
                if (!TextUtils.equals(resp.getCompany_name(), SpUtils.getCompanyName())) {
                    list.add(resp);
                }
            }
            initCompanyList(list);
        }else {
            initCompanyList(companyList);
        }
    }

    @Override
    public void getCompanyListFail(int code, String msg, CompanyListResp data) {
        setNoDataVisible(View.VISIBLE);
    }

    @Override
    public void getShopListSuccess(int authority, List<ShopInfo> shopList) {
        if (shopList.size() == 0) {
            // 当前商户没有门店，跳转创建门店
            SpUtils.setPerspective(CommonConstants.PERSPECTIVE_TOTAL);
            llBottomBtn.setVisibility(View.GONE);
            CreateShopPreviewActivity_.intent(context)
                    .companyId(companyId)
                    .companyName(companyName)
                    .saasExist(saasExist)
                    .isLoginSuccessSwitchCompany(isLoginSuccessSwitchCompany)
                    .start();
        } else if (authority == AppConstants.ACCOUNT_AUTH_SHOP) {
            // 门店视角，跳转选择门店
            SpUtils.setPerspective(CommonConstants.PERSPECTIVE_SHOP);
            llBottomBtn.setVisibility(View.GONE);
            LoginChooseShopActivity_.intent(context)
                    .companyId(companyId)
                    .companyName(companyName)
                    .saasExist(saasExist)
                    .shopList((ArrayList<ShopInfo>) shopList)
                    .isLoginSuccessSwitchCompany(isLoginSuccessSwitchCompany)
                    .action(CommonConstants.ACTION_LOGIN_CHOOSE_SHOP).start();
        } else {
            // 总部视角，跳转主页
            SpUtils.setPerspective(CommonConstants.PERSPECTIVE_TOTAL);
            ShopInfo info = shopList.get(0);
            shopId = info.getShopId();
            shopName = info.getShopName();
            llBottomBtn.setVisibility(View.VISIBLE);
            btnEnterMain.setEnabled(true);
        }
    }

    @Override
    public void getShopListFail() {
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
        rvChoose.setAdapter(new CommonListAdapter<CompanyInfoResp>(context,
                R.layout.item_common_checked, companyList) {

            private int selectedIndex = -1;

            @Override
            public void convert(ViewHolder holder, final CompanyInfoResp item) {
                SettingItemLayout silItem = holder.getView(R.id.sil_item);
                silItem.setTitle(item.getCompany_name());
                holder.itemView.setOnClickListener(v -> {
                    if (isFastClick(1200)) {
                        return;
                    }
                    selectedIndex = holder.getAdapterPosition();
                    companyId = item.getCompany_id();
                    companyName = item.getCompany_name();
                    saasExist = item.getSaas_exist();
                    notifyDataSetChanged();
                    mPresenter.getShopList(item.getCompany_id());
                });
                silItem.setChecked(selectedIndex == holder.getAdapterPosition());
            }
        });
    }

    @UiThread
    void initShopList(final List<ShopInfo> shopList) {
        activityVisible();
        rvChoose.setAdapter(new CommonListAdapter<ShopInfo>(context,
                R.layout.item_common_checked, shopList) {
            int selectedIndex = shopList.size() == 1 ? 0 : -1;

            @Override
            public void convert(ViewHolder holder, final ShopInfo item) {
                SettingItemLayout shopItem = holder.getView(R.id.sil_item);
                shopItem.setTitle(item.getShopName());
                holder.itemView.setOnClickListener(v -> {
                    selectedIndex = holder.getAdapterPosition();
                    shopId = item.getShopId();
                    shopName = item.getShopName();
                    notifyDataSetChanged();
                    btnEnterMain.setEnabled(true);
                });
                if (selectedIndex == holder.getAdapterPosition()) {
                    if (shopList.size() == 1) {
                        shopId = item.getShopId();
                        shopName = item.getShopName();
                        btnEnterMain.setEnabled(true);
                    }
                    shopItem.setChecked(true);
                } else {
                    shopItem.setChecked(false);
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
