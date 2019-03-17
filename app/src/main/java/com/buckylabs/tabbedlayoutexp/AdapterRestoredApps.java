package com.buckylabs.tabbedlayoutexp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AdapterRestoredApps  extends RecyclerView.Adapter<AdapterRestoredApps.ViewHolder>{


    List<Apk> apks;
    Context context;

    public AdapterRestoredApps(Context context,List<Apk> apks) {
        this.apks = apks;
        this.context = context;

    }

    @NonNull
    @Override
    public AdapterRestoredApps.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v= LayoutInflater.from(context).inflate(R.layout.list_item_restore,viewGroup,false);

        return new AdapterRestoredApps.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        final Apk apk = apks.get(i);
       // Log.e("Archive Bind",apk.toString());
        viewHolder.appName.setText(apk.getAppName());
        viewHolder.appIcon.setImageDrawable(apk.getAppIcon());
        viewHolder.checkBox.setChecked(apk.isChecked());

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
        ImageView appIcon;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appName = itemView.findViewById(R.id.textViewRestore);
            appIcon = itemView.findViewById(R.id.imageViewRestore);
            checkBox = itemView.findViewById(R.id.checkBoxRestore);
            itemView.setOnClickListener(this);
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


    }
}
