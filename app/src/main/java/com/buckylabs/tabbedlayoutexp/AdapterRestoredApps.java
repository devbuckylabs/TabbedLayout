package com.buckylabs.tabbedlayoutexp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AdapterRestoredApps extends RecyclerView.Adapter<AdapterRestoredApps.ViewHolder> {


    private List<Apk> apks;
    private Context context;


    public AdapterRestoredApps(Context context, List<Apk> apks) {
        this.apks = apks;
        this.context = context;


    }

    @NonNull
    @Override
    public AdapterRestoredApps.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(context).inflate(R.layout.list_item_restore, viewGroup, false);

        return new AdapterRestoredApps.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        final Apk apk = apks.get(i);
        // Log.e("Archive Bind",apk.toString());
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
        TextView appversion;
        TextView appsize;
        TextView appStatus;
        ImageView appIcon;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appName = itemView.findViewById(R.id.app_name_Text_Restore);
            appversion = itemView.findViewById(R.id.app_version_name_text_Restore);
            appsize = itemView.findViewById(R.id.appSize_text_Restore);
            appIcon = itemView.findViewById(R.id.image_Restore);
            checkBox = itemView.findViewById(R.id.checkbox_Restore);
            appStatus = itemView.findViewById(R.id.appStatus_Restore);
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
            ((Activity) context).getMenuInflater().inflate(R.menu.context_menu_frag2, menu);

            final ApkManager manager = new ApkManager(context);
            final DialogManager dialogs = new DialogManager(context);
            menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    Apk apk = apks.get(getAdapterPosition());
                    manager.restoreApk(apk);

                    return true;
                }
            });

            menu.getItem(1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    Apk apk = apks.get(getAdapterPosition());

                    StringBuilder Appname = new StringBuilder();
                    Appname.append(apk.getAppName());
                    Appname.append("  v");
                    Appname.append(apk.getAppVersionName());

                    StringBuilder details = new StringBuilder();
                    details.append("Path : ");
                    details.append(Constant.rootpath);
                    details.append(apk.getAppName());
                    details.append("-");
                    details.append(apk.getAppPackage());
                    details.append("-");
                    details.append(apk.getAppVersionName());
                    details.append(".apk");
                    details.append("\n\n");
                    details.append("File Size : ");
                    details.append(apk.getAppSize());
                    details.append("\n\n");
                    details.append("Date : ");
                    details.append(apk.getDate());
                    dialogs.alertDialog(Appname.toString(), details.toString());


                    return true;
                }
            });

            menu.getItem(2).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    Apk apk = apks.get(getAdapterPosition());
                    String Appname = manager.appNameGenerator(apk);
                    alertDialogDelete(Appname);




                    return true;
                }
            });

            menu.getItem(3).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    Apk apk = apks.get(getAdapterPosition());
                    manager.shareApk(apk);

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

        public void alertDialogDelete(final String Appname) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setMessage("Do you want to delete selected archive ?");
            alertDialog.setTitle("Confirm Delete");
            alertDialog.create();

            alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                    File file = new File(Constant.rootpath, Appname);
                    if (file.exists()) {
                        file.delete();
                        notifyItemRemoved(getAdapterPosition());
                    }
                    dialog.dismiss();

                }
            });

            alertDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();

                }
            });
            alertDialog.show();
        }


    }


}



