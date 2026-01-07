package com.example.freelance;

import android.app.Application;
import com.example.freelance.data.local.AppDatabase;

public class FreelanceApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // ✅ init Room singleton (pas de sync ici)
        AppDatabase.getInstance(getApplicationContext());
    }
}