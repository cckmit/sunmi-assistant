package com.sunmi.assistant.dashboard.subpage;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.data.DashboardCondition;
import com.sunmi.assistant.dashboard.util.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import sunmi.common.base.BaseMvpActivity;
import sunmi.common.constant.CommonConstants;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.CustomerShopDataResp;
import sunmi.common.model.FilterItem;
import sunmi.common.model.ShopInfo;
import sunmi.common.model.TotalRealTimeShopSalesResp;
import sunmi.common.notification.BaseNotification;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.DropdownAdapterNew;
import sunmi.common.view.DropdownAnimNew;
import sunmi.common.view.DropdownMenuNew;
import sunmi.common.view.SmRecyclerView;
import sunmi.common.view.dialog.CommonDialog;

import static com.sunmi.assistant.R.layout.activity_performance_rank;

@EActivity(activity_performance_rank)
public class PerformanceRankActivity extends BaseMvpActivity<PerformanceRankPresenter>
        implements BGARefreshLayout.BGARefreshLayoutDelegate, PerformanceRankContract.View {

    @ViewById(R.id.dm_motion_sale)
    DropdownMenuNew dmMotionSale;
    @ViewById(R.id.dropdown_title)
    TextView dropdownTitle;
    @ViewById(R.id.dropdown_img)
    ImageView dropdownImg;
    @ViewById(R.id.rv_rank)
    SmRecyclerView rvRank;
    @ViewById(R.id.layout_refresh)
    BGARefreshLayout refreshView;
    @ViewById(R.id.layout_error)
    View layoutError;


    @Extra
    DashboardCondition mCondition;

    private final int ID_COUNT = 1;
    private final int ID_AMOUNT = 2;

    private DropdownAdapterNew saleAdapter;
    private Adapter adapter;
    private List<CustomerShopDataResp.Item> customerList = new ArrayList<>();
    private List<TotalRealTimeShopSalesResp.Item> saleList = new ArrayList<>();
    private int filterId = -1;
    private SparseArray<ShopInfo> shopInfoData;
    private CommonDialog dialog;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        mPresenter = new PerformanceRankPresenter();
        mPresenter.attachView(this);
        mPresenter.getShopList();
        rvRank.init(R.drawable.shap_line_divider);
        initSort(true);
        refreshView.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格(参数1：应用程序上下文，参数2：是否具有上拉加载更多功能)
        BGANormalRefreshViewHolder refreshViewHolder =
                new BGANormalRefreshViewHolder(context, false);
        refreshViewHolder.setRefreshingText(getString(R.string.str_refresh_loading));
        refreshViewHolder.setPullDownRefreshText(getString(R.string.str_refresh_pull));
        refreshViewHolder.setReleaseRefreshText(getString(R.string.str_refresh_release));
        refreshView.setRefreshViewHolder(refreshViewHolder); // 为了增加下拉刷新头部和加载更多的通用性，提供了以下可选配置选项
        refreshView.setIsShowLoadingMoreView(false);
        if (mCondition.hasFs) {
            filterId = ID_COUNT;
        }
        if (mCondition.hasSaas && !mCondition.hasFs) {
            filterId = ID_AMOUNT;
        }
        initDialog();
        initFilters();
        getData();
        showLoadingDialog();
    }

    private void initSort(boolean isDesc) {
        CustomerShopDataResp.isDesc = isDesc;
        TotalRealTimeShopSalesResp.isDesc = isDesc;
        if (isDesc) {
            dropdownTitle.setText(R.string.str_sort_desc);
        } else {
            dropdownTitle.setText(R.string.str_sort_asce);
        }
        dropdownImg.setSelected(!isDesc);
    }

    private void initFilters() {
        saleAdapter = new DropdownAdapterNew(this);
        saleAdapter.setOnItemClickListener((adapter, model, position) -> {
            filterId = model.getId();
            getData();
            initAdapter();
        });
        dmMotionSale.setAnim(new DropdownAnimNew());
        dmMotionSale.setAdapter(saleAdapter);
        initFilterData();
    }

    private void initFilterData() {
        List<FilterItem> filterItems = new ArrayList<>(2);
        if (mCondition.hasFs) {
            filterItems.add(new FilterItem(ID_COUNT, getString(R.string.dashboard_var_customer_volume)));
        }
        if (mCondition.hasSaas) {
            filterItems.add(new FilterItem(ID_AMOUNT, getString(R.string.dashboard_var_sales_amount)));
        }
        filterItems.get(0).setChecked(true);
        saleAdapter.setData(filterItems);
    }

    private void initDialog() {
        dialog = new CommonDialog.Builder(context)
                .setTitle(R.string.ipc_setting_tip)
                .setMessage(R.string.dashboard_tip_no_shop)
                .setConfirmButton(R.string.str_confirm).create();
    }

    private void getData() {
        if (filterId == ID_COUNT) {
            mPresenter.getTotalCustomerShopData();
        } else {
            mPresenter.getTotalSaleShopData();
        }
    }

    @UiThread
    protected void initAdapter() {
        if (filterId == ID_COUNT) {
            Collections.sort(customerList);
        } else {
            Collections.sort(saleList);
        }
        if (adapter == null) {
            adapter = new Adapter();
            rvRank.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Click(R.id.btn_refresh)
    void refresh() {
        if (shopInfoData == null) {
            mPresenter.getShopList();
        }
        getData();
        showLoadingDialog();
    }

    @Click(R.id.dm_motion_customer)
    void sortClick() {
        initSort(!CustomerShopDataResp.isDesc);
        initAdapter();
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        getData();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }

    @Override
    public void getCustomerSuccess(List<CustomerShopDataResp.Item> customers) {
        refreshView.endRefreshing();
        hideLoadingDialog();
        if (layoutError.isShown() && shopInfoData != null) {
            layoutError.setVisibility(View.GONE);
        }
        customerList.clear();
        customerList.addAll(customers);
        initAdapter();
    }

    @Override
    public void getCustomerFail(int code, String msg) {
        if (filterId == ID_COUNT && customerList.size() <= 0) {
            layoutError.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void getSaleSuccess(List<TotalRealTimeShopSalesResp.Item> sales) {
        refreshView.endRefreshing();
        hideLoadingDialog();
        if (layoutError.isShown() && shopInfoData != null) {
            layoutError.setVisibility(View.GONE);
        }
        saleList.clear();
        saleList.addAll(sales);
        initAdapter();
    }

    @Override
    public void getSaleFail(int code, String msg) {
        if (filterId == ID_AMOUNT && saleList.size() <= 0) {
            layoutError.setVisibility(View.GONE);
        }
    }

    @Override
    public void getShopListSuccess(SparseArray<ShopInfo> result) {
        shopInfoData = result;
    }

    @Override
    public void getShopListFail() {
        layoutError.setVisibility(View.VISIBLE);
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_performance_rank,
                    viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            if (filterId == ID_COUNT) {
                CustomerShopDataResp.Item customer = customerList.get(i);
                viewHolder.tvShopName.setText(customer.getShopName());
                viewHolder.tvCount.setText(String.valueOf(customer.getTotalCount()));
            } else {
                TotalRealTimeShopSalesResp.Item sale = saleList.get(i);
                viewHolder.tvShopName.setText(sale.getShopName());
                viewHolder.tvCount.setText(Utils.formatNumber(context, sale.getOrderAmount(), true, false));
            }
        }

        @Override
        public int getItemCount() {
            if (filterId == ID_COUNT) {
                return customerList.size();
            } else {
                return saleList.size();
            }
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvShopName;
            TextView tvCount;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvShopName = itemView.findViewById(R.id.tv_shop_name);
                tvCount = itemView.findViewById(R.id.tv_count);
                itemView.setOnClickListener(v -> {
                    int shopId;
                    String shopName;
                    if (filterId == ID_COUNT) {
                        CustomerShopDataResp.Item customer = customerList.get(getAdapterPosition());
                        shopId = customer.getShopId();
                        shopName = customer.getShopName();
                    } else {
                        TotalRealTimeShopSalesResp.Item sale = saleList.get(getAdapterPosition());
                        shopId = sale.getShopId();
                        shopName = sale.getShopName();
                    }
                    if (shopInfoData.get(shopId) != null) {
                        new CommonDialog.Builder(context)
                                .setTitle(R.string.ipc_setting_tip)
                                .setMessage(R.string.dashboard_tip_subpage)
                                .setCancelButton(R.string.sm_cancel)
                                .setConfirmButton(R.string.sm_watch, (dialog, which) -> {
                                    SpUtils.setShopId(shopId);
                                    SpUtils.setShopName(shopName);
                                    SpUtils.setPerspective(CommonConstants.PERSPECTIVE_SHOP);
                                    BaseNotification.newInstance().postNotificationName(CommonNotifications.perspectiveSwitch);
                                    finish();
                                }).create().show();
                    } else {
                        dialog.show();
                    }
                });
            }
        }
    }
}
