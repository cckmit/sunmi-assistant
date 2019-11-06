package sunmi.common.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author yinhui
 * @date 2019-06-20
 */
public class ShopInfo implements Parcelable {

    /**
     * shop_id : 8264
     * shop_name : 测试
     * type_one : 0
     * type_two : 0
     * type_name :
     * sunmi_shop_no : 260988040206
     * business_status : 0
     * province : 0
     * city : 0
     * area : 0
     * address : 上帝给他
     * lat :
     * lng :
     * business_area : 0
     * region :
     * business_hours :
     * contact_person : 大家参加
     * contact_tel :
     * contact_email :
     * created_time : 1560324092
     * modified_time : 1567580500
     * saas_exist : 0
     */

    @SerializedName("shop_id")
    private int shopId;
    @SerializedName("shop_name")
    private String shopName;
    @SerializedName("type_one")
    private int typeOne;
    @SerializedName("type_two")
    private int typeTwo;
    @SerializedName("type_name")
    private String typeName;
    @SerializedName("sunmi_shop_no")
    private String sunmiShopNo;
    @SerializedName("business_status")
    private int businessStatus;
    @SerializedName("province")
    private int province;
    @SerializedName("city")
    private int city;
    @SerializedName("area")
    private int area;
    @SerializedName("address")
    private String address;
    @SerializedName("lat")
    private String lat;
    @SerializedName("lng")
    private String lng;
    @SerializedName("business_area")
    private float businessArea;
    @SerializedName("region")
    private String region;
    @SerializedName("business_hours")
    private String businessHours;
    @SerializedName("contact_person")
    private String contactPerson;
    @SerializedName("contact_tel")
    private String contactTel;
    @SerializedName("contact_email")
    private String contactEmail;
    @SerializedName("created_time")
    private int createdTime;
    @SerializedName("modified_time")
    private int modifiedTime;
    @SerializedName("saas_exist")
    private int saasExist;

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

    public String getSunmiShopNo() {
        return sunmiShopNo;
    }

    public int getBusinessStatus() {
        return businessStatus;
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

    public String getAddress() {
        return address;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public float getBusinessArea() {
        return businessArea;
    }

    public String getRegion() {
        return region;
    }

    public String getBusinessHours() {
        return businessHours;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public String getContactTel() {
        return contactTel;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public int getCreatedTime() {
        return createdTime;
    }

    public int getModifiedTime() {
        return modifiedTime;
    }

    public int getSaasExist() {
        return saasExist;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public void setTypeOne(int typeOne) {
        this.typeOne = typeOne;
    }

    public void setTypeTwo(int typeTwo) {
        this.typeTwo = typeTwo;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public void setSunmiShopNo(String sunmiShopNo) {
        this.sunmiShopNo = sunmiShopNo;
    }

    public void setBusinessStatus(int businessStatus) {
        this.businessStatus = businessStatus;
    }

    public void setProvince(int province) {
        this.province = province;
    }

    public void setCity(int city) {
        this.city = city;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public void setBusinessArea(float businessArea) {
        this.businessArea = businessArea;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setBusinessHours(String businessHours) {
        this.businessHours = businessHours;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public void setContactTel(String contactTel) {
        this.contactTel = contactTel;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public void setCreatedTime(int createdTime) {
        this.createdTime = createdTime;
    }

    public void setModifiedTime(int modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public void setSaasExist(int saasExist) {
        this.saasExist = saasExist;
    }

    protected ShopInfo(Parcel in) {
        shopId = in.readInt();
        shopName = in.readString();
        typeOne = in.readInt();
        typeTwo = in.readInt();
        typeName = in.readString();
        sunmiShopNo = in.readString();
        businessStatus = in.readInt();
        province = in.readInt();
        city = in.readInt();
        area = in.readInt();
        address = in.readString();
        lat = in.readString();
        lng = in.readString();
        businessArea = in.readFloat();
        region = in.readString();
        businessHours = in.readString();
        contactPerson = in.readString();
        contactTel = in.readString();
        contactEmail = in.readString();
        createdTime = in.readInt();
        modifiedTime = in.readInt();
        saasExist = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(shopId);
        dest.writeString(shopName);
        dest.writeInt(typeOne);
        dest.writeInt(typeTwo);
        dest.writeString(typeName);
        dest.writeString(sunmiShopNo);
        dest.writeInt(businessStatus);
        dest.writeInt(province);
        dest.writeInt(city);
        dest.writeInt(area);
        dest.writeString(address);
        dest.writeString(lat);
        dest.writeString(lng);
        dest.writeFloat(businessArea);
        dest.writeString(region);
        dest.writeString(businessHours);
        dest.writeString(contactPerson);
        dest.writeString(contactTel);
        dest.writeString(contactEmail);
        dest.writeInt(createdTime);
        dest.writeInt(modifiedTime);
        dest.writeInt(saasExist);
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
