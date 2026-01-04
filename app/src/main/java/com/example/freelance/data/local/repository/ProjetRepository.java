package com.example.freelance.data.local.repository;

import android.content.Context;

import com.example.freelance.data.local.AppDatabase;
import com.example.freelance.data.local.dao.ProjetDao;
import com.example.freelance.data.local.entity.Projet;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class ProjetRepository {

    private final ProjetDao projetDao;
    private final Executor executor;

    public ProjetRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        this.projetDao = database.projetDao();
        this.executor = Executors.newSingleThreadExecutor();
    }

    // -------------------------
    // CRUD
    // -------------------------

    public void insert(Projet projet) {
        executor.execute(() -> projetDao.insert(projet));
    }

    public void update(Projet projet) {
        executor.execute(() -> projetDao.update(projet));
    }

    public void delete(Projet projet) {
        executor.execute(() -> projetDao.delete(projet));
    }

    // -------------------------
    // READ
    // -------------------------

    public Projet getById(String id) {
        return projetDao.getById(id);
    }

    public List<Projet> getAll() {
        return projetDao.getAll();
    }

    // -------------------------
    // Requêtes métier
    // -------------------------

    public List<Projet> getUnsynced() {
        return projetDao.getUnsynced();
    }

    public void updateStatus(String id, String status) {
        executor.execute(() -> projetDao.updateStatus(id, status));
    }

    public List<Projet> getByStatus(String status) {
        return projetDao.getByStatus(status);
    }

    public void updateHourlyRate(String id, double rate, Date lastUpdated) {
        executor.execute(() -> projetDao.updateHourlyRate(id, rate, lastUpdated));
    }

    public List<Projet> getByDeadline() {
        return projetDao.getByDeadline();
    }

    public List<Projet> getOverdue(Date now) {
        return projetDao.getOverdue(now);
    }
}
