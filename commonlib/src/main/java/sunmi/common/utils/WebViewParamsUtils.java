package sunmi.common.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Description:
 *
 * @author linyuanpeng on 2020-01-09.
 */
public class WebViewParamsUtils {

    public static String getCloudStorageParams(List<String> snList, String productNo) {
        String params = "";
        try {
            JSONObject userInfo = new JSONObject()
                    .put("token", SpUtils.getStoreToken())
                    .put("company_id", SpUtils.getCompanyId())
                    .put("shop_id", SpUtils.getShopId());
            JSONObject cloudStorage = new JSONObject()
                    .put("sn_list", new JSONArray(snList))
                    .put("productNo", productNo);
            params = new JSONObject()
                    .put("userInfo", userInfo)
                    .put("cloudStorage", cloudStorage)
                    .toString();
            return params;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return params;
    }

    public static String getCloudStorageParams(String sn, String productNo) {
        String params = "";
        try {
            JSONObject userInfo = new JSONObject()
                    .put("token", SpUtils.getStoreToken())
                    .put("company_id", SpUtils.getCompanyId())
                    .put("shop_id", SpUtils.getShopId());
            JSONObject cloudStorage = new JSONObject()
                    .put("sn_list", new JSONArray().put(sn))
                    .put("productNo", productNo);
            params = new JSONObject()
                    .put("userInfo", userInfo)
                    .put("cloudStorage", cloudStorage)
                    .toString();
            return params;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return params;
    }


    public static String getCashVideoParams() {
        String params = "";
        try {
            JSONObject userInfo = new JSONObject()
                    .put("token", SpUtils.getStoreToken())
                    .put("company_id", SpUtils.getCompanyId())
                    .put("shop_id", SpUtils.getShopId());
            JSONObject cashVideo = new JSONObject()
                    .put("shop_name", SpUtils.getShopName());
            params = new JSONObject()
                    .put("userInfo", userInfo)
                    .put("cashVideo", cashVideo)
                    .toString();
            return params;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return params;
    }

    public static String getCashPreventLossParams(String sn) {
        String params = "";
        try {
            JSONObject userInfo = new JSONObject()
                    .put("token", SpUtils.getStoreToken())
                    .put("company_id", SpUtils.getCompanyId())
                    .put("shop_id", SpUtils.getShopId());
            JSONObject cashPreventLoss = new JSONObject()
                    .put("snList", new JSONArray().put(sn));
            params = new JSONObject()
                    .put("userInfo", userInfo)
                    .put("cashPreventLoss", cashPreventLoss)
                    .toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return params;
    }

    public static String getUserInfoParams() {
        String params = "";
        try {
            JSONObject userInfo = new JSONObject()
                    .put("token", SpUtils.getStoreToken())
                    .put("company_id", SpUtils.getCompanyId())
                    .put("shop_id", SpUtils.getShopId());
            params = new JSONObject()
                    .put("userInfo", userInfo)
                    .toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return params;
    }
}
