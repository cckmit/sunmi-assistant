package sunmi.common.router.model;

import java.util.List;

/**
 * Description:
 * Created by bruce on 2019/6/5.
 */
public class IpcListResp {

    private List<SsListBean> fs_list;
    private List<SsListBean> ss_list;

    public List<SsListBean> getFs_list() {
        return fs_list;
    }

    public void setFs_list(List<SsListBean> fs_list) {
        this.fs_list = fs_list;
    }

    public List<SsListBean> getSs_list() {
        return ss_list;
    }

    public void setSs_list(List<SsListBean> ss_list) {
        this.ss_list = ss_list;
    }

    public static class SsListBean {
        /**
         * id : 2224
         * sn : SS101D8BS00088
         * device_name : 测试用SS88
         * company_id : 98456
         * shop_id : 7905
         * model : SS1
         * bin_version : 1.0.0
         * check_version_time : 0
         * active_status : 1
         * cdn_address : http://test.cdn.sunmi.com/IMG/IPC/ea85fa916e1a63b5f855f37444519842ebb4d2407c16d33909e01984231fbe3d
         */

        private int id;
        private String sn;
        private String uid;
        private String device_name;
        private int company_id;
        private int shop_id;
        private String model;
        private String bin_version;
        private int check_version_time;
        private int active_status;
        private String cdn_address;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getSn() {
            return sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public String getDevice_name() {
            return device_name;
        }

        public void setDevice_name(String device_name) {
            this.device_name = device_name;
        }

        public int getCompany_id() {
            return company_id;
        }

        public void setCompany_id(int company_id) {
            this.company_id = company_id;
        }

        public int getShop_id() {
            return shop_id;
        }

        public void setShop_id(int shop_id) {
            this.shop_id = shop_id;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getBin_version() {
            return bin_version;
        }

        public void setBin_version(String bin_version) {
            this.bin_version = bin_version;
        }

        public int getCheck_version_time() {
            return check_version_time;
        }

        public void setCheck_version_time(int check_version_time) {
            this.check_version_time = check_version_time;
        }

        public int getActive_status() {
            return active_status;
        }

        public void setActive_status(int active_status) {
            this.active_status = active_status;
        }

        public String getCdn_address() {
            return cdn_address;
        }

        public void setCdn_address(String cdn_address) {
            this.cdn_address = cdn_address;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }
    }

}
