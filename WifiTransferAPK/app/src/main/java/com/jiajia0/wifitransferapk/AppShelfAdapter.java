package com.jiajia0.wifitransferapk;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Leafage on 2018/1/31 15:34.
 */

public class AppShelfAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<InfoModel> mApps;
    private APKManager mAPKManager;

    public AppShelfAdapter(List<InfoModel> apps, APKManager apkManager) {
        mApps = apps;
        mAPKManager = apkManager;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Timber.d("leafage-" + "onCreateViewHolder-" + "viewType:" + viewType);
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        // 如果此时APP为空，则返回空的视图
        if (viewType == 1) {
            View view = inflater.inflate(R.layout.empty_view, parent, false);
            return new EmptyViewHolder(view);
        } else { // 如果列表为不为空，咋添加APk视图
            View view = inflater.inflate(R.layout.layout_apk_item, parent, false);
            return new ApkListViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Timber.d("leafage-" + "onBindViewHolder");
        if (holder instanceof ApkListViewHolder) { // 非空列表加载内容
            ApkListViewHolder apkHolder = (ApkListViewHolder) holder;
            final InfoModel infoModel = mApps.get(position);
            apkHolder.mApkName.setText(infoModel.getName() + "(v" + infoModel.getVersion() + ")");
            apkHolder.mApkSize.setText(infoModel.getSize());
            apkHolder.mApkPath.setText(infoModel.getPath());
            apkHolder.mApkIcon.setImageDrawable(infoModel.getIcon());
            apkHolder.mApkInstall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAPKManager.installAPK(new File(infoModel.getPath()));
                }
            });
            apkHolder.mApkUninstall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAPKManager.uninstallAPK(infoModel.getPackageName());
                }
            });

            if ( infoModel.isInstalled() ) {
                apkHolder.mApkUninstall.setVisibility(View.VISIBLE);
                apkHolder.mApkInstall.setVisibility(View.GONE);
            } else {
                apkHolder.mApkInstall.setVisibility(View.VISIBLE);
                apkHolder.mApkUninstall.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        // 如果为空的话，需要返回1，否则不能加载视图
        return mApps.size() > 0 ? mApps.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if ( mApps.size() == 0 ) {
            return 1;
        }
        return super.getItemViewType(position);
    }
}

// 空视图
class EmptyViewHolder extends RecyclerView.ViewHolder {

    public EmptyViewHolder(View itemView) {
        super(itemView);
    }
}

// 包含APK的视图
class ApkListViewHolder extends RecyclerView.ViewHolder {
    TextView mApkName;
    TextView mApkSize;
    TextView mApkInstall;
    TextView mApkUninstall;
    TextView mApkPath;
    ImageView mApkIcon;
    public ApkListViewHolder(View itemView) {
        super(itemView);
        mApkName = itemView.findViewById(R.id.apk_name);
        mApkSize = itemView.findViewById(R.id.apk_size);
        mApkInstall = itemView.findViewById(R.id.apk_install);
        mApkUninstall = itemView.findViewById(R.id.apk_uninstall);
        mApkPath = itemView.findViewById(R.id.apk_path);
        mApkIcon = itemView.findViewById(R.id.apk_icon);
    }
}
