package com.sunmi.ipc.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sunmi.ipc.model.ApCloudTimeBean;
import com.sunmi.ipc.rpc.IPCCloudApi;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

import sunmi.common.base.BaseFragment;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.IVideoPlayer;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.TitleBarView;

@EFragment(resName = "fragment_ipc")
public class IPCFragment extends BaseFragment implements SurfaceHolder.Callback {

    @ViewById(resName = "title_bar")
    TitleBarView titleBar;
    @ViewById(resName = "vv_ipc")
    SurfaceView surface;//负责配合mediaPlayer显示视频图像播放的surfaceView
    @ViewById(resName = "btn_play")
    Button btnPlay;
    @ViewById(resName = "et_uid")
    EditText etUid;
    @ViewById(resName = "ocv_ipc")
    OverCameraView overCameraView;
    @ViewById(resName = "ivp_ipc")
    IVideoPlayer iVideoPlayer;

    @FragmentArg("shopId")
    String shopId;

    //用于播放视频的mediaPlayer对象
    private MediaPlayer firstPlayer,//负责播放进入视频播放界面后的第一段视频
            nextMediaPlayer, //负责一段视频播放结束后，播放下一段视频
            cachePlayer,     //负责setNextMediaPlayer的player缓存对象
            currentPlayer;   //负责当前播放视频段落的player对象
    private SurfaceHolder surfaceHolder;

//    //底部聊天栏
//    private LinearLayout bottom_bar_layout;
//    private FrameLayout video_layout;

    //================================================================

    //存放所有视频端的url
    private ArrayList<String> videoListQueue = new ArrayList<>();
    //所有player对象的缓存
    private HashMap<String, MediaPlayer> playersCache = new HashMap<>();
    //当前播放到的视频段落数
    private int currentVideoIndex;

    private static String UID = "C3YABT1MPRV4BM6GUHXJ";//ss
//    private static String UID = "CRYUBT1WKFV4UM6GUH71";//ss - shenzhen yangfeng
//    private static String UID = "EFKUA51CZVBW8NPGUHZJ";//ss - shenzhen ceshi
    // private static String  UID = "CVYA8T1WKFV49NPGYHRJ";//fs
// private static String     UID = "CBKA9T14URBC8MPGYHZJ";//fs --shenzhen

    @AfterViews
    void init() {
        //保持屏幕常亮
        mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        surfaceHolder = surface.getHolder();// SurfaceHolder是SurfaceView的控制接口
        surfaceHolder.addCallback(this); // 因为这个类实现了SurfaceHolder.Callback接口，所以回调参数直接this
        List<String> list = new ArrayList<>();
        videoListQueue.add("https://sunmi-test.oss-cn-hangzhou.aliyuncs.com/VIDEO/IPC/SS101D8BS00088/0_20.mp4");
        videoListQueue.add("https://sunmi-test.oss-cn-hangzhou.aliyuncs.com/VIDEO/IPC/SS101D8BS00088/20_40.mp4");
        videoListQueue.add("https://sunmi-test.oss-cn-hangzhou.aliyuncs.com/VIDEO/IPC/SS101D8BS00088/40_60.mp4");
        list.add("http://sunmi-test.oss-cn-hangzhou.aliyuncs.com/VIDEO/IPC/SS101D8BS00088/0_25.mp4");
        list.add("http://sunmi-test.oss-cn-hangzhou.aliyuncs.com/VIDEO/IPC/SS101D8BS00088/25_50.mp4");
        list.add("http://sunmi-test.oss-cn-hangzhou.aliyuncs.com/VIDEO/IPC/SS101D8BS00088/abcdefghijklmn.flv");
//        list.add("http://sunmi-test.oss-cn-hangzhou.aliyuncs.com/VIDEO/5b0549bee3a07.mp4");
//        list.add("http://sunmi-test.oss-cn-hangzhou.aliyuncs.com/VIDEO/IPC/SS101D8BS00088/D222066BBD2EACA182EA444D90CAF621.flv");
//        iVideoPlayer.setUrlQueue(list);
//        try {
//            iVideoPlayer.startPlay();
//        } catch (Exception e) {
//            shortTip("播放失败");
//            e.printStackTrace();
//        }
    }

