package com.example.freelance.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

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
                Paiement.class
        },
        version = 1,
        exportSchema = false
)
@TypeConverters(Converters.class)

public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract ProjetDao projetDao();
    public abstract TacheDao tacheDao();
    public abstract TimeEntryDao timeEntryDao();
    public abstract PaiementDao paiementDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "freelance_db"
            ).build();
        }
        return INSTANCE;
    }
}


