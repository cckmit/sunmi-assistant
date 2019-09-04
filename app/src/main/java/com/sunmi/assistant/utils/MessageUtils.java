package com.sunmi.assistant.utils;

import android.support.annotation.ArrayRes;

import com.sunmi.assistant.MyApplication;
import com.sunmi.assistant.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-26.
 */
public class MessageUtils {

    private Map<String, String> msgFirstMap = new HashMap<>();
    private Map<String, String> msgSecondMap = new HashMap<>();

    private MessageUtils() {
        String[] msgTag = getStrArray(R.array.msg_tag);
        String[] msgFirst = getStrArray(R.array.msg_analysis_first);
        String[] msgSecond = getStrArray(R.array.msg_analysis_second);
        for (int i = 0; i < msgSecond.length; i++) {
            msgFirstMap.put(msgTag[i], msgFirst[i]);
            msgSecondMap.put(msgTag[i], msgSecond[i]);
        }
        for (int i = msgSecond.length; i < msgFirst.length; i++){
            msgFirstMap.put(msgTag[i], msgFirst[i]);
        }

    }

    private static final class Singleton {
        private static final MessageUtils INSTANCE = new MessageUtils();
    }

    public static MessageUtils getInstance() {
        return Singleton.INSTANCE;
    }

    public String getMsgFirst(String key) {
        return msgFirstMap.get(key);
    }

    public String getMsgSecond(String key) {
        return msgSecondMap.get(key);
    }

    private String[] getStrArray(@ArrayRes int id) {
        return MyApplication.getInstance().getResources().getStringArray(id);
    }
}
