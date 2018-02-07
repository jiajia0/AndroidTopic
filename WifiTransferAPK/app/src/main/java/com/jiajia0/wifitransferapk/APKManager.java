package com.jiajia0.wifitransferapk;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.hwangjr.rxbus.RxBus;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

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
                icon = getIconFromPackageName(packageName);
            }
            infoModel.setName(appName);
            infoModel.setPackageName(packageName);
            infoModel.setPath(archiveFilePath);
            infoModel.setSize(getFileSize(file.length()));
            infoModel.setVersion(version);
            infoModel.setIcon(icon);
            infoModel.setInstalled(isAvilible(mContext, packageName));
        }
        return infoModel;
    }

    /**
     * 删除所有apk文件
     */
    public void deleteAll() {
        File dir = Constants.DIR;
        if ( dir.exists() && dir.isDirectory() ) {
            File[] fileNames = dir.listFiles();
            if ( fileNames != null ) {
                for ( File fileName : fileNames ) {
                    fileName.delete();
                }
            }
        }
        RxBus.get().post(Constants.RxBusEventType.LOAD_APK_LIST, 0);
    }

    /**
     * 判断相对应的APP是否存在
     *
     * @param context
     * @param packageName(包名)(若想判断QQ，则改为com.tencent.mobileqq，若想判断微信，则改为com.tencent.mm)
     * @return
     */
    public boolean isAvilible(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();

        //获取手机系统的所有APP包名，然后进行一一比较
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for ( int i = 0; i < pinfo.size(); i++ ) {
            if ( (pinfo.get(i)).packageName
                    .equalsIgnoreCase(packageName) )
                return true;
        }
        return false;
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

    /**
     * @param length
     * 转化为容量大小
     * @return
     */
    private String getFileSize(long length) {
        DecimalFormat df = new DecimalFormat("######0.00");
        double d1 = 3.23456;
        double d2 = 0.0;
        double d3 = 2.0;
        df.format(d1);
        df.format(d2);
        df.format(d3);
        long l = length / 1000;//KB
        if ( l < 1024 ) {
            return df.format(l) + "KB";
        } else if ( l < 1024 * 1024.f ) {
            return df.format((l / 1024.f)) + "MB";
        }
        return df.format(l / 1024.f / 1024.f) + "GB";
    }


    /**
     * @param packageName
     * 获取应用程序图标
     * @return
     */
    private synchronized Drawable getIconFromPackageName(String packageName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            try {
                PackageInfo packageInfo = mPackageManager.getPackageInfo(packageName, 0);
                Context otherAppCtx = mContext.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);
                int displayMetrics[] = {DisplayMetrics.DENSITY_XXXHIGH, DisplayMetrics.DENSITY_XXHIGH, DisplayMetrics.DENSITY_XHIGH, DisplayMetrics.DENSITY_HIGH, DisplayMetrics.DENSITY_TV};
                for ( int displayMetric : displayMetrics ) {
                    try {
                        Drawable d = otherAppCtx.getResources().getDrawableForDensity(packageInfo.applicationInfo.icon, displayMetric);
                        if ( d != null ) {
                            return d;
                        }
                    } catch ( Resources.NotFoundException e ) {
                        continue;
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        ApplicationInfo appInfo = null;
        try {
            appInfo = mPackageManager.getApplicationInfo(packageName, 0);
        } catch ( PackageManager.NameNotFoundException e ) {
            return null;
        }
        return appInfo.loadIcon(mPackageManager);
    }


    /**
     * @param packageName
     * 卸载APK
     */
    public void uninstallAPK(String packageName) {
        Uri uri = Uri.fromParts("package", packageName, null);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        mContext.startActivity(intent);
    }


    /**
     * @param file
     * 安装APK
     */
    public void installAPK(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //兼容7.0
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider", file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if ( mContext.getPackageManager().queryIntentActivities(intent, 0).size() > 0 ) {
            mContext.startActivity(intent);
        }
    }

}
