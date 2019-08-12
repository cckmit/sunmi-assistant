package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author yinhui
 * @date 2019-08-08
 */
public class ShopRegionResp {

    @SerializedName("region_list")
    private List<Province> regionList;

    public List<Province> getRegionList() {
        return regionList;
    }

    public static class Province {
        /**
         * province : 2
         * name : 黑龙江省
         * children : [{"city":"38","name":"大兴安岭地区","children":[{"county":"400","name":"塔河县"},{"county":"401","name":"漠河县"},{"county":"402","name":"呼玛县"}]}]
         */

        @SerializedName("province")
        private int province;
        @SerializedName("name")
        private String name;
        @SerializedName("children")
        private List<City> children;

        public int getProvince() {
            return province;
        }

        public String getName() {
            return name;
        }

        public List<City> getChildren() {
            return children;
        }

    }

    public static class City {
        /**
         * city : 38
         * name : 大兴安岭地区
         * children : [{"county":"400","name":"塔河县"},{"county":"401","name":"漠河县"},{"county":"402","name":"呼玛县"}]
         */

        @SerializedName("city")
        private int city;
        @SerializedName("name")
        private String name;
        @SerializedName("children")
        private List<Area> children;

        public int getCity() {
            return city;
        }

        public String getName() {
            return name;
        }

        public List<Area> getChildren() {
            return children;
        }


    }

    public static class Area {
        /**
         * county : 400
         * name : 塔河县
         */

        @SerializedName("county")
        private int county;
        @SerializedName("name")
        private String name;

        public int getCounty() {
            return county;
        }

        public String getName() {
            return name;
        }

    }
}