    private void showSingleChoiceDialog() {
        final String[] items = {"C3YABT1MPRV4BM6GUHXJ", "CRYUBT1WKFV4UM6GUH71", "EFKUA51CZVBW8NPGUHZJ",
                "CVYA8T1WKFV49NPGYHRJ", "CBKA9T14URBC8MPGYHZJ", "CWT4X19FUCZ6RSRC111A","B9PMFYGPLB6NWYRV111A"};
        final AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(getActivity());
        singleChoiceDialog.setSingleChoiceItems(items, 1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                etUid.setText(items[which]);
                if (etUid == null || TextUtils.isEmpty(etUid.getText().toString().trim())) {
                    shortTip("请输入uid");
                    return;
                }
                VideoPlayActivity_.intent(mActivity).UID(etUid.getText().toString().trim()).start();
            }
        });
        singleChoiceDialog.show();
    }


    @Click(resName = "btn_play")
    void playClick() {
        showSingleChoiceDialog();
    }

    @Click(resName = "btn_pause")
    void pauseClick() {

    }

    @Click(resName = "btn_unbind")
    void unbindClick() {
        getIpcList();
    }

    @Click(resName = "btn_config")
    void stopClick() {

    }

    @Click(resName = "btn_time")
    void timeClick() {
        getTimeList();
    }

    @Click(resName = "btn_time_ap")
    void timeApClick() {
    }

    @Click(resName = "btn_video")
    void videoClick() {
        // http://test.cdn.sunmi.com/VIDEO/IPC/4E58E60001B29C869E34C9506B9CCD23
        // https://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4
        // http://sunmi-test.oss-cn-hangzhou.aliyuncs.com/VIDEO/IPC/SS101D8BS00088/D222066BBD2EACA182EA444D90CAF621.flv
        // http://sunmi-test.oss-cn-hangzhou.aliyuncs.com/VIDEO/IPC/SS101D8BS00088/abcdefghijklmn.flv
        // http://sunmi-test.oss-cn-hangzhou.aliyuncs.com/VIDEO/5b0549bee3a07.mp4
        // http://sunmi-test.oss-cn-hangzhou.aliyuncs.com/VIDEO/IPC/SS101D8BS00088/0_25.mp4
        // http://sunmi-test.oss-cn-hangzhou.aliyuncs.com/VIDEO/IPC/SS101D8BS00088/25_50.mp4
        getVideoList();
    }

    @Click(resName = "btn_canvas")
    void canvasClick() {
        getCanvasList();
    }

    /*
     * 负责界面销毁时，release各个mediaplayer
     * @see android.app.Activity#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (firstPlayer != null) {
            if (firstPlayer.isPlaying()) {
                firstPlayer.stop();
            }
            firstPlayer.release();
        }
        if (nextMediaPlayer != null) {
            if (nextMediaPlayer.isPlaying()) {
                nextMediaPlayer.stop();
            }
            nextMediaPlayer.release();
        }

        if (currentPlayer != null) {
            if (currentPlayer.isPlaying()) {
                currentPlayer.stop();
            }
            currentPlayer.release();
        }
        currentPlayer = null;
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
    }

    MediaPlayer player;

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        //surfaceView创建完毕后，首先获取该直播间所有视频分段的url
//        getVideoUrls();
//        然后初始化播放手段视频的player对象
//        initFirstPlayer();


        // 必须在surface创建后才能初始化MediaPlayer,否则不会显示图像
//        player = new MediaPlayer();
//        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        player.setDisplay(surfaceHolder);
//        // 设置显示视频显示在SurfaceView上
//        try {
//            String filePath =
//                    "http://sunmi-test.oss-cn-hangzhou.aliyuncs.com/VIDEO/IPC/SS101D8BS00088/0_25.mp4";
////                    "http://sunmi-test.oss-cn-hangzhou.aliyuncs.com/VIDEO/IPC/SS101D8BS00088/abcdefghijklmn.flv";
//            //Environment.getExternalStorageDirectory() + "/Sunmi/111.flv";
//            Uri uri = Uri.parse(filePath);
//            player.setDataSource(filePath);
//            player.prepareAsync();
//            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    player.start();
//
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
    }

    /*
     * 初始化播放首段视频的player
     */
    private void initFirstPlayer() {
        firstPlayer = new MediaPlayer();
        firstPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        firstPlayer.setDisplay(surfaceHolder);

        firstPlayer
                .setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        onVideoPlayCompleted(mp);
                    }
                });

        //设置cachePlayer为该player对象
        cachePlayer = firstPlayer;
        initNexttPlayer();

        //player对象初始化完成后，开启播放
        startPlayFirstVideo();
    }

    private void startPlayFirstVideo() {
        try {
            firstPlayer.setDataSource(videoListQueue.get(currentVideoIndex));
            firstPlayer.prepare();
            firstPlayer.start();
        } catch (IOException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
    }

    /*
     * 新开线程负责初始化负责播放剩余视频分段的player对象,避免UI线程做过多耗时操作
     */
    private void initNexttPlayer() {
        new Thread(new Runnable() {

            @Override
            public void run() {

                for (int i = 1; i < videoListQueue.size(); i++) {
                    nextMediaPlayer = new MediaPlayer();
                    nextMediaPlayer
                            .setAudioStreamType(AudioManager.STREAM_MUSIC);

                    nextMediaPlayer
                            .setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    onVideoPlayCompleted(mp);
                                }
                            });

                    try {
                        nextMediaPlayer.setDataSource(videoListQueue.get(i));
                        nextMediaPlayer.prepare();
                    } catch (IOException e) {
                        // TODO 自动生成的 catch 块
                        e.printStackTrace();
                    }

                    //set next mediaplayer
                    cachePlayer.setNextMediaPlayer(nextMediaPlayer);
                    //set new cachePlayer
                    cachePlayer = nextMediaPlayer;
                    //put nextMediaPlayer in cache
                    playersCache.put(String.valueOf(i), nextMediaPlayer);

                }

            }
        }).start();
    }

    /*
     * 负责处理一段视频播放过后，切换player播放下一段视频
     */
    private void onVideoPlayCompleted(MediaPlayer mp) {
        mp.setDisplay(null);
        //get next player
        currentPlayer = playersCache.get(String.valueOf(++currentVideoIndex));
        if (currentPlayer != null) {
            currentPlayer.setDisplay(surfaceHolder);
        } else {
            shortTip("视频播放完毕");
        }
    }

    private void getVideoUrls() {
        videoListQueue.add("https://sunmi-test.oss-cn-hangzhou.aliyuncs.com/VIDEO/IPC/SS101D8BS00088/0_20.mp4");
        videoListQueue.add("https://sunmi-test.oss-cn-hangzhou.aliyuncs.com/VIDEO/IPC/SS101D8BS00088/20_40.mp4");
        videoListQueue.add("https://sunmi-test.oss-cn-hangzhou.aliyuncs.com/VIDEO/IPC/SS101D8BS00088/40_60.mp4");
    }

    //    /*
