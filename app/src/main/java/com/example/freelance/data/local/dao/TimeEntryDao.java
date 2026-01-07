package com.example.freelance.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

import com.example.freelance.data.local.entity.TimeEntry;

@Dao
public interface TimeEntryDao {

    // ----------------- CRUD -----------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TimeEntry timeEntry);
    @Update
    void update(TimeEntry timeEntry);
    @Delete
    void delete(TimeEntry timeEntry);

    // ----------------- READ -----------------
    @Query("SELECT * FROM time_entries")
    List<TimeEntry> getAll();

    @Query("SELECT * FROM time_entries WHERE idTime = :id")
    TimeEntry getById(String id);

    // ----------------- METIER -----------------

    // ✅ Tous les time entries d’une tâche (triés du + récent au + ancien)
    @Query("SELECT * FROM time_entries WHERE taskId = :taskId ORDER BY startTime DESC")
    List<TimeEntry> getByTask(String taskId);

    // ✅ Toutes les entrées d’un projet (triées)
    @Query("SELECT * FROM time_entries WHERE projectId = :projectId ORDER BY startTime DESC")
    List<TimeEntry> getByProject(String projectId);

    // ✅ Total durée d’une tâche
    @Query("SELECT COALESCE(SUM(duration), 0) FROM time_entries WHERE taskId = :taskId")
    long getTotalTask(String taskId);

    @Query("SELECT COALESCE(SUM(duration), 0) FROM time_entries " +
            "WHERE startTime >= :start AND startTime < :end " +
            "AND endTime IS NOT NULL AND duration > 0")
    long sumDuration(Date start, Date end);

    // ✅ Total durée d’un projet
    @Query("SELECT COALESCE(SUM(duration), 0) FROM time_entries WHERE projectId = :projectId")
    long getTotalProject(String projectId);

    // Time entry en cours
    @Query("SELECT * FROM time_entries WHERE isRunning = 1 LIMIT 1")
    List<TimeEntry> getRunning();

    // Non synchronisés
    @Query("SELECT * FROM time_entries WHERE isSynced = 0")
    List<TimeEntry> getUnsynced();

    // Dernière entrée d’une tâche
    @Query(
            "SELECT * FROM time_entries " +
                    "WHERE taskId = :taskId " +
                    "ORDER BY startTime DESC " +
                    "LIMIT 1"
    )
    TimeEntry getLastByTask(String taskId);

    // ----------------- ACTIONS TIMER -----------------

    // Arrêter un timer
    @Query("UPDATE time_entries SET isRunning = 0, endTime = :endTime, duration = :duration, lastUpdated = :lastUpdated WHERE idTime = :id")
    void stopTimer(String id, Date endTime, long duration, Date lastUpdated);

    // Pause timer
    @Query(
            "UPDATE time_entries " +
                    "SET isPaused = 1, " +
                    "lastUpdated = :lastUpdated " +
                    "WHERE idTime = :id"
    )
    void pauseTimer(String id, Date lastUpdated);

    // Resume timer
    @Query(
            "UPDATE time_entries " +
                    "SET isPaused = 0, " +
                    "pausedAccumulated = :pausedAccumulated, " +
                    "lastUpdated = :lastUpdated " +
                    "WHERE idTime = :id"
    )
    void resumeTimer(String id, long pausedAccumulated, Date lastUpdated);
}
