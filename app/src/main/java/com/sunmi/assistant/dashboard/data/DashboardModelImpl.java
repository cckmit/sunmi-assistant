package com.sunmi.assistant.dashboard.data;

import android.util.SparseArray;

import com.sunmi.assistant.dashboard.util.Constants;
import com.sunmi.assistant.data.Callback;

import java.util.List;

import sunmi.common.model.CustomerHistoryResp;
import sunmi.common.model.IpcDevice;
import sunmi.common.model.SaasStatus;
import sunmi.common.model.ShopBundledCloudInfo;
import sunmi.common.utils.CommonHelper;

/**
 * @author yinhui
 * @date 2020-03-04
 */
public class DashboardModelImpl implements DashboardModel {

    private DashboardRepo repo;

    private int loadFlag;
    private Callback<DashboardCondition> callback;

    private static final class Holder {
        private static final DashboardModel INSTANCE = new DashboardModelImpl();
    }

    public static DashboardModel get() {
        return Holder.INSTANCE;
    }

    private DashboardModelImpl() {
        repo = DashboardRepoImpl.get();
    }

    @Override
    public void hasSaasState(int companyId, Callback<Integer> callback) {
        // 海外版无SaaS服务
        if (CommonHelper.isGooglePlay()) {
            callback.onLoaded(Constants.SAAS_STATE_NONE);
            return;
        }
        repo.getSaasStatus(companyId, new Callback<SparseArray<List<SaasStatus>>>() {
            @Override
            public void onLoaded(SparseArray<List<SaasStatus>> result) {
                int state = Constants.SAAS_STATE_NONE;
                for (int i = 0, size = result.size(); i < size; i++) {
                    List<SaasStatus> saasList = result.valueAt(i);
                    if (saasList.isEmpty()) {
                        continue;
                    }
                    state = Constants.SAAS_STATE_AUTH;
                    for (SaasStatus info : saasList) {
                        if (info.getImportStatus() == Constants.IMPORT_STATE_SUCCESS) {
                            callback.onLoaded(Constants.SAAS_STATE_IMPORT);
                            return;
                        }
                    }
                }
                callback.onLoaded(state);
            }

            @Override
            public void onFail() {
                callback.onFail();
            }
        });
    }

    @Override
    public void hasSaasState(int companyId, int shopId, Callback<Integer> callback) {
        // 海外版无SaaS服务
        if (CommonHelper.isGooglePlay()) {
            callback.onLoaded(Constants.SAAS_STATE_NONE);
            return;
        }
        repo.getSaasStatus(companyId, new Callback<SparseArray<List<SaasStatus>>>() {
            @Override
            public void onLoaded(SparseArray<List<SaasStatus>> result) {
                List<SaasStatus> saasList = result.get(shopId);
                int state;
                if (saasList == null || saasList.isEmpty()) {
                    state = Constants.SAAS_STATE_NONE;
                } else {
                    state = Constants.SAAS_STATE_AUTH;
                    for (SaasStatus info : saasList) {
                        if (info.getImportStatus() == Constants.IMPORT_STATE_SUCCESS) {
                            state = Constants.SAAS_STATE_IMPORT;
                            break;
                        }
                    }
                }
                callback.onLoaded(state);
            }

            @Override
            public void onFail() {
                callback.onFail();
            }
        });
    }

    @Override
    public void hasFs(int companyId, Callback<Boolean> callback) {
        repo.getIpcList(companyId, new Callback<SparseArray<List<IpcDevice>>>() {
            @Override
            public void onLoaded(SparseArray<List<IpcDevice>> result) {
                for (int i = 0, size = result.size(); i < size; i++) {
                    List<IpcDevice> deviceList = result.valueAt(i);
                    if (!deviceList.isEmpty()) {
                        callback.onLoaded(true);
                        return;
                    }
                }
                callback.onLoaded(false);
            }

            @Override
            public void onFail() {
                callback.onFail();
            }
        });
    }

    @Override
    public void hasFs(int companyId, int shopId, Callback<Boolean> callback) {
        repo.getIpcList(companyId, new Callback<SparseArray<List<IpcDevice>>>() {
            @Override
            public void onLoaded(SparseArray<List<IpcDevice>> result) {
                List<IpcDevice> deviceList = result.get(shopId);
                callback.onLoaded(deviceList != null && !deviceList.isEmpty());
            }

            @Override
            public void onFail() {
                callback.onFail();
            }
        });
    }

    @Override
    public void hasCustomer(int companyId, int shopId, Callback<Boolean> callback) {
        repo.getCustomer(companyId, shopId, new Callback<CustomerHistoryResp>() {
            @Override
            public void onLoaded(CustomerHistoryResp result) {
                callback.onLoaded(result.getTotalCount() + result.getEntryHeadCount() > 0);
            }

            @Override
            public void onFail() {
                callback.onFail();
            }
        });
    }