//     * 初始化播放首段视频的player
//     */
//    @Background
//    void initFirstPlayer() {
//        firstPlayer = new MediaPlayer();
//        firstPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        firstPlayer.setDisplay(surfaceHolder);
//        firstPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                onVideoPlayCompleted(mp);
//            }
//        });
//
//        //设置cachePlayer为该player对象
//        cachePlayer = firstPlayer;
//        initNexttPlayer();
//        //player对象初始化完成后，开启播放
//        startPlayFirstVideo();
//    }
//
//    private void startPlayFirstVideo() {
//        try {
//            firstPlayer.setDataSource(videoListQueue.get(currentVideoIndex));
//            firstPlayer.prepare();
////            firstPlayer.prepareAsync();
//            firstPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    mp.start();
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /*
//     * 新开线程负责初始化负责播放剩余视频分段的player对象,避免UI线程做过多耗时操作
//     */
//    private void initNexttPlayer() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 1; i < videoListQueue.size(); i++) {
//                    nextMediaPlayer = new MediaPlayer();
//                    nextMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//
//                    nextMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                        @Override
//                        public void onCompletion(MediaPlayer mp) {
//                            onVideoPlayCompleted(mp);
//                        }
//                    });
//
//                    nextMediaPrepare(i);
//
//                    //set next mediaplayer
//                    cachePlayer.setNextMediaPlayer(nextMediaPlayer);
//                    //set new cachePlayer
//                    cachePlayer = nextMediaPlayer;
//                    //put nextMediaPlayer in cache
//                    playersCache.put(String.valueOf(i), nextMediaPlayer);
//                }
//            }
//        }).start();
//    }
//
//    @UiThread
//    void nextMediaPrepare(int i) {
//        try {
//            nextMediaPlayer.setDataSource(videoListQueue.get(i));
//                        nextMediaPlayer.prepare();
////            nextMediaPlayer.prepareAsync();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /*
//     * 负责处理一段视频播放过后，切换player播放下一段视频
//     */
//    private void onVideoPlayCompleted(MediaPlayer mp) {
//        mp.setDisplay(null);
//        //get next player
//        currentPlayer = playersCache.get(String.valueOf(++currentVideoIndex));
//        if (currentPlayer != null) {
//            currentPlayer.setDisplay(surfaceHolder);
//        } else {
//            shortTip("视频播放完毕");
//        }
//    }
//
//    private void getVideoUrls() {
//        IPCCloudApi.getVideoList(2227, 1557288000, 1557309600,
//                new RetrofitCallback<VideoListResp>() {
//                    @Override
//                    public void onSuccess(int code, String msg, VideoListResp data) {
//                        LogCat.e(TAG, "666666 getVideoList data = " + data);
//                        List<VideoListResp.VideoBean> list = data.getVideo_list();
//                        //然后初始化播放手段视频的player对象
//                        for (VideoListResp.VideoBean bean : list) {
////                            videoListQueue.add(bean.getUrl());
//                            videoListQueue.add("http://sunmi-test.oss-cn-hangzhou.aliyuncs.com/VIDEO/IPC/SS101D8BS00088/abcdefghijklmn.flv");
//                            videoListQueue.add("http://sunmi-test.oss-cn-hangzhou.aliyuncs.com/VIDEO/IPC/SS101D8BS00088/D222066BBD2EACA182EA444D90CAF621.flv");
//                        }
//                    }
//
//                    @Override
//                    public void onFail(int code, String msg, VideoListResp data) {
//                        LogCat.e(TAG, "666666 getVideoList msg = " + msg);
//                    }
//                });
//    }
//
    void unbind(String deviceId) {
        IPCCloudApi.unbindIPC(SpUtils.getCompanyId(), Integer.parseInt(shopId), Integer.parseInt(deviceId), new RetrofitCallback() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                shortTip("解绑成功");
            }

            @Override
            public void onFail(int code, String msg, Object data) {
                shortTip("解绑失败");
            }
        });
    }

    void getIpcList() {
        IPCCloudApi.getDetailList(SpUtils.getCompanyId(), SpUtils.getShopId(), new RetrofitCallback() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                try {
                    JSONObject jsonObject = new JSONObject(new Gson().toJson(data));
                    JSONArray jsonArray = jsonObject.getJSONArray("fs_list");
                    if (jsonArray == null || jsonArray.length() <= 0) {
                        jsonArray = jsonObject.getJSONArray("ss_list");
                    }
                    if (jsonArray == null || jsonArray.length() <= 0) {
                        return;
                    }
                    unbind(((JSONObject) jsonArray.opt(0)).getInt("id") + "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(int code, String msg, Object data) {

            }
        });
    }

    void getTimeList() {
        IPCCloudApi.getTimeSlots(2237, 1558537326, 1558537926, new RetrofitCallback<JsonObject>() {
            @Override
            public void onSuccess(int code, String msg, JsonObject data) {
                LogCat.e(TAG, "date11 getTimeSlots==" + data.toString());
            }

            @Override
            public void onFail(int code, String msg, JsonObject data) {
                LogCat.e(TAG, "date11 getTimeSlots onFail");
            }
        });
    }

    void getVideoList() {
        IPCCloudApi.getVideoList(2237, 1558537326, 1558537926, new RetrofitCallback() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                LogCat.e(TAG, "date11 getVideoList onSuccess== code=" + code + ",  " + data.toString());
            }

            @Override
            public void onFail(int code, String msg, Object data) {
                LogCat.e(TAG, "date11 getVideoLis onFail");

            }
        });
    }

    //绘制时间组合
