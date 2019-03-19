package com.buckylabs.tabbedlayoutexp;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.AppLaunchChecker;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static android.os.Looper.getMainLooper;
import static android.widget.GridLayout.HORIZONTAL;
import static android.widget.GridLayout.VERTICAL;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_2 extends Fragment {

    private RecyclerView recyclerViewRestore;
    private List<Apk> apks;
    private PackageManager pm;
    private boolean isChecked;
    private AdapterRestoredApps adapter;
    List<Apk> archivedApks;
    private Handler handler;
    SharedPreferences preferences;
    Button restore;
    String rootPath;
    List<Apk> installedapks;


    public Fragment_2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_2, container, false);
        recyclerViewRestore = v.findViewById(R.id.recyclerViewRestore);
        pm = getActivity().getPackageManager();
        isChecked = false;
        //isAllChecked = true;
        apks = new ArrayList<>();

        handler = new Handler(getMainLooper());
        restore = v.findViewById(R.id.restore);
        rootPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/App_Backup_Pro/";
        return v;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewRestore.hasFixedSize();
        recyclerViewRestore.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration itemDecor = new DividerItemDecoration(getActivity(), HORIZONTAL);
        itemDecor.setOrientation(VERTICAL);
        recyclerViewRestore.addItemDecoration(itemDecor);
        archivedApks =new ArrayList<>();
        installedapks=new ArrayList<>();
        adapter = new AdapterRestoredApps(getActivity(), apks);
        recyclerViewRestore.setAdapter(adapter);
      //  populateRecyclerview();

        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    InstallApplication();


            }
        });


    }


    @Override
    public void onResume() {
        super.onResume();
        refresh();

        Toast.makeText(getActivity(), "onResume", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
       super.onActivityCreated(savedInstanceState);
        populateRecyclerview();

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


        }

      return archivedApks;

    }




public void populateRecyclerview() {
    List<Apk> installedApks = new ArrayList<>();
    try {
        installedApks = getInstalledApks(false);
    } catch (PackageManager.NameNotFoundException e) {
        e.printStackTrace();
    }
    List<Apk> archivedApks = getArchivedApps();


    for (Apk apk1 : archivedApks) {

        for (Apk apk2 : installedApks) {

            if (apk1.getAppName().equals(apk2.getAppName())) {

                apk1.setAppStatus("Installed");

            }
        }

        apks.add(apk1);

    }

    Collections.sort(apks, new Comparator<Apk>() {
        @Override
        public int compare(final Apk object1, final Apk object2) {
            return object1.getAppName().compareTo(object2.getAppName());
        }
    });

    refresh();
}





    public List<Apk> getInstalledApks(boolean isSys) throws PackageManager.NameNotFoundException {

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



                    installedapks.add(apk);

                }



            }
        }

        if(installedapks.size()<=0)
        return new ArrayList<>();

return installedapks;
    }


    public void InstallApplication() {

        refresh();
        for (Apk apk : apks) {

            if (apk.isChecked()) {
                File file = new File(rootPath, apk.getAppName() + ".apk");

                if(Build.VERSION.SDK_INT>=24) {

                    Uri path = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", file);
                    Intent intent = new Intent(Intent.ACTION_VIEW).setDataAndType(path,
                            "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                }else{

                    Uri uri=Uri.fromFile(file);
                    Intent intent = new Intent(Intent.ACTION_VIEW).setDataAndType(uri,
                            "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);


                }
            }
        }

        uncheckAllBoxes();

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
        Log.e("Travis", "ingetSize");
        if (sizeInBytes < 1048576) {
            Log.e("Travis", "ingetSize1");
            double sizeInKB = sizeInBytes / 1024;
            String size = String.format("%.1f", sizeInKB);


            return size + "KB";
        } else if (sizeInBytes < 1073741824) {
            Log.e("Travis", "ingetSize2");
            double sizeInMb = sizeInBytes / 1048576;
            String size = String.format("%.1f", sizeInMb);
            return size + "MB";

        }
        return "";
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


    public void refresh() {
        adapter.notifyDataSetChanged();
    }


}