    @Override
    public void isFloatingShow(int companyId, int shopId, Callback<Boolean> callback) {
        // 海外版无云服务
        if (CommonHelper.isGooglePlay()) {
            callback.onLoaded(false);
            return;
        }
        repo.getBundledList(companyId, shopId, new Callback<ShopBundledCloudInfo>() {
            @Override
            public void onLoaded(ShopBundledCloudInfo result) {
                callback.onLoaded(result.isFloatingShow());
            }

            @Override
            public void onFail() {
                callback.onFail();
            }
        });
    }

    @Override
    public void loadCondition(int flag, DashboardCondition condition,
                              int companyId, Callback<DashboardCondition> callback) {
        if (condition == null) {
            condition = new DashboardCondition();
        }
        this.loadFlag = flag & Constants.FLAG_CONDITION_COMPANY_MASK;
        this.callback = callback;

        if ((this.loadFlag & Constants.FLAG_SAAS) != 0) {
            hasSaasState(companyId, new CallbackSaas(condition));
        }
        if ((this.loadFlag & Constants.FLAG_FS) != 0) {
            hasFs(companyId, new CallbackFs(condition));
        }
    }

    @Override
    public void loadCondition(int flag, DashboardCondition condition,
                              int companyId, int shopId, Callback<DashboardCondition> callback) {
        if (condition == null) {
            condition = new DashboardCondition();
        }
        this.loadFlag = flag & Constants.FLAG_CONDITION_SHOP_MASK;
        this.callback = callback;

        if ((this.loadFlag & Constants.FLAG_SAAS) != 0) {
            hasSaasState(companyId, shopId, new CallbackSaas(condition));
        }
        if ((this.loadFlag & Constants.FLAG_FS) != 0) {
            hasFs(companyId, shopId, new CallbackFs(condition));
        }
        if ((this.loadFlag & Constants.FLAG_CUSTOMER) != 0) {
            hasCustomer(companyId, shopId, new CallbackCustomer(condition));
        }
        if ((this.loadFlag & Constants.FLAG_BUNDLED_LIST) != 0) {
            isFloatingShow(companyId, shopId, new CallbackFloating(condition));
        }
    }

    private void loadComplete(int flag, DashboardCondition condition, boolean isSuccess) {
        if (this.loadFlag == 0) {
            return;
        }
        if (!isSuccess) {
            this.loadFlag = 0;
            this.callback.onFail();
            return;
        }
        this.loadFlag &= ~flag;
        if (this.loadFlag == 0) {
            this.callback.onLoaded(condition);
        }
    }

    @Override
    public void clearCache(int flag) {
        repo.clearCache(flag);
    }

    private class CallbackSaas implements Callback<Integer> {

        DashboardCondition condition;

        public CallbackSaas(DashboardCondition condition) {
            this.condition = condition;
        }

        @Override
        public void onLoaded(Integer result) {
            condition.hasSaas = result != Constants.SAAS_STATE_NONE;
            condition.hasImport = result == Constants.SAAS_STATE_IMPORT;
            loadComplete(Constants.FLAG_SAAS, condition, true);
        }

        @Override
        public void onFail() {
            loadComplete(Constants.FLAG_SAAS, condition, false);
        }
    }

    private class CallbackFs implements Callback<Boolean> {

        DashboardCondition condition;

        public CallbackFs(DashboardCondition condition) {
            this.condition = condition;
        }

        @Override
        public void onLoaded(Boolean result) {
            condition.hasFs = result;
            loadComplete(Constants.FLAG_FS, condition, true);
        }

        @Override
        public void onFail() {
            loadComplete(Constants.FLAG_FS, condition, false);
        }
    }

    private class CallbackCustomer implements Callback<Boolean> {

        DashboardCondition condition;

        public CallbackCustomer(DashboardCondition condition) {
            this.condition = condition;
        }

        @Override
        public void onLoaded(Boolean result) {
            condition.hasCustomer = result;
            loadComplete(Constants.FLAG_CUSTOMER, condition, true);
        }

        @Override
        public void onFail() {
            loadComplete(Constants.FLAG_CUSTOMER, condition, false);
        }
    }

    private class CallbackFloating implements Callback<Boolean> {

        DashboardCondition condition;

        public CallbackFloating(DashboardCondition condition) {
            this.condition = condition;
        }

        @Override
        public void onLoaded(Boolean result) {
            condition.hasFloating = result;
            loadComplete(Constants.FLAG_BUNDLED_LIST, condition, true);
        }

        @Override
        public void onFail() {
            loadComplete(Constants.FLAG_BUNDLED_LIST, condition, false);
        }
    }


}
