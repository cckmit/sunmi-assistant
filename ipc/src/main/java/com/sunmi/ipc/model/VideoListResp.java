package com.sunmi.ipc.model;

import java.util.List;

/**
 * Description:
 * Created by bruce on 2019/5/10.
 */
public class VideoListResp {
    /**
     * video_list : [{"device_id":2222,"sn":"FS101D8BS00088","video_name":"FS101D8BS00088_1551854897_1551854958.flv","url":"http://test.cdn.sunmi.com/VIDEO/IPC/4EBC673EA927F122A58FAED2FDEEF9F6","video_start_time":1551854897,"video_end_time":1551854958,"storage_source":0},{"device_id":2222,"sn":"FS101D8BS00088","video_name":"FS101D8BS00088_1551854958_1551855019.flv","url":"http://test.cdn.sunmi.com/VIDEO/IPC/E84E3174B74875F0E9EB57A5086EDF4E","video_start_time":1551854958,"video_end_time":1551855019,"storage_source":0}]
     * total_count : 2
     */

    private int total_count;
    private List<VideoBean> video_list;

    public int getTotal_count() {
        return total_count;
    }

    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }

    public List<VideoBean> getVideo_list() {
        return video_list;
    }

    public void setVideo_list(List<VideoBean> video_list) {
        this.video_list = video_list;
    }

    public static class VideoBean {
        /**
         * name : 0E5E023AB
         * url : http: //sunmi-test.oss-cn-hangzhou.aliyuncs.com/VIDEO/IPC/SS101D8BS00087/3C85FC47FF82F66E6E9989C0E5E023AB
         * device_id : 2237.0
         * start_time : 1.55853732E9
         * end_time : 1.558537328E9
         */

        private String name;
        private String url;
        private double device_id;
        private double start_time;
        private double end_time;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public double getDevice_id() {
            return device_id;
        }

        public void setDevice_id(double device_id) {
            this.device_id = device_id;
        }

        public double getStart_time() {
            return start_time;
        }

        public void setStart_time(double start_time) {
            this.start_time = start_time;
        }

        public double getEnd_time() {
            return end_time;
        }

        public void setEnd_time(double end_time) {
            this.end_time = end_time;
        }
    }

}
