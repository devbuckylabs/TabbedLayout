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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.spi.FileTypeDetector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.os.Looper.getMainLooper;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_1 extends Fragment {

    private RecyclerView recyclerView;
    private List<Apk> apks;
    private PackageManager pm;
    private boolean isChecked;
    private AdapterInstalledApps adapter;
    private boolean isAllChecked;
    private List<ApplicationInfo> listofApkstoBackup;
    private Handler handler;
    SharedPreferences preferences;
    Button backup;

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
        isAllChecked = true;
        apks = new ArrayList<>();
        listofApkstoBackup = new ArrayList<>();
        handler = new Handler(getMainLooper());
        backup = v.findViewById(R.id.backUp);

        return v;


    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AdapterInstalledApps adapter = new AdapterInstalledApps(getActivity(), apks);
        recyclerView.setAdapter(adapter);
        getStoragePermission();
        createDirectory();
        //getApks(false);
        try {
            getArchivedapks();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getContext(), "Toast", Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void getStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[]{Manifest.permission.WRITE_SETTINGS}, 1);
            requestPermissions(new String[]{Manifest.permission.MANAGE_DOCUMENTS}, 1);

        }
    }


    public void getApks(boolean isSys) {

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);
        Collections.sort(apps, new ApplicationInfo.DisplayNameComparator(pm));
        for (ApplicationInfo app : apps) {

            if (isSys) {
                Apk apk = new Apk((String) app.loadLabel(pm), app.loadIcon(pm), app, isChecked);
                apks.add(apk);

            } else {

                if ((app.flags & (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP | ApplicationInfo.FLAG_SYSTEM)) > 0) {

                } else {
                    Apk apk = new Apk((String) app.loadLabel(pm), app.loadIcon(pm), app, isChecked);
                    apks.add(apk);
                }


            }
        }


    }

    public void getArchivedapks() throws PackageManager.NameNotFoundException {



        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/App_Backup_Pro";
        File directory = new File(path);
        File[] files = directory.listFiles();
        for (File file:files)
        {

            Package p=Package.getPackage(file.getName());
            //PackageInfo info=pm.getApplicationInfo()

              ApplicationInfo info=pm.getApplicationInfo("com.andsuper.bucket",PackageManager.GET_META_DATA);
            //   Toast.makeText(getActivity(), ""+p, Toast.LENGTH_SHORT).show();
            Log.d("Files",""+p+"  "+p.getName()+"  "+p.getSpecificationVersion());
             Log.d("FilesInfo",""+info.loadLabel(pm));
             Apk apk = new Apk(info.loadLabel(pm).toString(), info.loadIcon(pm), info, false);
              apks.add(apk);

        }








       /* String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/App_Backup_Pro";
        File directory = new File(path);
        File[] files = directory.listFiles();
        for (File file:files)
        {

            Package p=Package.getPackage(file.getName());
            PackageInfo info=pm.getPackageArchiveInfo(path+"/bucket.apk",PackageManager.GET_META_DATA);
            Toast.makeText(getActivity(), ""+p, Toast.LENGTH_SHORT).show();
           Log.d("Files",""+p+"  "+p.getName()+"  "+p.getSpecificationVersion());
            Log.d("FilesInfo",""+info.applicationInfo.loadLabel(pm));
            Apk apk = new Apk(info.packageName, info.applicationInfo.loadIcon(pm), new ApplicationInfo(), false);
            apks.add(apk);

        }
*/

    }





    public void createDirectory() {
        String rootPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/App_Backup_Pro/";
        File f2 = new File(rootPath);
        if (!f2.exists()) {
            f2.mkdirs();
        }
    }
}
