package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author yinhui
 * @date 2019-06-20
 */
public class ShopInfoResp {

    /**
     * shop_id : 7557
     * shop_name : 门店123
     * type_one : 1
     * type_two : 15
     * type_name :
     * business_status : 0
     * province : 1
     * city : 11
     * area : 111
     * address : 淞沪路321
     * region :
     * business_hours :
     * contact_person : 张三
     * contact_tel : 123456
     * contact_email :
     * created_time : 0
     * modified_time : 0
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
    @SerializedName("business_status")
    private int businessStatus;
    @SerializedName("business_area")
    private float businessArea;
    @SerializedName("province")
    private int province;
    @SerializedName("city")
    private int city;
    @SerializedName("area")
    private int area;
    @SerializedName("address")
    private String address;
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
    @SerializedName("lat")
    private String lat;
    @SerializedName("lng")
    private String lng;

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public int getTypeOne() {
        return typeOne;
    }

    public void setTypeOne(int typeOne) {
        this.typeOne = typeOne;
    }

    public int getTypeTwo() {
        return typeTwo;
    }

    public void setTypeTwo(int typeTwo) {
        this.typeTwo = typeTwo;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getBusinessStatus() {
        return businessStatus;
    }

    public void setBusinessStatus(int businessStatus) {
        this.businessStatus = businessStatus;
    }

    public int getProvince() {
        return province;
    }

    public void setProvince(int province) {
        this.province = province;
    }

    public int getCity() {
        return city;
    }

    public void setCity(int city) {
        this.city = city;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getBusinessHours() {
        return businessHours;
    }

    public void setBusinessHours(String businessHours) {
        this.businessHours = businessHours;
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

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public int getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(int createdTime) {
        this.createdTime = createdTime;
    }

    public int getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(int modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
