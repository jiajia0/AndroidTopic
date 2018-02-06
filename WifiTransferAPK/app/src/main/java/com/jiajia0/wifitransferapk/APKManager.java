package com.jiajia0.wifitransferapk;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by Leafage on 2018/2/6 20:54.
 * DESCRIPTION : APK的管理类
 */

public class APKManager {

    private Context mContext;
    private PackageManager mPackageManager;

    public APKManager(Context context) {
        mContext = context;
        mPackageManager = mContext.getPackageManager();
    }

    /**
     * @param file
     * @return
     * 得到对应文件的文件信息
     */
    public InfoModel getAPKInfo(File file) {
        InfoModel infoModel = new InfoModel();
        String archiveFilePath = file.getAbsolutePath();
        PackageInfo packageInfo = mPackageManager.getPackageArchiveInfo(archiveFilePath, 0);
        if (packageInfo != null) {
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            applicationInfo.sourceDir = archiveFilePath;
            applicationInfo.publicSourceDir = archiveFilePath;
            String packageName = applicationInfo.packageName;// 得到安装包名称
            String version = packageInfo.versionName;// 得到版本信息
            Drawable icon = mPackageManager.getApplicationIcon(applicationInfo);
            String appName = mPackageManager.getApplicationLabel(applicationInfo).toString();
            if (TextUtils.isEmpty(appName)) {
                appName = getApplicationName(packageName);// 再次尝试得到名称
            }
            if (icon == null) {

            }
        }
        return infoModel;
    }

    /**
     * @param packageName
     * 根据包名尝试得到app的名称
     * @return
     */
    public String getApplicationName(String packageName) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = mContext.getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            applicationInfo = null;
        }
        if (packageManager != null && applicationInfo != null) {
            String applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
            return applicationName;
        }
        return packageName;
    }

}
