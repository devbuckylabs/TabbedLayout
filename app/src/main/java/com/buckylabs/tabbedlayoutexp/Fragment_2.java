package com.buckylabs.tabbedlayoutexp;


import android.content.ActivityNotFoundException;
import android.content.Context;
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
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.AppLaunchChecker;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.os.Looper.getMainLooper;
import static android.widget.GridLayout.HORIZONTAL;
import static android.widget.GridLayout.VERTICAL;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_2 extends Fragment implements SearchView.OnQueryTextListener {

    private RecyclerView recyclerViewRestore;
    private List<Apk> apks;
    private PackageManager pm;
    private AdapterRestoredApps adapter;
    private Handler handler;
    private String rootPath;
    private CheckBox checkBox_selectAll;
    private Context context;
    private ProgressBar progressBar;


    public Fragment_2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_2, container, false);
        context = getActivity();
        recyclerViewRestore = v.findViewById(R.id.recyclerViewRestore);
        pm = context.getPackageManager();
        apks = new ArrayList<>();
        handler = new Handler(getMainLooper());
        progressBar = v.findViewById(R.id.progressBar2);
        rootPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/App_Backup_Pro/";
        return v;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRecyclerView();
        registerForContextMenu(recyclerViewRestore);
        setHasOptionsMenu(true);


    }


    @Override
    public void onResume() {
        super.onResume();
        refresh();
        Toast.makeText(context, "onResume", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                populateRecyclerview();
            }
        }, 30);

    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        String input = s.toLowerCase();
        List<Apk> list = new ArrayList<>();

        for (Apk apk : apks) {

            if (apk.getAppName().toLowerCase().contains(input)) {

                list.add(apk);

            }

            adapter.updateList(list);
        }

        return true;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

    }

    public void initRecyclerView() {
        recyclerViewRestore.hasFixedSize();
        recyclerViewRestore.setLayoutManager(new LinearLayoutManager(context));
        DividerItemDecoration itemDecor = new DividerItemDecoration(context, HORIZONTAL);
        itemDecor.setOrientation(VERTICAL);
        recyclerViewRestore.addItemDecoration(itemDecor);
        adapter = new AdapterRestoredApps(context, apks);
        recyclerViewRestore.setAdapter(adapter);

    }




    public void populateRecyclerview() {


        apks.clear();
        uncheckAllBoxes();
        ApkManager manager = new ApkManager(context);
        List<Apk> installedApks = new ArrayList<>();
        try {
            installedApks = manager.getinstalledApks(false);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        List<Apk> archivedApks = manager.getArchivedApks();


        for (Apk apk1 : archivedApks) {

            for (Apk apk2 : installedApks) {

                if (apk1.getAppName().equals(apk2.getAppName()) && apk1.getAppVersionName().equals(apk2.getAppVersionName())) {

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


    public void InstallApplication() {

        refresh();
        ApkManager manager = new ApkManager(context);
        for (Apk apk : apks) {
            if (apk.isChecked()) {
                manager.restoreApk(apk);
            }
        }

        uncheckAllBoxes();
        checkBox_selectAll.setChecked(false);

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

    public void shareApks() {

        List<Apk> shareApks = new ArrayList<>();
        refresh();
        for (Apk apk : apks) {
            if (apk.isChecked()) {
                shareApks.add(apk);
            }
        }
        ApkManager manager = new ApkManager(context);
        manager.getApksToShare(shareApks);
        uncheckAllBoxes();



    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search...");

        MenuItem selectAll = menu.findItem(R.id.select_all);
        checkBox_selectAll = (CheckBox) selectAll.getActionView();
        checkBox_selectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    checkAllBoxes();
                } else {
                    uncheckAllBoxes();

                }


            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
