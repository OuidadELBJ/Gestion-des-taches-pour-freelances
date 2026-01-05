package com.example.freelance.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.freelance.data.local.dao.NotificationReminderDao;
import com.example.freelance.data.local.entity.NotificationReminder;
import com.example.freelance.data.local.entity.Projet;
import com.example.freelance.data.local.entity.Tache;
import com.example.freelance.data.local.entity.TimeEntry;
import com.example.freelance.data.local.entity.Paiement;

import com.example.freelance.data.local.dao.ProjetDao;
import com.example.freelance.data.local.dao.TacheDao;
import com.example.freelance.data.local.dao.TimeEntryDao;
import com.example.freelance.data.local.dao.PaiementDao;

import kotlin.jvm.Volatile;

@Database(
        entities = {
                Projet.class,
                Tache.class,
                TimeEntry.class,
                Paiement.class,
                NotificationReminder.class
        },
        version = 2,
        exportSchema = false
)
@TypeConverters(Converters.class)

public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract ProjetDao projetDao();
    public abstract TacheDao tacheDao();
    public abstract TimeEntryDao timeEntryDao();
    public abstract PaiementDao paiementDao();

    public abstract NotificationReminderDao notificationReminderDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "freelance_db"
                            )
                            .fallbackToDestructiveMigration() // <-- Option dev rapide
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}


