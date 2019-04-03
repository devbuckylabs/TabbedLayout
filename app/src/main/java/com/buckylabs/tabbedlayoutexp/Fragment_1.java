package com.buckylabs.tabbedlayoutexp;

import android.Manifest;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
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
import android.widget.TableLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileTypeDetector;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static android.app.Activity.RESULT_OK;
import static android.os.Looper.getMainLooper;
import static android.widget.GridLayout.HORIZONTAL;
import static android.widget.GridLayout.VERTICAL;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_1 extends Fragment implements SearchView.OnQueryTextListener {

    private RecyclerView recyclerView;
    private List<Apk> apks;
    private List<Apk> installedApks;
    private List<Apk> archivedApks;
    private PackageManager pm;
    private boolean isChecked;
    private boolean isSys;
    private boolean isOverride;
    private boolean isAutoBackup;
    private AdapterInstalledApps adapter;
    CheckBox checkBox_selectAll;
    private boolean isAllChecked;
    SharedPreferences preferences;

    String rootPath;
    Handler handler;
    //ProgressDialog progressDialog;

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
        isOverride = false;
        isAutoBackup = false;
        isAllChecked = true;
        handler = new Handler(getMainLooper());
        apks = new ArrayList<>();
        installedApks = new ArrayList<>();
        archivedApks = new ArrayList<>();

        rootPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/App_Backup_Pro/";
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

  /*  @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.backup:
                Toast.makeText(getContext(), "Backup", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.uninstall:

                Toast.makeText(getContext(), "Uninstall", Toast.LENGTH_SHORT).show();

                return true;
            default:
                return super.onContextItemSelected(item);
        }


    }*/

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration itemDecor = new DividerItemDecoration(getActivity(), HORIZONTAL);
        itemDecor.setOrientation(VERTICAL);
        recyclerView.addItemDecoration(itemDecor);
        adapter = new AdapterInstalledApps(getActivity(), apks);
        recyclerView.setAdapter(adapter);

    }


    public void getPreferences() {
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        isSys = preferences.getBoolean("show_sys_apps", false);
        isOverride = preferences.getBoolean("override", false);
        isAutoBackup = preferences.getBoolean("auto_backup", false);
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

                Apk apk = getApk(packageInfo);
                installedApks.add(apk);
            } else {

                if ((app.flags & (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP | ApplicationInfo.FLAG_SYSTEM)) > 0) {

                } else {

                    Apk apk = getApk(packageInfo);
                    installedApks.add(apk);

                }


            }
        }

        if (installedApks.size() <= 0) {
            Log.e("Empty List", "*****************************************************");
            return new ArrayList<>();
        }
        Log.e("InstalledList", "*****" + installedApks.toString());
        return installedApks;
    }


    public void populateRecyclerview() {

        installedApks.clear();
        archivedApks.clear();
        apks.clear();
        uncheckAllBoxes();
        try {
            installedApks = getApks(isSys);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        List<Apk> archivedApks = getArchivedApps();


        for (Apk apk1 : installedApks) {

            for (Apk apk2 : archivedApks) {

                if (apk1.getAppName().equals(apk2.getAppName()) && apk1.getAppVersionName().equals(apk2.getAppVersionName())) {

                    apk1.setAppStatus("Archived");

                }
            }
            apks.add(apk1);

        }

        refresh();

    }


    public List<Apk> getArchivedApps() {

        File directory = new File(rootPath);
        File[] files = directory.listFiles();
        for (File file : files) {
            Log.e("Archive FilesName", "" + file.getName());
            PackageInfo packinfo = pm.getPackageArchiveInfo(rootPath + "/" + file.getName(), 0);
            packinfo.applicationInfo.sourceDir = file.getAbsolutePath();
            packinfo.applicationInfo.publicSourceDir = file.getAbsolutePath();

            Apk apk = getApk(packinfo);
            archivedApks.add(apk);

        }

        return archivedApks;

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


    public void backupHelperInit() {

        List<Apk> listApk = new ArrayList<>();

        for (Apk apk : apks) {
            if (apk.isChecked()) {
                listApk.add(apk);
            }
        }
        //checkBox_selectAll.setChecked(false);

        Apk arrayApk[] = new Apk[listApk.size()];
        for (int k = 0; k < listApk.size(); k++) {
            arrayApk[k] = listApk.get(k);
        }

        BackupHelper backupHelper = new BackupHelper(getContext());
        backupHelper.execute(arrayApk);
    }


    public void backupApks() {

        Log.e("Apksssssssss", apks.toString());
        for (Apk apk : apks) {

            if (apk.isChecked()) {
                writeData(apk);
                updateStatus(apk);

            }

        }
        Toasty.success(getContext(), "Archived", Toast.LENGTH_SHORT, true).show();
        uncheckAllBoxes();

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
        File f2 = new File(rootPath);
        if (!f2.exists()) {
            f2.mkdirs();
        }
    }

    public void refresh() {
        adapter.notifyDataSetChanged();
    }

    public void shareApks() {
        List<Uri> shareApks = new ArrayList<>();
        //File file = new File("");
        refresh();
        for (Apk apk : apks) {
            if (apk.isChecked()) {
                File file = new File(apk.getSourceDirectory());

                Uri uri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", file);
                shareApks.add(uri);


            }
        }

        if (shareApks.isEmpty()) {
            Toast.makeText(getContext(), "Select a app to share ", Toast.LENGTH_SHORT).show();
        } else {


            uncheckAllBoxes();

            Log.e("SHAREAPKS", shareApks.size() + "");
            Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.setType("*/*");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "AppBackupPro");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "great");
            shareIntent.putExtra(Intent.EXTRA_EMAIL, "Checkout my app");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, (ArrayList<? extends Parcelable>) shareApks);
            startActivity(Intent.createChooser(shareIntent, "Share app via"));


        }
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
                        StringBuilder Appname = new StringBuilder();
                        Appname.append(apk.getAppName());
                        Appname.append("-");
                        Appname.append(apk.getAppPackage());
                        Appname.append("-");
                        Appname.append(apk.getAppVersionName());
                        try {
                            File f1 = new File(apk.getSourceDirectory());
                            File f2 = new File(rootPath);
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
                // Toasty.success(getContext(), "Archived", Toast.LENGTH_SHORT, true).show();


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
            Toasty.success(getContext(), "Archived", Toast.LENGTH_SHORT, true).show();
            refresh();

        }
    }


}




