package sunmi.common.model;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author yinhui
 * @date 2019-08-08
 */
public class ShopInfo implements Parcelable {

    private int shopId;
    private String shopName;
    private int typeOne;
    private int typeTwo;
    private String typeName;
    private int province;
    private int city;
    private int area;
    private String region;
    private String address;
    private float businessArea;
    private String contactPerson;
    private String contactTel;

    public ShopInfo(ShopInfoResp response) {
        shopId = response.getShopId();
        shopName = response.getShopName();
        typeOne = response.getTypeOne();
        typeTwo = response.getTypeTwo();
        typeName = response.getTypeName();
        province = response.getProvince();
        city = response.getCity();
        area = response.getArea();
        region = response.getRegion();
        address = response.getAddress();
        businessArea = response.getBusinessArea();
        contactPerson = response.getContactPerson();
        contactTel = response.getContactTel();
    }

    public int getShopId() {
        return shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public int getTypeOne() {
        return typeOne;
    }

    public int getTypeTwo() {
        return typeTwo;
    }

    public String getTypeName() {
        return typeName;
    }

    public int getProvince() {
        return province;
    }

    public int getCity() {
        return city;
    }

    public int getArea() {
        return area;
    }

    public String getRegionName() {
        return region;
    }

    public String getAddress() {
        return address;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public void setType(int typeOne, int typeTwo) {
        this.typeOne = typeOne;
        this.typeTwo = typeTwo;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public void setRegion(int province, int city, int area) {
        this.province = province;
        this.city = city;
        this.area = area;
    }

    public void setRegionName(String region) {
        this.region = region;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getBusinessArea() {
        return businessArea;
    }

    public void setBusinessArea(float businessArea) {
        this.businessArea = businessArea;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactTel() {
        return contactTel;
    }

    public void setContactTel(String contactTel) {
        this.contactTel = contactTel;
    }

    @Override
    public String toString() {
        return "ShopInfo{" +
                "shopId=" + shopId +
                ", shopName='" + shopName + '\'' +
                ", typeOne=" + typeOne +
                ", typeTwo=" + typeTwo +
                ", typeName='" + typeName + '\'' +
                ", province=" + province +
                ", city=" + city +
                ", area=" + area +
                ", region='" + region + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

    protected ShopInfo(Parcel in) {
        shopId = in.readInt();
        shopName = in.readString();
        typeOne = in.readInt();
        typeTwo = in.readInt();
        typeName = in.readString();
        province = in.readInt();
        city = in.readInt();
        area = in.readInt();
        region = in.readString();
        address = in.readString();
        businessArea = in.readFloat();
        contactPerson = in.readString();
        contactTel = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(shopId);
        dest.writeString(shopName);
        dest.writeInt(typeOne);
        dest.writeInt(typeTwo);
        dest.writeString(typeName);
        dest.writeInt(province);
        dest.writeInt(city);
        dest.writeInt(area);
        dest.writeString(region);
        dest.writeString(address);
        dest.writeFloat(businessArea);
        dest.writeString(contactPerson);
        dest.writeString(contactTel);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ShopInfo> CREATOR = new Creator<ShopInfo>() {
        @Override
        public ShopInfo createFromParcel(Parcel in) {
            return new ShopInfo(in);
        }

        @Override
        public ShopInfo[] newArray(int size) {
            return new ShopInfo[size];
        }
    };

}
