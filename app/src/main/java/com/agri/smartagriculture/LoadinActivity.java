package com.agri.smartagriculture;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class LoadinActivity {
    Activity activity;
    AlertDialog dialog;
    LoadinActivity(Activity myactivity){
        activity = myactivity;
    }
    void LoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_dialog,null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }
    void dismissDialog(){
        dialog.dismiss();
    }
}
