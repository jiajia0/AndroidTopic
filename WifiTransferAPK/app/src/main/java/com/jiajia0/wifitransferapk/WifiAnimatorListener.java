package com.jiajia0.wifitransferapk;

import android.animation.Animator;
import android.content.Context;

/**
 * Created by Leafage on 2018/2/4 13:09.
 * DESCRIPTION : Wifi开启的动画监听器。
 */

public class WifiAnimatorListener implements Animator.AnimatorListener {

    private Context mContext;

    public WifiAnimatorListener(Context context) {
        mContext = context;
    }

    @Override
    public void onAnimationStart(Animator animator) {
        WebService.start(mContext);
        new PopupMenuDialog(mContext).builder().setCancelable(false)
                .setCanceledOnTouchOutside(false).show();
    }

    @Override
    public void onAnimationEnd(Animator animator) {

    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }
}
