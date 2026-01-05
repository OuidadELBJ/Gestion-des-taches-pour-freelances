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
    // READ
    // -------------------------

    public List<Paiement> getAll() {
        return paiementDao.getAll();
    }

    public Paiement getById(String id) {
        return paiementDao.getById(id);
    }

    // -------------------------
    // Requêtes métier
    // -------------------------

    public List<Paiement> getByProject(String projectId) {
        return paiementDao.getByProject(projectId);
    }

    public List<Paiement> getByPaidStatus(boolean paid) {
        return paiementDao.getByPaidStatus(paid);
    }

    public List<Paiement> getOverdue(Date now) {
        return paiementDao.getOverdue(now);
    }

    public List<Paiement> getUpcoming(Date now) {
        return paiementDao.getUpcoming(now);
    }

    public Double getTotalPaidByProject(String projectId) {
        return paiementDao.getTotalPaidByProject(projectId);
    }

    public double getTotalByProject(String projectId) {
        return paiementDao.getTotalByProject(projectId);
    }

    public List<Paiement> getUnsynced() {
        return paiementDao.getUnsynced();
    }

    public void markAsSynced(String id, Date lastUpdated) {
        executor.execute(() -> paiementDao.markAsSynced(id, lastUpdated));
    }
}
