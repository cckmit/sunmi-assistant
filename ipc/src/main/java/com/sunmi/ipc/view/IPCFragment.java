package com.sunmi.ipc.view;

import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.sunmi.ipc.rpc.IPCCloudApi;
import com.sunmi.ipc.utils.AACDecoder;
import com.sunmi.ipc.utils.H264Decoder;
import com.sunmi.ipc.utils.IOTCClient;
import com.zhy.http.okhttp.callback.StringCallback;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;
import sunmi.common.base.BaseFragment;
import sunmi.common.rpc.http.HttpResponse;
import sunmi.common.utils.ThreadPool;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.TitleBarView;

@EFragment(resName = "fragment_ipc")
public class IPCFragment extends BaseFragment implements SurfaceHolder.Callback {

    @ViewById(resName = "title_bar")
    TitleBarView titleBar;
    @ViewById(resName = "vv_ipc")
    SurfaceView videoView;
    @ViewById(resName = "btn_play")
    Button btnPlay;
    @ViewById(resName = "btn_pause")
    Button btnPause;
    @ViewById(resName = "et_uid")
    EditText etUid;

    @FragmentArg("shopId")
    String shopId;

//        private static String UID = "C3YABT1MPRV4BM6GUHXJ";//ss
    private static String UID = "CVYA8T1WKFV49NPGYHRJ";//fs

    private H264Decoder mPlayer = null;
    private AACDecoder mAudioPlayer = null;

    @AfterViews
    void init() {
        //保持屏幕常亮
        mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        IOTCClient.setCallback(new IOTCClient.Callback() {
            @Override
            public void onVideoReceived(byte[] videoBuffer) {
                if (mPlayer != null)
                    mPlayer.setVideoData(videoBuffer);
            }

            @Override
            public void onAudioReceived(byte[] audioBuffer) {
                mAudioPlayer.setAudioData(audioBuffer);
            }
        });
        videoView.getHolder().addCallback(this);
        mAudioPlayer = new AACDecoder();
    }

    @Click(resName = "btn_play")
    void playClick() {
        etUid.setText(UID);
        if (etUid == null || TextUtils.isEmpty(etUid.getText().toString().trim())) {
            shortTip("请输入uid");
            return;
        }
        ThreadPool.getCachedThreadPool().submit(new Runnable() {
            @Override
            public void run() {
                IOTCClient.start(etUid.getText().toString().trim());
            }
        });
    }

    @Click(resName = "btn_unbind")
    void unbindClick() {
        getIpcList();
    }

    void unbind(String deviceId) {
        IPCCloudApi.unbindIPC(deviceId, new StringCallback() {
            @Override
            public void onError(Call call, Response response, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                LogCat.e(TAG, "666666 unbind response = " + response);
            }
        });
    }

    void getIpcList() {
        IPCCloudApi.getIpcList(shopId, new StringCallback() {
            @Override
            public void onError(Call call, Response response, Exception e, int id) {
                LogCat.e(TAG, "666666 getIpcList onError response = " + response);
            }

            @Override
            public void onResponse(String response, int id) {
                LogCat.e(TAG, "666666 getIpcList onResponse response = " + response);
                HttpResponse res = new HttpResponse(response);
                try {
                    JSONObject jsonObject = new JSONObject(res.getData());
                    JSONArray jsonArray = jsonObject.getJSONArray("device_list");
                    if (jsonArray == null || jsonArray.length() <= 0) {
                        return;
                    }
                    unbind(((JSONObject) jsonArray.opt(0)).getInt("id") + "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Click(resName = "btn_config")
    void stopClick() {
        IPCConfigActivity_.intent(mActivity).shopId(shopId).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mPlayer = new H264Decoder(holder.getSurface(), 0);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //关闭操作
        if (mPlayer != null) {
            mPlayer.stopRunning();
            mPlayer = null;
        }
    }

}
