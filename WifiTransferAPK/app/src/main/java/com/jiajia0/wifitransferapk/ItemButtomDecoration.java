package com.jiajia0.wifitransferapk;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Leafage on 2018/2/9 13:32.
 * DESCRIPTION : ItemDecoration重写，添加padding效果
 */

public class ItemButtomDecoration extends RecyclerView.ItemDecoration {

    private Context mContext;
    private int mHeight;

    public ItemButtomDecoration(Context context) {
        mContext = context;
    }

    public ItemButtomDecoration(Context context, int height) {
        mContext = context;
        mHeight = height;
    }

    /**
     * @param outRect 边界
     * @param view    recyclerView ItemView
     * @param parent  recyclerView
     * @param state   recycler 内部数据管理
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //设定底部边距为10px
        if ( parent.getChildLayoutPosition(view) == state.getItemCount() - 1
                || parent.getChildLayoutPosition(view) == -1 ) {
            outRect.set(0, 0, 0, 0);
        } else {
            outRect.set(0, 0, 0, ( int ) (getScreenScale(mContext) * (mHeight == 0 ? 10 : mHeight)));
        }
    }


    /**
     * 获取屏幕的缩放级别
     *
     * @param context
     * @return
     */
    public static float getScreenScale(Context context) {
        TextView tv = new TextView(context);
        tv.setTextSize(1);
        return tv.getTextSize();
    }

}
