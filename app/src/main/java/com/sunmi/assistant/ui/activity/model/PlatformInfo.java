package com.sunmi.assistant.ui.activity.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by YangShiJie on 2019/6/26.
 */
public class PlatformInfo{


    private List<SaasListBean> SaasList;

    public List<SaasListBean> getSaasList() {
        return SaasList;
    }

    public void setSaasList(List<SaasListBean> SaasList) {
        this.SaasList = SaasList;
    }

    public static class SaasListBean implements Serializable {
        /**
         * name : 客无忧
         * source : 1
         */

        private String name;
        private int source;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getSource() {
            return source;
        }

        public void setSource(int source) {
            this.source = source;
        }
    }
}
