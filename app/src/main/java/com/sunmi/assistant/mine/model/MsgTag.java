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
        try {
            String[] msgContent;
            if (content.contains(":")) {
                String[] msg = content.split(":");
                tag = msg[0];
                msgContent = msg[1].split("&");
            } else {
                tag = content;
                msgContent = content.split("&");
            }
            for (String str : msgContent) {
                //LogCat.e("MsgTag", "77777777777:" + str);
                String[] detail = str.split("=");
                String key = detail[0];
                String value = "";
                if (detail.length > 1) {
                    value = getUrlDecoderString(detail[1]);
                }
                msgMap.put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getUrlDecoderString(String str) {
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

    public String getTag() {
        return tag;
    }

    public Map<String, String> getMsgMap() {
        return msgMap;
    }
}
