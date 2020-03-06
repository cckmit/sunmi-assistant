package com.sunmi.assistant.dashboard.subpage;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.data.AgeCustomer;
import com.sunmi.assistant.dashboard.data.GenderCustomer;
import com.sunmi.assistant.dashboard.data.NewOldCustomer;
import com.sunmi.ipc.face.model.FaceAge;

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
import sunmi.common.model.FilterItem;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.DropdownAdapterNew;
import sunmi.common.view.DropdownAnimNew;
import sunmi.common.view.DropdownMenuNew;
import sunmi.common.view.SmRecyclerView;

/**
 * Description: 客流分析-客群分布-下钻列表
 *
 * @author linyuanpeng on 2020-03-04.
 */
@EActivity(R.layout.activity_customer_distribution)
public class CustomerDistributionActivity extends BaseMvpActivity<CustomerDistributionPresenter>
        implements CustomerDistributionContract.View, BGARefreshLayout.BGARefreshLayoutDelegate {

    private static final int DATA_TYPE_NEW_OLD = 0;
    private static final int DATA_TYPE_GENDER = 1;
    private static final int DADA_TYPE_AGE = 2;

    @ViewById(R.id.dm_motion_customer)
    DropdownMenuNew dmMotionSale;
    @ViewById(R.id.dropdown_title)
    TextView dropdownTitle;
    @ViewById(R.id.dropdown_img)
    ImageView dropdownImg;
    @ViewById(R.id.rv_customer)
    SmRecyclerView rvCustomer;
    @ViewById(R.id.layout_refresh)
    BGARefreshLayout refreshView;
    @ViewById(R.id.layout_error)
    View layoutError;

    @Extra
    String startTime;
    @Extra
    int type;

    private int dataType;
    private List<NewOldCustomer> newOldCustomers = new ArrayList<>();
    private List<GenderCustomer> genderCustomers = new ArrayList<>();
    private List<AgeCustomer> ageCustomers = new ArrayList<>();
    private List<FilterItem> newOldFilters;
    private List<FilterItem> genderFilters;
    private List<FilterItem> ageFilters;
    private Adapter adapter;
    private DropdownAdapterNew dropdownAdapter;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        dataType = DATA_TYPE_NEW_OLD;
        refreshView.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格(参数1：应用程序上下文，参数2：是否具有上拉加载更多功能)
        BGANormalRefreshViewHolder refreshViewHolder =
                new BGANormalRefreshViewHolder(context, false);
        refreshViewHolder.setRefreshingText(getString(R.string.str_refresh_loading));
        refreshViewHolder.setPullDownRefreshText(getString(R.string.str_refresh_pull));
        refreshViewHolder.setReleaseRefreshText(getString(R.string.str_refresh_release));
        refreshView.setRefreshViewHolder(refreshViewHolder); // 为了增加下拉刷新头部和加载更多的通用性，提供了以下可选配置选项
        refreshView.setIsShowLoadingMoreView(false);
        mPresenter = new CustomerDistributionPresenter(startTime, type);
        mPresenter.attachView(this);
        mPresenter.ageRange();
        mPresenter.getCustomerShopAgeDistribution();
        mPresenter.getCustomerShopAgeGenderDistribution();
        showLoadingDialog();
        rvCustomer.init(R.drawable.shap_line_divider);
        adapter = new Adapter();
        rvCustomer.setAdapter(adapter);
        initSort(true);
        initFilterData();
        initFilters();
    }

    private void initSort(boolean isDesc) {
        NewOldCustomer.isDesc = isDesc;
        GenderCustomer.isDesc = isDesc;
        AgeCustomer.isDesc = isDesc;
        if (isDesc) {
            dropdownTitle.setText(R.string.str_sort_desc);
        } else {
            dropdownTitle.setText(R.string.str_sort_asce);
        }
    }

    private void initFilters() {
        dropdownAdapter = new DropdownAdapterNew(this);
        dropdownAdapter.setOnItemClickListener((adapter, model, position) -> {
            switch (dataType) {
                case DATA_TYPE_NEW_OLD:
                    NewOldCustomer.isSortByNew = (model.getId() == 1);
                    break;
                case DATA_TYPE_GENDER:
                    GenderCustomer.isSortByMale = (model.getId() == 1);
                    break;
                case DADA_TYPE_AGE:
                    AgeCustomer.ageCode = model.getId();
                    break;
                default:
                    break;
            }
        });
        dmMotionSale.setAnim(new DropdownAnimNew());
        dmMotionSale.setAdapter(dropdownAdapter);
        setFilterData();
    }

    private void initFilterData() {
        newOldFilters = new ArrayList<>(2);
        newOldFilters.add(new FilterItem(1, getString(R.string.dashboard_var_new_count), true));
        newOldFilters.add(new FilterItem(2, getString(R.string.dashboard_var_old_count)));
        NewOldCustomer.isSortByNew = true;
        genderFilters = new ArrayList<>(2);
        genderFilters.add(new FilterItem(1, getString(R.string.dashboard_var_male_count), true));
        genderFilters.add(new FilterItem(2, getString(R.string.dashboard_var_female_count)));
        GenderCustomer.isSortByMale = true;
    }

    private void setFilterData() {
        switch (dataType) {
            case DATA_TYPE_NEW_OLD:
                dropdownAdapter.setData(newOldFilters);
                break;
            case DATA_TYPE_GENDER:
                dropdownAdapter.setData(genderFilters);
                break;
            case DADA_TYPE_AGE:
                if (ageFilters != null) {
                    dropdownAdapter.setData(ageFilters);
                }
                break;
            default:
                break;
        }
        dropdownAdapter.notifyDataSetChanged();
    }

    private void switchType(int type) {
        dataType = type;
        setFilterData();
        adapter.notifyDataSetChanged();
    }

    @Click(R.id.rb_new_old)
    void newOldClick() {
        if (newOldCustomers.size() <= 0) {
            showDarkLoading();
            mPresenter.getCustomerShopAgeDistribution();
        }
        switchType(DATA_TYPE_NEW_OLD);
    }

    @Click(R.id.rb_gender)
    void genderClick() {
        if (genderCustomers.size() <= 0) {
            showDarkLoading();
            mPresenter.getCustomerShopAgeGenderDistribution();
        }
        switchType(DATA_TYPE_GENDER);
    }

    @Click(R.id.rb_age)
    void ageClick() {
        if (ageFilters == null) {
            showDarkLoading();
            mPresenter.ageRange();
        }
        if (ageCustomers.size() <= 0) {
            showDarkLoading();
            mPresenter.getCustomerShopAgeDistribution();
        }
        switchType(DADA_TYPE_AGE);
    }

    @Click(R.id.dm_motion_sort)
    void sortClick() {
        initSort(!NewOldCustomer.isDesc);
        Collections.sort(newOldCustomers);
        Collections.sort(genderCustomers);
        if (ageFilters != null) {
            Collections.sort(ageCustomers);
        }
        adapter.notifyDataSetChanged();
    }

    @Click(R.id.btn_refresh)
    void refresh() {
        if (dataType == DATA_TYPE_GENDER) {
            mPresenter.getCustomerShopAgeGenderDistribution();
        } else {
            mPresenter.getCustomerShopAgeDistribution();
            if (dataType == DADA_TYPE_AGE && ageFilters == null) {
                mPresenter.ageRange();
            }
        }
    }

    @Override
    public void ageRangeSuccess(SparseArray<FaceAge> ageMap) {
        if (dataType == DADA_TYPE_AGE) {
            hideLoadingDialog();
        }
        int size = ageMap.size();
        ageFilters = new ArrayList<>(size);
        String ageLabel = getString(R.string.dashboard_unit_age);
        for (int i = 0; i < size; i++) {
            FaceAge faceAge = ageMap.valueAt(i);
            ageFilters.add(new FilterItem(faceAge.getCode(), faceAge.getName() + ageLabel, true));
        }
        AgeCustomer.ageCode = ageFilters.get(0).getId();
    }

    @Override
    public void ageRangeFail(int code, String msg) {
        if (dataType == DADA_TYPE_AGE) {
            hideLoadingDialog();
            layoutError.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void getCustomerShopAgeSuccess(List<NewOldCustomer> newOldCustomers, List<AgeCustomer> ageCustomers) {
        hideLoadingDialog();
        refreshView.endRefreshing();
        this.newOldCustomers.clear();
        this.newOldCustomers.addAll(newOldCustomers);
        Collections.sort(this.newOldCustomers);
        this.ageCustomers.clear();
        this.ageCustomers.addAll(ageCustomers);
        if (dataType == DADA_TYPE_AGE) {
            if (ageFilters != null) {
                Collections.sort(this.ageCustomers);
                layoutError.setVisibility(View.GONE);
            }
        } else {
            layoutError.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void getCustomerShopAgeFail(int code, String msg) {
        hideLoadingDialog();
        refreshView.endRefreshing();
        switch (dataType) {
            case DATA_TYPE_NEW_OLD:
                if (newOldCustomers.size() <= 0) {
                    layoutError.setVisibility(View.VISIBLE);
                }
                break;
            case DADA_TYPE_AGE:
                if (ageCustomers.size() <= 0) {
                    layoutError.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void getCustomerShopAgeGenderSuccess(List<GenderCustomer> genderCustomers) {
        hideLoadingDialog();
        refreshView.endRefreshing();
        layoutError.setVisibility(View.GONE);
        this.genderCustomers.clear();
        this.genderCustomers.addAll(genderCustomers);
        Collections.sort(this.genderCustomers);
        if (dataType == DATA_TYPE_GENDER) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void getCustomerShopAgeGenderFail(int code, String msg) {
        hideLoadingDialog();
        refreshView.endRefreshing();
        if (dataType == DATA_TYPE_GENDER && genderCustomers.size() <= 0) {
            layoutError.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        if (dataType == DATA_TYPE_GENDER) {
            mPresenter.getCustomerShopAgeGenderDistribution();
        } else {
            mPresenter.getCustomerShopAgeDistribution();
        }
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
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
            switch (dataType) {
                case DATA_TYPE_NEW_OLD:
                    NewOldCustomer newOldCustomer = newOldCustomers.get(i);
                    viewHolder.tvShopName.setText(newOldCustomer.getShopName());
                    viewHolder.tvCount.setText(String.valueOf(newOldCustomer.getCount()));
                    break;
                case DATA_TYPE_GENDER:
                    GenderCustomer genderCustomer = genderCustomers.get(i);
                    viewHolder.tvShopName.setText(genderCustomer.getShopName());
                    viewHolder.tvCount.setText(String.valueOf(genderCustomer.getCount()));
                    break;
                case DADA_TYPE_AGE:
                    AgeCustomer ageCustomer = ageCustomers.get(i);
                    viewHolder.tvShopName.setText(ageCustomer.getShopName());
                    viewHolder.tvCount.setText(String.valueOf(ageCustomer.getAgeCount()));
                    break;
                default:
                    break;
            }
        }

        @Override
        public int getItemCount() {
            switch (dataType) {
                case DATA_TYPE_NEW_OLD:
                    return newOldCustomers.size();
                case DATA_TYPE_GENDER:
                    return genderCustomers.size();
                case DADA_TYPE_AGE:
                    return ageCustomers.size();
                default:
                    return 0;
            }
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvShopName;
            TextView tvCount;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvShopName = itemView.findViewById(R.id.tv_shop_name);
                tvCount = itemView.findViewById(R.id.tv_count);
            }
        }
    }
}
