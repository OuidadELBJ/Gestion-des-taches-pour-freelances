package com.example.freelance.data;

import android.content.Context;

import com.example.freelance.data.local.repository.NotificationReminderRepository;
import com.example.freelance.data.local.repository.PaiementRepository;
import com.example.freelance.data.local.repository.ProjetRepository;
import com.example.freelance.data.local.repository.TacheRepository;
import com.example.freelance.data.local.repository.TimeEntryRepository;

public final class DataProvider {

    private static Context appContext;

    private static ProjetRepository projetRepo;
    private static TacheRepository tacheRepo;
    private static PaiementRepository paiementRepo;
    private static TimeEntryRepository timeEntryRepo;
    private static NotificationReminderRepository notifRepo;

    private DataProvider() {}

    /** À appeler UNE fois au démarrage (Application.onCreate) */
    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    private static Context ctx() {
        if (appContext == null) {
            throw new IllegalStateException("DataProvider.init(context) n'a pas été appelé !");
        }
        return appContext;
    }

    public static ProjetRepository projets() {
        if (projetRepo == null) projetRepo = new ProjetRepository(ctx());
        return projetRepo;
    }

    public static TacheRepository taches() {
        if (tacheRepo == null) tacheRepo = new TacheRepository(ctx());
        return tacheRepo;
    }

    public static PaiementRepository paiements() {
        if (paiementRepo == null) paiementRepo = new PaiementRepository(ctx());
        return paiementRepo;
    }

    public static TimeEntryRepository timeEntries() {
        if (timeEntryRepo == null) timeEntryRepo = new TimeEntryRepository(ctx());
        return timeEntryRepo;
    }

    public static NotificationReminderRepository notifications() {
        if (notifRepo == null) notifRepo = new NotificationReminderRepository(ctx());
        return notifRepo;
    }
}
