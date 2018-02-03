package com.jiajia0.wifitransferapk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import timber.log.Timber;

/**
 * Created by Leafage on 2018/2/3 16:29.
 */

public class WifiConnectChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.d("leafage" + "Wifireceive");
        // Wifi链接的状态广播接收
        if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            // 获取Wifi状态
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (networkInfo != null) {
                Timber.d("leafage" + networkInfo.getState());
            }
        }
    }
}
