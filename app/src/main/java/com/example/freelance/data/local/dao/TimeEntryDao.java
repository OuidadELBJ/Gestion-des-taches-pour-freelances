package com.example.freelance.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.Date;
import com.example.freelance.data.local.entity.TimeEntry;

@Dao
public interface TimeEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TimeEntry timeEntry);

    @Update
    void update(TimeEntry timeEntry);

    // Tous les time entries d’une tâche
    @Query("SELECT * FROM time_entries WHERE taskId = :taskId")
    List<TimeEntry> getByTask(String taskId);

    // Time entries en cours
    @Query("SELECT * FROM time_entries WHERE isRunning = 1 LIMIT 1")
    List<TimeEntry> getRunning();

    // Non synchronisés
    @Query("SELECT * FROM time_entries WHERE isSynced = 0")
    List<TimeEntry> getUnsynced();

    // Arrêter un timer
    @Query("UPDATE time_entries SET isRunning = 0, endTime = :endTime, duration = :duration, lastUpdated = :lastUpdated WHERE idTime = :id")
    void stopTimer(String id, Date endTime, long duration, Date lastUpdated);

    @Delete
    void delete(TimeEntry timeEntry);
    }
