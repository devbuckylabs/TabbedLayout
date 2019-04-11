package com.buckylabs.tabbedlayoutexp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.util.List;

public class DialogManager {

    private Context context;
    SharedPreferences preferences;

    public DialogManager(Context context) {
        this.context = context;

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
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


    public void alertDialogRate(String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage(message);
        alertDialog.setTitle(title);

        alertDialog.setPositiveButton("RATE NOW", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("never_rate", true);
                editor.commit();

                String appPackageName = MainActivity.class.getPackage().getName();
                Log.d("Nameeeeeee", appPackageName);
                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                dialog.dismiss();

            }
        });

        alertDialog.setNegativeButton("REMIND ME LATER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        alertDialog.setNeutralButton("NEVER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("never_rate", true);
                editor.commit();
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


                File file = new File(Constant.rootpath, Appname);
                if (file.exists()) {
                    file.delete();
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


    public void alertDialogDeleteMultiple(final List<String> appnames) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage("Do you want to delete selected archive ?");
        alertDialog.setTitle("Confirm Delete");
        alertDialog.create();

        alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                for (String Appname : appnames) {
                    File file = new File(Constant.rootpath, Appname);
                    if (file.exists()) {
                        file.delete();
                    }


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
