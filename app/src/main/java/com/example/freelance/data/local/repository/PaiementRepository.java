package com.example.freelance.data.local.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.freelance.data.local.AppDatabase;
import com.example.freelance.data.local.dao.PaiementDao;
import com.example.freelance.data.local.entity.Paiement;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PaiementRepository {

    public interface Callback<T> {
        void onResult(T value);
    }

    private final PaiementDao paiementDao;
    private final Executor io;
    private final Handler main;

    public PaiementRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());
        this.paiementDao = db.paiementDao();
        this.io = Executors.newSingleThreadExecutor();
        this.main = new Handler(Looper.getMainLooper());
    }

    // -------------------------------------------------
    // Utilitaire
    // -------------------------------------------------
    public void runOnIo(Runnable r) {
        io.execute(r);
    }

    // -------------------------------------------------
    // CRUD (async)
    // -------------------------------------------------
    public void insert(Paiement paiement) {
        io.execute(() -> paiementDao.insert(paiement));
    }

    public void update(Paiement paiement) {
        io.execute(() -> paiementDao.update(paiement));
    }

    public void delete(Paiement paiement) {
        io.execute(() -> paiementDao.delete(paiement));
    }

    public void markAsSynced(String id, Date lastUpdated) {
        io.execute(() -> paiementDao.markAsSynced(id, lastUpdated));
    }

    // -------------------------------------------------
    // READ (async -> callback sur main thread)
    // -------------------------------------------------
    public void getAll(Callback<List<Paiement>> cb) {
        io.execute(() -> {
            List<Paiement> res = paiementDao.getAll();
            main.post(() -> cb.onResult(res));
        });
    }

    public void getById(String id, Callback<Paiement> cb) {
        io.execute(() -> {
            Paiement res = paiementDao.getById(id);
            main.post(() -> cb.onResult(res));
        });
    }

    public void getByProject(String projectId, Callback<List<Paiement>> cb) {
        io.execute(() -> {
            List<Paiement> res = paiementDao.getByProject(projectId);
            main.post(() -> cb.onResult(res));
        });
    }

    public void getByProjectBetween(String projectId, Date start, Date end, Callback<List<Paiement>> cb) {
        io.execute(() -> {
            List<Paiement> res = paiementDao.getByProjectBetween(projectId, start, end);
            main.post(() -> cb.onResult(res));
        });
    }

    public void getByPaidStatus(boolean paid, Callback<List<Paiement>> cb) {
        io.execute(() -> {
            List<Paiement> res = paiementDao.getByPaidStatus(paid);
            main.post(() -> cb.onResult(res));
        });
    }

    public void getOverdue(Date now, Callback<List<Paiement>> cb) {
        io.execute(() -> {
            List<Paiement> res = paiementDao.getOverdue(now);
            main.post(() -> cb.onResult(res));
        });
    }

    public void getUpcoming(Date now, Callback<List<Paiement>> cb) {
        io.execute(() -> {
            List<Paiement> res = paiementDao.getUpcoming(now);
            main.post(() -> cb.onResult(res));
        });
    }

    public void getTotalPaidByProject(String projectId, Callback<Double> cb) {
        io.execute(() -> {
            Double res = paiementDao.getTotalPaidByProject(projectId);
            main.post(() -> cb.onResult(res));
        });
    }

    public void getTotalPaidByProjectBetween(String projectId, Date start, Date end, Callback<Double> cb) {
        io.execute(() -> {
            Double res = paiementDao.getTotalPaidByProjectBetween(projectId, start, end);
            main.post(() -> cb.onResult(res));
        });
    }

    public void getTotalByProject(String projectId, Callback<Double> cb) {
        io.execute(() -> {
            Double res = paiementDao.getTotalByProject(projectId);
            main.post(() -> cb.onResult(res));
        });
    }

    public void getUnsynced(Callback<List<Paiement>> cb) {
        io.execute(() -> {
            List<Paiement> res = paiementDao.getUnsynced();
            main.post(() -> cb.onResult(res));
        });
    }
}
