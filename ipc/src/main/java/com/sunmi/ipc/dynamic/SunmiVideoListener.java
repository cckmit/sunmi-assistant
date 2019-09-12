package com.sunmi.ipc.dynamic;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * @author yangShiJie
 * @date 2019-09-09
 */
interface SunmiVideoListener extends IMediaPlayer.OnBufferingUpdateListener,
        IMediaPlayer.OnCompletionListener,
        IMediaPlayer.OnPreparedListener,
        IMediaPlayer.OnInfoListener,
        IMediaPlayer.OnVideoSizeChangedListener,
        IMediaPlayer.OnErrorListener,
        IMediaPlayer.OnSeekCompleteListener {
}
