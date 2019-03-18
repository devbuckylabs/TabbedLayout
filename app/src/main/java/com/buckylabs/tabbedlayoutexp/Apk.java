package com.buckylabs.tabbedlayoutexp;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

public class Apk {

    private String AppPackage;
    private String AppStatus;
    private String AppVersionName;
    private String date;
    private String AppSize;
    private ApplicationInfo AppInfo;
    private boolean isChecked;
    private String AppName;
    private Drawable AppIcon;


    public Apk(String appName, Drawable appIcon, String appPackage, String appStatus, String appVersionName, String date, String appSize, ApplicationInfo appInfo, boolean isChecked) {
        AppName = appName;
        AppIcon = appIcon;
        AppPackage = appPackage;
        AppStatus = appStatus;
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

    public String getAppStatus() {
        return AppStatus;
    }

    public void setAppStatus(String appStatus) {
        AppStatus = appStatus;
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
                "AppPackage='" + AppPackage + '\'' +
                ", AppStatus='" + AppStatus + '\'' +
                ", AppVersionName='" + AppVersionName + '\'' +
                ", date='" + date + '\'' +
                ", AppSize='" + AppSize + '\'' +
                ", AppInfo=" + AppInfo +
                ", isChecked=" + isChecked +
                ", AppName='" + AppName + '\'' +
                ", AppIcon=" + AppIcon +
                '}';
    }



}