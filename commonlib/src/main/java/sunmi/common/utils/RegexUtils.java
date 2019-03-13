package sunmi.common.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

    /**
     * 验证手机号
     * 130，131，132，133，134，135，136，137，138，139
     * 145，147
     * 150，151，152，153，155，156，157，158，159
     * 166，
     * 176，177，178
     * 180，181，182，183，184，185，186，187，188，189
     * 198，199
     */
    public static boolean isChinaPhone(String mobiles) {
        Pattern p = Pattern
                .compile("^((13[0-9])|(14[57])|(15[^4,\\D])|(166)|(17[0-9])|(18[0-9])|(19[8-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 数字字母组合，不能全是数组，不能全是字母，8-30位
     */
    public static boolean isValidPassword(String str) {
        //(?!^\\d+$)(?!^[a-zA-Z]+$)(?!^[_#@]+$).{8,30}
        Pattern pattern = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,30}$");
        Matcher match = pattern.matcher(str);
        return match.matches();
    }

    /**
     * 判断是否为邮箱
     *
     * @param emails
     * @return
     */
    public static boolean isEmail(String emails) {
        String reg = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(emails);
        return m.matches();
    }

    //mac地址合法性校验
    public static boolean isValidMac(String macStr) {
        if (TextUtils.isEmpty(macStr)) {
            return false;
        }
        String macAddressRule = "([A-Fa-f0-9]{2}[-,:]){5}[A-Fa-f0-9]{2}";
        return macStr.matches(macAddressRule);
    }

    /**
     * 校验ip是否合法
     */
    public static boolean isIP(final String input) {
        Pattern IPV4_PATTERN = Pattern.compile(
                "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");
        return IPV4_PATTERN.matcher(input).matches();
    }

    /**
     * 非D类、E类，非全0、非全1、非回环
     */
    public static boolean isInvalidDns(final String input) {
        if (TextUtils.isEmpty(input)) return true;
        Pattern IPV4_PATTERN = Pattern.compile(
                "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");
        boolean isIp = IPV4_PATTERN.matcher(input).matches();
        if (!isIp) return true;
        int first = Integer.parseInt(input.split("\\.")[0]);
        return first == 127 || first >= 224 || TextUtils.equals("0.0.0.0", input)
                || TextUtils.equals("255.255.255.255", input);
    }

    /**
     * 校验子网掩码是否合法
     */
    public static boolean isValidSubnetMask(final String input) {
        return !TextUtils.equals(input, "0.0.0.0")
                && !TextUtils.equals(input, "255.255.255.255");
    }

}
