package com.example.freelance.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;

import com.example.freelance.data.local.entity.NotificationReminder;

import java.util.List;

@Dao
public interface NotificationReminderDao {


    // ------------CRUD-----------------


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(NotificationReminder reminder);

    @Update
    void update(NotificationReminder reminder);

    @Delete
    void delete(NotificationReminder reminder);

    // ------------Queries métier-------------
    @Query("SELECT * FROM notifications WHERE id = :id")
    NotificationReminder getById(String id);

    // Rappels par type cible (TASK / PROJECT / PAYMENT)
    @Query("SELECT * FROM notifications WHERE targetType = :type")
    List<NotificationReminder> getByTargetType(String type);

    // Tous les rappels pour une tâche spécifique
    @Query("SELECT * FROM notifications WHERE taskId = :taskId")
    List<NotificationReminder> getByTask(String taskId);

    // Tous les rappels pour un projet spécifique
    @Query("SELECT * FROM notifications WHERE projectId = :projectId")
    List<NotificationReminder> getByProject(String projectId);

    // Rappels pour un paiement spécifique
    @Query("SELECT * FROM notifications WHERE paymentId = :paymentId")
    List<NotificationReminder> getByPayment(String paymentId);

    // Rappels actifs / à exécuter
    @Query("SELECT * FROM notifications WHERE status = :status AND triggerAtMillis <= :currentTime")
    List<NotificationReminder> getPendingReminders(String status, long currentTime);

    // Marquer une notification comme exécutée / FIRED
    @Query("UPDATE notifications SET status = :status, lastUpdatedMillis = :lastUpdatedMillis WHERE id = :id")
    void markAsStatus(String id, String status, long lastUpdatedMillis);

    // Rappels non synchronisés
    @Query("SELECT * FROM notifications WHERE isSynced = 0")
    List<NotificationReminder> getUnsynced();
}
