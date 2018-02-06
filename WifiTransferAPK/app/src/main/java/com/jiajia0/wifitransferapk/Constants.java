package com.jiajia0.wifitransferapk;

import android.os.Environment;

import java.io.File;

/**
 * Created by Leafage on 2018/2/5 11:08.
 * DESCRIPTION : 常量
 */

public class Constants {
    public static final int HTTP_PORT = 12345;// 端口号
    public static final String DIR_IN_SDCARD = "WifiTransferAPK";
    public static final File DIR = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.DIR_IN_SDCARD);

    // RxBus传递用的参数
    public static final class RxBusEventType {
        public static final String POPUP_MENU_DIALOG_SHOW_DISMISS = "POPUP MENU DIALOG SHOW DISMISS";
        public static final String WIFI_CONNECT_CHANGE_EVENT = "WIFI CONNECT CHANGE EVENT";
        public static final String LOAD_APK_LIST = "LOAD APK LIST";
        public static final int MSG_DIALOG_DISMISS = 0;
    }
}
