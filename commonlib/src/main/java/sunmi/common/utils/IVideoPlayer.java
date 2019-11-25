package sunmi.common.utils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Description: 封装ijkPlayer的videoView
 * Created by bruce on 2019/6/19.
 */
public class IVideoPlayer extends RelativeLayout {

    VideoPlayListener videoPlayListener;
    private Context mContext;
    private SurfaceView surfaceView, cacheSurfaceView;
    //由ijkplayer提供，用于播放视频，需要给他传入一个surfaceView
    private IMediaPlayer mediaPlayer = null, cacheMediaPlayer = null;
    private IMediaPlayer currentMediaPlayer = null;
    //视频文件地址
    private Queue<String> urlQueue = new LinkedBlockingQueue<>();
    //视频请求header
    private Map<String, String> header;

    //    private AudioManager audioManager;
//    private AudioFocusHelper audioFocusHelper;
    IMediaPlayer.OnCompletionListener completionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer iMediaPlayer) {
            iMediaPlayer.setDisplay(null);
            if (currentMediaPlayer == mediaPlayer) {
                initPlayer(false);
                startCachePlayer();
                return;
            }
            if (currentMediaPlayer == cacheMediaPlayer) {
                initCachePlayer();
                startPlayer();
            }
        }
    };

    public IVideoPlayer(@NonNull Context context) {
        this(context, null);
    }

    public IVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    //初始化
    private void init(Context context) {
        mContext = context;
        createSurfaceView();
        createCacheSurfaceView();
//        mAudioManager = (AudioManager) mContext.getApplicationContext()
//                .getSystemService(Context.AUDIO_SERVICE);
//        audioFocusHelper = new AudioFocusHelper();
    }

    //设置播放地址
    public void setUrlQueue(List<String> urlList) {
        urlQueue.clear();
        urlQueue.addAll(urlList);
    }

    public void setVideoPlayListener(VideoPlayListener videoPlayListener) {
        this.videoPlayListener = videoPlayListener;
    }

    //创建默认surfaceView
    private void createSurfaceView() {
        surfaceView = new SurfaceView(mContext);
        surfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                if (mediaPlayer != null) {
                    mediaPlayer.setDisplay(surfaceHolder);
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        addView(surfaceView, -1, layoutParams);
    }

    //创建第二个surfaceView
    private void createCacheSurfaceView() {
        cacheSurfaceView = new SurfaceView(mContext);
        cacheSurfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        addView(cacheSurfaceView, 0, layoutParams);
//        cacheSurfaceView.setVisibility(GONE);
//        LayoutParams lp0 = new LayoutParams(0, 0);
//        cacheSurfaceView.setLayoutParams(lp0);
        cacheSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                if (cacheMediaPlayer != null) {
                    cacheMediaPlayer.setDisplay(surfaceHolder);
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });
    }

    private void initPlayer(final boolean isFirstVideo) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (urlQueue.isEmpty()) {
            if (videoPlayListener != null) {
                videoPlayListener.onPlayComplete();
            }
            return;
        }
        mediaPlayer = createPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDisplay(surfaceView.getHolder());
        String url = urlQueue.poll();
        try {
            mediaPlayer.setDataSource(mContext, Uri.parse(url), header);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnCompletionListener(completionListener);
        if (mediaPlayer != null)
            mediaPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(IMediaPlayer iMediaPlayer) {
                    if (isFirstVideo) {
                        startPlayer();
                        if (videoPlayListener != null) {
                            videoPlayListener.onStartPlay();
                        }
                    } else {
                        mediaPlayer.start();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mediaPlayer.pause();
                            }
                        }, 100);
                    }
                }
            });
    }

    private void initCachePlayer() {
        if (cacheMediaPlayer != null) {
            cacheMediaPlayer.stop();
            cacheMediaPlayer.release();
            cacheMediaPlayer = null;
        }
        if (urlQueue.isEmpty()) {
            if (videoPlayListener != null) videoPlayListener.onPlayComplete();
            return;
        }
        cacheMediaPlayer = createPlayer();
        cacheMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        cacheMediaPlayer.setDisplay(cacheSurfaceView.getHolder());
        String url = urlQueue.poll();
        try {
            cacheMediaPlayer.setDataSource(mContext, Uri.parse(url), header);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cacheMediaPlayer.prepareAsync();
        cacheMediaPlayer.setOnCompletionListener(completionListener);
        cacheMediaPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                cacheMediaPlayer.start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (cacheMediaPlayer != null) {
                            cacheMediaPlayer.pause();
                        }
                    }
                }, 100);
            }
        });
    }

    private void startPlayer() {
        if (mediaPlayer != null) {
            currentMediaPlayer = mediaPlayer;
            mediaPlayer.start();
//            audioFocusHelper.requestFocus();
//            LayoutParams lp1 = new LayoutParams(LayoutParams.MATCH_PARENT,
//                    LayoutParams.MATCH_PARENT);
//            surfaceView.setLayoutParams(lp1);
            surfaceView.setVisibility(VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                    LayoutParams lp0 = new LayoutParams(0, 0);
//                    cacheSurfaceView.setLayoutParams(lp0);
                    cacheSurfaceView.setVisibility(GONE);
                }
            }, 200);
        }
    }

    private void startCachePlayer() {
        if (cacheMediaPlayer != null) {
            currentMediaPlayer = cacheMediaPlayer;
            cacheMediaPlayer.start();
//            audioFocusHelper.requestFocus();
//            LayoutParams lp1 = new LayoutParams(LayoutParams.MATCH_PARENT,
//                    LayoutParams.MATCH_PARENT);
//            cacheSurfaceView.setLayoutParams(lp1);
            cacheSurfaceView.setVisibility(VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                    LayoutParams lp0 = new LayoutParams(0, 0);
//                    surfaceView.setLayoutParams(lp0);
                    surfaceView.setVisibility(GONE);
                }
            }, 200);
        }
    }

    //创建一个新的player
    private IMediaPlayer createPlayer() {
        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "http-detect-range-support", 1);

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "min-frames", 100);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);

        ijkMediaPlayer.setVolume(1.0f, 1.0f);
        setEnableMediaCodec(ijkMediaPlayer, false);
        return ijkMediaPlayer;
    }

    //设置是否开启硬解码
    private void setEnableMediaCodec(IjkMediaPlayer ijkMediaPlayer, boolean isEnable) {
        int value = isEnable ? 1 : 0;
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", value);//开启硬解码
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", value);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", value);
    }

    /**
     * 开始播放
     */
    public void startPlay() {
        release();
        initPlayer(true);
        initCachePlayer();
    }

    /**
     * 暂停
     */
    public void pause() {
        try {
            if (currentMediaPlayer != null) {
                currentMediaPlayer.pause();
//            audioFocusHelper.abandonFocus();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 恢复
     */
    public void play() {
        if (currentMediaPlayer != null) {
            currentMediaPlayer.start();
        }
    }

    /**
     * 停止
     */
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        if (cacheMediaPlayer != null) {
            cacheMediaPlayer.stop();
        }
//            audioFocusHelper.abandonFocus();
    }

    public void reset() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
        }
        if (cacheMediaPlayer != null) {
            cacheMediaPlayer.reset();
        }
//            audioFocusHelper.abandonFocus();
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (cacheMediaPlayer != null) {
            cacheMediaPlayer.reset();
            cacheMediaPlayer.release();
            cacheMediaPlayer = null;
        }
//            audioFocusHelper.abandonFocus();
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying()
                || cacheMediaPlayer != null && cacheMediaPlayer.isPlaying();
    }

    public void setOnPreparedListener(IMediaPlayer.OnPreparedListener listener) {
        if (mediaPlayer != null) {
            mediaPlayer.setOnPreparedListener(listener);
        }
    }

    public void setOnVideoSizeChangedListener(IMediaPlayer.OnVideoSizeChangedListener listener) {
        if (mediaPlayer != null) {
            mediaPlayer.setOnVideoSizeChangedListener(listener);
        }
    }

    public void setOnBufferingUpdateListener(IMediaPlayer.OnBufferingUpdateListener listener) {
        if (mediaPlayer != null) {
            mediaPlayer.setOnBufferingUpdateListener(listener);
        }
    }

    public void setOnCompletionListener(IMediaPlayer.OnCompletionListener listener) {
        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(listener);
        }
    }

    public void setOnSeekCompleteListener(IMediaPlayer.OnSeekCompleteListener listener) {
        if (mediaPlayer != null) {
            mediaPlayer.setOnSeekCompleteListener(listener);
        }
    }

    public void setOnInfoListener(IMediaPlayer.OnInfoListener listener) {
        if (mediaPlayer != null) {
            mediaPlayer.setOnInfoListener(listener);
        }
    }

    public void setOnErrorListener(IMediaPlayer.OnErrorListener listener) {
        if (mediaPlayer != null) {
            mediaPlayer.setOnErrorListener(listener);
        }
    }

    /**
     * 加载视频
     *
     * @param url
     */
    public void load(String url) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = createPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDisplay(surfaceView.getHolder());
        try {
            mediaPlayer.setDataSource(mContext, Uri.parse(url), header);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
    }

    /**
     * 开始播放
     */
    public void startVideo() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    public void pauseVideo() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public long getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        } else {
            return 0;
        }
    }

    public long getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    public void seekTo(long l) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(l);
        }
    }

    /**
     * 时长格式化显示
     */
    public String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds)
                : String.format("%02d:%02d", minutes, seconds);
    }

    public interface VideoPlayListener {
        void onStartPlay();

        void onPlayComplete();
    }

}
