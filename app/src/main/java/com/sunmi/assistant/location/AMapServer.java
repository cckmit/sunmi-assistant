package com.sunmi.assistant.location;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * 高德定位服务
 *
 * @author yangShiJie
 * @date 2019-09-03
 */
public class AMapServer {

    private static AMapLocationClient locationClient = null;
    private static AMapLocationClientOption locationOption = null;
    private CallBackLocation callBackLocationListener;
    /**
     * 定位监听
     */
    AMapLocationListener locationListener = location -> {
        if (null != location) {
            //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
            if (location.getErrorCode() == 0) {
                if (callBackLocationListener != null) {
                    callBackLocationListener.aMapLocation(location);
                }
            } else {
                if (callBackLocationListener != null) { //定位失败
                    callBackLocationListener.aMapLocationFail(location.getErrorInfo());
                }
            }
        } else {
            if (callBackLocationListener != null) { //定位失败
                callBackLocationListener.aMapLocationFail("定位失败");
            }
        }
    };

    /**
     * 初始化定位
     *
     * @param context
     * @param isOnceLocation 设置是否单次定位
     */
    public void initLocation(Context context, boolean isOnceLocation) {
        //初始化client
        locationClient = new AMapLocationClient(context);
        locationOption = getDefaultOption(isOnceLocation);
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
        //开启定位
        locationClient.startLocation();
    }

    /**
     * 默认的定位参数
     */
    private AMapLocationClientOption getDefaultOption(boolean isOnceLocation) {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(1000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(isOnceLocation);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTPS);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);//可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        return mOption;
    }

    /**
     * 开始定位
     */
    public void startLocation() {
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    /**
     * 停止定位
     */
    public void stopLocation() {
        locationClient.stopLocation();
    }

    /**
     * 销毁定位
     */
    public void destroyLocation() {
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    public void setCallBackLocation(CallBackLocation callBackLocationListener) {
        this.callBackLocationListener = callBackLocationListener;
    }

    public interface CallBackLocation {
        /**
         * @param aMapLocation
         */
        void aMapLocation(AMapLocation aMapLocation);

        void aMapLocationFail(String msg);
    }
}
