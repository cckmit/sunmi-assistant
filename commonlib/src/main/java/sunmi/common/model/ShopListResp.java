package sunmi.common.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ShopListResp {

    private List<ShopInfo> shop_list;
    private int total_count;
    private int return_count;

    public List<ShopInfo> getShop_list() {
        return shop_list;
    }

    public int getTotal_count() {
        return total_count;
    }

    public int getReturn_count() {
        return return_count;
    }

    public static class ShopInfo implements Parcelable {

        private int shop_id;
        private String shop_name;
        private int type_one;
        private int type_two;
        private String type_name;
        private int business_status;
        private int province;
        private int city;
        private int area;
        private String address;
        private String region;
        private String business_hours;
        private String contact_person;
        private String contact_tel;
        private String contact_email;
        private int created_time;
        private int modified_time;

        protected ShopInfo(Parcel in) {
            shop_id = in.readInt();
            shop_name = in.readString();
            type_one = in.readInt();
            type_two = in.readInt();
            type_name = in.readString();
            business_status = in.readInt();
            province = in.readInt();
            city = in.readInt();
            area = in.readInt();
            address = in.readString();
            region = in.readString();
            business_hours = in.readString();
            contact_person = in.readString();
            contact_tel = in.readString();
            contact_email = in.readString();
            created_time = in.readInt();
            modified_time = in.readInt();
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

        public int getShop_id() {
            return shop_id;
        }

        public String getShop_name() {
            return shop_name;
        }

        public int getType_one() {
            return type_one;
        }

        public int getType_two() {
            return type_two;
        }

        public String getType_name() {
            return type_name;
        }

        public int getBusiness_status() {
            return business_status;
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

        public String getRegion() {
            return region;
        }

        public String getBusiness_hours() {
            return business_hours;
        }

        public String getContact_person() {
            return contact_person;
        }

        public String getContact_tel() {
            return contact_tel;
        }

        public String getContact_email() {
            return contact_email;
        }

        public int getCreated_time() {
            return created_time;
        }

        public int getModified_time() {
            return modified_time;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(shop_id);
            dest.writeString(shop_name);
            dest.writeInt(type_one);
            dest.writeInt(type_two);
            dest.writeString(type_name);
            dest.writeInt(business_status);
            dest.writeInt(province);
            dest.writeInt(city);
            dest.writeInt(area);
            dest.writeString(address);
            dest.writeString(region);
            dest.writeString(business_hours);
            dest.writeString(contact_person);
            dest.writeString(contact_tel);
            dest.writeString(contact_email);
            dest.writeInt(created_time);
            dest.writeInt(modified_time);
        }
    }
}
