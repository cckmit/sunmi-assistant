package com.sunmi.ipc.presenter;

import android.support.test.runner.AndroidJUnit4;

import com.sunmi.ipc.contract.IpcManagerContract;
import com.sunmi.ipc.view.activity.IpcManagerActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Description:
 * Created by bruce on 2019/9/27.
 */
@RunWith(AndroidJUnit4.class)
public class IpcManagerPresenterTest {
    @Mock
    private IpcManagerContract.View iView;

    private IpcManagerPresenter ipcManagerPresenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ipcManagerPresenter = new IpcManagerPresenter();
        iView = new IpcManagerActivity();
        ipcManagerPresenter.attachView(iView);
    }

    @Test
    public void getTimeSlots() {
//        verify(mockWebService).logout();

    }

    @Test
    public void getPlaybackList() {
    }

    @Test
    public void startLive() {
    }

    @Test
    public void startPlayback() {
    }

    @Test
    public void getCloudVideoList() {
    }

    @Test
    public void changeQuality() {
    }

    @Test
    public void getTimeSlots1() {
    }

    @Test
    public void getPlaybackList1() {
    }

    @Test
    public void startLive1() {
    }

    @Test
    public void startPlayback1() {
    }

    @Test
    public void getCloudVideoList1() {
    }

    @Test
    public void changeQuality1() {
    }
}