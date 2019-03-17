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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
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
import java.util.ArrayList;
import java.util.Collections;
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
    private PackageManager pm;
    private boolean isChecked;
    private boolean isSys;
    private AdapterInstalledApps adapter;
    private boolean isAllChecked;
    private List<ApplicationInfo> listofApkstoBackup;
    private Handler handler;
    SharedPreferences preferences;

    String rootPath;
    int installedAppsSize=0;


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
        getStoragePermission();
        createDirectory();
        getApks(isSys);





    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    public void getStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[]{Manifest.permission.WRITE_SETTINGS}, 1);
            requestPermissions(new String[]{Manifest.permission.MANAGE_DOCUMENTS}, 1);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    public void getApks(boolean isSys) {

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);
        Collections.sort(apps, new ApplicationInfo.DisplayNameComparator(pm));
        Log.e("App Size ", "" + apps.size());
        int i = 0;
        for (ApplicationInfo app : apps) {
            Log.e("App ", "" + i++);


            if (isSys) {
                Apk apk = new Apk((String) app.loadLabel(pm), app.loadIcon(pm), app, isChecked);
                apks.add(apk);

            } else {

                if ((app.flags & (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP | ApplicationInfo.FLAG_SYSTEM)) > 0) {

                } else {
                    Apk apk = new Apk((String) app.loadLabel(pm), app.loadIcon(pm), app, isChecked);
                    apks.add(apk);
                }

                installedAppsSize=apks.size();
                Log.e("Size ", "" + apks.size());
            }
        }


    }


    public void backupApks() {
       createDirectory();
        //adapter.notifyDataSetChanged();
        Log.e("Apksssssssss", apks.toString());
        for (Apk apk : apks) {

            if (apk.isChecked()) {
                listofApkstoBackup.add(apk.getAppInfo());
                Log.e("AppName", " " + apk.getAppName());
            }
        }
        if (listofApkstoBackup.size() == 0) {
            Toasty.info(getContext(), "No Apps Selected.", Toast.LENGTH_SHORT, true).show();
        } else {
            //Toast.makeText(getActivity(), "Backing Up", Toast.LENGTH_SHORT).show();
            Log.e("Apps", (listofApkstoBackup.size()) + "");

            writeData(listofApkstoBackup);
            Toasty.success(getContext(),"Archived", Toast.LENGTH_SHORT, true).show();
            listofApkstoBackup.clear();
            uncheckAllBoxes();
        }


    }

    public int getInstalledAppsSize(){

        return installedAppsSize;
    }


    public void writeData(List<ApplicationInfo> listapks) {
        for (ApplicationInfo info : listapks) {
            Log.e("Size------  ", listapks.size() + "  ");
            try {
                File f1 = new File(info.sourceDir);

                String file_name = info.loadLabel(pm).toString();
                File f2 = new File(rootPath);
                if (!f2.exists()) {
                    f2.mkdirs();
                }
                Log.e("Backing up ", file_name);
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




