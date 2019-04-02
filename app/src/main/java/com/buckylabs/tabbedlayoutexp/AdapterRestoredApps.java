package com.buckylabs.tabbedlayoutexp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
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


    List<Apk> apks;
    Context context;
    String rootPath;

    public AdapterRestoredApps(Context context, List<Apk> apks) {
        this.apks = apks;
        this.context = context;

    }

    @NonNull
    @Override
    public AdapterRestoredApps.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(context).inflate(R.layout.list_item_restore, viewGroup, false);
        rootPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/App_Backup_Pro/";
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


            menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    Apk apk = apks.get(getAdapterPosition());
                    InstallApplication(apk);


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
                    details.append(rootPath);
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

                    alertDialog(Appname.toString(), details.toString());


                    return true;
                }
            });

            menu.getItem(2).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    Apk apk = apks.get(getAdapterPosition());
                    StringBuilder Appname = new StringBuilder();
                    Appname.append(apk.getAppName());
                    Appname.append("-");
                    Appname.append(apk.getAppPackage());
                    Appname.append("-");
                    Appname.append(apk.getAppVersionName());
                    Appname.append(".apk");
                    alertDialogDelete(Appname.toString());


                    return true;
                }
            });

            menu.getItem(3).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    shareApk();

                    return true;
                }
            });

            menu.getItem(4).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    Toast.makeText(context, "Share", Toast.LENGTH_SHORT).show();

                    StringBuilder marketLink = new StringBuilder();
                    marketLink.append("https://play.google.com/store/apps/details?id=");
                    marketLink.append(apks.get(getAdapterPosition()).getAppPackage());

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");

                    shareIntent.putExtra(Intent.EXTRA_TEXT, marketLink.toString());
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(Intent.createChooser(shareIntent, "Share via"));

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


        public void InstallApplication(Apk apk) {

            StringBuilder Appname = new StringBuilder();
            Appname.append(apk.getAppName());
            Appname.append("-");
            Appname.append(apk.getAppPackage());
            Appname.append("-");
            Appname.append(apk.getAppVersionName());

            File file = new File(rootPath, Appname + ".apk");

            if (Build.VERSION.SDK_INT >= 24) {

                Uri path = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
                Intent intent = new Intent(Intent.ACTION_VIEW).setDataAndType(path,
                        "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            } else {

                Uri uri = Uri.fromFile(file);
                Intent intent = new Intent(Intent.ACTION_VIEW).setDataAndType(uri,
                        "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);


            }


        }


        public void alertDialog(String title, String message) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setMessage(message);
            alertDialog.setTitle(title);
            alertDialog.create();

            alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();

                }
            });
            alertDialog.show();
        }

        public void alertDialogDelete(final String Appname) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setMessage("Do you want to delete selected archive ?");
            alertDialog.setTitle("Confirm Delete");
            alertDialog.create();

            alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                    File file = new File(rootPath, Appname);
                    if (file.exists()) {
                        file.delete();
                    }
                    notifyItemRemoved(getAdapterPosition());
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

        public void shareApk() {

            Apk apk = apks.get(getAdapterPosition());
            File file = new File(apk.getSourceDirectory());
            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/vnd.android.package-archive");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, apk.getAppName() + ".apk");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this amazing app");
            shareIntent.putExtra(Intent.EXTRA_EMAIL, "Checkout my app");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            context.startActivity(Intent.createChooser(shareIntent, "Share app via"));


        }
    }
}
