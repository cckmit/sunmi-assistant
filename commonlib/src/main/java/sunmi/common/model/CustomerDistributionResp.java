package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CustomerDistributionResp {

    @SerializedName("count_list")
    private List<CountListBean> countList;

    public List<CountListBean> getCountList() {
        return countList;
    }

    public void setCountList(List<CountListBean> countList) {
        this.countList = countList;
    }

}
