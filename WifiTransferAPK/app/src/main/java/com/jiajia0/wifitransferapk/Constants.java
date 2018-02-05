package com.jiajia0.wifitransferapk;

/**
 * Created by Leafage on 2018/2/5 11:08.
 * DESCRIPTION : 常量
 */

public class Constants {
    public static final int HTTP_PORT = 12345;// 端口号

    // RxBus传递用的参数
    public static final class RxBusEventType {
        public static final String POPUP_MENU_DIALOG_SHOW_DISMISS = "POPUP MENU DIALOG SHOW DISMISS";
        public static final String WIFI_CONNECT_CHANGE_EVENT = "WIFI CONNECT CHANGE EVENT";
        public static final String LOAD_BOOK_LIST = "LOAD APK LIST";
        public static final int MSG_DIALOG_DISMISS = 0;
    }
}
