package com.sunmi.assistant.dashboard.subpage;

import android.content.Context;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.assistant.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sunmi.common.base.BaseActivity;
import sunmi.common.model.CustomerShopDataResp;
import sunmi.common.model.FilterItem;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.DropdownAdapterNew;
import sunmi.common.view.DropdownAnimNew;
import sunmi.common.view.DropdownMenuNew;
import sunmi.common.view.SmRecyclerView;
import sunmi.common.view.ViewHolder;

@EActivity(R.layout.activity_performance_rank)
public class PerformanceRankActivity extends BaseActivity {

    @ViewById(R.id.dm_motion_sale)
    DropdownMenuNew dmMotionSale;
    @ViewById(R.id.dropdown_title)
    TextView dropdownTitle;
    @ViewById(R.id.dropdown_img)
    ImageView dropdownImg;
    @ViewById(R.id.rv_rank)
    SmRecyclerView rvRank;
    @ViewById(R.id.layout_error)
    View layoutError;

    private final int ID_COUNT = 1;
    private final int ID_AMOUNT = 2;

    private DropdownAdapterNew saleAdapter;
    private Adapter adapter;
    private List<CustomerShopDataResp.Item> dataList = new ArrayList<>();

    @AfterViews
    void init() {
        rvRank.init(R.drawable.shap_line_divider);
        CustomerShopDataResp.isSortByCount = true;
        CustomerShopDataResp.isDesc = true;
        initSort();
        initFilters();
        getData();
    }

    private void initSort() {
        if (CustomerShopDataResp.isDesc) {
            dropdownTitle.setText(R.string.str_sort_desc);
        } else {
            dropdownTitle.setText(R.string.str_sort_asce);
        }
        dropdownImg.setSelected(!CustomerShopDataResp.isDesc);
    }

    private void initFilters() {
        saleAdapter = new DropdownAdapterNew(this);
        saleAdapter.setOnItemClickListener((adapter, model, position) -> {
            CustomerShopDataResp.isSortByCount = (model.getId() == ID_COUNT);
            initAdapter();
        });
        dmMotionSale.setAnim(new DropdownAnimNew());
        dmMotionSale.setAdapter(saleAdapter);
        initFilterData();
    }

    private void initFilterData() {
        List<FilterItem> filterItems = new ArrayList<>(2);
        filterItems.add(new FilterItem(ID_COUNT, getString(R.string.dashboard_var_customer_volume), true));
        filterItems.add(new FilterItem(ID_AMOUNT, getString(R.string.dashboard_var_sales_amount)));
        saleAdapter.setData(filterItems);
    }

    private void getData() {
        SunmiStoreApi.getInstance().getTotalSaleShopData(SpUtils.getCompanyId(), new RetrofitCallback<CustomerShopDataResp>() {
            @Override
            public void onSuccess(int code, String msg, CustomerShopDataResp data) {
                layoutError.setVisibility(View.GONE);
                if (data != null && data.getShopList().size() > 0) {
                    dataList = data.getShopList();
                    initAdapter();
                }
            }

            @Override
            public void onFail(int code, String msg, CustomerShopDataResp data) {
                layoutError.setVisibility(View.VISIBLE);
            }
        });
    }

    @UiThread
    protected void initAdapter() {
        Collections.sort(dataList);
        if (adapter == null) {
            adapter = new Adapter(context, dataList);
            rvRank.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Click(R.id.btn_refresh)
    void refresh() {
        getData();
    }

    @Click(R.id.dm_motion_customer)
    void sortClick(){
        CustomerShopDataResp.isDesc = ! CustomerShopDataResp.isDesc;
        initSort();
        initAdapter();
    }

    private class Adapter extends CommonListAdapter<CustomerShopDataResp.Item> {

        public Adapter(Context context, List<CustomerShopDataResp.Item> list) {
            super(context, R.layout.item_performance_rank, list);
        }

        @Override
        public void convert(ViewHolder holder, CustomerShopDataResp.Item item) {
            holder.setText(R.id.tv_shop_name, item.getShopName());
            if (CustomerShopDataResp.isSortByCount) {
                holder.setText(R.id.tv_count, String.valueOf(item.getOrderCount()));
            } else {
                holder.setText(R.id.tv_count, String.valueOf(item.getOrderAmount()));
            }
        }
    }
}
