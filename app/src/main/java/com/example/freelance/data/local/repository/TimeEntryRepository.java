package com.example.freelance.data.local.repository;

import android.content.Context;

import com.example.freelance.data.local.AppDatabase;
import com.example.freelance.data.local.dao.TimeEntryDao;
import com.example.freelance.data.local.entity.TimeEntry;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TimeEntryRepository {

    private final TimeEntryDao timeEntryDao;
    private final Executor executor;

    public TimeEntryRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        this.timeEntryDao = database.timeEntryDao();
        this.executor = Executors.newSingleThreadExecutor();
    }

    // -------------------------
    // CRUD
    // -------------------------

    public void insert(TimeEntry entry) {
        executor.execute(() -> timeEntryDao.insert(entry));
    }

    public void update(TimeEntry entry) {
        executor.execute(() -> timeEntryDao.update(entry));
    }

    public void delete(TimeEntry entry) {
        executor.execute(() -> timeEntryDao.delete(entry));
    }

    //---------------------------
    //READ
    //--------------------------

    public List<TimeEntry> getAll() {
        return timeEntryDao.getAll();
    }

    public TimeEntry getById(String id) {
        return timeEntryDao.getById(id);
    }

    // -------------------------
    // Queries métier
    // -------------------------

    public List<TimeEntry> getByTask(String taskId) {
        return timeEntryDao.getByTask(taskId);
    }

    public TimeEntry getRunningTimeEntry() {
        List<TimeEntry> running = timeEntryDao.getRunning();
        return running.isEmpty() ? null : running.get(0);
    }
    public List<TimeEntry> getUnsynced() {
        return timeEntryDao.getUnsynced();
    }

    public TimeEntry getLastByTask(String taskId) {
        return timeEntryDao.getLastByTask(taskId);
    }

    //-----------------------
    //Actions Timer
    //-----------------------

    public void stopTimer(String id, Date endTime, long duration, Date lastUpdated) {
        executor.execute(() -> timeEntryDao.stopTimer(id, endTime, duration, lastUpdated));
    }

    public void pauseTimer(String id, Date lastUpdated) {
        executor.execute(() -> timeEntryDao.pauseTimer(id, lastUpdated));
    }

    public void resumeTimer(String id, long pausedAccumulated, Date lastUpdated) {
        executor.execute(() -> timeEntryDao.resumeTimer(id, pausedAccumulated, lastUpdated));
    }
}
