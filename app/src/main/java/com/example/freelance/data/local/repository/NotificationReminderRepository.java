package com.example.freelance.data.local.repository;

import android.content.Context;

import com.example.freelance.data.local.AppDatabase;
import com.example.freelance.data.local.dao.NotificationReminderDao;
import com.example.freelance.data.local.entity.NotificationReminder;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NotificationReminderRepository {

    private final NotificationReminderDao reminderDao;
    private final Executor executor;

    public NotificationReminderRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.reminderDao = db.notificationReminderDao();
        this.executor = Executors.newSingleThreadExecutor();
    }

    // -------------------------
    // CRUD
    // -------------------------

    public void insert(NotificationReminder reminder) {
        executor.execute(() -> reminderDao.insert(reminder));
    }

    public void update(NotificationReminder reminder) {
        executor.execute(() -> reminderDao.update(reminder));
    }

    public void delete(NotificationReminder reminder) {
        executor.execute(() -> reminderDao.delete(reminder));
    }

    // -------------------------
    // Queries métier
    // -------------------------

    public NotificationReminder getById(String id) {
        return reminderDao.getById(id);
    }

    public List<NotificationReminder> getByTargetType(String type) {
        return reminderDao.getByTargetType(type);
    }

    public List<NotificationReminder> getByProject(String projectId) {
        return reminderDao.getByProject(projectId);
    }

    public List<NotificationReminder> getByTask(String taskId) {
        return reminderDao.getByTask(taskId);
    }

    public List<NotificationReminder> getByPayment(String paymentId) {
        return reminderDao.getByPayment(paymentId);
    }

    /**
     * Rappels programmés et prêts à être exécutés
     * @param status exemple: NotificationReminder.STATUS_SCHEDULED
     * @param currentTime System.currentTimeMillis()
     */
    public List<NotificationReminder> getPendingReminders(String status, long currentTime) {
        return reminderDao.getPendingReminders(status, currentTime);
    }

    /**
     * Marquer une notification comme FIRED ou CANCELED
     */
    public void markAsStatus(String id, String status, long lastUpdatedMillis) {
        executor.execute(() -> reminderDao.markAsStatus(id, status, lastUpdatedMillis));
    }

    /**
     * Notifications non synchronisées pour sync Firebase
     */
    public List<NotificationReminder> getUnsynced() {
        return reminderDao.getUnsynced();
    }
}
