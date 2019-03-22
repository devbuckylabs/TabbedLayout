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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
    SharedPreferences preferences;
    String rootPath;
    ProgressDialog progressDialog;

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
        installedApks = new ArrayList<>();
        archivedApks = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());
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
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        isSys = preferences.getBoolean("show_sys_apps", false);

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        populateRecyclerview();


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

        try {
            getApks(isSys);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        List<Apk> archivedApks = getArchivedApps();


        for (Apk apk1 : installedApks) {

            for (Apk apk2 : archivedApks) {

                if (apk1.getAppName().equals(apk2.getAppName())) {

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


    public void backupHelperInit() {
        refresh();
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

        BackupHelper backupHelper = new BackupHelper(getContext(), progressDialog);
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


    class BackupHelper extends AsyncTask<Apk, Integer, String> {

        Context context;
        ProgressDialog progressDialog;

        public BackupHelper(Context context, ProgressDialog progressDialog) {
            this.context = context;
            this.progressDialog = progressDialog;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setTitle("Baacking Up Apps");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMax(100);
            progressDialog.setMessage("hey");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Apk... listApks) {
            progressDialog.setMax(listApks.length);
            int k = 0;
            for (Apk apk : listApks) {
                {

                    if (apk.isChecked()) {
                        progressDialog.setMessage(apk.getAppName());
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
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
                            //   Log.e("BackUp Complete ", file_name);
                            out.flush();
                            out.close();
                        } catch (Exception e) {

                            // Log.e("Exception", "********************************************* ");
                            e.printStackTrace();
                        }


                        updateStatus(apk);
                        publishProgress(k++);
                    }

                }
                // Toasty.success(getContext(), "Archived", Toast.LENGTH_SHORT, true).show();


            }

            return null;
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.incrementProgressBy(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.setProgress(0);
            progressDialog.dismiss();
            uncheckAllBoxes();
            refresh();

        }
    }


}




