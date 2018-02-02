package com.jiajia0.wifitransferapk;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import timber.log.Timber;

/**
 * Created by Leafage on 2018/2/2 19:20.
 */

public class WifiUtils {
    // 得到Wifi的IP地址
    public static String getWifiIp(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            Timber.d("WifiAddress:" + intToIp(wifiInfo.getIpAddress()));
        }
        return null;
    }

    // 将整型转换为IP形式
    private static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
    }

}
