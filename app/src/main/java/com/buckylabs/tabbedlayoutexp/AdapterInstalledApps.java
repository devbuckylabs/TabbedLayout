package com.buckylabs.tabbedlayoutexp;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AdapterInstalledApps extends RecyclerView.Adapter<AdapterInstalledApps.ViewHolder> {

    List<Apk> apks=new ArrayList<>();
    Context context;

    public AdapterInstalledApps(Context context,List<Apk> apks) {
        this.apks = apks;
        this.context = context;

    }

    @NonNull
    @Override
    public AdapterInstalledApps.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(context).inflate(R.layout.listitem, viewGroup, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull AdapterInstalledApps.ViewHolder viewHolder, int i) {

        final Apk apk = apks.get(i);
       // Log.e("APPPPP",""+apk.getAppName());
        viewHolder.appName.setText(apk.getAppName());
        viewHolder.appIcon.setImageDrawable(apk.getAppIcon());
        viewHolder.appversion.setText("v"+apk.getAppVersionName());
        viewHolder.appsize.setText(apk.getAppSize()+" | "+apk.getDate());
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
            appStatus= itemView.findViewById(R.id.appStatus);
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
               /* Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                animation1.setDuration(4000);
                v.startAnimation(animation1);*/
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

            ((Activity) context).getMenuInflater().inflate(R.menu.popup_menu, menu);

        }
    }



}
