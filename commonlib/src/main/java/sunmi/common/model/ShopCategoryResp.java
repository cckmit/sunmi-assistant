package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author yinhui
 * @date 2019-08-08
 */
public class ShopCategoryResp {

    @SerializedName("shopType_list")
    private List<ShopTypeListBean> shopTypeList;

    public List<ShopTypeListBean> getShopTypeList() {
        return shopTypeList;
    }

    public static class ShopTypeListBean {
        /**
         * child : [{"id":"199","name":"餐饮店","pid":"1"}]
         * id : 1
         * name : 餐饮店
         * pid : 0
         */

        @SerializedName("id")
        private int id;
        @SerializedName("name")
        private String name;
        @SerializedName("pid")
        private String pid;
        @SerializedName("child")
        private List<ChildBean> child;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getPid() {
            return pid;
        }

        public List<ChildBean> getChild() {
            return child;
        }

        public static class ChildBean {
            /**
             * id : 199
             * name : 餐饮店
             * pid : 1
             */

            @SerializedName("id")
            private int id;
            @SerializedName("name")
            private String name;
            @SerializedName("pid")
            private String pid;

            public int getId() {
                return id;
            }

            public String getName() {
                return name;
            }

            public String getPid() {
                return pid;
            }

        }
    }
}
