package com.sunmi.cloudprinter.rpc;

import com.sunmi.cloudprinter.config.PrinterConfig;

/**
 * Description:
 * Created by bruce on 2019/5/31.
 */
public interface MerchantInterface {
    public String BIND_PRINTER = PrinterConfig.IOT_CLOUD_URL + "Machine.bindMachine";//绑定打印机
    public String GET_PRINTER_LIST = PrinterConfig.IOT_CLOUD_URL + "Machine.getMerchantInfo";//获取打印机列表
    public String GET_PRINTER_STATUS = PrinterConfig.IOT_CLOUD_URL + "Machine.getMachineIsOnLine";//获取打印机状态
    public String UNBIND_PRINTER = PrinterConfig.IOT_CLOUD_URL + "Machine.untiedMachine";//解绑打印机

//    String path = "web/merchant/1.0/";
//
//    /**
//     * ipc绑定
//     */
//    @POST(path + "?service=Merchant.bindMachine")
//    Call<BaseResponse<Object>> bind(@Body BaseRequest request);

}
