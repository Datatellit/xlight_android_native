package io.particle.android.sdk.devicesetup.model;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by 75932 on 2017/11/3.
 */

public class DeviceInfo {
    /*
    获取手机IMEI
     */
    public static String getIMEI(Context context) {
        try {
            TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            return TelephonyMgr.getDeviceId();
        } catch (Exception ex) {
            return "";
        }
    }

    /*
    获取网卡地址
     */
    public static String getWlanMAC(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wm.getConnectionInfo().getMacAddress();
    }

    /*
    获取手机蓝牙地址
     */
    public static String getBluetoothMAC() {
        try {
            BluetoothAdapter mBlueth = BluetoothAdapter.getDefaultAdapter();
            return mBlueth.getAddress();
        } catch (Exception e) {
            return "00:00:00:00:00:00";
        }
    }

    public static String getSign(Context context) {
        String m_szLongID = "";
        String imei = getIMEI(context);
        String wlan = getWlanMAC(context);
        String bluetooth = getBluetoothMAC();
        if (imei == null) imei = "XLight";
        if (wlan == null) imei = "XLight";
        if (bluetooth == null) imei = "XLight";
        m_szLongID = imei + wlan + bluetooth;
        // compute md5
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
        // get md5 bytes
        byte p_md5Data[] = m.digest();
        // create a hex string
        String m_szUniqueID = new String();
        for (int i = 0; i < p_md5Data.length; i++) {
            int b = (0xFF & p_md5Data[i]);
            // if it is a single digit, make sure it have 0 in front (proper padding)
            if (b <= 0xF)
                m_szUniqueID += "0";
            // add number to string
            m_szUniqueID += Integer.toHexString(b);
        }   // hex string to uppercase
        m_szUniqueID = m_szUniqueID.toUpperCase();
        return m_szUniqueID;
    }

    public static String getSystemInfo() {
        try {
            JSONObject jb = new JSONObject();
            jb.put("SDK", Build.VERSION.SDK);
            jb.put("SDK_INT", Build.VERSION.SDK_INT);
            jb.put("RELEASE", Build.VERSION.RELEASE);
            jb.put("BASE_OS", Build.VERSION.BASE_OS);
            jb.put("CODENAME", Build.VERSION.CODENAME);
            jb.put("INCREMENTAL", Build.VERSION.INCREMENTAL);
            return jb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public static String getHardwareInfo() {
        try {
            JSONObject jb = new JSONObject();
            jb.put("BRAND", Build.BRAND);
            jb.put("MODEL", Build.MODEL);
            jb.put("ID", Build.ID);
            jb.put("DISPLAY", Build.DISPLAY);
            jb.put("PRODUCT", Build.PRODUCT);
            jb.put("MANUFACTURER", Build.MANUFACTURER);
            jb.put("DEVICE", Build.DEVICE);
            jb.put("HARDWARE", Build.HARDWARE);
            jb.put("SERIAL", Build.SERIAL);
            jb.put("USER", Build.USER);
            jb.put("CPU", Build.CPU_ABI);
            jb.put("BOARD", Build.BOARD);
            return jb.toString();
        } catch (Exception e) {
            return "";
        }
    }
}
