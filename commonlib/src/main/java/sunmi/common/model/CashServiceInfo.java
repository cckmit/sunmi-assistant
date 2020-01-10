package sunmi.common.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-12-03.
 */
public class CashServiceInfo implements Parcelable {

    private int deviceId;
    private String deviceSn;
    private String deviceName;
    private int totalCount;
    private int abnormalVideoCount;
    private boolean hasCloudStorage;
    private boolean hasCashLossPrevention;
    private int abnormalBehaviorCount;
    private String imgUrl;

    public CashServiceInfo() {
    }

    public CashServiceInfo(ServiceResp.Info info) {
        this.deviceId = info.getDeviceId();
        this.deviceSn = info.getDeviceSn();
        this.deviceName = info.getDeviceName();
        this.imgUrl = info.getImgUrl();
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceSn() {
        return deviceSn;
    }

    public void setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getAbnormalVideoCount() {
        return abnormalVideoCount;
    }

    public void setAbnormalVideoCount(int abnormalVideoCount) {
        this.abnormalVideoCount = abnormalVideoCount;
    }

    public boolean isHasCloudStorage() {
        return hasCloudStorage;
    }

    public void setHasCloudStorage(boolean hasCloudStorage) {
        this.hasCloudStorage = hasCloudStorage;
    }

    public boolean isHasCashLossPrevention() {
        return hasCashLossPrevention;
    }

    public void setHasCashLossPrevention(boolean hasCashLossPrevention) {
        this.hasCashLossPrevention = hasCashLossPrevention;
    }

    public int getAbnormalBehaviorCount() {
        return abnormalBehaviorCount;
    }

    public void setAbnormalBehaviorCount(int abnormalBehaviorCount) {
        this.abnormalBehaviorCount = abnormalBehaviorCount;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    protected CashServiceInfo(Parcel in) {
        deviceId = in.readInt();
        deviceSn = in.readString();
        deviceName = in.readString();
        totalCount = in.readInt();
        abnormalVideoCount = in.readInt();
        hasCloudStorage = in.readByte() != 0;
        hasCashLossPrevention = in.readByte() != 0;
        abnormalBehaviorCount = in.readInt();
        imgUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(deviceId);
        dest.writeString(deviceSn);
        dest.writeString(deviceName);
        dest.writeInt(totalCount);
        dest.writeInt(abnormalVideoCount);
        dest.writeByte((byte) (hasCloudStorage ? 1 : 0));
        dest.writeByte((byte) (hasCashLossPrevention ? 1 : 0));
        dest.writeInt(abnormalBehaviorCount);
        dest.writeString(imgUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CashServiceInfo> CREATOR = new Creator<CashServiceInfo>() {
        @Override
        public CashServiceInfo createFromParcel(Parcel in) {
            return new CashServiceInfo(in);
        }

        @Override
        public CashServiceInfo[] newArray(int size) {
            return new CashServiceInfo[size];
        }
    };

}
