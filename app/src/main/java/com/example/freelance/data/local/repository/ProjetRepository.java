package com.example.freelance.data.local.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.freelance.data.local.AppDatabase;
import com.example.freelance.data.local.dao.ProjetDao;
import com.example.freelance.data.local.entity.Projet;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ProjetRepository {

    public interface Callback<T> { void onResult(T value); }

    private final ProjetDao dao;
    private final Executor io = Executors.newSingleThreadExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());

    public ProjetRepository(Context context) {
        dao = AppDatabase.getInstance(context.getApplicationContext()).projetDao();
    }

    public void insert(Projet p) { io.execute(() -> dao.insert(p)); }
    public void update(Projet p) { io.execute(() -> dao.update(p)); }
    public void delete(Projet p) { io.execute(() -> dao.delete(p)); }

    public void getAll(Callback<List<Projet>> cb) {
        io.execute(() -> {
            List<Projet> res = dao.getAll();
            main.post(() -> cb.onResult(res));
        });
    }

    public void getById(String id, Callback<Projet> cb) {
        io.execute(() -> {
            Projet res = dao.getById(id);
            main.post(() -> cb.onResult(res));
        });
    }

    // ✅ notes en Room
    public void updateDescription(String id, String description, Date lastUpdated) {
        io.execute(() -> dao.updateDescription(id, description, lastUpdated));
    }

    // Sync (IO thread only)
    public Projet getByIdSync(String id) { return dao.getById(id); }
}
