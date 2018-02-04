package com.jiajia0.wifitransferapk;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hwangjr.rxbus.RxBus;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    TextView mTxtHint;
    @BindView(R.id.shared_wifi_address)
    TextView mTxtWifiAddress;
    @BindView(R.id.shared_wifi_settings)
    Button mSBtnettings;
    @BindView(R.id.shared_wifi_button_split_line)
    View mBtnSplitLIne;

    WifiConnectChangeReceiver mWifiConnectChangeReceiver = new WifiConnectChangeReceiver();
    private Context mContext;
    private DisplayMetrics mMetrics;// 获取屏幕信息
    private Dialog mDialog;

    public PopupMenuDialog(Context context) {
        mContext = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(mMetrics);
        RxBus.get().register(this);
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

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        Timber.d("dialog dismiss!");
        if (mUnbinder != null) {
            mUnbinder.unbind();// 解除绑定
        }
    }
}

