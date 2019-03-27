package com.buckylabs.tabbedlayoutexp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

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


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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


    }
}
