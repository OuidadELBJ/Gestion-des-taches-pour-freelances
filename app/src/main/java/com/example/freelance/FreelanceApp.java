package com.example.freelance;

import android.app.Application;

import androidx.room.Room;

import com.example.freelance.data.local.AppDatabase;

public class FreelanceApp extends Application {

    private static AppDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();

        db = Room.databaseBuilder(
                        getApplicationContext(),
                        AppDatabase.class,
                        "freelance.db"
                )
                .fallbackToDestructiveMigration() // OK en dev, à enlever plus tard
                .build();
    }

    public static AppDatabase db() {
        return db;
    }
}
