package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.BaseRefreshCard;

import java.util.Locale;

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


    private RealtimeEnterRateCard(Presenter presenter, int source) {
        super(presenter, source);
    }

    public static RealtimeEnterRateCard get(Presenter presenter, int source) {
        if (sInstance == null) {
            sInstance = new RealtimeEnterRateCard(presenter, source);
        } else {
            sInstance.reset(presenter, source);
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
        model.passConsumer = response.getLatestPassCount();
        model.consumer = response.getLatestCount();
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        TextView tvPassConsumer = holder.getView(R.id.tv_volume_pass_by);
        TextView tvConsumer = holder.getView(R.id.tv_volume_enter);
        TextView tvTotal = holder.getView(R.id.tv_volume_totals);
        TextView percentVolume = holder.getView(R.id.tv_customer_volume_percent);
        ProgressBar pbVolume = holder.getView(R.id.pb_volume);

        tvPassConsumer.setText(model.getPassConsumer());
        tvConsumer.setText(model.getConsumer());
        tvTotal.setText(model.getTotal());
        percentVolume.setText(String.format(Locale.getDefault(), "%.2f%%", model.getEnterRate() * 100));
        pbVolume.setProgress((int) (model.getEnterRate() * 100));
    }

    @Override
    protected void showLoading(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        super.showLoading(holder, model, position);
    }

    @Override
    protected void showError(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        TextView tvPassConsumer = holder.getView(R.id.tv_volume_pass_by);
        TextView tvConsumer = holder.getView(R.id.tv_volume_enter);
        TextView tvTotal = holder.getView(R.id.tv_volume_totals);
        TextView percentVolume = holder.getView(R.id.tv_customer_volume_percent);
        ProgressBar pbVolume = holder.getView(R.id.pb_volume);

        tvPassConsumer.setText(DATA_NONE);
        tvConsumer.setText(DATA_NONE);
        tvTotal.setText(DATA_NONE);
        percentVolume.setText(DATA_NONE);
        pbVolume.setProgress(0);
    }

    public static class Model extends BaseRefreshCard.BaseModel {
        //路过
        private int passConsumer;
        //进店
        private int consumer;

        private String getPassConsumer() {
            if (passConsumer < 0) {
                return DATA_NONE;
            } else {
                return String.valueOf(passConsumer);
            }
        }

        private String getConsumer() {
            if (consumer < 0) {
                return DATA_NONE;
            } else {
                return String.valueOf(consumer);
            }
        }

        private String getTotal() {
            if (passConsumer < 0 || consumer < 0) {
                return DATA_NONE;
            } else {
                return String.valueOf(passConsumer + consumer);
            }
        }

        private float getEnterRate() {
            if (consumer < 0 || passConsumer < 0 || consumer + passConsumer == 0) {
                return 0f;
            } else {
                return (float) consumer / (passConsumer + consumer);
            }
        }
    }
}
