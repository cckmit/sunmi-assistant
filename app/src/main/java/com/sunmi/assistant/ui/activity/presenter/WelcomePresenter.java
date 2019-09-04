package com.sunmi.assistant.ui.activity.presenter;

import android.text.TextUtils;

import com.sunmi.apmanager.rpc.cloud.CloudApi;
import com.sunmi.assistant.ui.activity.contract.WelcomeContract;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;
import sunmi.common.base.BasePresenter;
import sunmi.common.utils.SpUtils;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-05.
 */
public class WelcomePresenter extends BasePresenter<WelcomeContract.View>
        implements WelcomeContract.Presenter {

    @Override
    public void checkUpgrade() {
        CloudApi.checkUpgrade(new StringCallback() {
            @Override
            public void onResponse(String response, int id) {
                if (isViewAttached()) {
                    try {
                        if (response != null) {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("code") && jsonObject.getInt("code") == 1) {
                                JSONObject object = (JSONObject) jsonObject.getJSONArray("data").opt(0);
                                if (object.has("is_force_upgrade")) {
                                    // 是否需要强制升级 0-否 1-是
                                    int needMerge = object.getInt("is_force_upgrade");
                                    if (needMerge == 1) {
                                        mView.forceUpdate(object.getString("url"));
                                        return;
                                    } else {
                                        //首次安装或清空数据时
                                        if (!TextUtils.equals(SpUtils.getLead(), "TRUE")) {
                                            mView.gotoLeadPagesActivity();
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mView.handleLaunch();
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e, int id) {
                if (isViewAttached()) {
                    mView.handleLaunch();
                }
            }
        });
    }

}
