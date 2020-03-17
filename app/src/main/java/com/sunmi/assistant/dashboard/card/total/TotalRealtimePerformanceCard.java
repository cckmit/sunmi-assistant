package com.sunmi.assistant.dashboard.card.total;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.constraint.Group;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.card.BaseRefreshCard;
import com.sunmi.assistant.dashboard.data.Callback;
import com.sunmi.assistant.dashboard.data.DashboardCondition;
import com.sunmi.assistant.dashboard.data.DashboardModelImpl;
import com.sunmi.assistant.dashboard.subpage.PerformanceRankActivity_;
import com.sunmi.assistant.dashboard.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import sunmi.common.base.adapter.CommonAdapter;
import sunmi.common.base.adapter.ViewHolder;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.constant.CommonConstants;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.CustomerHistoryTrendResp;
import sunmi.common.model.CustomerShopDataResp;
import sunmi.common.model.Interval;
import sunmi.common.model.ShopInfo;
import sunmi.common.model.TotalRealTimeShopSalesResp;
import sunmi.common.notification.BaseNotification;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.BaseResponse;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;
import sunmi.common.view.dialog.CommonDialog;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class TotalRealtimePerformanceCard extends BaseRefreshCard<TotalRealtimePerformanceCard.Model, CustomerHistoryTrendResp> {

    private static TotalRealtimePerformanceCard sInstance;

    private static final int TYPE_CUSTOMER = 0;
    private static final int TYPE_SALES = 1;

    private DetailListAdapter mAdapter;
    private SparseArray<ShopInfo> shopList;

    private TotalRealtimePerformanceCard(Presenter presenter, DashboardCondition condition, int period, Interval periodTime) {
        super(presenter, condition, period, periodTime);
    }

    public static TotalRealtimePerformanceCard get(Presenter presenter, DashboardCondition condition, int period, Interval periodTime) {
        if (sInstance == null) {
            sInstance = new TotalRealtimePerformanceCard(presenter, condition, period, periodTime);
        } else {
            sInstance.reset(presenter, condition, period, periodTime);
        }
        return sInstance;
    }

    @Override
    public void init(Context context) {
    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_item_total_realtime_performance;
    }

    @Override
    protected Model createModel() {
        return new Model();
    }

    @Override
    protected Call<BaseResponse<CustomerHistoryTrendResp>> load(int companyId, int shopId, int period, Interval periodTime,
                                                                CardCallback callback) {
        DashboardModelImpl.get().loadShopList(companyId, new Callback<SparseArray<ShopInfo>>() {
            @Override
            public void onLoaded(SparseArray<ShopInfo> result) {
                shopList = result;
                if (mCondition.hasFs) {
                    loadCustomer(companyId, callback);
                } else if (mCondition.hasSaas) {
                    loadSales(companyId, callback);
                }
            }

            @Override
            public void onFail() {
                callback.onFail(-1, "Load shop list failed.", null);
            }
        });
        return null;
    }

    private void loadCustomer(int companyId, CardCallback callback) {
        SunmiStoreApi.getInstance().getTotalRealtimeCustomerByShop(companyId,
                new RetrofitCallback<CustomerShopDataResp>() {
                    @Override
                    public void onSuccess(int code, String msg, CustomerShopDataResp data) {
                        if (data == null || data.getList() == null) {
                            onFail(code, msg, data);
                            return;
                        }
                        List<Item> dataSet = getModel().dataSets.get(TYPE_CUSTOMER);
                        dataSet.clear();
                        List<CustomerShopDataResp.Item> list = data.getList();
                        Collections.sort(list, (o1, o2) -> o2.getTotalCount() - o1.getTotalCount());
                        int max = Math.min(5, list.size());
                        for (int i = 0; i < max; i++) {
                            CustomerShopDataResp.Item item = list.get(i);
                            dataSet.add(new Item(item.getShopId(), item.getShopName(), Math.max(0, item.getTotalCount())));
                        }
                        if (mCondition.hasSaas) {
                            loadSales(companyId, callback);
                        } else {
                            callback.onSuccess();
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, CustomerShopDataResp data) {
                        callback.onFail(code, msg, null);
                    }
                });
    }

    private void loadSales(int companyId, CardCallback callback) {
        SunmiStoreApi.getInstance().getTotalRealtimeSalesByShop(companyId, new RetrofitCallback<TotalRealTimeShopSalesResp>() {
            @Override
            public void onSuccess(int code, String msg, TotalRealTimeShopSalesResp data) {
                if (data == null || data.getList() == null) {
                    onFail(code, msg, data);
                    return;
                }
                List<Item> dataSet = getModel().dataSets.get(TYPE_SALES);
                dataSet.clear();
                List<TotalRealTimeShopSalesResp.Item> list = data.getList();
                Collections.sort(list, (o1, o2) -> Double.compare(o2.getOrderAmount(), o1.getOrderAmount()));
                int max = Math.min(5, list.size());
                for (int i = 0; i < max; i++) {
                    TotalRealTimeShopSalesResp.Item item = list.get(i);
                    dataSet.add(new Item(item.getShopId(), item.getShopName(), (float) item.getOrderAmount()));
                }
                callback.onSuccess();
            }

            @Override
            public void onFail(int code, String msg, TotalRealTimeShopSalesResp data) {
                callback.onFail(code, msg, null);
            }
        });
    }

    @NonNull
    @Override
    public BaseViewHolder<Model> onCreateViewHolder(@NonNull View view, @NonNull ItemType<Model, BaseViewHolder<Model>> type) {
        BaseViewHolder<Model> holder = super.onCreateViewHolder(view, type);
        Context context = view.getContext();

        holder.addOnClickListener(R.id.tv_dashboard_customer, (h, model, position) -> {
            if (model.type != TYPE_CUSTOMER) {
                model.type = TYPE_CUSTOMER;
                updateViews();
            }
        });
        holder.addOnClickListener(R.id.tv_dashboard_sales, (h, model, position) -> {
            if (model.type != TYPE_SALES) {
                model.type = TYPE_SALES;
                updateViews();
            }
        });
        holder.addOnClickListener(R.id.btn_more, (holder1, model, position) ->
                PerformanceRankActivity_.intent(context).mCondition(mCondition).start());

        ListView lv = holder.getView(R.id.lv_dashboard_list);
        mAdapter = new DetailListAdapter(context, shopList);
        lv.setDivider(ContextCompat.getDrawable(context, R.color.transparent));
        lv.setDividerHeight((int) context.getResources().getDimension(R.dimen.dp_8));
        lv.setAdapter(mAdapter);
        return holder;
    }

    @Override
    protected void setupModel(Model model, CustomerHistoryTrendResp response) {
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        Context context = holder.getContext();
        // 设置Tab样式
        setupLabelState(holder, model);
        View view = holder.itemView;
        List<Item> items = model.dataSets.get(model.type);
        mAdapter.setType(model.type);
        mAdapter.setDatas(items);
        mAdapter.notifyDataSetChanged();
        view.post(() -> {
            mAdapter.notifyDataSetChanged();
            holder.getView(R.id.lv_dashboard_list).requestLayout();
        });
    }

    @Override
    protected void showLoading(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        List<Item> items = model.dataSets.get(model.type);
        if (items.isEmpty()) {
            items.add(new Item());
        }
        for (Item item : items) {
            item.setLoading();
        }
        setupView(holder, model, position);
    }

    @Override
    protected void showError(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        List<Item> items = model.dataSets.get(model.type);
        if (items.isEmpty()) {
            items.add(new Item());
        }
        for (Item item : items) {
            item.setError();
        }
        setupView(holder, model, position);
    }

    private void setupLabelState(@NonNull BaseViewHolder<Model> holder, Model model) {
        // Get views
        TextView tabCustomer = holder.getView(R.id.tv_dashboard_customer);
        TextView tabSales = holder.getView(R.id.tv_dashboard_sales);

        // Set button selected
        tabSales.setVisibility(mCondition.hasSaas ? View.VISIBLE : View.GONE);
        tabCustomer.setVisibility(mCondition.hasFs ? View.VISIBLE : View.GONE);
        tabCustomer.setSelected(model.type == TYPE_CUSTOMER);
        tabCustomer.setTypeface(null, model.type == TYPE_CUSTOMER ? Typeface.BOLD : Typeface.NORMAL);
        tabSales.setSelected(model.type == TYPE_SALES);
        tabSales.setTypeface(null, model.type == TYPE_SALES ? Typeface.BOLD : Typeface.NORMAL);
    }

    private static class DetailListAdapter extends CommonAdapter<Item> {

        private int type;
        private final SparseArray<ShopInfo> shopList;

        private CommonDialog denyDialog;

        private DetailListAdapter(Context context, SparseArray<ShopInfo> shopList) {
            super(context, R.layout.dashboard_item_total_realtime_performance_item);
            this.shopList = shopList;
            denyDialog = new CommonDialog.Builder(context)
                    .setTitle(R.string.ipc_setting_tip)
                    .setMessage(R.string.dashboard_tip_no_shop)
                    .setConfirmButton(R.string.str_confirm).create();
        }

        @Override
        public void convert(ViewHolder holder, Item item) {
            if (item.state == Item.STATE_LOADING) {
                showLoading(holder);
                return;
            }
            showContent(holder);
            holder.getConvertView().setOnClickListener(v -> {
                if (shopList.indexOfKey(item.shopId) < 0) {
                    denyDialog.show();
                } else {
                    new CommonDialog.Builder(v.getContext())
                            .setTitle(R.string.ipc_setting_tip)
                            .setMessage(R.string.dashboard_tip_subpage)
                            .setCancelButton(R.string.sm_cancel)
                            .setConfirmButton(R.string.sm_watch, (dialog, which) -> {
                                SpUtils.setShopId(item.shopId);
                                SpUtils.setShopName(item.shopName);
                                SpUtils.setPerspective(CommonConstants.PERSPECTIVE_SHOP);
                                BaseNotification.newInstance().postNotificationName(CommonNotifications.perspectiveSwitch);
                            }).create().show();
                }
            });

            TextView tvRank = holder.getView(R.id.tv_rank);
            TextView tvTitle = holder.getView(R.id.tv_title);
            TextView tvValue = holder.getView(R.id.tv_value);

            tvRank.setSelected(holder.getPosition() == 0);
            tvTitle.setSelected(holder.getPosition() == 0);
            tvValue.setSelected(holder.getPosition() == 0);

            if (item.state == Item.STATE_ERROR) {
                tvRank.setText(Utils.DATA_NONE);
                tvTitle.setText(Utils.DATA_NONE);
                tvValue.setText(Utils.DATA_NONE);
            } else {
                tvRank.setText(String.valueOf(holder.getPosition() + 1));
                tvTitle.setText(item.shopName);
                tvValue.setText(Utils.formatNumber(mContext, item.value, type == TYPE_SALES, true));
            }
        }

        public void setType(int type) {
            this.type = type;
        }

        private void showContent(ViewHolder holder) {
            Group content = holder.getView(R.id.group_content);
            View skeleton = holder.getView(R.id.view_skeleton);
            content.setVisibility(View.VISIBLE);
            skeleton.setVisibility(View.INVISIBLE);
        }

        private void showLoading(ViewHolder holder) {
            Group content = holder.getView(R.id.group_content);
            View skeleton = holder.getView(R.id.view_skeleton);
            content.setVisibility(View.INVISIBLE);
            skeleton.setVisibility(View.VISIBLE);
            holder.getConvertView().setOnClickListener(null);
        }
    }

    public static class Item {

        private static final int STATE_NORMAL = 0;
        private static final int STATE_LOADING = 1;
        private static final int STATE_ERROR = 2;

        private int state = STATE_NORMAL;

        private int shopId;
        private String shopName;
        private float value;

        private Item() {
        }

        public Item(int shopId, String shopName, float value) {
            this.shopId = shopId;
            this.shopName = shopName;
            this.value = value;
        }

        private void setLoading() {
            this.state = STATE_LOADING;
        }

        private void setError() {
            this.state = STATE_ERROR;
        }

        @Override
        @NonNull
        public String toString() {
            return "Item{" +
                    "shopId=" + shopId +
                    ", shopName='" + shopName + '\'' +
                    ", value=" + value +
                    '}';
        }
    }

    public static class Model extends BaseRefreshCard.BaseModel {
        private int type;
        private SparseArray<List<Item>> dataSets = new SparseArray<>(2);

        public Model() {
            dataSets.put(TYPE_CUSTOMER, new ArrayList<>(5));
            dataSets.put(TYPE_SALES, new ArrayList<>(5));
        }

        @Override
        public void init(DashboardCondition condition) {
            type = condition.hasFs ? TYPE_CUSTOMER : TYPE_SALES;
            for (int i = 0, size = dataSets.size(); i < size; i++) {
                int key = dataSets.keyAt(i);
                dataSets.get(key).clear();
            }
        }

    }
}
