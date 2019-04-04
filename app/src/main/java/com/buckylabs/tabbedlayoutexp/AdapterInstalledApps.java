package com.buckylabs.tabbedlayoutexp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class AdapterInstalledApps extends RecyclerView.Adapter<AdapterInstalledApps.ViewHolder> {

    private List<Apk> apks;
    private Context context;
    private String rootPath;

    public AdapterInstalledApps(Context context, List<Apk> apks) {
        this.apks = apks;
        this.context = context;

    }

    @NonNull
    @Override
    public AdapterInstalledApps.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(context).inflate(R.layout.listitem, viewGroup, false);
        rootPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/App_Backup_Pro/";
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull AdapterInstalledApps.ViewHolder viewHolder, int i) {

        final Apk apk = apks.get(i);

        viewHolder.appName.setText(apk.getAppName());
        viewHolder.appIcon.setImageDrawable(apk.getAppIcon());
        viewHolder.appversion.setText("v" + apk.getAppVersionName());
        viewHolder.appsize.setText(apk.getAppSize() + " | " + apk.getDate());
        viewHolder.checkBox.setChecked(apk.isChecked());
        viewHolder.appStatus.setText(apk.getAppStatus());

    }

    @Override
    public int getItemCount() {
        if (apks == null) {
            return 0;
        }
        return apks.size();
    }

    public void updateList(List<Apk> list) {

        apks = new ArrayList<>();
        apks.addAll(list);
        notifyDataSetChanged();

    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, View.OnCreateContextMenuListener {

        TextView appName;
        TextView appStatus;
        TextView appversion;
        TextView appsize;
        ImageView appIcon;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appName = itemView.findViewById(R.id.app_name_Text);
            appversion = itemView.findViewById(R.id.app_version_name_text);
            appsize = itemView.findViewById(R.id.appSize_text);
            appIcon = itemView.findViewById(R.id.image);
            checkBox = itemView.findViewById(R.id.checkbox);
            appStatus = itemView.findViewById(R.id.appStatus);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            itemView.setOnCreateContextMenuListener(this);


        }


        @Override
        public void onClick(View v) {


            if (apks.get(getAdapterPosition()).isChecked()) {

                checkBox.setChecked(false);
                apks.get(getAdapterPosition()).setChecked(false);

            } else {

                checkBox.setChecked(true);
                apks.get(getAdapterPosition()).setChecked(true);

            }
        }


        @Override
        public boolean onLongClick(View v) {
            Toast.makeText(context, "Hola", Toast.LENGTH_SHORT).show();
            Log.e("Hola", "" + apks.get(getAdapterPosition()));

            ((Activity) context).openContextMenu(v);
            return true;
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            StringBuilder title = new StringBuilder();
            title.append(apks.get(getAdapterPosition()).getAppName());
            title.append("  v");
            title.append(apks.get(getAdapterPosition()).getAppVersionName());

            menu.setHeaderIcon(apks.get(getAdapterPosition()).getAppIcon());
            menu.setHeaderTitle(title);
            ((Activity) context).getMenuInflater().inflate(R.menu.context_menu_frag1, menu);


            final ApkManager manager = new ApkManager(context);

            menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Toast.makeText(context, "Backup", Toast.LENGTH_SHORT).show();
                    Apk apk = apks.get(getAdapterPosition());
                    manager.backupApk(apk);
                    return true;
                }
            });

            menu.getItem(1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    Toast.makeText(context, "Launch", Toast.LENGTH_SHORT).show();

                    Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(apks.get(getAdapterPosition()).getAppPackage());
                    if (launchIntent != null) {
                        context.startActivity(launchIntent);//null pointer check in case package name was not found
                    }


                    return true;
                }
            });


            menu.getItem(2).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    Toast.makeText(context, "Uninstall", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_DELETE);
                    intent.setData(Uri.parse("package:" + apks.get(getAdapterPosition()).getAppPackage()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);


                    return true;
                }
            });

            menu.getItem(3).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    Toast.makeText(context, "App info", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent.setData(Uri.parse("package:" + apks.get(getAdapterPosition()).getAppPackage()));
                    context.startActivity(intent);


                    return true;
                }
            });
            menu.getItem(4).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    Toast.makeText(context, "Share", Toast.LENGTH_SHORT).show();
                    String packageName = apks.get(getAdapterPosition()).getAppPackage();
                    manager.shareApkLink(packageName);

                    return true;
                }
            });

            menu.getItem(5).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    Toast.makeText(context, "Google play", Toast.LENGTH_SHORT).show();

                    final String appPackageName = apks.get(getAdapterPosition()).getAppPackage();
                    try {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                    return true;
                }
            });


        }


    }


}



