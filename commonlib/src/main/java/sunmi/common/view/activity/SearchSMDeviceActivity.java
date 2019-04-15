package sunmi.common.view.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import sunmi.common.base.BaseActivity;
import sunmi.common.constant.CommonConstants;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.DiffuseView;
import sunmi.common.view.TitleBarView;

/**
 * Created by YangShiJie on 2019/3/29.
 * 主路由器开始配置-搜索
 */
@EActivity(resName = "activity_primary_router_search")
public class SearchSMDeviceActivity extends BaseActivity implements View.OnClickListener {

    @ViewById(resName = "title_bar")
    TitleBarView titleBar;
    @ViewById(resName = "dv_view")
    DiffuseView dvView;
    @ViewById(resName = "tv_searching_tip")
    TextView tvSearchingTip;
    @ViewById(resName = "rl_searching_router")
    RelativeLayout rlSearchingRouter;
    @ViewById(resName = "iv_router")
    ImageView ivRouter;
    @ViewById(resName = "tv_find_tip")
    TextView tvFindTip;
    @ViewById(resName = "tv_find_name")
    TextView tvFindName;
    @ViewById(resName = "tv_find_bind_tip")
    TextView tvFindBindTip;
    @ViewById(resName = "rl_find_router")
    RelativeLayout rlFindRouter;
    @ViewById(resName = "iv_no_router")
    ImageView ivNoRouter;
    @ViewById(resName = "tv_no_find_tip")
    TextView tvNoFindTip;
    @ViewById(resName = "tv_no_find_name")
    TextView tvNoFindName;
    @ViewById(resName = "btn_refresh")
    Button btnRefresh;
    @ViewById(resName = "rl_no_router")
    RelativeLayout rlNoRouter;
    @ViewById(resName = "ctv_privacy")
    CheckedTextView ctvPrivacy;
    @ViewById(resName = "btnNext")
    Button btnNext;
    @ViewById(resName = "rl_bottom")
    RelativeLayout rlBottom;

    @Extra
    int deviceType;

    //search sn
    private String deviceidSearchSn = "", searchName = "", searchMac = "";
    private boolean isSearch;
    private Dialog mDialog = null;
    private String password = "";
    //timer
    private int timerNum;
    private Timer timer = null;
    private TimerTask myTask = null;
    //search timeout
    private final int TIMEOUT = 6;
    private int OPERATE_BUTTON;
    private final int OPERATE_NEXT = 0;
    private final int OPERATE_NO_BIND = 1;
    private final int OPERATE_ALREADY_BIND = 2;
    private boolean isRun;

    //start Timer
    private void startTimer() {
        timer = new Timer();
        timer.schedule(myTask = new TimerTask() {
            @Override
            public void run() {
                timerNum++;
                LogCat.e(TAG, "timerNum=" + timerNum);
                if (isSearch && timerNum < TIMEOUT) {//当搜索到了设备
                    closeTimer();
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        searchTimeOut(timerNum);
                    }
                });
            }
        }, 0, 1000);
    }

    //close Timer
    private void closeTimer() {
        timerNum = 0;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (myTask != null) {
            myTask.cancel();
            myTask = null;
        }
    }

    private Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case CommonConstants.WHAT_UDP://udp搜索
                    String result = (String) msg.obj;
                    showSearchResult(result);
                    break;
            }
            return false;
        }
    };

    private Handler mHandler = new WeakRefHandler(mCallback);


    private static class WeakRefHandler extends Handler {
        private WeakReference<Callback> mWeakReference;

        public WeakRefHandler(Callback callback) {
            mWeakReference = new WeakReference<>(callback);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mWeakReference != null && mWeakReference.get() != null) {
                Callback callback = mWeakReference.get();
                callback.handleMessage(msg);
            }
        }
    }

