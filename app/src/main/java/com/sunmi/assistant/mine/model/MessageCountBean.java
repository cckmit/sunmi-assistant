package com.sunmi.assistant.mine.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-14.
 */
public class MessageCountBean {

    /**
     * total_count : 100
     * unread_count : 80
     * remind_unread_count : 50
     * model_count_list : [{"model_id":1,"model_name":"系统（tag）","total_count":20,"unread_count":18,"remind_unread_count":18,"children":[{"model_id":11,"model_name":"任务跟踪提醒（tag）","total_count":18,"unread_count":18,"remind_unread_count":18,"children":[{"model_id":111,"model_name":"商品库对接完成（tag）","total_count":18,"unread_count":18,"remind_unread_count":18,"last_receive_time":1563246183}]}]},{"model_id":2,"model_name":"设备（tag）","unread_count":6,"children":[{"model_id":21,"model_name":"IPC设备消息（tag）","unread_count":6,"children":[{"model_id":211,"model_name":"IPC动态侦测-画面侦测（tag）","unread_count":6,"total_count":6,"last_receive_time":1563246183},{"model_id":212,"model_name":"IPC动态侦测-声音侦测（tag）","unread_count":0,"total_count":2,"last_receive_time":1563246183},{"model_id":213,"model_name":"IPC上下线通知（tag）","unread_count":0,"total_count":0,"last_receive_time":0}]}]}]
     */

    @SerializedName("total_count")
    private int totalCount;
    @SerializedName("unread_count")
    private int unreadCount;
    @SerializedName("remind_unread_count")
    private int remindUnreadCount;
    @SerializedName("model_count_list")
    private List<ModelCountListBean> modelCountList;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public int getRemindUnreadCount() {
        return remindUnreadCount;
    }

    public void setRemindUnreadCount(int remindUnreadCount) {
        this.remindUnreadCount = remindUnreadCount;
    }

    public List<ModelCountListBean> getModelCountList() {
        return modelCountList;
    }

    public void setModelCountList(List<ModelCountListBean> modelCountList) {
        this.modelCountList = modelCountList;
    }

    public static class ModelCountListBean {
        /**
         * model_id : 1
         * model_name : 系统（tag）
         * total_count : 20
         * unread_count : 18
         * remind_unread_count : 18
         * children : [{"model_id":11,"model_name":"任务跟踪提醒（tag）","total_count":18,"unread_count":18,"remind_unread_count":18,"children":[{"model_id":111,"model_name":"商品库对接完成（tag）","total_count":18,"unread_count":18,"remind_unread_count":18,"last_receive_time":1563246183}]}]
         */

        @SerializedName("model_id")
        private int modelId;
        @SerializedName("model_name")
        private String modelName;
        @SerializedName("total_count")
        private int totalCount;
        @SerializedName("unread_count")
        private int unreadCount;
        @SerializedName("remind_unread_count")
        private int remindUnreadCount;
        @SerializedName("children")
        private List<ChildrenBeanX> children;

        public int getModelId() {
            return modelId;
        }

        public void setModelId(int modelId) {
            this.modelId = modelId;
        }

        public String getModelName() {
            return modelName;
        }

        public void setModelName(String modelName) {
            this.modelName = modelName;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public int getUnreadCount() {
            return unreadCount;
        }

        public void setUnreadCount(int unreadCount) {
            this.unreadCount = unreadCount;
        }

        public int getRemindUnreadCount() {
            return remindUnreadCount;
        }

        public void setRemindUnreadCount(int remindUnreadCount) {
            this.remindUnreadCount = remindUnreadCount;
        }

        public List<ChildrenBeanX> getChildren() {
            return children;
        }

        public void setChildren(List<ChildrenBeanX> children) {
            this.children = children;
        }

        public static class ChildrenBeanX {
            /**
             * model_id : 11
             * model_name : 任务跟踪提醒（tag）
             * total_count : 18
             * unread_count : 18
             * remind_unread_count : 18
             * children : [{"model_id":111,"model_name":"商品库对接完成（tag）","total_count":18,"unread_count":18,"remind_unread_count":18,"last_receive_time":1563246183}]
             */

            @SerializedName("model_id")
            private int modelId;
            @SerializedName("model_name")
            private String modelName;
            @SerializedName("total_count")
            private int totalCount;
            @SerializedName("unread_count")
            private int unreadCount;
            @SerializedName("remind_unread_count")
            private int remindUnreadCount;
            @SerializedName("children")
            private List<ChildrenBean> children;

            public int getModelId() {
                return modelId;
            }

            public void setModelId(int modelId) {
                this.modelId = modelId;
            }

            public String getModelName() {
                return modelName;
            }

            public void setModelName(String modelName) {
                this.modelName = modelName;
            }

            public int getTotalCount() {
                return totalCount;
            }

            public void setTotalCount(int totalCount) {
                this.totalCount = totalCount;
            }

            public int getUnreadCount() {
                return unreadCount;
            }

            public void setUnreadCount(int unreadCount) {
                this.unreadCount = unreadCount;
            }

            public int getRemindUnreadCount() {
                return remindUnreadCount;
            }

            public void setRemindUnreadCount(int remindUnreadCount) {
                this.remindUnreadCount = remindUnreadCount;
            }

            public List<ChildrenBean> getChildren() {
                return children;
            }

            public void setChildren(List<ChildrenBean> children) {
                this.children = children;
            }

            public static class ChildrenBean {
                /**
                 * model_id : 111
                 * model_name : 商品库对接完成（tag）
                 * total_count : 18
                 * unread_count : 18
                 * remind_unread_count : 18
                 * last_receive_time : 1563246183
                 */

                @SerializedName("model_id")
                private int modelId;
                @SerializedName("model_name")
                private String modelName;
                @SerializedName("total_count")
                private int totalCount;
                @SerializedName("unread_count")
                private int unreadCount;
                @SerializedName("remind_unread_count")
                private int remindUnreadCount;
                @SerializedName("last_receive_time")
                private int lastReceiveTime;

                public int getModelId() {
                    return modelId;
                }

                public void setModelId(int modelId) {
                    this.modelId = modelId;
                }

                public String getModelName() {
                    return modelName;
                }

                public void setModelName(String modelName) {
                    this.modelName = modelName;
                }

                public int getTotalCount() {
                    return totalCount;
                }

                public void setTotalCount(int totalCount) {
                    this.totalCount = totalCount;
                }

                public int getUnreadCount() {
                    return unreadCount;
                }

                public void setUnreadCount(int unreadCount) {
                    this.unreadCount = unreadCount;
                }

                public int getRemindUnreadCount() {
                    return remindUnreadCount;
                }

                public void setRemindUnreadCount(int remindUnreadCount) {
                    this.remindUnreadCount = remindUnreadCount;
                }

                public int getLastReceiveTime() {
                    return lastReceiveTime;
                }

                public void setLastReceiveTime(int lastReceiveTime) {
                    this.lastReceiveTime = lastReceiveTime;
                }
            }
        }
    }

}
