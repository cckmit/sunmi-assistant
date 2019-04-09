package com.sunmi.ipc.view;

import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Button;

import com.sunmi.ipc.rpc.IPCCloudApi;
import com.sunmi.ipc.utils.AACDecoder;
import com.sunmi.ipc.utils.H264Decoder;
import com.sunmi.ipc.utils.IOTCClient;
import com.zhy.http.okhttp.callback.StringCallback;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
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
    @ViewById(resName = "btn_stop")
    Button btnStop;

    private static String UID = "C3YABT1MPRV4BM6GUHXJ";//ss
//    private static String UID = "CVYA8T1WKFV49NPGYHRJ";//fs
//    private static String UID = "CRYUBT1WKFV4UM6GUH71";

    private H264Decoder mPlayer = null;
    private AACDecoder mAudioPlayer = null;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            shortTip("播放结束!");
        }
    };

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
//        ReadAACFileThread audioThread = new ReadAACFileThread();
//        audioThread.start();
        ThreadPool.getCachedThreadPool().submit(new Runnable() {
            @Override
            public void run() {
                IOTCClient.start(UID);
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
        IPCCloudApi.getIpcList("6878", new StringCallback() {
            @Override
            public void onError(Call call, Response response, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                //{"code":1,"msg":"","data":{"device_list":[{"id":2227,"device_name":"绑定测试2"}]}}
                HttpResponse res = new HttpResponse(response);
                try {
                    JSONObject jsonObject = new JSONObject(res.getData());
                    JSONArray jsonArray = jsonObject.getJSONArray("device_list");
                    unbind(((JSONObject) jsonArray.opt(0)).getInt("id") + "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LogCat.e(TAG, "666666 getIpcList response = " + response);
            }
        });
    }

    @Click(resName = "btn_stop")
    void stopClick() {
        IPCConfigActivity_.intent(mActivity).shopId("6878").start();
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
