package com.example.freelance.data.local.repository;

import android.content.Context;

import com.example.freelance.data.local.AppDatabase;
import com.example.freelance.data.local.dao.PaiementDao;
import com.example.freelance.data.local.entity.Paiement;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PaiementRepository {

    private final PaiementDao paiementDao;
    private final Executor executor;

    public PaiementRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        this.paiementDao = database.paiementDao();
        this.executor = Executors.newSingleThreadExecutor();
    }

    // -------------------------
    // CRUD
    // -------------------------

    public void insert(Paiement paiement) {
        executor.execute(() -> paiementDao.insert(paiement));
    }

    public void update(Paiement paiement) {
        executor.execute(() -> paiementDao.update(paiement));
    }

    public void delete(Paiement paiement) {
        executor.execute(() -> paiementDao.delete(paiement));
    }

    // -------------------------
    // Queries métier
    // -------------------------

    public List<Paiement> getByProjet(String projetId) {
        return paiementDao.getByProject(projetId);
    }

    public double getTotalByProjet(String projetId) {
        Double total = paiementDao.getTotalByProject(projetId);
        return total != null ? total : 0.0;
    }
    public List<Paiement> getUnsynced() {
        return paiementDao.getUnsynced();
    }

    public void markAsSynced(String id, Date lastUpdated) {
        executor.execute(() -> paiementDao.markAsSynced(id, lastUpdated));
    }
}
