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
import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class OverviewVolumeCard extends BaseRefreshCard<OverviewVolumeCard.Model, CustomerCountResp> {

    private static OverviewVolumeCard sInstance;


    private OverviewVolumeCard(Presenter presenter, int source) {
        super(presenter, source);
    }

    public static OverviewVolumeCard get(Presenter presenter, int source) {
        if (sInstance == null) {
            sInstance = new OverviewVolumeCard(presenter, source);
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
        return R.layout.dashboard_recycle_item_customer_volume;
    }

    @Override
    protected Call<BaseResponse<CustomerCountResp>> load(int companyId, int shopId, int period, CardCallback callback) {
        loadCustomer(companyId, shopId, period, callback);
        return null;
    }

    private void loadCustomer(int companyId, int shopId, int period, CardCallback callback) {
        SunmiStoreApi.getInstance().getCustomer(companyId, shopId, period,
                new RetrofitCallback<CustomerCountResp>() {
                    @Override
                    public void onSuccess(int code, String msg, CustomerCountResp data) {
                        if (data == null) {
                            onFail(code, msg, null);
                            return;
                        }
                        Model model = getModel();
                        model.latestPassCount = data.getLatestPassCount();
                        model.latestCount = data.getLatestCount();
                        callback.onSuccess();
                    }

                    @Override
                    public void onFail(int code, String msg, CustomerCountResp data) {
                        callback.onFail(code, msg, data);
                    }
                });
    }


    @Override
    protected Model createModel() {
        return new Model();
    }

    @Override
    protected void setupModel(Model model, CustomerCountResp response) {

    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        TextView latestPassCount = holder.getView(R.id.tv_volume_pass_by);//路过
        TextView latestCount = holder.getView(R.id.tv_volume_enter);//进店
        TextView totals = holder.getView(R.id.tv_volume_totals);//总客流
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
        int latestPassCount;
        //进店
        int latestCount;

        String passByVolume() {
            return String.format(Locale.getDefault(), "%d", latestPassCount);
        }

        String enterVolume() {
            return String.format(Locale.getDefault(), "%d", latestCount);
        }

        String totalsVolume() {
            return String.format(Locale.getDefault(), "%d", latestPassCount + latestCount);
        }

        int getPercent() {
            if (latestCount == 0) {
                return 0;
            } else {
                return (int) (((double) latestCount / (latestPassCount + latestCount)) * 100);
            }
        }
    }
}
