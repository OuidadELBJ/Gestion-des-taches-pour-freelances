package com.example.freelance.data.local.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.freelance.data.local.AppDatabase;
import com.example.freelance.data.local.dao.TacheDao;
import com.example.freelance.data.local.entity.Tache;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TacheRepository {

    public interface Callback<T> { void onResult(T value); }

    private final TacheDao dao;
    private final Executor io = Executors.newSingleThreadExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());

    public TacheRepository(Context context) {
        dao = AppDatabase.getInstance(context.getApplicationContext()).tacheDao();
    }

    // ✅ Insert simple
    public void insert(Tache t) {
        io.execute(() -> dao.insert(t));
    }

    // ✅ Insert + callback (très utile pour revenir après écriture DB)
    public void insert(Tache t, Runnable onDone) {
        io.execute(() -> {
            dao.insert(t);
            if (onDone != null) main.post(onDone);
        });
    }

    public void update(Tache t) {
        io.execute(() -> dao.update(t));
    }

    public void delete(Tache t) {
        io.execute(() -> dao.delete(t));
    }

    public void getByProject(String projectId, Callback<List<Tache>> cb) {
        io.execute(() -> {
            List<Tache> res = dao.getByProject(projectId);
            main.post(() -> cb.onResult(res));
        });
    }

    public void getAll(Callback<List<Tache>> cb) {
        io.execute(() -> {
            List<Tache> res = dao.getAll();
            main.post(() -> cb.onResult(res));
        });
    }

    // ⚠️ sync => seulement thread IO
    public Tache getByIdSync(String id) {
        return dao.getById(id);
    }
}
