package com.example.freelance.data.local.repository;

import android.content.Context;

import com.example.freelance.data.local.AppDatabase;
import com.example.freelance.data.local.dao.TacheDao;
import com.example.freelance.data.local.entity.Tache;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TacheRepository {

    private final TacheDao tacheDao;
    private final Executor executor;

    public TacheRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        this.tacheDao = database.tacheDao();
        this.executor = Executors.newSingleThreadExecutor();
    }

    // -------------------------
    // CRUD
    // -------------------------

    public void insert(Tache tache) {
        executor.execute(() -> tacheDao.insert(tache));
    }

    public void update(Tache tache) {
        executor.execute(() -> tacheDao.update(tache));
    }

    public void delete(Tache tache) {
        executor.execute(() -> tacheDao.delete(tache));
    }

    // -------------------------
    // READ
    // -------------------------

    public List<Tache> getAll() {
        return tacheDao.getAll();
    }

    public Tache getById(String id) {
        return tacheDao.getById(id);
    }

    // -------------------------
    // Requêtes métier
    // -------------------------

    public List<Tache> getByProject(String projectId) {
        return tacheDao.getByProject(projectId);
    }

    public List<Tache> getUnsynced() {
        return tacheDao.getUnsynced();
    }

    public void updateStatus(String id, String status, Date lastUpdated) {
        executor.execute(() -> tacheDao.updateStatus(id, status, lastUpdated));
    }

    public List<Tache> getByStatus(String status) {
        return tacheDao.getByStatus(status);
    }

    public List<Tache> getByDeadline() {
        return tacheDao.getByDeadline();
    }

    public List<Tache> getOverdue(Date now) {
        return tacheDao.getOverdue(now);
    }
}
