package com.sunmi.assistant.ui.activity;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunmi.apmanager.constant.NotificationConstant;
import com.sunmi.assistant.R;
import com.sunmi.assistant.contract.ChooseShopContract;
import com.sunmi.assistant.data.response.ShopListResp;
import com.sunmi.assistant.presenter.ChooseShopPresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import sunmi.common.base.BaseMvpActivity;
import sunmi.common.notification.BaseNotification;
import sunmi.common.utils.SpUtils;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.ViewHolder;

/**
 * Description: ChooseShopActivity
 * Created by Bruce on 2019/7/1.
 */
@EActivity(R.layout.activity_choose_shop)
public class ChooseShopActivity extends BaseMvpActivity<ChooseShopPresenter>
        implements ChooseShopContract.View, BGARefreshLayout.BGARefreshLayoutDelegate {

    @ViewById(R.id.rl_root)
    RelativeLayout rlRoot;
    @ViewById(R.id.tv_current_shop)
    TextView tvCurrShop;
    @ViewById(R.id.bgarl_choose)
    BGARefreshLayout mRefreshLayout;
    @ViewById(R.id.rv_choose)
    RecyclerView rvChoose;
    @ViewById(R.id.tv_tip_no_data)
    TextView tvNoData;

    @Extra
    int action;

    public static int ACTION_LOGIN_CHOOSE_COMPANY = 0;
    public static int ACTION_LOGIN_CHOOSE_SHOP = 1;
    public static int ACTION_CHANGE_COMPANY = 2;
    public static int ACTION_CHANGE_SHOP = 3;

    private int pageNum = 1, pageSize = 99;

    @AfterViews
    void init() {
        mPresenter = new ChooseShopPresenter();
        mPresenter.attachView(this);
        tvCurrShop.setText(SpUtils.getShopName());
        mRefreshLayout.setDelegate(this);
        mRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(context, true));
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rvChoose.setLayoutManager(layoutManager);
        rvChoose.addItemDecoration(new DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL));
        mRefreshLayout.setPullDownRefreshEnable(false);
        mPresenter.getShopList();

//        if (action == ACTION_LOGIN_CHOOSE_COMPANY) {
//            titleBar.setVisibility(View.GONE);
//            titleBar1.setVisibility(View.VISIBLE);
//            tvTitle1.setText(R.string.str_choose_company);
//            mPresenter.getCompanyList(pageNum, pageSize);
//        } else if (action == ACTION_LOGIN_CHOOSE_SHOP) {
//            titleBar.setVisibility(View.GONE);
//            titleBar1.setVisibility(View.VISIBLE);
//            tvTitle1.setText(R.string.str_choose_shop);
//            mPresenter.getShopList(pageNum, pageSize);
//        } else if (action == ACTION_CHANGE_COMPANY) {
//            rlRoot.setVisibility(View.VISIBLE);
//            titleBar.getAppTitle().setText(R.string.str_choose_company);
//            mPresenter.getCompanyList(pageNum, pageSize);
//        } else if (action == ACTION_CHANGE_SHOP) {
//            rlRoot.setVisibility(View.VISIBLE);
//            titleBar.setVisibility(View.VISIBLE);
//            mPresenter.getShopList(pageNum, pageSize);
//        }
    }

//    @Click(R.id.rl_back_1)
//    void backClick() {
//        finish();
//    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {

    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }

    @Override
    public void getShopListSuccess(List<ShopListResp.ShopInfo> shopList) {
        tvNoData.setVisibility(View.GONE);
        initShopList(shopList);
    }

    //    @Override
//    public void getShopListSuccess(int total, int returnCount, List<ShopListResp.ShopInfo> data) {
//        int n = data.size();
//        if (n == 1) {
//            gotoMainActivity(data.get(0));
//        } else if (n > 0) {
//            initShopList(data);
//        } else {
//            showNoData();
//        }
//    }

    private void initShopList(final List<ShopListResp.ShopInfo> data) {
        rvChoose.setAdapter(new CommonListAdapter<ShopListResp.ShopInfo>(context,
                R.layout.item_shop_list, data) {
            @Override
            public void convert(ViewHolder holder, final ShopListResp.ShopInfo shopInfo) {
                holder.setText(R.id.tv_shop, shopInfo.getShop_name());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SpUtils.setShopId(shopInfo.getShop_id());
                        SpUtils.setShopName(shopInfo.getShop_name());
                        BaseNotification.newInstance().postNotificationName(NotificationConstant.shopSwitched);
                        finish();
                    }
                });
            }
        });
    }

//    private void gotoMainActivity(ShopListResp.ShopInfo shopListBean) {
//        SpUtils.setLoginStatus(true);
//        SpUtils.setShopId(shopListBean.getShop_id());
//        SpUtils.setStoreName(shopListBean.getShop_name());
//        MainActivity_.intent(context).flags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK).start();
//    }
//
//    @Override
//    public void getShopListFail(int code, String msg) {
//
//    }
//
//    @Override
//    public void getCompanyListSuccess(int total, int returnCount, List<CompanyInfoResp.CompanyListBean> data) {
//        if (data.size() == 1) {
//            mPresenter.getShopList(pageNum, pageSize);
//        } else if (data.size() > 0) {
//            initCompanyList(data);
//        } else {
//            showNoData();
//        }
//    }
//
//    private void initCompanyList(final List<CompanyInfoResp.CompanyListBean> data) {
//        rlRoot.setVisibility(View.VISIBLE);
//        rvChoose.setAdapter(new CommonListAdapter<CompanyInfoResp.CompanyListBean>(context,
//                R.layout.item_company_shop, data) {
//            @Override
//            public void convert(ViewHolder holder, final CompanyInfoResp.CompanyListBean companyListBean) {
//                ListItem listItem = holder.getView(R.id.list_item);
//                listItem.setLeftPrompt(companyListBean.getCompany_name());
//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        SpUtils.setCompanyId(companyListBean.getCompany_id());
//                        SpUtils.setCompanyName(companyListBean.getCompany_name());
//                        ChooseShopActivity_.intent(context).action(ACTION_LOGIN_CHOOSE_SHOP).start();
//                    }
//                });
//            }
//        });
//    }
//
//    @Override
//    public void getCompanyListFail(int code, String msg) {
//
//    }
//
//    private void postNotification(final long shopId, final String shopName) {
//        SpUtils.setShopId(shopId);
//        SpUtils.setStoreName(shopName);
//        BaseNotification.newInstance().postNotificationName(NotificationConstant.shopInfoGet);
//    }
//
//    @UiThread
//    void showNoData() {
//        rlRoot.setVisibility(View.VISIBLE);
//        tvNoData.setVisibility(View.VISIBLE);
//        if (action == ACTION_LOGIN_CHOOSE_COMPANY) {
//            tvNoData.setText(R.string.str_tip_add_merchant);
//        } else if (action == ACTION_LOGIN_CHOOSE_SHOP) {
//            tvNoData.setText(R.string.str_tip_add_shop);
//        }
//    }

}
