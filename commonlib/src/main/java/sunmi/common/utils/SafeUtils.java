package sunmi.common.utils;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import sunmi.common.constant.CommonConfig;

public class SafeUtils {
    public static final String ALGORITHM_DES = "DES/ECB/PKCS5Padding";
    public static final String ALGORITHM_DES_CBC = "DES/CBC/PKCS5Padding";

    /**
     * DES-CBC
     * 加密
     **/
    public static String EncryptDES_CBC(String message) {
        try {
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

            DESKeySpec desKeySpec = new DESKeySpec(CommonConfig.DES_KEY.getBytes("UTF-8"));

            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            IvParameterSpec iv = new IvParameterSpec(CommonConfig.DES_IV.getBytes("UTF-8"));
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

            return Base64.encodeToString(cipher.doFinal(message
                    .getBytes("UTF-8")), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    /**
     * DES-CBC
     * 解密
     **/
    public static String decrypt(String message) {
        try {
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            DESKeySpec desKeySpec = new DESKeySpec(CommonConfig.DES_KEY.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            IvParameterSpec iv = new IvParameterSpec(CommonConfig.DES_IV.getBytes("UTF-8"));

            byte[] encryptedWithoutB64 = Base64.decode(message, Base64.DEFAULT);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byte[] plainTextPwdBytes = cipher.doFinal(encryptedWithoutB64);
            return new String(plainTextPwdBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    /**
     * DES-ECB
     * 加密
     **/
    public static String encryptPassword(String clearText) {
        try {
            DESKeySpec keySpec = new DESKeySpec(CommonConfig.DES_KEY.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.encodeToString(cipher.doFinal(clearText
                    .getBytes("UTF-8")), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clearText;
    }

    /**
     * DES-ECB
     * 解密
     **/
    public static String decryptPassword(String encryptedPwd) {
        try {
            DESKeySpec keySpec = new DESKeySpec(CommonConfig.DES_KEY.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);
//            IvParameterSpec zeroIv = new IvParameterSpec(iv);

            byte[] encryptedWithoutB64 = Base64.decode(encryptedPwd, Base64.DEFAULT);
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] plainTextPwdBytes = cipher.doFinal(encryptedWithoutB64);
            return new String(plainTextPwdBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedPwd;
    }

    /**
     * base64 解密
     *
     * @param string
     * @return
     */
    public static String decodeBase64(String string) {
        //String jm = new String(org.apache.commons.codec.binary.Base64.decodeBase64(string.getBytes())); //解密
        String jm = new String(Base64.decode(string.getBytes(), Base64.NO_WRAP)); //解密
        return jm;
    }

    /**
     * base64 加密
     *
     * @param string
     * @return
     */
    public static String encodeBase64ToString(String string) {
        String str = new String(Base64.encode(string.getBytes(), Base64.NO_WRAP)); //加密
        return str;
    }

    public static byte[] encodeBase64ToByte(String string) {
        return Base64.encode(string.getBytes(), Base64.NO_WRAP);//加密
    }

}
