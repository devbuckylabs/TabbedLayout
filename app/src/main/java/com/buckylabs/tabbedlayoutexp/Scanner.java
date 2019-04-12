package com.buckylabs.tabbedlayoutexp;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import static android.widget.GridLayout.HORIZONTAL;
import static android.widget.GridLayout.VERTICAL;

public class Scanner extends AppCompatActivity {
    private Context context;
    private RecyclerView recyclerView;
    private List<String> list = new ArrayList<>();
    private List<Apk> apks;
    private ScannerAdapter adapter;
    private Button scanBtn;
    private PackageManager pm;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        recyclerView = findViewById(R.id.recyclerView_scanner);
        scanBtn = findViewById(R.id.scan_btn);
        apks = new ArrayList<>();
        context = this;
        pm = context.getPackageManager();
        initRecyclerView();
        registerForContextMenu(recyclerView);
        recyclerView.setVisibility(View.GONE);

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getApksFromSD();

            }
        });


    }


    public void getApksFromSD() {
        scanBtn.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        final ApkManager manager = new ApkManager(context);
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());

        for (File f : file.listFiles()) {

            Log.e("file", f.getName());


            if (f.isDirectory()) {

                Log.e("dir", f.getName());
                f.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {


                        if (name.endsWith(".apk")) {
                            list.add(name);
                            Log.e("File Dir", dir.getName() + " ---" + dir.getAbsolutePath() + "----" + name);

                            Log.e("List", list.toString());
                            PackageInfo packageInfo = null;
                            try {
                                Log.e("path", dir.getAbsolutePath() + "/" + name);
                                packageInfo = pm.getPackageArchiveInfo(dir.getAbsolutePath() + "/" + name, 0);

                            } catch (Exception e) {
                                Log.e("Exceptions ", i + "*******************");
                                i++;
                                packageInfo.applicationInfo.sourceDir = dir.getAbsolutePath();
                                packageInfo.applicationInfo.publicSourceDir = dir.getAbsolutePath();
                                e.printStackTrace();
                            }
                            packageInfo.applicationInfo.sourceDir = dir.getAbsolutePath();
                            packageInfo.applicationInfo.publicSourceDir = dir.getAbsolutePath();

                            Apk apk = manager.getApk(packageInfo);
                            Log.e("Adding", apk.getAppName());
                            apks.add(apk);
                        }
                        return false;
                    }
                });


            } else {
                f.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {

                        if (name.endsWith(".apk")) {
                            list.add(name);
                            try {
                                Log.e("path", dir.getAbsolutePath() + "/" + name);
                                PackageInfo packageInfo = pm.getPackageArchiveInfo(dir.getAbsolutePath() + "/" + name, 0);

                                Apk apk = manager.getApk(packageInfo);
                                Log.e("Adding", apk.getAppName());
                                apks.add(apk);
                            } catch (Exception e) {
                                Log.e("Exceptions ", i + "*******************");
                                i++;
                                e.printStackTrace();
                            }
                        }
                        return false;
                    }
                });

            }

        }

        Log.e("List Size", list.size() + "");
        Log.e("List", list.toString());
        Log.e("apks", apks.size() + "  " + apks.toString());
        Log.e("i", " " + i);
        adapter.notifyDataSetChanged();
    }

    public void initRecyclerView() {
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        DividerItemDecoration itemDecor = new DividerItemDecoration(context, HORIZONTAL);
        itemDecor.setOrientation(VERTICAL);
        recyclerView.addItemDecoration(itemDecor);
        adapter = new ScannerAdapter(context, apks);
        recyclerView.setAdapter(adapter);

    }
}
