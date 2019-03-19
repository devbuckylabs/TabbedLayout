package com.buckylabs.tabbedlayoutexp;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.spi.FileTypeDetector;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static android.os.Looper.getMainLooper;
import static android.widget.GridLayout.HORIZONTAL;
import static android.widget.GridLayout.VERTICAL;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_1 extends Fragment {

    private RecyclerView recyclerView;
    private List<Apk> apks;
    private List<Apk> installedApks;
    private List<Apk> archivedApks;
    private PackageManager pm;
    private boolean isChecked;
    private boolean isSys;
    private AdapterInstalledApps adapter;
    private boolean isAllChecked;
    private List<ApplicationInfo> listofApkstoBackup;
    private Handler handler;
    SharedPreferences preferences;
    String rootPath;
    public static final int  MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 346;

    public Fragment_1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_1, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);
        pm = getActivity().getPackageManager();
        isChecked = false;
        isSys = false;
        isAllChecked = true;
        apks = new ArrayList<>();
        listofApkstoBackup = new ArrayList<>();
        installedApks = new ArrayList<>();
        archivedApks = new ArrayList<>();
        handler = new Handler(getMainLooper());
        rootPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/App_Backup_Pro/";
        return v;


    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration itemDecor = new DividerItemDecoration(getActivity(), HORIZONTAL);
        itemDecor.setOrientation(VERTICAL);
        recyclerView.addItemDecoration(itemDecor);

        adapter = new AdapterInstalledApps(getActivity(), apks);
        recyclerView.setAdapter(adapter);

        createDirectory();


    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new Thread(new Runnable() {
            @Override
            public void run() {
                populateRecyclerview();
            }
        }).start();

    }




    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    public List<Apk> getApks(boolean isSys) throws PackageManager.NameNotFoundException {

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);
        Collections.sort(apps, new ApplicationInfo.DisplayNameComparator(pm));
        Log.e("App Size ", "" + apps.size());

        for (ApplicationInfo app : apps) {

            PackageInfo packageInfo = pm.getPackageInfo(app.packageName, PackageManager.GET_META_DATA);
            if (isSys) {


                String AppName = app.loadLabel(pm).toString();
                Drawable AppIcon = app.loadIcon(pm);
                String AppPackage = packageInfo.packageName;
                String AppStatus = "";
                String AppVersionName = packageInfo.versionName;
                String date = getAppDate(packageInfo.lastUpdateTime);
                ApplicationInfo AppInfo = app;
                File file = new File(app.sourceDir);
                String Appsize = (String) getAppSize(file.length());
                boolean isChecked = isSys;
                Apk apk = new Apk(AppName, AppIcon, AppPackage, AppStatus, AppVersionName, date, Appsize, AppInfo, isChecked);
                apks.add(apk);

            } else {

                if ((app.flags & (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP | ApplicationInfo.FLAG_SYSTEM)) > 0) {

                } else {


                    String AppName = app.loadLabel(pm).toString();
                    Drawable AppIcon = app.loadIcon(pm);
                    String AppStatus = "";
                    String AppPackage = packageInfo.packageName;
                    String AppVersionName = packageInfo.versionName;
                    String date = getAppDate(packageInfo.lastUpdateTime);
                    ApplicationInfo AppInfo = app;
                    File file = new File(app.sourceDir);
                    String Appsize = (String) getAppSize(file.length());

                    boolean isChecked = isSys;
                    Apk apk = new Apk(AppName, AppIcon, AppPackage, AppStatus, AppVersionName, date, Appsize, AppInfo, isChecked);

                    installedApks.add(apk);

                }


            }
        }

        if (installedApks.size() <= 0) {
            return new ArrayList<>();
        }

        return installedApks;
    }




    public void populateRecyclerview(){
        List<Apk> installedApks=new ArrayList<>();
        try {
            installedApks=getApks(isSys);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        List<Apk> archivedApks=getArchivedApps();


        for(Apk apk1:installedApks){

            for (Apk apk2:archivedApks){

                if(apk1.getAppName().equals(apk2.getAppName())){

                    apk1.setAppStatus("Archived");

                }
            }
            apks.add(apk1);

        }



    }




    public List<Apk> getArchivedApps() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/App_Backup_Pro";
        File directory = new File(path);
        File[] files = directory.listFiles();
        for (File file : files) {
            Log.e("Archive FilesName", "" + file.getName());
            PackageInfo packinfo = pm.getPackageArchiveInfo(path + "/" + file.getName(), 0);
            ApplicationInfo info;
            try {
                info = pm.getApplicationInfo(packinfo.packageName, PackageManager.GET_META_DATA);

            } catch (PackageManager.NameNotFoundException e) {

                info = null;
                packinfo.applicationInfo.sourceDir=file.getAbsolutePath();
                packinfo.applicationInfo.publicSourceDir=file.getAbsolutePath();

            }

            packinfo.applicationInfo.sourceDir=file.getAbsolutePath();
            packinfo.applicationInfo.publicSourceDir=file.getAbsolutePath();


            String AppName = (String) packinfo.applicationInfo.loadLabel(pm);
            Drawable AppIcon = packinfo.applicationInfo.loadIcon(pm);
            String AppPackage = packinfo.packageName;
            String AppVersionName = packinfo.versionName;
            long time = new File(packinfo.applicationInfo.sourceDir).lastModified();
            String date=getAppDate(time);
            Log.e("Dateeeee",packinfo.lastUpdateTime+"");
            ApplicationInfo AppInfo = info;
            String AppStatus = "";
            File file1 = new File(packinfo.applicationInfo.sourceDir);
            String Appsize = getAppSize(file1.length());
            boolean isChecked = false;
            Apk apk = new Apk(AppName, AppIcon, AppPackage, AppStatus, AppVersionName, date, Appsize, AppInfo, isChecked);
            archivedApks.add(apk);


            // apks.add(apk);

        }

        return archivedApks;

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


    public void backupApks() {
        createDirectory();

        Log.e("Apksssssssss", apks.toString());
        for (Apk apk : apks) {

            if (apk.isChecked()) {
                listofApkstoBackup.add(apk.getAppInfo());
                updateStatus(apk);

            }
        }
        if (listofApkstoBackup.size() == 0) {
            Toasty.info(getContext(), "No Apps Selected.", Toast.LENGTH_SHORT, true).show();
        } else {

            Log.e("Apps", (listofApkstoBackup.size()) + "");

            writeData(listofApkstoBackup);
            Toasty.success(getContext(), "Archived", Toast.LENGTH_SHORT, true).show();
            listofApkstoBackup.clear();
            uncheckAllBoxes();
        }


    }

    private void updateStatus(Apk apk) {

        apk.setAppStatus("Archived");
        refresh();

    }


    public void writeData(List<ApplicationInfo> listapks) {
        for (ApplicationInfo info : listapks) {

            try {
                File f1 = new File(info.sourceDir);

                String file_name = info.loadLabel(pm).toString();
                File f2 = new File(rootPath);
                if (!f2.exists()) {
                    f2.mkdirs();
                }

                f2 = new File(f2.getPath() + "/" + file_name + ".apk");
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
                e.printStackTrace();
            }
        }


    }


    public void uncheckAllBoxes() {

        for (Apk apk : apks) {
            apk.setChecked(false);

        }
        refresh();
    }


    public void checkAllBoxes() {
        for (Apk apk : apks) {
            apk.setChecked(true);

        }
        refresh();


    }


    public void createDirectory() {
        File f2 = new File(rootPath);
        if (!f2.exists()) {
            f2.mkdirs();
        }
    }

    public void refresh() {
        adapter.notifyDataSetChanged();
    }


}




