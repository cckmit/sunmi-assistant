package sunmi.common.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by YangShiJie on 2019/6/26.
 */
public class PlatformInfo{


    private List<SaasListBean> saas_list;

    public List<SaasListBean> getSaasList() {
        return saas_list;
    }

    public void setSaasList(List<SaasListBean> SaasList) {
        this.saas_list = SaasList;
    }

    public static class SaasListBean implements Serializable {
        /**
         * name : 客无忧
         * source : 1
         */

        private String saas_name;
        private int saas_source;

        public String getSaas_name() {
            return saas_name;
        }

        public void setSaas_name(String saas_name) {
            this.saas_name = saas_name;
        }

        public int getSaas_source() {
            return saas_source;
        }

        public void setSaas_source(int saas_source) {
            this.saas_source = saas_source;
        }
    }
}
