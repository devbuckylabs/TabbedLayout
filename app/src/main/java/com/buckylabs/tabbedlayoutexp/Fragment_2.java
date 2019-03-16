package com.buckylabs.tabbedlayoutexp;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import java.util.ArrayList;
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
    private List<ApplicationInfo> listofApkstoRestore;
    private Handler handler;
    SharedPreferences preferences;
    Button restore;
    String rootPath;

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
        handler = new Handler(getMainLooper());
        restore = v.findViewById(R.id.restore);
        rootPath=Environment.getExternalStorageDirectory()
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
            }
        });


    }


    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), "onResume", Toast.LENGTH_SHORT).show();
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
            Apk apk = new Apk(info.loadLabel(pm).toString(), info.loadIcon(pm), info, false);
            apks.add(apk);


        }

        Log.e("apks", "" + apks.toString());
    }

    public void InstallApplication() throws PackageManager.NameNotFoundException {

        for (Apk apk : apks) {

            //PackageInfo packinfo=pm.getPackageInfo(apk.getAppName(),PackageManager.GET_META_DATA);
            File file= new File(rootPath,apk.getAppName()+".apk");
            Uri path=FileProvider.getUriForFile(getActivity(),getActivity().getPackageName()+".provider",file);
          //  Uri path = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
            Intent intent=new Intent(Intent.ACTION_VIEW).setDataAndType(path,
                    "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(intent);
        }
    }

    public void restoreApp() {

        //  String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/App_Backup/";
        File file = new File(Environment.getExternalStorageDirectory(),
                "Calculator.apk");
        //Uri path = Uri.fromFile(file);
        Uri path = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(path, "application/vnd.android.package-archive");
        try {
            Toast.makeText(getActivity(), "opening", Toast.LENGTH_LONG).show();
            // startActivityForResult(intent, Activity.RESULT_OK);
            startActivity(intent);

        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "-------------------------", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }


    }







}
