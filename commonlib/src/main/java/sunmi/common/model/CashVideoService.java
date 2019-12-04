package sunmi.common.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-12-03.
 */
public class CashVideoService implements Parcelable {

    private int deviceId;
    private String deviceSn;
    private String deviceName;
    private int totalCount;
    private int abnormalVideoCount;
    private String imgUrl;

    public CashVideoService() {

    }

    protected CashVideoService(Parcel in) {
        deviceId = in.readInt();
        deviceSn = in.readString();
        deviceName = in.readString();
        totalCount = in.readInt();
        abnormalVideoCount = in.readInt();
        imgUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(deviceId);
        dest.writeString(deviceSn);
        dest.writeString(deviceName);
        dest.writeInt(totalCount);
        dest.writeInt(abnormalVideoCount);
        dest.writeString(imgUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CashVideoService> CREATOR = new Creator<CashVideoService>() {
        @Override
        public CashVideoService createFromParcel(Parcel in) {
            return new CashVideoService(in);
        }

        @Override
        public CashVideoService[] newArray(int size) {
            return new CashVideoService[size];
        }
    };

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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
