package com.tutk.IOTC;

//base on struct st_SInfo of IOTCAPIs.h
public class St_SInfo {
    public byte Mode;
    public byte CorD;
    public byte[] UID = new byte[21];
    public byte[] RemoteIP = new byte[17];//RemoteIP[17]
    //char reserve1[2];
    public int RemotePort;
    public int TX_count;
    public int RX_count;
    //----------------------
    public int VID;
    public int PID;
    public int GID;
    public int IOTCVersion;
    public byte NatType;
    public byte isSecure;
}
