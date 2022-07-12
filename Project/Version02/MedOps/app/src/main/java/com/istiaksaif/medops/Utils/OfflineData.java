package com.istiaksaif.medops.Utils;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class OfflineData extends Application {
    public void onCreate(){
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
