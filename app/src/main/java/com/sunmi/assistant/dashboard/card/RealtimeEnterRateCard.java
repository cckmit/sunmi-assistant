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
        model.latestPassCount = response.getLatestPassCount();
        model.latestCount = response.getLatestCount();
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        TextView latestPassCount = holder.getView(R.id.tv_volume_pass_by);
        TextView latestCount = holder.getView(R.id.tv_volume_enter);
        TextView totals = holder.getView(R.id.tv_volume_totals);
        TextView percentVolume = holder.getView(R.id.tv_customer_volume_percent);
        ProgressBar pbVolume = holder.getView(R.id.pb_volume);
        latestPassCount.setText(model.passByVolume());
        latestCount.setText(model.enterVolume());
        totals.setText(model.totalsVolume());
        percentVolume.setText(String.format(Locale.getDefault(), "%d%%", model.getPercent()));
        pbVolume.setProgress(model.getPercent());
    }

    public static class Model extends BaseRefreshCard.BaseModel {
        //路过
        private int latestPassCount;
        //进店
        private int latestCount;

        private String passByVolume() {
            return String.valueOf(latestPassCount);
        }

        private String enterVolume() {
            return String.valueOf(latestCount);
        }

        private String totalsVolume() {
            return String.valueOf(latestPassCount + latestCount);
        }

        private int getPercent() {
            if (latestCount == 0 || latestPassCount + latestCount == 0) {
                return 0;
            } else {
                return (int) ((float) latestCount * 100 / (latestPassCount + latestCount));
            }
        }
    }
}
