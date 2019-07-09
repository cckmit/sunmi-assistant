package com.sunmi.assistant.data.response;

import java.util.List;

/**
 * Description:
 * Created by bruce on 2019/6/27.
 */
public class AdListResp {
    private List<AdListBean> ad_list;

    public List<AdListBean> getAd_list() {
        return ad_list;
    }

    public void setAd_list(List<AdListBean> ad_list) {
        this.ad_list = ad_list;
    }

    public static class AdListBean {
        /**
         * name : zhangweinoad
         * link : www.zhangwei.com
         * image_addr : www.weizhang.com/tmp/zhangwei.png
         * upload_time : 123345
         */

        private String name;
        private String link;
        private String image_addr;
        private int upload_time;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getImage_addr() {
            return image_addr;
        }

        public void setImage_addr(String image_addr) {
            this.image_addr = image_addr;
        }

        public int getUpload_time() {
            return upload_time;
        }

        public void setUpload_time(int upload_time) {
            this.upload_time = upload_time;
        }
    }
}
