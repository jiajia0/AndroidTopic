package com.jiajia0.wifitransferapk;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * Created by Leafage on 2018/2/4 17:36.
 * DESCRIPTION : Wifi开启的动画
 */

public class PopupMenuDialog implements DialogInterface.OnDismissListener {
    Unbinder mUnbinder;
    @BindView(R.id.popup_menu_title)
    TextView mTxtTitle;
    @BindView(R.id.popup_menu_subtitle)
    TextView mTxtSubTitle;
    @BindView(R.id.shared_wifi_state)
    ImageView mImgWifiState;
    @BindView(R.id.shared_wifi_state_hint)
    TextView mTxtStateHint;
    @BindView(R.id.shared_wifi_address)
    TextView mTxtWifiAddress;
    @BindView(R.id.shared_wifi_settings)
    Button mBtnSettings;
    @BindView(R.id.shared_wifi_button_split_line)
    View mBtnSplitLIne;

    WifiConnectChangeReceiver mWifiConnectChangeReceiver = new WifiConnectChangeReceiver();
    private Context mContext;
    private DisplayMetrics mMetrics = new DisplayMetrics();// 获取屏幕信息
    private Dialog mDialog;

    public PopupMenuDialog(Context context) {
        mContext = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(mMetrics);
        RxBus.get().register(this);// 注册
    }

    public PopupMenuDialog builder() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_popup_menu_dialog, null);
        // 设置视图的宽度
        view.setMinimumWidth(mMetrics.widthPixels);
        mDialog = new Dialog(mContext, R.style.PopupMenuDialogStyle);
        mDialog.setContentView(view);// 加载视图
        mUnbinder = ButterKnife.bind(this, mDialog);
        mDialog.setOnDismissListener(this);// 添加对话框取消执行的任务
        // 设置对话框的窗口
        Window dialogWindow = mDialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
        layoutParams.x = 0;
        layoutParams.y = 0;
        dialogWindow.setAttributes(layoutParams);
        return this;
    }

    // 设置按下返回键对话框是否可退出
    public PopupMenuDialog setCancelable(boolean cancel) {
        mDialog.setCancelable(cancel);
        return this;
    }

    // 对话框外点击，对话框是否消失
    public PopupMenuDialog setCanceledOnTouchOutside(boolean cancel) {
        mDialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    // 展示对话框
    public void show() {
        chechWifiState(WifiUtils.getWifiConnectState(mContext));// 检查WiFi，并显示对应的信息
        mDialog.show();
        registerWifiConnectChangedReceiver();// 注册广播监听
    }

    @OnClick({R.id.shared_wifi_cancel, R.id.shared_wifi_settings})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.shared_wifi_cancel:
                mDialog.dismiss();
                break;
            case R.id.shared_wifi_settings:
                mContext.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                break;
        }
    }

    @Subscribe(tags = {@Tag(Constants.RxBusEventType.WIFI_CONNECT_CHANGE_EVENT)})
    public void onWifiConnectStateChanged(NetworkInfo.State wifiConnectState) {
        chechWifiState(wifiConnectState);
    }

    // 检查Wifi状态
    private void chechWifiState(NetworkInfo.State wifiConnectState) {
        if (wifiConnectState == NetworkInfo.State.CONNECTED || wifiConnectState == NetworkInfo.State.CONNECTING) {
            // 如果已经连接
            if (wifiConnectState == NetworkInfo.State.CONNECTED) {
                String ip = WifiUtils.getWifiIp(mContext);
                if (!TextUtils.isEmpty(ip)) {
                    onWifiConnected(ip);
                    return;
                }
            }
            onWifiConnecting();
            return;
        }
        onWifiDisconnected();
    }

    // 设置Wifi已连接的信息
    void onWifiConnected(String ipAddr) {
        mTxtTitle.setText(R.string.wlan_enabled);
        mTxtTitle.setTextColor(mContext.getResources().getColor(R.color.colorWifiConnected));
        mTxtSubTitle.setVisibility(View.GONE);
        mImgWifiState.setImageResource(R.drawable.shared_wifi_enable);
        mTxtStateHint.setText(R.string.input_address_in_pc_browser);
        mTxtWifiAddress.setVisibility(View.VISIBLE);
        mTxtWifiAddress.setText(String.format(mContext.getString(R.string.http_address), ipAddr, Constants.HTTP_PORT));
        mBtnSplitLIne.setVisibility(View.GONE);
        mBtnSettings.setVisibility(View.GONE);
    }

    // 设置Wifi正在连接的信息
    void onWifiConnecting() {
        mTxtTitle.setText(R.string.wlan_enabled);
        mTxtTitle.setTextColor(mContext.getResources().getColor(R.color.colorWifiConnected));
        mTxtSubTitle.setVisibility(View.GONE);
        mImgWifiState.setImageResource(R.drawable.shared_wifi_enable);
        mTxtStateHint.setText(R.string.retrofit_wlan_address);
        mTxtWifiAddress.setVisibility(View.GONE);
        mBtnSplitLIne.setVisibility(View.GONE);
        mBtnSettings.setVisibility(View.GONE);
    }

    // 设置Wifi没有连接的信息
    void onWifiDisconnected() {
        mTxtTitle.setText(R.string.wlan_disabled);
        mTxtTitle.setTextColor(mContext.getResources().getColor(android.R.color.black));
        mTxtSubTitle.setVisibility(View.VISIBLE);
        mImgWifiState.setImageResource(R.drawable.shared_wifi_shut_down);
        mTxtStateHint.setText(R.string.fail_to_start_http_service);
        mTxtWifiAddress.setVisibility(View.GONE);
        mBtnSplitLIne.setVisibility(View.VISIBLE);
        mBtnSettings.setVisibility(View.VISIBLE);
    }

    void registerWifiConnectChangedReceiver() {
        IntentFilter intentFilter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mContext.registerReceiver(mWifiConnectChangeReceiver, intentFilter);
    }

    void unregisterWifiConnectChangedReceiver() {
        mContext.unregisterReceiver(mWifiConnectChangeReceiver);
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        Timber.d("dialog dismiss!");
        if (mUnbinder != null) {
            mUnbinder.unbind();// 解除绑定
            RxBus.get().post(Constants.RxBusEventType.POPUP_MENU_DIALOG_SHOW_DISMISS, Constants.RxBusEventType.MSG_DIALOG_DISMISS);
            unregisterWifiConnectChangedReceiver();// 取消广播
            RxBus.get().unregister(this);
        }
    }
}

