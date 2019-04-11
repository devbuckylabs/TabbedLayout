package com.buckylabs.tabbedlayoutexp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Service extends BroadcastReceiver {

    private PackageManager pm;
    boolean isAutoBackup;
    boolean isAutoBackupNotify;
    private SharedPreferences preferences;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        isAutoBackup = preferences.getBoolean("auto_backup", true);
        isAutoBackupNotify = preferences.getBoolean("auto_backup_notify", true);
        pm = context.getPackageManager();
        Log.e("%%%Service", "pref isAutoBackup  " + isAutoBackup);
        Log.e("%%%Service", "pref isAutoBackupNotify  " + isAutoBackupNotify);

        String packageName = intent.getData().getEncodedSchemeSpecificPart();
        PackageInfo packageInfo;
        String Appname = "";
        Apk apk = null;
        ApkManager manager = new ApkManager(context);
        try {
            packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA);
            apk = manager.getApk(packageInfo);
            Appname = apk.getAppName();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Toast.makeText(context, "New App Installed", Toast.LENGTH_SHORT).show();
        Log.e("%%%%%%%%%%%%%%%%%%%%%", "New App Installed  " + packageName + " " + Appname);

        if (isAutoBackup) {
            if (apk != null) {
                Boolean isBackup = manager.backupApk(apk);
                Toast.makeText(context, "Auto Backup Completed", Toast.LENGTH_SHORT).show();
                Log.e("%%%Service", "Auto Backup Completed");
                if (isAutoBackupNotify) {
                    NotifManager notifManager = new NotifManager(context);
                    notifManager.displayNotification(Appname);
                    Log.e("%%%Service", "Notifaction sent");
                } else {
                    Log.e("%%%Service", "Notifaction not sent");
                }
            }

        } else {
            Log.e("%%%Service", "Auto Backup not enabled ");
        }


    }




}