//    int mStart = 0, mEnd = 15;
    int mStart = 1559838000, mEnd = 1568888888;

    void getCanvasList() {
        List<ApCloudTimeBean> listAp = new ArrayList<>();
        List<ApCloudTimeBean> listCloud = new ArrayList<>();
        List<ApCloudTimeBean> list = new ArrayList<>();

//        ApCloudTimeBean a1 = new ApCloudTimeBean();
//        a1.setStartTime(5);
//        a1.setEndTime(7);
//        a1.setApPlay(true);
//        listAp.add(a1);
//
//        ApCloudTimeBean a2 = new ApCloudTimeBean();
//        a2.setStartTime(8);
//        a2.setEndTime(10);
//        a2.setApPlay(true);
//        listAp.add(a2);
//
//        ApCloudTimeBean a3 = new ApCloudTimeBean();
//        a3.setStartTime(11);
//        a3.setEndTime(13);
//        a3.setApPlay(true);
//        listAp.add(a3);
//
//        ApCloudTimeBean c1 = new ApCloudTimeBean();
//        c1.setStartTime(3);
//        c1.setEndTime(5);
//        listCloud.add(c1);
//
//        ApCloudTimeBean c2 = new ApCloudTimeBean();
//        c2.setStartTime(6);
//        c2.setEndTime(11);
//        listCloud.add(c2);

        ApCloudTimeBean a1 = new ApCloudTimeBean();
        a1.setStartTime(1560159550);
        a1.setEndTime(1560159610);
        a1.setApPlay(true);
        listAp.add(a1);

//        ApCloudTimeBean a2 = new ApCloudTimeBean();
//        a2.setStartTime(1560159678);
//        a2.setEndTime(1560249099);
//        a2.setApPlay(true);
//        listAp.add(a2);


        ApCloudTimeBean c1 = new ApCloudTimeBean();
        c1.setStartTime(1559836797);
        c1.setEndTime(1559959755);
        listCloud.add(c1);

        ApCloudTimeBean c2 = new ApCloudTimeBean();
        c2.setStartTime(1559959763);
        c2.setEndTime(1560145715);
        listCloud.add(c2);

        ApCloudTimeBean c3 = new ApCloudTimeBean();
        c3.setStartTime(1560146301);
        c3.setEndTime(1560146337);
        listCloud.add(c3);

        ApCloudTimeBean c4 = new ApCloudTimeBean();
        c4.setStartTime(1560155951);
        c4.setEndTime(1560158202);
        listCloud.add(c4);


        int apSize = listAp.size();
        int cloudSize = listCloud.size();

        ApCloudTimeBean bean;
        //AP时间
        for (int i = 0; i < apSize + 1; i++) {

            long startAp = 0, endAp = 0;
            //不包含ap时间轴内的时间
            if (i == 0) {
                startAp = mStart;
                endAp = listAp.get(i).getStartTime();
                LogCat.e(TAG, "getCanvasList ap=  " + startAp + "," + endAp);
            } else if (i < apSize) {
                startAp = listAp.get(i - 1).getEndTime();
                endAp = listAp.get(i).getStartTime();
                LogCat.e(TAG, "getCanvasList ap=  " + startAp + "," + endAp);
            } else if (i == apSize) {
                startAp = listAp.get(i - 1).getEndTime();
                endAp = mEnd;
                LogCat.e(TAG, "getCanvasList ap=  " + startAp + "," + endAp);
            }
            //cloud时间
            for (int j = 0; j < cloudSize; j++) {
                bean = new ApCloudTimeBean();
                long startCloud = listCloud.get(j).getStartTime();
                long endCloud = listCloud.get(j).getEndTime();
                LogCat.e(TAG, "getCanvasList cloud=  " + startCloud + "," + endCloud + "**** ap=" + startAp + "," + endAp);
                if (startCloud >= startAp && endAp > startCloud && endCloud >= endAp) {
                    bean.setStartTime(startCloud);
                    bean.setEndTime(endAp);
                    bean.setApPlay(false);
                    listAp.add(bean);
                } else if (startAp >= startCloud && endCloud > startAp && endAp >= endCloud) {
                    bean.setStartTime(startAp);
                    bean.setEndTime(endCloud);
                    bean.setApPlay(false);
                    listAp.add(bean);
                } else if (startAp != endAp && startAp >= startCloud && endAp <= endCloud) {
                    bean.setStartTime(startAp);
                    bean.setEndTime(endAp);
                    bean.setApPlay(false);
                    listAp.add(bean);
                } else if (startCloud != endCloud && startCloud >= startAp && endCloud <= endAp) {
                    bean.setStartTime(startCloud);
                    bean.setEndTime(endCloud);
                    bean.setApPlay(false);
                    listAp.add(bean);
                }
            }
        }


        listAp = duplicateRemoval(listAp);
        Collections.sort(listAp);//正序比较
        LogCat.e(TAG, "getCanvasList=  ************************************************************");
        for (ApCloudTimeBean aa : listAp) {
            LogCat.e(TAG, "getCanvasList=  " + aa.getStartTime() + "," + aa.getEndTime() + " ,pPlay== " + aa.isApPlay());
        }
    }

    //去重
    private List<ApCloudTimeBean> duplicateRemoval(List<ApCloudTimeBean> list) {
        LinkedHashSet<ApCloudTimeBean> tmpSet = new LinkedHashSet<>(list.size());
        tmpSet.addAll(list);
        list.clear();
        list.addAll(tmpSet);
        return list;
    }

}
