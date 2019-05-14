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
         * device_id : 2222
         * sn : FS101D8BS00088
         * video_name : FS101D8BS00088_1551854897_1551854958.flv
         * url : http://test.cdn.sunmi.com/VIDEO/IPC/4EBC673EA927F122A58FAED2FDEEF9F6
         * video_start_time : 1551854897
         * video_end_time : 1551854958
         * storage_source : 0
         */

        private int device_id;
        private String sn;
        private String video_name;
        private String url;
        private int video_start_time;
        private int video_end_time;
        private int storage_source;

        public int getDevice_id() {
            return device_id;
        }

        public void setDevice_id(int device_id) {
            this.device_id = device_id;
        }

        public String getSn() {
            return sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public String getVideo_name() {
            return video_name;
        }

        public void setVideo_name(String video_name) {
            this.video_name = video_name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getVideo_start_time() {
            return video_start_time;
        }

        public void setVideo_start_time(int video_start_time) {
            this.video_start_time = video_start_time;
        }

        public int getVideo_end_time() {
            return video_end_time;
        }

        public void setVideo_end_time(int video_end_time) {
            this.video_end_time = video_end_time;
        }

        public int getStorage_source() {
            return storage_source;
        }

        public void setStorage_source(int storage_source) {
            this.storage_source = storage_source;
        }
    }

}
