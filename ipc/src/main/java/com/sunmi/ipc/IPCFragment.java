package com.sunmi.ipc;

import android.widget.Button;
import android.widget.VideoView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseFragment;
import sunmi.common.view.TitleBarView;

@EFragment(resName = "fragment_ipc")
public class IPCFragment extends BaseFragment {

    @ViewById(resName = "title_bar")
    TitleBarView titleBar;
    @ViewById(resName = "vv_ipc")
    VideoView videoView;
    @ViewById(resName = "btn_play")
    Button btnPlay;
    @ViewById(resName = "btn_pause")
    Button btnPause;
    @ViewById(resName = "btn_stop")
    Button btnStop;

    private static String UID = "C3YABT1MPRV4BM6GUHXJ";

    @AfterViews
    void init() {
    }

    @Click(resName = "btn_play")
    void playClick() {
        IOTCClient.start(UID);
    }

}
