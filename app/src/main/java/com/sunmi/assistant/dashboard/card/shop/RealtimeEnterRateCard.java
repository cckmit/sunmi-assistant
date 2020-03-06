package com.sunmi.assistant.dashboard.card.shop;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.card.BaseRefreshCard;
import com.sunmi.assistant.dashboard.data.DashboardCondition;
import com.sunmi.assistant.dashboard.util.Utils;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.model.CustomerCountResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class RealtimeEnterRateCard extends BaseRefreshCard<RealtimeEnterRateCard.Model, CustomerCountResp> {

    private static RealtimeEnterRateCard sInstance;


    private RealtimeEnterRateCard(Presenter presenter, DashboardCondition condition) {
        super(presenter, condition);
    }

    public static RealtimeEnterRateCard get(Presenter presenter, DashboardCondition condition) {
        if (sInstance == null) {
            sInstance = new RealtimeEnterRateCard(presenter, condition);
        } else {
            sInstance.reset(presenter, condition);
        }
        return sInstance;
    }

    @Override
    public void init(Context context) {
    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_item_realtime_enter_rate;
    }

    @Override
    protected Call<BaseResponse<CustomerCountResp>> load(int companyId, int shopId, int period, CardCallback callback) {
        SunmiStoreApi.getInstance().getCustomer(companyId, shopId, period, callback);
        return null;
    }

    @Override
    protected Model createModel() {
        return new Model();
    }

    @Override
    protected void setupModel(Model model, CustomerCountResp response) {
        model.passCustomer = response.getLatestPassCount();
        model.customer = response.getLatestCount() + response.getLatestEntryHeadCount();
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        Context context = holder.getContext();
        TextView tvPassCustomer = holder.getView(R.id.tv_volume_pass_by);
        TextView tvCustomer = holder.getView(R.id.tv_volume_enter);
        TextView tvTotal = holder.getView(R.id.tv_volume_totals);
        TextView percentVolume = holder.getView(R.id.tv_customer_volume_percent);
        ProgressBar pbVolume = holder.getView(R.id.pb_volume);

        tvPassCustomer.setText(model.getPassCustomer(context));
        tvCustomer.setText(model.getCustomer(context));
        tvTotal.setText(model.getTotal(context));
        percentVolume.setText(Utils.formatPercent(model.getEnterRate(), true, true));
        pbVolume.setProgress((int) (model.getEnterRate() * 100));
    }

    @Override
    protected void showLoading(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        super.showLoading(holder, model, position);
    }

    @Override
    protected void showError(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        TextView tvPassCustomer = holder.getView(R.id.tv_volume_pass_by);
        TextView tvCustomer = holder.getView(R.id.tv_volume_enter);
        TextView tvTotal = holder.getView(R.id.tv_volume_totals);
        TextView percentVolume = holder.getView(R.id.tv_customer_volume_percent);
        ProgressBar pbVolume = holder.getView(R.id.pb_volume);

        tvPassCustomer.setText(Utils.DATA_NONE);
        tvCustomer.setText(Utils.DATA_NONE);
        tvTotal.setText(Utils.DATA_NONE);
        percentVolume.setText(Utils.DATA_NONE);
        pbVolume.setProgress(0);
    }

    public static class Model extends BaseRefreshCard.BaseModel {
        //路过
        private int passCustomer;
        //进店
        private int customer;

        private CharSequence getPassCustomer(Context context) {
            return Utils.formatNumber(context, passCustomer, false, true);
        }

        private CharSequence getCustomer(Context context) {
            return Utils.formatNumber(context, customer, false, true);
        }

        private CharSequence getTotal(Context context) {
            return Utils.formatNumber(context, passCustomer + customer, false, true);
        }

        private float getEnterRate() {
            if (customer > 0 && passCustomer >= 0) {
                return (float) customer / (passCustomer + customer);
            } else {
                return 0f;
            }
        }
    }
}
