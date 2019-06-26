package com.sunmi.assistant.dashboard.data;

import com.sunmi.assistant.dashboard.Utils;
import com.sunmi.assistant.dashboard.data.response.CompanyInfoResponse;
import com.sunmi.ipc.rpc.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * 订单管理远程接口
 *
 * @author yinhui
 * @since 2019-06-20
 */
public class CompanyManagementRemote {

    private CompanyManageService mService;

    private CompanyManagementRemote() {
        mService = RetrofitClient.getInstance().create(CompanyManageService.class);
    }

    private static final class Holder {
        private static final CompanyManagementRemote INSTANCE = new CompanyManagementRemote();
    }

    public static CompanyManagementRemote get() {
        return Holder.INSTANCE;
    }

    public void getCompanyInfo(int companyId, RetrofitCallback<CompanyInfoResponse> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .toString();
            mService.getInfo(Utils.createRequestBody(params)).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
