package com.ahmet.iphonewallpaper.Config;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.ahmet.iphonewallpaper.Model.WallpaperItem;

import java.util.List;

public class Common {

    public static final String STR_CATEGORY_BG = "CategoryBackground";
    public static final String STR_WALLPAPER = "backgroundlist";

    public static String CATEGORY_SELECTED;
    public static String STR_CATEGORY_ID_SELECTED;
    public static String select_background_key;

    public static WallpaperItem wallpaperSelected = new WallpaperItem();

    public static final int PERMISSION_REQUEST_CODE = 888;

    public static final int SIGN_IN_REQUEST_CODE = 8888;
    public static final int GOOGLE_SIGN_IN_CODE = 818;

    public static final int PICK_IMAGE_REQUEST = 88;
    public static final int PICK_IMAGE_REQUEST2 = 8;

    public static void  setBadge(Context mContext, int count){

        String launcherClassName = getLauncherClassName(mContext);
        if (launcherClassName == null){
            return;
        }

        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", count);
        intent.putExtra("badge_count_package_name", mContext.getPackageName());
        intent.putExtra("badge_count_class_name", launcherClassName);
        mContext.sendBroadcast(intent);

    }

    private static String getLauncherClassName(Context mContext) {

        PackageManager manager = mContext.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> mListResolveInfos = manager.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : mListResolveInfos) {

            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(mContext.getPackageName())){
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }
        return null;
    }
}
