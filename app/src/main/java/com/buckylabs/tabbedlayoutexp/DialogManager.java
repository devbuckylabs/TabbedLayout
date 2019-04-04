package com.buckylabs.tabbedlayoutexp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;

import java.io.File;
import java.util.List;

public class DialogManager {

    private Context context;
    private String rootPath;

    public DialogManager(Context context) {
        this.context = context;
        rootPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/App_Backup_Pro/";
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
                    File file = new File(rootPath, Appname);
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
