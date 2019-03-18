package com.buckylabs.tabbedlayoutexp;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

public class Apk {

    private String AppName;
    private Drawable AppIcon;
    private String AppPackage;
    private String AppVersionName;
    private String date;
    private String AppSize;
    private ApplicationInfo AppInfo;
    private boolean isChecked;


    Apk(String appName, Drawable appIcon, String appPackage, String appVersionName, String date, String appSize, ApplicationInfo appInfo, boolean isChecked) {
        AppName = appName;
        AppIcon = appIcon;
        AppPackage = appPackage;
        AppVersionName = appVersionName;
        this.date = date;
        AppSize = appSize;
        AppInfo = appInfo;
        this.isChecked = isChecked;
    }


    public String getAppName() {
        return AppName;
    }

    public void setAppName(String appName) {
        AppName = appName;
    }

    public Drawable getAppIcon() {
        return AppIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        AppIcon = appIcon;
    }

    public String getAppPackage() {
        return AppPackage;
    }

    public void setAppPackage(String appPackage) {
        AppPackage = appPackage;
    }

    public String getAppVersionName() {
        return AppVersionName;
    }

    public void setAppVersionName(String appVersionName) {
        AppVersionName = appVersionName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAppSize() {
        return AppSize;
    }

    public void setAppSize(String appSize) {
        AppSize = appSize;
    }

    public ApplicationInfo getAppInfo() {
        return AppInfo;
    }

    public void setAppInfo(ApplicationInfo appInfo) {
        AppInfo = appInfo;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }


    @Override
    public String toString() {
        return "Apk{" +
                "AppName='" + AppName + '\'' +
                ", AppIcon=" + AppIcon +
                ", AppPackage='" + AppPackage + '\'' +
                ", AppVersionName='" + AppVersionName + '\'' +
                ", date='" + date + '\'' +
                ", AppSize='" + AppSize + '\'' +
                ", AppInfo=" + AppInfo +
                ", isChecked=" + isChecked +
                '}';
    }

}