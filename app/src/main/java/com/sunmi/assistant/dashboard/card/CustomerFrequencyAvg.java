package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.sunmi.assistant.dashboard.BaseRefreshCard;
import com.sunmi.assistant.dashboard.Constants;
import com.sunmi.ipc.face.model.FaceAge;
import com.sunmi.ipc.model.FaceAgeRangeResp;
import com.sunmi.ipc.rpc.IpcCloudApi;

import java.util.List;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.model.CustomerFrequencyAvgResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.BaseResponse;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.CacheManager;

/**
 * @author yinhui
 * @date 2020-01-15
 */
public class CustomerFrequencyAvg extends BaseRefreshCard<CustomerFrequencyAvg.Model, CustomerFrequencyAvgResp> {

    private static CustomerFrequencyAvg sInstance;

    private SparseArray<FaceAge> mAgeMap;

    private CustomerFrequencyAvg(Presenter presenter, int source) {
        super(presenter, source);
    }

    public static CustomerFrequencyAvg get(Presenter presenter, int source) {
        if (sInstance == null) {
            sInstance = new CustomerFrequencyAvg(presenter, source);
        } else {
            sInstance.reset(presenter, source);
        }
        return sInstance;
    }

    @Override
    public int getLayoutId(int type) {
        return 0;
    }

    @Override
    public void init(Context context) {
    }

    @Override
    protected Call<BaseResponse<CustomerFrequencyAvgResp>> load(int companyId, int shopId, int period, CardCallback callback) {
        if (mAgeMap == null) {
            loadAgeList(companyId, shopId, period, callback);
        } else {
            loadAvg(companyId, shopId, period, callback);
        }
        return null;
    }

    private void loadAgeList(int companyId, int shopId, int period, CardCallback callback) {
        mAgeMap = CacheManager.get().get(CacheManager.CACHE_AGE_NAME);
        if (mAgeMap != null) {
            loadAvg(companyId, shopId, period, callback);
            return;
        }
        IpcCloudApi.getInstance().getFaceAgeRange(companyId, shopId, new RetrofitCallback<FaceAgeRangeResp>() {
            @Override
            public void onSuccess(int code, String msg, FaceAgeRangeResp data) {
                if (data == null || data.getAgeRangeList() == null) {
                    onFail(code, msg, data);
                    return;
                }
                List<FaceAge> list = data.getAgeRangeList();
                mAgeMap = new SparseArray<>(list.size());
                int size = 4;
                for (FaceAge age : list) {
                    mAgeMap.put(age.getCode(), age);
                    size += age.getName().length() * 2 + 8;
                }
                CacheManager.get().put(CacheManager.CACHE_AGE_NAME, mAgeMap, size);
                loadAvg(companyId, shopId, period, callback);
            }

            @Override
            public void onFail(int code, String msg, FaceAgeRangeResp data) {
                callback.onFail(code, msg, null);
            }
        });
    }

    private void loadAvg(int companyId, int shopId, int period, CardCallback callback) {
        SunmiStoreApi.getInstance().getCustomerFrequencyAvg(companyId, shopId, period, callback);
    }

    @Override
    protected Model createModel() {
        return new Model();
    }

    @Override
    protected void setupModel(Model model, CustomerFrequencyAvgResp response) {
        // Reset model data
        model.maxAvg = 0;
        model.maleAvg = 0;
        model.femaleAvg = 0;
        if (model.ageMap == null) {
            model.ageMap = new SparseArray<>(mAgeMap.size());
            for (int i = 0, size = mAgeMap.size(); i < size; i++) {
                model.ageMap.put(mAgeMap.keyAt(i), new Item());
            }
        } else {
            for (int i = 0, size = model.ageMap.size(); i < size; i++) {
                Item item = model.ageMap.valueAt(i);
                item.maleFrequency = 0f;
                item.femaleFrequency = 0f;
            }
        }
        // Check response
        if (response == null || response.getFrequencyList() == null) {
            return;
        }
        // Arrange response data & setup model
        List<CustomerFrequencyAvgResp.Item> list = response.getFrequencyList();
        int maleTotal = 0;
        int maleUniqueTotal = 0;
        int femaleTotal = 0;
        int femaleUniqueTotal = 0;
        for (CustomerFrequencyAvgResp.Item bean : list) {
            Item item = model.ageMap.get(bean.getAgeRangeCode());
            if (item == null) {
                continue;
            }
            float frequency = bean.getUniqPassengerCount() <= 0 ?
                    0f : (float) bean.getPassengerCount() / bean.getUniqPassengerCount();
            model.maxAvg = Math.max(model.maxAvg, frequency);
            if (bean.getGender() == Constants.GENDER_MALE) {
                item.maleFrequency = frequency;
                maleTotal += bean.getPassengerCount();
                maleUniqueTotal += bean.getUniqPassengerCount();
            } else {
                item.femaleFrequency = frequency;
                femaleTotal += bean.getPassengerCount();
                femaleUniqueTotal += bean.getUniqPassengerCount();
            }
        }
        model.maleAvg = maleUniqueTotal == 0 ? 0f : (float) maleTotal / maleUniqueTotal;
        model.femaleAvg = femaleUniqueTotal == 0 ? 0f : (float) femaleTotal / femaleUniqueTotal;
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {

    }

    public static class Item {
        private float maleFrequency;
        private float femaleFrequency;
    }

    public static class Model extends BaseRefreshCard.BaseModel {
        private float maxAvg;
        private float maleAvg;
        private float femaleAvg;
        private SparseArray<Item> ageMap;
    }
}
