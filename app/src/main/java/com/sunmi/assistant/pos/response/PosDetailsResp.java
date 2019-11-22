package com.sunmi.assistant.pos.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author yangShiJie
 * @date 2019-11-20
 */
public class PosDetailsResp implements Serializable {


    /**
     * device_info : {"imei":"","location":"","model":"","sn":"","rom_version":"","system_version":"","resolution":""}
     * net_info : {"use_ethernet":0,"use_wifi":0,"use_3g":0,"3g_intensity":"","ip":"","mac":"","operator":"","sim_number":""}
     * printer_info : {"sn":"","model":"","version":"","prt_length":"","prt_size":""}
     * running_info : {"battery_percent":"","battery_temper":"","is_charging":0,"used_cpu_percent":"","cpu_frequency":"","used_mem_percent":"","total_mem":"","used_sd":"","total_sd":""}
     */

    @SerializedName("device_info")
    private DeviceInfoBean deviceInfo;
    @SerializedName("net_info")
    private NetInfoBean netInfo;
    @SerializedName("printer_info")
    private PrinterInfoBean printerInfo;
    @SerializedName("running_info")
    private RunningInfoBean runningInfo;

    public DeviceInfoBean getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfoBean deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public NetInfoBean getNetInfo() {
        return netInfo;
    }

    public void setNetInfo(NetInfoBean netInfo) {
        this.netInfo = netInfo;
    }

    public PrinterInfoBean getPrinterInfo() {
        return printerInfo;
    }

    public void setPrinterInfo(PrinterInfoBean printerInfo) {
        this.printerInfo = printerInfo;
    }

    public RunningInfoBean getRunningInfo() {
        return runningInfo;
    }

    public void setRunningInfo(RunningInfoBean runningInfo) {
        this.runningInfo = runningInfo;
    }

    public static class DeviceInfoBean implements Serializable {
        /**
         * imei :
         * location :
         * model :
         * sn :
         * rom_version :
         * system_version :
         * resolution :
         */

        @SerializedName("imei")
        private String imei;
        @SerializedName("location")
        private String location;
        @SerializedName("model")
        private String model;
        @SerializedName("sn")
        private String sn;
        @SerializedName("rom_version")
        private String romVersion;
        @SerializedName("system_version")
        private String systemVersion;
        @SerializedName("resolution")
        private String resolution;

        public String getImei() {
            return imei;
        }

        public void setImei(String imei) {
            this.imei = imei;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getSn() {
            return sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public String getRomVersion() {
            return romVersion;
        }

        public void setRomVersion(String romVersion) {
            this.romVersion = romVersion;
        }

        public String getSystemVersion() {
            return systemVersion;
        }

        public void setSystemVersion(String systemVersion) {
            this.systemVersion = systemVersion;
        }

        public String getResolution() {
            return resolution;
        }

        public void setResolution(String resolution) {
            this.resolution = resolution;
        }
    }

    public static class NetInfoBean implements Serializable {
        /**
         * use_ethernet : 0
         * use_wifi : 0
         * use_3g : 0
         * 3g_intensity :
         * ip :
         * mac :
         * operator :
         * sim_number :
         */

        @SerializedName("use_ethernet")
        private int useEthernet;
        @SerializedName("use_wifi")
        private int useWifi;
        @SerializedName("use_3g")
        private int use3g;
        @SerializedName("3g_intensity")
        private String $3gIntensity;
        @SerializedName("ip")
        private String ip;
        @SerializedName("mac")
        private String mac;
        @SerializedName("operator")
        private String operator;
        @SerializedName("sim_number")
        private String simNumber;

        public int getUseEthernet() {
            return useEthernet;
        }

        public void setUseEthernet(int useEthernet) {
            this.useEthernet = useEthernet;
        }

        public int getUseWifi() {
            return useWifi;
        }

        public void setUseWifi(int useWifi) {
            this.useWifi = useWifi;
        }

        public int getUse3g() {
            return use3g;
        }

        public void setUse3g(int use3g) {
            this.use3g = use3g;
        }

        public String get$3gIntensity() {
            return $3gIntensity;
        }

        public void set$3gIntensity(String $3gIntensity) {
            this.$3gIntensity = $3gIntensity;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public String getSimNumber() {
            return simNumber;
        }

        public void setSimNumber(String simNumber) {
            this.simNumber = simNumber;
        }
    }

    public static class PrinterInfoBean implements Serializable {
        /**
         * sn :
         * model :
         * version :
         * prt_length :
         * prt_size :
         */

        @SerializedName("sn")
        private String sn;
        @SerializedName("model")
        private String model;
        @SerializedName("version")
        private String version;
        @SerializedName("prt_length")
        private String prtLength;
        @SerializedName("prt_size")
        private String prtSize;

        public String getSn() {
            return sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getPrtLength() {
            return prtLength;
        }

        public void setPrtLength(String prtLength) {
            this.prtLength = prtLength;
        }

        public String getPrtSize() {
            return prtSize;
        }

        public void setPrtSize(String prtSize) {
            this.prtSize = prtSize;
        }
    }

    public static class RunningInfoBean implements Serializable {
        /**
         * battery_percent :
         * battery_temper :
         * is_charging : 0
         * used_cpu_percent :
         * cpu_frequency :
         * used_mem_percent :
         * total_mem :
         * used_sd :
         * total_sd :
         */

        @SerializedName("battery_percent")
        private String batteryPercent;
        @SerializedName("battery_temper")
        private String batteryTemper;
        @SerializedName("is_charging")
        private int isCharging;
        @SerializedName("used_cpu_percent")
        private String usedCpuPercent;
        @SerializedName("cpu_frequency")
        private String cpuFrequency;
        @SerializedName("used_mem_percent")
        private String usedMemPercent;
        @SerializedName("total_mem")
        private String totalMem;
        @SerializedName("used_sd")
        private String usedSd;
        @SerializedName("total_sd")
        private String totalSd;

        public String getBatteryPercent() {
            return batteryPercent;
        }

        public void setBatteryPercent(String batteryPercent) {
            this.batteryPercent = batteryPercent;
        }

        public String getBatteryTemper() {
            return batteryTemper;
        }

        public void setBatteryTemper(String batteryTemper) {
            this.batteryTemper = batteryTemper;
        }

        public int getIsCharging() {
            return isCharging;
        }

        public void setIsCharging(int isCharging) {
            this.isCharging = isCharging;
        }

        public String getUsedCpuPercent() {
            return usedCpuPercent;
        }

        public void setUsedCpuPercent(String usedCpuPercent) {
            this.usedCpuPercent = usedCpuPercent;
        }

        public String getCpuFrequency() {
            return cpuFrequency;
        }

        public void setCpuFrequency(String cpuFrequency) {
            this.cpuFrequency = cpuFrequency;
        }

        public String getUsedMemPercent() {
            return usedMemPercent;
        }

        public void setUsedMemPercent(String usedMemPercent) {
            this.usedMemPercent = usedMemPercent;
        }

        public String getTotalMem() {
            return totalMem;
        }

        public void setTotalMem(String totalMem) {
            this.totalMem = totalMem;
        }

        public String getUsedSd() {
            return usedSd;
        }

        public void setUsedSd(String usedSd) {
            this.usedSd = usedSd;
        }

        public String getTotalSd() {
            return totalSd;
        }

        public void setTotalSd(String totalSd) {
            this.totalSd = totalSd;
        }
    }
}
