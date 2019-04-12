package com.buckylabs.tabbedlayoutexp;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import static android.os.Looper.getMainLooper;
import static android.widget.GridLayout.HORIZONTAL;
import static android.widget.GridLayout.VERTICAL;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_1 extends Fragment implements SearchView.OnQueryTextListener {

    private RecyclerView recyclerView;
    private List<Apk> apks;
    private PackageManager pm;
    private boolean isSys;
    private boolean isOverride;
    private boolean isAutoBackup;
    private AdapterInstalledApps adapter;
    private CheckBox checkBox_selectAll;
    private SharedPreferences preferences;
    private Handler handler;
    private Context context;
    private ProgressBar progressBar;
    private boolean isAutoBackupNotify;
    public static int LAUNCH_COUNT = 0;
    public boolean isNeverRate;
    private DialogManager dialogManager;

    public Fragment_1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_1, container, false);
        context = getActivity();
        recyclerView = v.findViewById(R.id.recyclerView);
        pm = context.getPackageManager();
        isSys = false;
        isOverride = false;
        isAutoBackup = false;
        isAutoBackupNotify = false;
        isNeverRate = false;
        handler = new Handler(getMainLooper());
        apks = new ArrayList<>();
        dialogManager = new DialogManager(context);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        progressBar = v.findViewById(R.id.progressBar1);

        return v;


    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRecyclerView();
        createDirectory();
        getPreferences();
        registerForContextMenu(recyclerView);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LAUNCH_COUNT += 1;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("launch_count", LAUNCH_COUNT);
        editor.commit();

        int temp_launch_count = preferences.getInt("launch_count", 1);
        Log.d("LaunchCounter", temp_launch_count + "  ");
        populateRcInit();


    }

    public void populateRcInit() {
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                populateRecyclerview();
            }
        }, 30);


    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferences();
        refresh();
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


    public void initRecyclerView() {
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        DividerItemDecoration itemDecor = new DividerItemDecoration(context, HORIZONTAL);
        itemDecor.setOrientation(VERTICAL);
        recyclerView.addItemDecoration(itemDecor);
        adapter = new AdapterInstalledApps(context, apks);
        recyclerView.setAdapter(adapter);

    }


    public void getPreferences() {
        isSys = preferences.getBoolean("show_sys_apps", false);
        isOverride = preferences.getBoolean("override", false);
        isAutoBackup = preferences.getBoolean("auto_backup", false);
        isAutoBackupNotify = preferences.getBoolean("auto_backup_notify", true);
        isNeverRate = preferences.getBoolean("never_rate", false);

    }


    public void populateRecyclerview() {

        apks.clear();
        refresh();
        uncheckAllBoxes();
        ApkManager manager = new ApkManager(context);
        List<Apk> installedApks = new ArrayList<>();

        try {
            installedApks = manager.getinstalledApks(isSys);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        List<Apk> archivedApks = manager.getArchivedApks();

        for (Apk apk1 : installedApks) {

            for (Apk apk2 : archivedApks) {

                if (apk1.getAppName().equals(apk2.getAppName()) && apk1.getAppVersionName().equals(apk2.getAppVersionName())) {

                    apk1.setAppStatus("Archived");

                }
            }
            apks.add(apk1);

        }
        Log.e("apks", apks.toString());
        refresh();

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search...");


        MenuItem uninstallItem = menu.findItem(R.id.uninstall_item);
        uninstallItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                uninstallApks();
                uncheckAllBoxes();
                refresh();

                return true;
            }
        });

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


    public void backupHelperInit() {

        List<Apk> listApk = new ArrayList<>();

        for (Apk apk : apks) {
            if (apk.isChecked()) {
                listApk.add(apk);
            }
        }


        Apk arrayApk[] = new Apk[listApk.size()];
        for (int k = 0; k < listApk.size(); k++) {
            arrayApk[k] = listApk.get(k);
        }

        BackupHelper backupHelper = new BackupHelper(context);
        backupHelper.execute(arrayApk);
    }


    private void updateStatus(Apk apk) {

        apk.setAppStatus("Archived");
        //refresh();

    }

    public void uncheckAllBoxes() {


        if (checkBox_selectAll.isChecked()) {
            checkBox_selectAll.setChecked(false);
        }
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
        File f2 = new File(Constant.rootpath);
        if (!f2.exists()) {
            f2.mkdirs();
        }
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
        manager.getApksToSharewithName(shareApks);
        uncheckAllBoxes();

    }

    public void uninstallApks() {

        List<Apk> listapks = new ArrayList<>();

        for (Apk apk : apks) {
            if (apk.isChecked()) {
                listapks.add(apk);
                Log.e("Uninstall", apk.toString());
            }
            if (listapks.size() == 6) {
                Toast.makeText(context, "You can uninstall only 5 apps at a time", Toast.LENGTH_SHORT).show();
                Log.e("Uninstall", "Max reached");
                return;
            }

        }
        Log.e("List Uninstall", listapks.toString());
        if (listapks.isEmpty()) {
            Toast.makeText(context, "Select a app to uninstall", Toast.LENGTH_SHORT).show();
        } else {

            ApkManager manager = new ApkManager(context);
            manager.uninstallApks(listapks);
        }

    }

    public void rate(boolean neverRate) {

        if (!neverRate) {

            if (LAUNCH_COUNT % 10 == 0) {
                dialogManager.alertDialogRate("Rate this app", "If you enjoy using this app, would you mind taking a moment to rate it? It wont't take more than a minute. Thanks for your support!");
            }

        }
    }

    @Override
    public void onDestroy() {

        Log.e("OnDestroy", "");
        super.onDestroy();
    }

    class BackupHelper extends AsyncTask<Apk, Integer, String> {

        Context context;
        ProgressDialog progressDialog;
        boolean isDeleted;

        public BackupHelper(Context context) {
            this.context = context;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Backing Up Apps");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCanceledOnTouchOutside(false);
            //progressDialog.setMax(100);

            //progressDialog.setIndeterminate(true);
            progressDialog.setMessage("hey");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Apk... listApks) {
            progressDialog.setMax(listApks.length);
            int k = 1;
            int i = 1;
            for (final Apk apk : listApks) {
                {
                    progressDialog.setProgress(0);
                    if (apk.isChecked()) {

                        progressDialog.setMessage(apk.getAppName() + "  " + (i) + "/" + listApks.length);
                        /*try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }*/
                        ApkManager manager = new ApkManager(context);
                        String Appname = manager.appNameGenerator(apk);

                        try {
                            File f1 = new File(apk.getSourceDirectory());
                            File f2 = new File(Constant.rootpath);
                            if (!f2.exists()) {
                                f2.mkdirs();
                            }
                            if (isOverride) {
                                File[] files = f2.listFiles(new FilenameFilter() {
                                    @Override
                                    public boolean accept(File dir, String name) {

                                        boolean result;
                                        if (name.startsWith(apk.getAppName())) {
                                            result = true;
                                            return result;
                                        } else {
                                            result = false;
                                        }

                                        return result;
                                    }
                                });

                                for (File f : files) {
                                    f.delete();
                                    isDeleted = true;
                                }
                            }

                            f2 = new File(Constant.rootpath + "/" + Appname);

                            if (!f2.exists()) {

                                f2.createNewFile();
                                InputStream in = new FileInputStream(f1);
                                FileOutputStream out = new FileOutputStream(f2);
                                byte[] buf = new byte[1024];
                                int len;
                                while ((len = in.read(buf)) > 0) {
                                    out.write(buf, 0, len);
                                }
                                //   Log.e("BackUp Complete ", file_name);
                                out.flush();
                                out.close();
                            }
                        } catch (Exception e) {

                            // Log.e("Exception", "********************************************* ");
                            e.printStackTrace();
                        }

                        updateStatus(apk);

                        k++;
                        publishProgress(k);
                        i++;
                        // progressDialog.setProgress(0);

                    }

                }
                // Toasty.success(context, "Archived", Toast.LENGTH_SHORT, true).show();


            }

            return null;
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.e("onProgessUpdate", values[0] + "");
            progressDialog.incrementProgressBy(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("Delete", isDeleted + " ");
            // progressDialog.setProgress(0);
            progressDialog.dismiss();
            progressDialog.cancel();
            uncheckAllBoxes();
            checkBox_selectAll.setChecked(false);
            Toasty.success(context, "Archived", Toast.LENGTH_SHORT, true).show();
            refresh();
            Log.e("isNeverRate", isNeverRate + "");
            isNeverRate = preferences.getBoolean("never_rate", false);
            rate(isNeverRate);

        }
    }


}




