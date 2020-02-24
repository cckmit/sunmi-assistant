package sunmi.common.utils;

import android.support.annotation.StringRes;

import com.commonlibrary.R;

import java.util.HashMap;
import java.util.Map;

import sunmi.common.base.BaseApplication;

public class ServiceNameUtils {

    private Map<String,String> nameMap;

    private ServiceNameUtils(){
        nameMap = new HashMap<>();
        nameMap.put("YCC0002",getString(R.string.service_cloud_7));
        nameMap.put("A08000075",getString(R.string.service_cloud_7));
        nameMap.put("A08000076",getString(R.string.service_cloud_7));
        nameMap.put("A08000077",getString(R.string.service_cloud_30));
        nameMap.put("A08000078",getString(R.string.service_cloud_30));
    }

    private static final class Singleton{
        private static final ServiceNameUtils INSTANCE = new ServiceNameUtils();
    }

    public static ServiceNameUtils getInstance(){
        return Singleton.INSTANCE;
    }

    public String getServiceName(String productNo){
        return nameMap.get(productNo);
    }

    private String getString(@StringRes int id) {
        return BaseApplication.getInstance().getResources().getString(id);
    }
}
