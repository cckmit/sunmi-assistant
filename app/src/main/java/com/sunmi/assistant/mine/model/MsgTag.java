package com.sunmi.assistant.mine.model;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import sunmi.common.utils.log.LogCat;

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
            //LogCat.e("MsgTag", "77777777777:" + str);
            String[] detail = str.split("=");
            if (detail.length > 1) {
                String key = detail[0];
                String value = getUrlDecoderString(detail[1]);
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
        return "";
    }
}
