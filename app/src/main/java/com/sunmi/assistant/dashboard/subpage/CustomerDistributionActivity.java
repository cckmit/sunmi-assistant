package com.sunmi.assistant.dashboard.subpage;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.data.AgeCustomer;
import com.sunmi.assistant.dashboard.data.GenderCustomer;
import com.sunmi.assistant.dashboard.data.NewOldCustomer;
import com.sunmi.assistant.dashboard.util.Constants;
import com.sunmi.ipc.face.model.FaceAge;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.constant.CommonConstants;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.FilterItem;
import sunmi.common.model.ShopInfo;
import sunmi.common.notification.BaseNotification;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.DropdownAdapterNew;
import sunmi.common.view.DropdownMenuNew;
import sunmi.common.view.SmRecyclerView;
import sunmi.common.view.dialog.CommonDialog;

/**
 * Description: 客流分析-客群分布-下钻列表
 *
 * @author linyuanpeng on 2020-03-04.
 */
@EActivity(R.layout.activity_customer_distribution)
public class CustomerDistributionActivity extends BaseMvpActivity<CustomerDistributionPresenter>
        implements CustomerDistributionContract.View {

    @ViewById(R.id.dm_motion_customer)
    DropdownMenuNew dmMotionSale;
    @ViewById(R.id.dropdown_title)
    TextView dropdownTitle;
    @ViewById(R.id.dropdown_img)
    ImageView dropdownImg;
    @ViewById(R.id.rv_customer)
    SmRecyclerView rvCustomer;
    @ViewById(R.id.rb_new_old)
    RadioButton rbNewOld;
    @ViewById(R.id.rb_gender)
    RadioButton rbGender;
    @ViewById(R.id.rb_age)
    RadioButton rbAge;
    @ViewById(R.id.layout_error)
    View layoutError;

    @Extra
    long startTime;
    @Extra
    int period;
    @Extra
    int dataType;

    private List<NewOldCustomer> newOldCustomers = new ArrayList<>();
    private List<GenderCustomer> genderCustomers = new ArrayList<>();
    private List<AgeCustomer> ageCustomers = new ArrayList<>();
    private List<FilterItem> newOldFilters;
    private List<FilterItem> genderFilters;
    private List<FilterItem> ageFilters;
    private Adapter adapter;
    private DropdownAdapterNew dropdownAdapter;
    private SparseArray<ShopInfo> shopInfoData;
    private CommonDialog dialog;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        initRadioButton();
        NewOldCustomer.isDesc = true;
        GenderCustomer.isDesc = true;
        AgeCustomer.isDesc = true;
        mPresenter = new CustomerDistributionPresenter(startTime, period);
        mPresenter.attachView(this);
        mPresenter.getShopList();
        mPresenter.ageRange();
        mPresenter.getCustomerShopAgeDistribution();
        mPresenter.getCustomerShopAgeGenderDistribution();
        showLoadingDialog();
        rvCustomer.init(R.drawable.shap_line_divider);
        adapter = new Adapter();
        rvCustomer.setAdapter(adapter);
        initDialog();
        initFilterData();
        initFilters();
    }

    private void initRadioButton() {
        switch (dataType) {
            case Constants.DATA_TYPE_NEW_OLD:
                rbNewOld.setChecked(true);
                break;
            case Constants.DATA_TYPE_GENDER:
                rbGender.setChecked(true);
                break;
            case Constants.DATA_TYPE_AGE:
                rbAge.setChecked(true);
                break;
            default:
                break;
        }
    }

    private void initSort(boolean isDesc) {
        if (isDesc) {
            dropdownTitle.setText(R.string.str_sort_desc);
        } else {
            dropdownTitle.setText(R.string.str_sort_asce);
        }
        dropdownImg.setSelected(!isDesc);
    }

    private void initFilters() {
        dropdownAdapter = new DropdownAdapterNew(this);
        dropdownAdapter.setOnItemClickListener((adapter, model, position) -> {
            switch (dataType) {
                case Constants.DATA_TYPE_NEW_OLD:
                    NewOldCustomer.isSortByNew = (model.getId() == 1);
                    Collections.sort(newOldCustomers);
                    break;
                case Constants.DATA_TYPE_GENDER:
                    GenderCustomer.isSortByMale = (model.getId() == 1);
                    Collections.sort(genderCustomers);
                    break;
                case Constants.DATA_TYPE_AGE:
                    AgeCustomer.ageCode = model.getId();
                    Collections.sort(ageCustomers);
                    break;
                default:
                    break;
            }
            this.adapter.notifyDataSetChanged();
        });
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
            case Constants.DATA_TYPE_NEW_OLD:
                dropdownAdapter.setData(newOldFilters);
                break;
            case Constants.DATA_TYPE_GENDER:
                dropdownAdapter.setData(genderFilters);
                break;
            case Constants.DATA_TYPE_AGE:
                if (ageFilters != null) {
                    dropdownAdapter.setData(ageFilters);
                }
                break;
            default:
                break;
        }
    }

    private void initDialog() {
        dialog = new CommonDialog.Builder(context)
                .setTitle(R.string.ipc_setting_tip)
                .setMessage(R.string.dashboard_tip_no_shop)
                .setConfirmButton(R.string.str_confirm).create();
    }

    private void switchType(int type) {
        if (dataType == type) {
            return;
        }
        dmMotionSale.dismiss(false);
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
        initSort(NewOldCustomer.isDesc);
        switchType(Constants.DATA_TYPE_NEW_OLD);
    }

    @Click(R.id.rb_gender)
    void genderClick() {
        if (genderCustomers.size() <= 0) {
            showDarkLoading();
            mPresenter.getCustomerShopAgeGenderDistribution();
        }
        initSort(GenderCustomer.isDesc);
        switchType(Constants.DATA_TYPE_GENDER);
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
        initSort(AgeCustomer.isDesc);
        switchType(Constants.DATA_TYPE_AGE);
    }

    @Click(R.id.dm_motion_sort)
    void sortClick() {
        switch (dataType) {
            case Constants.DATA_TYPE_NEW_OLD:
                NewOldCustomer.isDesc = !NewOldCustomer.isDesc;
                initSort(NewOldCustomer.isDesc);
                Collections.sort(newOldCustomers);
                break;
            case Constants.DATA_TYPE_GENDER:
                GenderCustomer.isDesc = !GenderCustomer.isDesc;
                initSort(GenderCustomer.isDesc);
                Collections.sort(genderCustomers);
                break;
            case Constants.DATA_TYPE_AGE:
                if (ageFilters != null) {
                    AgeCustomer.isDesc = !AgeCustomer.isDesc;
                    initSort(AgeCustomer.isDesc);
                    Collections.sort(ageCustomers);
                }
                break;
            default:
                break;
        }
        adapter.notifyDataSetChanged();
    }

    @Click(R.id.btn_refresh)
    void refresh() {
        if (shopInfoData == null) {
            mPresenter.getShopList();
        }
        if (dataType == Constants.DATA_TYPE_GENDER) {
            mPresenter.getCustomerShopAgeGenderDistribution();
        } else {
            mPresenter.getCustomerShopAgeDistribution();
            if (dataType == Constants.DATA_TYPE_AGE && ageFilters == null) {
                mPresenter.ageRange();
            }
        }
        showLoadingDialog();
    }

    @Override
    public void ageRangeSuccess(SparseArray<FaceAge> ageMap) {
        if (dataType == Constants.DATA_TYPE_AGE) {
            hideLoadingDialog();
        }
        int size = ageMap.size();
        ageFilters = new ArrayList<>(size);
        String ageLabel = getString(R.string.dashboard_unit_age);
        for (int i = 0; i < size; i++) {
            FaceAge faceAge = ageMap.valueAt(i);
            ageFilters.add(new FilterItem(faceAge.getCode(), faceAge.getName() + ageLabel));
        }
        ageFilters.get(0).setChecked(true);
        AgeCustomer.ageCode = ageFilters.get(0).getId();
    }

    @Override
    public void ageRangeFail(int code, String msg) {
        if (dataType == Constants.DATA_TYPE_AGE) {
            hideLoadingDialog();
            layoutError.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void getCustomerShopAgeSuccess(List<NewOldCustomer> newOldCustomers, List<AgeCustomer> ageCustomers) {
        hideLoadingDialog();
        if (dataType == Constants.DATA_TYPE_AGE) {
            if (ageFilters != null && shopInfoData != null) {
                layoutError.setVisibility(View.GONE);
                this.ageCustomers.clear();
                this.ageCustomers.addAll(ageCustomers);
                Collections.sort(this.ageCustomers);
            }
        } else if (shopInfoData != null) {
            layoutError.setVisibility(View.GONE);
        }
        this.newOldCustomers.clear();
        this.newOldCustomers.addAll(newOldCustomers);
        Collections.sort(this.newOldCustomers);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void getCustomerShopAgeFail(int code, String msg) {
        hideLoadingDialog();
        switch (dataType) {
            case Constants.DATA_TYPE_NEW_OLD:
                if (newOldCustomers.size() <= 0) {
                    layoutError.setVisibility(View.VISIBLE);
                }
                break;
            case Constants.DATA_TYPE_AGE:
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
        if (shopInfoData != null) {
            layoutError.setVisibility(View.GONE);
        }
        this.genderCustomers.clear();
        this.genderCustomers.addAll(genderCustomers);
        Collections.sort(this.genderCustomers);
        if (dataType == Constants.DATA_TYPE_GENDER) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void getCustomerShopAgeGenderFail(int code, String msg) {
        hideLoadingDialog();
        if (dataType == Constants.DATA_TYPE_GENDER && genderCustomers.size() <= 0) {
            layoutError.setVisibility(View.VISIBLE);
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
            switch (dataType) {
                case Constants.DATA_TYPE_NEW_OLD:
                    NewOldCustomer newOldCustomer = newOldCustomers.get(i);
                    viewHolder.tvShopName.setText(newOldCustomer.getShopName());
                    viewHolder.tvCount.setText(String.valueOf(newOldCustomer.getCount()));
                    break;
                case Constants.DATA_TYPE_GENDER:
                    GenderCustomer genderCustomer = genderCustomers.get(i);
                    viewHolder.tvShopName.setText(genderCustomer.getShopName());
                    viewHolder.tvCount.setText(String.valueOf(genderCustomer.getCount()));
                    break;
                case Constants.DATA_TYPE_AGE:
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
                case Constants.DATA_TYPE_NEW_OLD:
                    return newOldCustomers.size();
                case Constants.DATA_TYPE_GENDER:
                    return genderCustomers.size();
                case Constants.DATA_TYPE_AGE:
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
                itemView.setOnClickListener(v -> {
                    int i = getAdapterPosition();
                    int shopId;
                    String shopName;
                    switch (dataType) {
                        case Constants.DATA_TYPE_NEW_OLD:
                            NewOldCustomer newOldCustomer = newOldCustomers.get(i);
                            shopId = newOldCustomer.getShopId();
                            shopName = newOldCustomer.getShopName();
                            break;
                        case Constants.DATA_TYPE_GENDER:
                            GenderCustomer genderCustomer = genderCustomers.get(i);
                            shopId = genderCustomer.getShopId();
                            shopName = genderCustomer.getShopName();
                            break;
                        case Constants.DATA_TYPE_AGE:
                            AgeCustomer ageCustomer = ageCustomers.get(i);
                            shopId = ageCustomer.getShopId();
                            shopName = ageCustomer.getShopName();
                            break;
                        default:
                            shopId = SpUtils.getShopId();
                            shopName = SpUtils.getShopName();
                            break;
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
