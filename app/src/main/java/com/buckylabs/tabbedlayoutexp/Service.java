package com.buckylabs.tabbedlayoutexp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.dmoral.toasty.Toasty;

public class Service extends BroadcastReceiver {
    String rootPath = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/App_Backup_Pro/";
    PackageManager pm;
    boolean isAutoBackup;
    SharedPreferences preferences;

    @Override
    public void onReceive(Context context, Intent intent) {

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        isAutoBackup = preferences.getBoolean("auto_backup", false);
        pm = context.getPackageManager();

        String packageName = intent.getData().getEncodedSchemeSpecificPart();
        PackageInfo packageInfo;
        String Appname = "";
        Apk apk = null;
        try {
            packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA);
            Appname = packageInfo.applicationInfo.loadLabel(pm).toString();
            apk = getApk(packageInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Toast.makeText(context, "New App Installed", Toast.LENGTH_SHORT).show();
        Log.e("%%%%%%%%%%%%%%%%%%%%%", "New App Installed  " + packageName + " " + Appname);

        if (isAutoBackup) {
            if (apk != null) {
                writeData(apk);
                Toast.makeText(context, "Auto Backup Completed", Toast.LENGTH_SHORT).show();
                Log.e("%%%Service", "Auto Backup Completed");
            }

        }


    }

    public Apk getApk(PackageInfo packinfo) {

        String AppName = (String) packinfo.applicationInfo.loadLabel(pm);
        Drawable AppIcon = packinfo.applicationInfo.loadIcon(pm);
        String AppPackage = packinfo.packageName;
        String AppVersionName = packinfo.versionName;
        long time = new File(packinfo.applicationInfo.sourceDir).lastModified();
        String date = getAppDate(time);
        Log.e("Dateeeee", packinfo.lastUpdateTime + "");
        String sourcedirectory = packinfo.applicationInfo.sourceDir;
        String AppStatus = "";
        File file1 = new File(packinfo.applicationInfo.sourceDir);
        String Appsize = getAppSize(file1.length());
        boolean isChecked = false;
        Apk apk = new Apk(AppName, AppIcon, AppPackage, AppStatus, AppVersionName, date, Appsize, sourcedirectory, isChecked);

        return apk;

    }

    public String getAppDate(long milliseconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String dateString = formatter.format(new Date(milliseconds));
        if (dateString == null) {
            return "";
        }

        return dateString;
    }


    public String getAppSize(double sizeInBytes) {

        if (sizeInBytes < 1048576) {

            double sizeInKB = sizeInBytes / 1024;
            String size = String.format("%.1f", sizeInKB);


            return size + " KB";
        } else if (sizeInBytes < 1073741824) {

            double sizeInMb = sizeInBytes / 1048576;
            String size = String.format("%.1f", sizeInMb);
            return size + " MB";

        }
        return "";
    }


    public void writeData(Apk apk) {

        try {
            File f1 = new File(apk.getSourceDirectory());

            String file_name = apk.getAppName();
            File f2 = new File(rootPath);
            if (!f2.exists()) {
                f2.mkdirs();
            }

            f2 = new File(rootPath + "/" + file_name + ".apk");
            f2.createNewFile();
            InputStream in = new FileInputStream(f1);
            FileOutputStream out = new FileOutputStream(f2);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            Log.e("BackUp Complete ", file_name);
            out.flush();
            out.close();
        } catch (Exception e) {

            Log.e("Exception", "********************************************* ");
            e.printStackTrace();
        }


    }

}
