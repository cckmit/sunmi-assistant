package sunmi.common.model;

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

}
