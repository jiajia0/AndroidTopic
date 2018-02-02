package com.jiajia0.wifitransferapk;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import timber.log.Timber;

/**
 * Created by Leafage on 2018/2/2 19:20.
 */

public class WifiUtils {
    // 得到Wifi的IP地址
    public static String getWifiIp(Context context) {
        // 获取Wifi管理类
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // 获取当前连接Wifi的信息
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        // 转换Wifi为IP后返回
        if (wifiInfo != null) {
            Timber.d("WifiAddress:" + intToIp(wifiInfo.getIpAddress()));
            return intToIp(wifiInfo.getIpAddress());
        }
        return null;
    }

    // 将整型转换为IP形式
    private static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
    }

    // 获取当前Wifi的链接状态
    public static NetworkInfo.State getWifiConnectState(Context context) {
        // 获取管理网络与连接的类
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 获取WIfi的链接状态信息
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        // 如果Wifi状态不为空
        if (networkInfo != null) {
            // 获取去当前连接的网络状态
            return networkInfo.getState();
        }
        // 返回断开链接的状态
        return NetworkInfo.State.DISCONNECTED;
    }

}
