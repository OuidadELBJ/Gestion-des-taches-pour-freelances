package com.example.freelance.data.local.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.freelance.data.local.AppDatabase;
import com.example.freelance.data.local.dao.TimeEntryDao;
import com.example.freelance.data.local.entity.TimeEntry;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TimeEntryRepository {

    public interface Callback<T> { void onResult(T value); }

    private final TimeEntryDao dao;
    private final Executor io = Executors.newSingleThreadExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());

    public TimeEntryRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());
        this.dao = db.timeEntryDao();
    }

    // CRUD
    public void insert(TimeEntry entry) { io.execute(() -> dao.insert(entry)); }
    public void update(TimeEntry entry) { io.execute(() -> dao.update(entry)); }
    public void delete(TimeEntry entry) { io.execute(() -> dao.delete(entry)); }

    // READ async
    public void getByTask(String taskId, Callback<List<TimeEntry>> cb) {
        io.execute(() -> {
            List<TimeEntry> res = dao.getByTask(taskId);
            main.post(() -> cb.onResult(res));
        });
    }

    public void getTotalTask(String taskId, Callback<Long> cb) {
        io.execute(() -> {
            long res = dao.getTotalTask(taskId);
            main.post(() -> cb.onResult(res));
        });
    }

    public void getTotalProject(String projectId, Callback<Long> cb) {
        io.execute(() -> {
            long res = dao.getTotalProject(projectId);
            main.post(() -> cb.onResult(res));
        });
    }

    // (tes actions timer restent si tu veux)
    public void stopTimer(String id, Date endTime, long duration, Date lastUpdated) {
        io.execute(() -> dao.stopTimer(id, endTime, duration, lastUpdated));
    }

    public void pauseTimer(String id, Date lastUpdated) {
        io.execute(() -> dao.pauseTimer(id, lastUpdated));
    }

    public void resumeTimer(String id, long pausedAccumulated, Date lastUpdated) {
        io.execute(() -> dao.resumeTimer(id, pausedAccumulated, lastUpdated));
    }
}
