package com.sunmi.assistant.mine.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author yinhui
 * @date 2019-08-09
 */
public class RegionProvince {

    /**
     * province : 2
     * name : 黑龙江省
     * children : [{"city":"38","name":"大兴安岭地区","children":[{"county":"400","name":"塔河县"},{"county":"401","name":"漠河县"},{"county":"402","name":"呼玛县"}]},{"city":"39","name":"黑河市","children":[{"county":"403","name":"爱辉区"},{"county":"404","name":"嫩江县"},{"county":"405","name":"逊克县"},{"county":"406","name":"孙吴县"},{"county":"409","name":"五大连池市"},{"county":"411","name":"北安市"}]},{"city":"40","name":"伊春市","children":[{"county":"407","name":"嘉荫县"},{"county":"419","name":"铁力市"},{"county":"8032","name":"乌伊岭区"},{"county":"8033","name":"汤旺河区"},{"county":"8034","name":"新青区"},{"county":"8035","name":"红星区"},{"county":"8036","name":"美溪区"},{"county":"8037","name":"五营区"},{"county":"8038","name":"友好区"},{"county":"8040","name":"金山屯区"},{"county":"8041","name":"南岔区"},{"county":"8042","name":"乌马河区"},{"county":"8043","name":"翠峦区"},{"county":"8052","name":"上甘岭区"},{"county":"8059","name":"带岭区"},{"county":"8075","name":"西林区"},{"county":"8077","name":"伊春区"}]},{"city":"41","name":"齐齐哈尔市","children":[{"county":"410","name":"讷河市"},{"county":"412","name":"克东县"},{"county":"413","name":"克山县"},{"county":"414","name":"依安县"},{"county":"415","name":"富裕县"},{"county":"423","name":"拜泉县"},{"county":"432","name":"龙江县"},{"county":"457","name":"甘南县"},{"county":"462","name":"碾子山区"},{"county":"1024","name":"泰来县"},{"county":"8053","name":"昂昂溪区"},{"county":"8054","name":"铁锋区"},{"county":"8055","name":"龙沙区"},{"county":"8061","name":"梅里斯达斡尔族区"},{"county":"8062","name":"富拉尔基区"},{"county":"8071","name":"建华区"}]},{"city":"42","name":"佳木斯市","children":[{"county":"416","name":"同江市"},{"county":"426","name":"富锦市"},{"county":"428","name":"桦川县"},{"county":"429","name":"汤原县"},{"county":"430","name":"市辖区"},{"county":"437","name":"桦南县"},{"county":"454","name":"抚远县"}]},{"city":"43","name":"鹤岗市","children":[{"county":"417","name":"萝北县"},{"county":"427","name":"绥滨县"},{"county":"8039","name":"东山区"},{"county":"8065","name":"兴山区"},{"county":"8066","name":"向阳区"},{"county":"8067","name":"工农区"},{"county":"8078","name":"南山区"},{"county":"8079","name":"兴安区"}]},{"city":"44","name":"绥化市","children":[{"county":"420","name":"庆安县"},{"county":"421","name":"绥棱县"},{"county":"422","name":"海伦市"},{"county":"424","name":"明水县"},{"county":"431","name":"北林区"},{"county":"443","name":"望奎县"},{"county":"444","name":"兰西县"},{"county":"445","name":"青冈县"},{"county":"446","name":"安达市"},{"county":"1020","name":"肇东市"}]},{"city":"45","name":"双鸭山市","children":[{"county":"425","name":"饶河县"},{"county":"434","name":"宝清县"},{"county":"455","name":"集贤县"},{"county":"458","name":"友谊县"},{"county":"8063","name":"四方台区"},{"county":"8064","name":"尖山区"},{"county":"8069","name":"岭东区"},{"county":"8073","name":"宝山区"}]},{"city":"46","name":"鸡西市","children":[{"county":"433","name":"虎林市"},{"county":"447","name":"密山市"},{"county":"448","name":"鸡东县"},{"county":"8047","name":"滴道区"},{"county":"8057","name":"城子河区"},{"county":"8058","name":"鸡冠区"},{"county":"8068","name":"恒山区"},{"county":"8070","name":"麻山区"},{"county":"8074","name":"梨树区"}]},{"city":"47","name":"七台河市","children":[{"county":"436","name":"勃利县"},{"county":"8044","name":"茄子河区"},{"county":"8072","name":"桃山区"},{"county":"8076","name":"新兴区"}]},{"city":"48","name":"哈尔滨市","children":[{"county":"438","name":"依兰县"},{"county":"439","name":"通河县"},{"county":"440","name":"木兰县"},{"county":"441","name":"巴彦县"},{"county":"442","name":"市辖区"},{"county":"450","name":"方正县"},{"county":"451","name":"宾县"},{"county":"452","name":"延寿县"},{"county":"1026","name":"双城市"},{"county":"1031","name":"尚志市"},{"county":"1032","name":"五常市"}]},{"city":"49","name":"牡丹江市","children":[{"county":"460","name":"林口县"},{"county":"463","name":"绥芬河市"},{"county":"1028","name":"穆棱市"},{"county":"1029","name":"东宁县"},{"county":"1030","name":"海林市"},{"county":"1033","name":"宁安市"},{"county":"8048","name":"阳明区"},{"county":"8049","name":"东安区"},{"county":"8050","name":"西安区"},{"county":"8051","name":"爱民区"}]},{"city":"50","name":"大庆市","children":[{"county":"459","name":"林甸县"},{"county":"1021","name":"肇州县"},{"county":"1023","name":"杜尔伯特蒙古族自治县"},{"county":"1027","name":"肇源县"},{"county":"8045","name":"红岗区"},{"county":"8046","name":"让胡路区"},{"county":"8056","name":"萨尔图区"},{"county":"8060","name":"龙凤区"},{"county":"8341","name":"大同区"}]}]
     */

    @SerializedName("province")
    private int province;
    @SerializedName("name")
    private String name;
    @SerializedName("children")
    private List<City> children;

    public int getProvince() {
        return province;
    }

    public String getName() {
        return name;
    }

    public List<City> getChildren() {
        return children;
    }

    public static class City {
        /**
         * city : 38
         * name : 大兴安岭地区
         * children : [{"county":"400","name":"塔河县"},{"county":"401","name":"漠河县"},{"county":"402","name":"呼玛县"}]
         */

        @SerializedName("city")
        private int city;
        @SerializedName("name")
        private String name;
        @SerializedName("children")
        private List<Area> children;

        public int getCity() {
            return city;
        }

        public String getName() {
            return name;
        }

        public List<Area> getChildren() {
            return children;
        }

    }

    public static class Area {
        /**
         * county : 400
         * name : 塔河县
         */

        @SerializedName("county")
        private int county;
        @SerializedName("name")
        private String name;

        public int getCounty() {
            return county;
        }

        public String getName() {
            return name;
        }

    }
}
