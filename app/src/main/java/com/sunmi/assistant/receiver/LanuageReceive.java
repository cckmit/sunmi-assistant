package com.sunmi.assistant.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sunmi.assistant.MyApplication;
import com.sunmi.assistant.ui.activity.MainActivity;

public class LanuageReceive extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 杀掉进程
        //MainActivity.getInstance().finish();
        MyApplication.getInstance().finishActivities();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
