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
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import sunmi.common.base.BaseMvpActivity;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.DropdownAdapterNew;
import sunmi.common.view.DropdownAnimNew;
import sunmi.common.view.DropdownMenuNew;
import sunmi.common.view.SmRecyclerView;

@EActivity(R.layout.activity_customer_distribution)
public class CustomerDistributionActivity extends BaseMvpActivity<CustomerDistributionPresenter>
        implements CustomerDistributionContract.View {

    private static final int DATA_TYPE_NEW_OLD = 0;
    private static final int DATA_TYPE_GENDER = 1;
    private static final int DADA_TYPE_AGE = 2;

    @ViewById(R.id.dm_motion_customer)
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
    String startTime;
    @Extra
    int type;

    private int dataType;
    private List<NewOldCustomer> newOldCustomers = new ArrayList<>();
    private List<GenderCustomer> genderCustomers = new ArrayList<>();
    private List<AgeCustomer> ageCustomers = new ArrayList<>();
    private Adapter adapter;
    private DropdownAdapterNew dropdownAdapter;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        dataType = 0;
        mPresenter = new CustomerDistributionPresenter(startTime, type);
        mPresenter.attachView(this);
        mPresenter.ageRange();
        mPresenter.getCustomerShopAgeDistribution();
        mPresenter.getCustomerShopAgeGenderDistribution();
        rvRank.init(R.drawable.shap_line_divider);
        adapter = new Adapter();
        rvRank.setAdapter(adapter);
        initSort(true);
    }

    private void initSort(boolean isDesc) {
        NewOldCustomer.isDesc = isDesc;
        GenderCustomer.isDesc = isDesc;
        AgeCustomer.isDesc = isDesc;
    }

    private void initFilters() {
        dropdownAdapter = new DropdownAdapterNew(this);
        dropdownAdapter.setOnItemClickListener((adapter, model, position) -> {
            switch (dataType) {
                case DATA_TYPE_NEW_OLD:
                    NewOldCustomer.isSortByOld = (model.getId() == 1);
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
        initFilterData();
    }

    private void initFilterData() {

    }

    @Click(R.id.rb_new_old)
    void newOldClick() {

    }

    @Click(R.id.rb_gender)
    void genderClick() {

    }

    @Click(R.id.rb_age)
    void ageClick() {

    }

    @Click(R.id.dm_motion_sort)
    void sortClick() {

    }


    @Override
    public void ageRangeSuccess(SparseArray<FaceAge> ageMap) {

    }

    @Override
    public void ageRangeFail(int code, String msg) {

    }

    @Override
    public void getCustomerShopAgeSuccess(List<NewOldCustomer> newOldCustomers, List<AgeCustomer> ageCustomers) {
        this.newOldCustomers.clear();
        this.newOldCustomers.addAll(newOldCustomers);
        this.ageCustomers.clear();
        this.ageCustomers.addAll(ageCustomers);
    }

    @Override
    public void getCustomerShopAgeFail(int code, String msg) {

    }

    @Override
    public void getCustomerShopAgeGenderSuccess(List<GenderCustomer> genderCustomers) {
        this.genderCustomers.clear();
        this.genderCustomers.addAll(genderCustomers);
    }

    @Override
    public void getCustomerShopAgeGenderFail(int code, String msg) {

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
