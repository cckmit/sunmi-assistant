package com.sunmi.assistant.mine.model;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-21.
 */
public class MsgTag {

    private String tag;
    private Map<String, String> msgMap = new HashMap<>();

    public MsgTag(String content) {
        String[] msg = content.split(":");
        tag = msg[0];
        String[] msgContent = msg[1].split("&");
        for (String str : msgContent) {
            String[] detail = str.split("=");
            String key = detail[0];
            String value = getUrlDecoderString(detail[1]);
            if (value != null) {
                msgMap.put(key, value);
            }
        }
    }

    public String getTag() {
        return tag;
    }

    public Map<String, String> getMsgMap() {
        return msgMap;
    }

    private static String getUrlDecoderString(String str) {
        if (str == null) {
            return "";
        }
        try {
            return URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