//    @Override
//    protected int activityLayoutId() {
//        return R.layout.activity_primary_router_search;
//    }
//
//    @Override
//    protected void initView() {
//        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
//        titleBar.getLeftLayout().setOnClickListener(this);
//        ViewUtils.setPrivacy(this, ctvPrivacy, R.color.colorText, false);
//        startDiffuse();
//        searching();
//    }
//
//    private void startDiffuse() {
//        dvView.start();
//    }
//
//    private void stopDiffuse() {
//        dvView.stop();
//    }

    @Override
    protected void onResume() {
        super.onResume();
        isRun = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRun = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        stopDiffuse();
    }

    @Override
    public void onClick(View v) {
        closeTimer();
        finish();
    }

    //    @OnClick(R2.id.btn_refresh)
    public void refreshClick(View v) {
//        startDiffuse();
        searching();
    }

    //    @OnClick(R2.id.btnNext)
    public void nextClick(View v) {
        if (isFastClick(1000)) return;
//        if (OPERATE_BUTTON == OPERATE_NEXT) {//开始配置
//            if (!ctvPrivacy.isChecked()) {
//                shortTip(R.string.tip_agree_protocol);
//                return;
//            }
//            Bundle bundle = new Bundle();
//            bundle.putString("sn", deviceidSearchSn);
//            openActivity(this, PrimaryRouteSetPasswordActivity.class, bundle, false);
//
//        } else if (OPERATE_BUTTON == OPERATE_ALREADY_BIND) {//已经绑定
//            goToMainActivity();
//        } else if (OPERATE_BUTTON == OPERATE_NO_BIND) {//未绑定
//            bindRouterDialog();
//        }
    }

    //搜索路由器
    private void searchRouter() {
        startTimer();//开启计时器
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                UDPUtils.UdpManual.initSearchRouter(mHandler, SearchSMDeviceActivity.this);
            }
        }, 1000);
    }

    //搜索结果
    private void showSearchResult(String result) {
        LogCat.e(TAG, "udp=" + result);
        if (isFastClick(800)) return;
//        try {
//            JSONObject object = new JSONObject(result);
//            searchName = object.getString("name");
//            int factory = object.getInt("factory");//是否已经设置 0已初始配置 1未初始化设置
//            deviceidSearchSn = object.getString("deviceid");
//            searchMac = object.getString("mac");
//            SEARCH_DEV_MSG[0] = deviceidSearchSn;
//            SEARCH_DEV_MSG[1] = searchMac;
////            APCall.getInstance().getApInfo(this,deviceidSearchSn);
//            if (!TextUtils.isEmpty(searchName)) {
//                closeTimer();
//                isSearch = true;
//                if (factory == 0) {
//                    //已配置已经绑定
//                    for (StoreBean sb : globalDevList) {
//                        if (sb.getSn().equalsIgnoreCase(deviceidSearchSn)) {
//                            findRouterConfigDoneBind(HelpUtils.getSSID(this, searchName));
//                            return;
//                        }
//                    }
//                    //已配置未绑定(查询云端绑定状态--判断无网络状态)
//                    getBindRouterMessage(deviceidSearchSn, HelpUtils.getSSID(this, searchName));
//                } else {
//                    //未设置未绑定--快速配置
//                    LogCat.e("TAG", "ssid=" + HelpUtils.getSSID(this, searchName));
//                    findRouterConfigNo(HelpUtils.getSSID(this, searchName));
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    //没有搜索到（指定搜索时间）
    private void searchTimeOut(int result) {
        if (result == TIMEOUT) {
            cannotSearchRouter();
        }
    }

    //查询绑定信息（云端）
    private void getBindRouterMessage(String sn, final String name) {
//        CloudApi.getBindBySn(sn, new RpcCallback1(this, false) {
//            @Override
//            public void onSuccess(int code, String msg, String data) {
//                getBindBySnSuccess(code, data, name);
//            }
//
//            @Override
//            public void onError(Response response, Exception e) {
//                LogCat.e(TAG, "getBindRouterMessage :  response=" + response + "  e=" + e.getMessage());
//                shortTip(getString(R.string.str_bind_net_exception));
//                closeTimer();
//                finish();
//            }
//        });
    }

    //查询绑定信息（云端）
    private void getBindBySnSuccess(final int code, final String result, final String name) {
        LogCat.e(TAG, "getBindBySnSuccess=" + result);
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    if (code == 1) {//成功
//                        JSONObject object1 = new JSONObject(result);
//                        //有返回数据说明已经绑定，无数据则没有绑定
//                        if (!StringHelper.isNull(object1.getString("phone"))) {
//                            String phone = object1.getString("phone");
//                            findRouterConfigDoneBindOtherUser(
//                                    HelpUtils.getSSID(SearchSMDeviceActivity.this, searchName),
//                                    getString(R.string.str_call_unbind, StringHelper.getEncryptPhone(phone)));
//
//                        } else if (!StringHelper.isNull(object1.getString("email"))) {
//                            String email = object1.getString("email");
//                            findRouterConfigDoneBindOtherUser(
//                                    HelpUtils.getSSID(SearchSMDeviceActivity.this, searchName),
//                                    getString(R.string.str_call_unbind, StringHelper.getEncryptEmail(email)));
//                        } else {
//                            findRouterConfigDoneUnbind(name);
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    //绑定校验管理员密码
    private void bindRouterDialog() {
        if (mDialog != null) return;
//        mDialog = new Dialog(this, R.style.Son_dialog);
//        LayoutInflater inflater = this.getLayoutInflater();
//        View view = inflater.inflate(R.layout.dialog_manger_password, null);
//        final ClearableEditText etPassword = view.findViewById(R.id.etPassword);
//        TextView tvContent = view.findViewById(R.id.tvContent);
//        Button btnCancel = view.findViewById(R.id.btnCancel);
//        Button btnSure = view.findViewById(R.id.btnSure);
//        tvContent.setText(R.string.str_text_bind_router);
//
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mDialog.dismiss();
//                mDialog = null;
//            }
//        });
//        btnSure.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                password = etPassword.getText().toString().trim();
//                if (TextUtils.isEmpty(password)) {
//                    shortTip(R.string.hint_input_manger_password);
//                    return;
//                }
//                //校验管理密码
//                showLoadingDialog();
//                APCall.getInstance().apBindCheckPassword(SearchSMDeviceActivity.this, password);
//            }
//        });
//        mDialog.setContentView(view);
//        mDialog.setCancelable(false);
//        mDialog.show();
    }


    //开始搜索
    private void searching() {
        searchRouter();//搜索路由器

        rlSearchingRouter.setVisibility(View.VISIBLE);
        rlFindRouter.setVisibility(View.GONE);
        rlNoRouter.setVisibility(View.GONE);
        rlBottom.setVisibility(View.GONE);
    }

    //搜索到-未配置
    private void findRouterConfigNo(String name) {
//        stopDiffuse();
//        closeTimer();
//        tvFindName.setText(name);
//        tvFindTip.setText(R.string.str_primary_search_find);
//        tvFindBindTip.setVisibility(View.GONE);
//        rlSearchingRouter.setVisibility(View.GONE);
//        rlFindRouter.setVisibility(View.VISIBLE);
//        rlNoRouter.setVisibility(View.GONE);
//        rlBottom.setVisibility(View.VISIBLE);
//        ctvPrivacy.setVisibility(View.VISIBLE);
//        btnNext.setText(R.string.str_button_start_config);
//        OPERATE_BUTTON = OPERATE_NEXT;
    }

    //搜索到-已配置-未绑定
    private void findRouterConfigDoneUnbind(String name) {
//        stopDiffuse();
//        closeTimer();
//        tvFindName.setText(name);
//        tvFindTip.setText(R.string.str_primary_search_bind);
//        tvFindBindTip.setVisibility(View.GONE);
//        rlSearchingRouter.setVisibility(View.GONE);
//        rlFindRouter.setVisibility(View.VISIBLE);
//        rlNoRouter.setVisibility(View.GONE);
//        rlBottom.setVisibility(View.VISIBLE);
//        ctvPrivacy.setVisibility(View.GONE);
//        btnNext.setText(R.string.str_button_bind);
//        OPERATE_BUTTON = OPERATE_NO_BIND;
    }

    //搜索到-已配置-本人已绑定(跳转首页)
    private void findRouterConfigDoneBind(String name) {
//        stopDiffuse();
//        closeTimer();
//        tvFindName.setText(name);
//        tvFindTip.setText(R.string.str_primary_search_bind_done);
//        tvFindBindTip.setVisibility(View.VISIBLE);
//        tvFindBindTip.setText(R.string.str_primary_search_already_bind);
//        rlSearchingRouter.setVisibility(View.GONE);
//        rlFindRouter.setVisibility(View.VISIBLE);
//        rlNoRouter.setVisibility(View.GONE);
//        rlBottom.setVisibility(View.VISIBLE);
//        ctvPrivacy.setVisibility(View.GONE);
//        btnNext.setText(R.string.str_button_sure);
//        OPERATE_BUTTON = OPERATE_ALREADY_BIND;
    }

    //搜索到-已配置-其他用户已绑定(跳转首页)
    private void findRouterConfigDoneBindOtherUser(String name, String tip) {
//        stopDiffuse();
//        tvFindName.setText(name);
//        tvFindTip.setText(R.string.str_primary_search_other_bind);
//        tvFindBindTip.setVisibility(View.VISIBLE);
//        tvFindBindTip.setText(tip);
//        rlSearchingRouter.setVisibility(View.GONE);
//        rlFindRouter.setVisibility(View.VISIBLE);
//        rlNoRouter.setVisibility(View.GONE);
//        rlBottom.setVisibility(View.VISIBLE);
//        ctvPrivacy.setVisibility(View.GONE);
//        btnNext.setText(R.string.str_button_sure);
//        OPERATE_BUTTON = OPERATE_ALREADY_BIND;
    }

    //没有搜索到
    private void cannotSearchRouter() {
//        stopDiffuse();
        closeTimer();
        rlSearchingRouter.setVisibility(View.GONE);
        rlFindRouter.setVisibility(View.GONE);
        rlNoRouter.setVisibility(View.VISIBLE);
        rlBottom.setVisibility(View.GONE);
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        super.didReceivedNotification(id, args);
//        if (!isRun || args == null) return;
//        ResponseBean res = (ResponseBean) args[0];
//        if (TextUtils.equals(res.getErrCode(), AppConfig.WHAT_ERROR + "")) {
//            hideLoadingDialog();
//            NetConnectUtils.isNetConnected(context);
//
//        } else if (NotificationConstant.checkApPassword == id) {
//            LogCat.e(TAG, "result=" + res.getResult());
//            checkedPasswordSuccess(res);
//        } else if (NotificationConstant.apGetInfo == id) {
//            getApInfoSuccess(res);
//        }
    }

//    @Override
//    public int[] getUnStickNotificationId() {
//        return new int[]{NotificationConstant.checkApPassword, NotificationConstant.apGetInfo};
//    }

    //1 ap 管理密码校验
    private void checkedPasswordSuccess(ResponseBean res) {
//        try {
//            if (TextUtils.equals("0", res.getErrCode())) {
//                JSONObject object2 = res.getResult();
//                JSONObject object3 = object2.getJSONObject("account");
//                String token = object3.getString("token");
//                SpUtils.saveRouterToken(token); //保存路由token
//                DBUtils.saveApMangerMsg(deviceidSearchSn, password);//搜索的路由器当前sn和password
//
//                mDialog.dismiss();
//                mDialog = null;
//                //ap获取设备sn和token
//                APCall.getInstance().routerGetApInfo(SearchSMDeviceActivity.this);
//
//            } else if (TextUtils.equals(res.getErrCode(), AppConfig.ERROR_CODE_PASSWORD_ERROR) ||
//                    TextUtils.equals(res.getErrCode(), AppConfig.ERROR_CODE_PASSWORD_INVALID)) {// 账户密码错误
//                hideLoadingDialog();
//                shortTip(getString(R.string.tip_password_error));
//            } else if (TextUtils.equals(res.getErrCode(), AppConfig.ERROR_CODE_PASSWORD_INCORRECT_MANY)) { // 账户密码错误次数过多
//                hideLoadingDialog();
//                shortTip(R.string.tip_password_fail_too_often);
//            } else {
//                hideLoadingDialog();
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    //2 绑定--ap获取设备sn和token
    private void getApInfoSuccess(ResponseBean res) {
        LogCat.e(TAG, "getApInfoSuccess=" + res);
        try {
            if (TextUtils.equals("0", res.getErrCode())) {
                JSONObject object2 = res.getResult();
                JSONObject object3 = object2.getJSONObject("apinfo");
                String bind_sn = object3.getString("sn");
                String bind_token = object3.getString("token");
                bindAp(bind_sn, bind_token);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //3 绑定--默认进行绑定
    private void bindAp(String sn, String token) {
        hideLoadingDialog();
//        CloudApi.bind(sn, token, CURRENT_SHOP_ID, "", "",
//                new RpcCallback1(this) {
//                    @Override
//                    public void onSuccess(int code, String msg, String data) {
//                        LogCat.e(TAG, "bind data = " + data);
//                        if (code == 1) {
//                            String AP_UNKNOWN = getString(R.string.str_text_router_unknown);
//                            DBUtils.saveBindDevLocal(CURRENT_SHOP_ID, SEARCH_DEV_MSG[0], AP_UNKNOWN);//绑定保存本地
//                        } else if (code == 5100) {
//                            shortTip(getString(R.string.str_dev_binded));
//                        } else if (code == 5012) {
//                            shortTip(getString(R.string.str_dev_no_sn));
//                        }
//                        goToMainActivity();
//                    }
//
//                    @Override
//                    public void onError(Response response, Exception e) {
//                        LogCat.e(TAG, "bindAp onError:  response=" + response + "  e=" + e.getMessage());
//                        shortTip(getString(R.string.str_bind_net_exception));
//                        finish();
//                    }
//                });
    }

    private void goToMainActivity() {
        try {
            Class<?> mainActivity = Class.forName("com.sunmi.assistant.ui.activity.MainActivity_");
            Intent intent = new Intent(context, mainActivity);
            context.startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
