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
    private List<ApplicationInfo> listofArchivedApks;
    private List<ApplicationInfo> listofApkstoRestore;
    private Handler handler;
    SharedPreferences preferences;
    Button restore;
    String rootPath;
    int archivedAppsSize = 0;

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
        listofApkstoRestore = new ArrayList<>();
        listofArchivedApks = new ArrayList<>();
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

        adapter = new AdapterRestoredApps(getActivity(), apks);
        recyclerViewRestore.setAdapter(adapter);
        try {
            getArchivedApps();
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Exception", "***************************");
            e.printStackTrace();
        }

        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InstallApplication();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                listofApkstoRestore.clear();
                uncheckAllBoxes();

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


    }

    public void getArchivedApps() throws PackageManager.NameNotFoundException {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/App_Backup_Pro";
        File directory = new File(path);
        File[] files = directory.listFiles();
        for (File file : files) {
            Log.e("Archive FilesName", "" + file.getName());
            //Package p=Package.getPackage(file.getName());
            PackageInfo packinfo = pm.getPackageArchiveInfo(path + "/" + file.getName(), PackageManager.GET_META_DATA);
            Log.e("Archive FilespackInfo", "" + packinfo.applicationInfo.loadLabel(pm));
            ApplicationInfo info = pm.getApplicationInfo(packinfo.packageName, PackageManager.GET_META_DATA);

            //   Toast.makeText(getActivity(), ""+p, Toast.LENGTH_SHORT).show();
            //Log.d("Files",""+p+"  "+p.getName()+"  "+p.getSpecificationVersion());
            Log.e("Archive FilesInfo", "" + info.loadLabel(pm));

            listofArchivedApks.add(info);
            /*Apk apk = new Apk(info.loadLabel(pm).toString(), info.loadIcon(pm), info, false);
            apks.add(apk);*/
        }

        readdata(listofArchivedApks);

        archivedAppsSize = listofArchivedApks.size();
    }

    public void readdata(List<ApplicationInfo> apps) throws PackageManager.NameNotFoundException {
        Collections.sort(apps, new ApplicationInfo.DisplayNameComparator(pm));
        for (ApplicationInfo info : apps) {
            PackageInfo packageInfo = pm.getPackageInfo(info.packageName, PackageManager.GET_META_DATA);

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String dateString = formatter.format(new Date(packageInfo.firstInstallTime));
            Log.e("DATEFORMAT", dateString);
            Log.e("Rare", "");

            String AppName = info.loadLabel(pm).toString();
            Drawable AppIcon = info.loadIcon(pm);
            String AppPackage = packageInfo.packageName;
            String AppVersionName = packageInfo.versionName;
            String date = getAppDate(packageInfo.lastUpdateTime);
            ApplicationInfo AppInfo = info;
            File file=new File(info.sourceDir);
            String Appsize= getAppSize(file.length());
            boolean isChecked = false;
            Apk apk = new Apk(AppName,AppIcon,AppPackage,AppVersionName,date,Appsize,AppInfo,isChecked);
            Log.e("CONAN_FRAG2",apk.toString());
            apks.add(apk);
        }


    }


    public void InstallApplication() throws PackageManager.NameNotFoundException {

        refresh();
        for (Apk apk : apks) {

            if (apk.isChecked()) {

                listofApkstoRestore.add(apk.getAppInfo());
                Log.e("checkedFor", apk.getAppName());

            }
        }
        if (listofApkstoRestore.size() == 0) {

            Toast.makeText(getActivity(), "No Apps Selected", Toast.LENGTH_SHORT).show();
        } else {

            Log.e("checkedFor", listofApkstoRestore.toString());
            int i = 0;
            for (ApplicationInfo info : listofApkstoRestore) {

                PackageInfo packageInfo = pm.getPackageInfo(info.packageName, PackageManager.GET_META_DATA);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    Log.e("DataPackage", "PackageName :" + packageInfo.packageName + "  VersionName  :" + packageInfo.versionName + "  VersionCode : " + packageInfo.getLongVersionCode() + "");
                }

                Log.e("Noob " + i++, info.loadLabel(pm).toString());

                //PackageInfo packinfo=pm.getPackageInfo(apk.getAppName(),PackageManager.GET_META_DATA);
                File file = new File(rootPath, info.loadLabel(pm).toString() + ".apk");

                DecimalFormat decimalFormat=new DecimalFormat("0.00");
                decimalFormat.format(file.length()/1024.0F);
               String c= decimalFormat.format(file.length()/1048576.0F);
                //decimalFormat.format(file.length()/1024.0F);

               double bytes = file.length();
                double kb = (bytes / 1024 );
                double mb=(kb/1024);

              //double a=(double) Math.round(mb*100)/100;
                String MB=String.format("%.1f", mb);


                Log.e("DataPackage ", mb + "  "+" "+MB);
                Log.e("DataPackageKB ",  decimalFormat.format(file.length()/1024.0F) + " "+" "+"KB");
                Log.e("DataPackageMB ",  decimalFormat.format(file.length()/1048576.0F) + " "+" "+"MB");


                Uri path = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", file);
                //  Uri path = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
                Intent intent = new Intent(Intent.ACTION_VIEW).setDataAndType(path,
                        "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);

            }
        }

        listofApkstoRestore.clear();
        uncheckAllBoxes();

    }

    public String getAppDate(long milliseconds){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formatter.format(new Date(milliseconds));
        if(dateString==null){
            return "";
        }

        return dateString;
    }

    public String getAppSize(double sizeInBytes){
        Log.e("Travis","ingetSize");
        if(sizeInBytes<1048576){
            Log.e("Travis","ingetSize1");
            double sizeInKB=sizeInBytes/1024;
            String size=String.format("%.1f", sizeInKB);


            return size+"KB";
        }
        else if(sizeInBytes<1073741824) {
            Log.e("Travis","ingetSize2");
            double sizeInMb = sizeInBytes / 1048576;
            String size=String.format("%.1f", sizeInMb);
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


    public int getArchivedAppsSize() {

        return archivedAppsSize;
    }
}
