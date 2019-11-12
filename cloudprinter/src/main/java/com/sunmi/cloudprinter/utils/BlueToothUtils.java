package com.sunmi.cloudprinter.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import sunmi.common.utils.ToastUtils;

/**
 * Description:
 * Created by bruce on 2019/11/11.
 */

public class BlueToothUtils {
    private static final String TAG = "BlueToothUtils";
    // UUID.randomUUID()随机获取UUID
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    // 连接对象的名称
    private final String NAME = "Sunmi";

    private Context mContext;
    public static BlueToothUtils sInstance;

    private BluetoothAdapter bluetoothAdapter;

    // 这里本身即是服务端也是客户端，需要如下类
    private BluetoothSocket mSocket;
    private BluetoothDevice mOldDevice;
    private BluetoothDevice mCurDevice;

    // 输出流_客户端需要往服务端输出
    private OutputStream os;

    //线程类的实例
    private AcceptThread acceptThread;

    public static synchronized BlueToothUtils getInstance() {
        if (sInstance == null) {
            sInstance = new BlueToothUtils();
        }
        return sInstance;
    }

    public BlueToothUtils() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        acceptThread = new AcceptThread();
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public AcceptThread getAcceptThread() {
        return acceptThread;
    }

    public BluetoothDevice getCurDevice() {
        return mCurDevice;
    }

    /**
     * 判断是否打开蓝牙
     */
    public boolean isEnabled() {
        return bluetoothAdapter.isEnabled();
    }

    /**
     * 搜索设备
     */
    public void searchDevices() {
        if (bluetoothAdapter.isDiscovering()) { // 判断是否在搜索,如果在搜索，就取消搜索
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery(); // 开始搜索
        Log.e(TAG, "正在搜索...");
    }

    /**
     * 获取已经配对的设备
     */
    public List<BluetoothDevice> getBondedDevices() {
        List<BluetoothDevice> devices = new ArrayList<>();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        // 判断是否有配对过的设备
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName() != null && device.getName().startsWith("CloudPrint")) {
                    devices.add(device);
                    Log.e(TAG, "BondedDevice:" + device.getName());
                }
            }
        }
        return devices;
    }

    /**
     * 与设备配对
     */
    public void createBond(BluetoothDevice device) {
        try {
            Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
            createBondMethod.invoke(device);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 与设备解除配对
     */
    public void removeBond(BluetoothDevice device) {
        try {
            Method removeBondMethod = device.getClass().getMethod("removeBond");
            removeBondMethod.invoke(device);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置PIN码
     */
    public boolean setPin(BluetoothDevice device, String str) {
        try {
            Method removeBondMethod = device.getClass().getDeclaredMethod("setPin",
                    new Class[]{byte[].class});
            Boolean returnValue = (Boolean) removeBondMethod.invoke(device,
                    new Object[]{str.getBytes()});
            Log.e("returnValue", "" + returnValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 取消用户输入
     */
    public boolean cancelPairingUserInput(BluetoothDevice device) {
        Boolean returnValue = false;
        try {
            Method createBondMethod = device.getClass().getMethod("cancelPairingUserInput");
            returnValue = (Boolean) createBondMethod.invoke(device);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnValue.booleanValue();
    }

    /**
     * 取消配对
     */
    public boolean cancelBondProcess(BluetoothDevice device) {
        Boolean returnValue = null;
        try {
            Method createBondMethod = device.getClass().getMethod("cancelBondProcess");
            returnValue = (Boolean) createBondMethod.invoke(device);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnValue.booleanValue();
    }

    /**
     * 配对
     *
     * @param strAddr
     * @param strPsw
     * @return
     */
    public boolean pair(String strAddr, String strPsw) {
        boolean result = false;
        bluetoothAdapter.cancelDiscovery();

        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }

        if (!BluetoothAdapter.checkBluetoothAddress(strAddr)) { // 检查蓝牙地址是否有效
            Log.d(TAG, "devAdd un effient!");
        }

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(strAddr);
        try {
            if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                Log.d(TAG, "NOT BOND_BONDED");
                setPin(device, strPsw); // 手机和蓝牙采集器配对
                createBond(device);
                result = true;
            } else {
                Log.d(TAG, "HAS BOND_BONDED");
                createBond(device);
                setPin(device, strPsw); // 手机和蓝牙采集器配对
                createBond(device);
                result = true;
            }
        } catch (Exception e) {
            Log.d(TAG, "setPiN failed!");
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 获取device.getClass()这个类中的所有Method
     *
     * @param clsShow
     */
    public void printAllInform(Class clsShow) {
        try {
            // 取得所有方法
            Method[] hideMethod = clsShow.getMethods();
            int i = 0;
            for (; i < hideMethod.length; i++) {
                Log.e("method name", hideMethod[i].getName() + ";and the i is:" + i);
            }
            // 取得所有常量
            Field[] allFields = clsShow.getFields();
            for (i = 0; i < allFields.length; i++) {
                Log.e("Field name", allFields[i].getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开蓝牙
     */
    public void openBlueTooth() {
        if (!bluetoothAdapter.isEnabled()) {
            // 不做提示，强行打开
            bluetoothAdapter.enable();
            showToast("打开蓝牙");
        } else {
            showToast("蓝牙已打开");
        }
    }

    /**
     * 关闭蓝牙
     */
    public void closeBlueTooth() {
        bluetoothAdapter.disable();
        showToast("关闭蓝牙");
    }

    /**
     * 弹出Toast窗口
     */
    private void showToast(String message) {
        if (mContext != null) {
            ToastUtils.toastForShort(mContext, message);
        }
    }

    /**
     * 主动连接蓝牙
     */
    public void connectDevice(BluetoothDevice device) {
        // 判断是否在搜索,如果在搜索，就取消搜索
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        // 获得远程设备
        new Thread(() -> {
            try {
                mSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                mSocket.connect();//需要在子线程调用connect
                os = mSocket.getOutputStream();// 获得输出流
                Log.e(TAG, "socket connected");
                mCurDevice = bluetoothAdapter.getRemoteDevice(device.getAddress());
                mOldDevice = mCurDevice;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 传输数据
     */
    public void write(String message) {
        try {
            if (os != null) {
                os.write(message.getBytes("GBK"));
            }
            Log.e(TAG, "write:" + message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 传输数据
     */
    public void write(byte[] bytes) {
        try {
            if (os != null) {
                os.write(bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // 线程服务类
    public class AcceptThread extends Thread {
        private BluetoothServerSocket serverSocket;
        private BluetoothSocket socket;
        // 输入、输出流
        private OutputStream os;
        private InputStream is;

        AcceptThread() {
            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            // 截获客户端的蓝牙消息
            try {
                socket = serverSocket.accept(); // 如果阻塞了，就会一直停留在这里
                is = socket.getInputStream();
                os = socket.getOutputStream();
                while (true) {
                    synchronized (this) {
                        byte[] tt = new byte[is.available()];
                        if (tt.length > 0) {
                            is.read(tt, 0, tt.length);
                            Message msg = new Message();
                            msg.obj = new String(tt, "GBK");
                            Log.e(TAG, "客户端:" + msg.obj);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}