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
    String rootPath = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/App_Backup_Pro/";
    PackageManager pm;
    boolean isAutoBackup;
    SharedPreferences preferences;

    private static final String CHANNEL_ID = "Auto_Backup";
    private static final String CHANNEL_NAME = "Spooks";
    private static final String CHANNEL_DESC = "Adios";

    private static final int NOTIFICATION_ID = 404;
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        isAutoBackup = preferences.getBoolean("auto_backup", false);
        pm = context.getPackageManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }

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
                Boolean isBackup = writeData(apk);
                Toast.makeText(context, "Auto Backup Completed", Toast.LENGTH_SHORT).show();
                Log.e("%%%Service", "Auto Backup Completed");
                if (isBackup) {
                    displayNotification(Appname);
                }
            }

        }


    }

    public void displayNotification(String appname) {


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_notifications_black_24dp);
        builder.setContentTitle("Auto Backup Agent");
        builder.setContentText("New App : " + appname.toUpperCase() + "  successfully archived");
        builder.setAutoCancel(true);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        builder.setContentIntent(pendingIntent);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);


        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());


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
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        String dateString = formatter.format(new Date(milliseconds));
        if (dateString == null) {
            return "";
        }

        return dateString;
    }


    public String getAppSize(double sizeInBytes) {

        if (sizeInBytes < 1048576) {

            double sizeInKB = sizeInBytes / 1024;
            String size = String.format(Locale.US, "%.1f", sizeInKB);


            return size + " KB";
        } else if (sizeInBytes < 1073741824) {

            double sizeInMb = sizeInBytes / 1048576;
            String size = String.format(Locale.US, "%.1f", sizeInMb);
            return size + " MB";

        }
        return "";
    }


    public boolean writeData(Apk apk) {

        try {
            File f1 = new File(apk.getSourceDirectory());

            StringBuilder Appname = new StringBuilder();
            Appname.append(apk.getAppName());
            Appname.append("-");
            Appname.append(apk.getAppPackage());
            Appname.append("-");
            Appname.append(apk.getAppVersionName());

            File f2 = new File(rootPath);
            if (!f2.exists()) {
                f2.mkdirs();
            }

            f2 = new File(rootPath + "/" + Appname + ".apk");
            if (!f2.exists()) {

                f2.createNewFile();
                InputStream in = new FileInputStream(f1);
                FileOutputStream out = new FileOutputStream(f2);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                Log.e("BackUp Complete ", Appname.toString());
                out.flush();
                out.close();
                return true;
            } else {
                Toast.makeText(context, "Already Backed Up", Toast.LENGTH_SHORT).show();
                Log.e("%%%Service", "Already Backed Up");
            }
        } catch (Exception e) {

            Log.e("Exception", "********************************************* ");
            e.printStackTrace();
        }
        return false;

    }


}
