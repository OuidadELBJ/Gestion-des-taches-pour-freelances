package com.example.freelance.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;
import java.util.Date;
import com.example.freelance.data.local.entity.Tache;

@Dao
public interface TacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Tache tache);

    @Update
    void update(Tache tache);

    // Toutes les tâches d’un projet
    @Query("SELECT * FROM taches WHERE projectId = :projectId")
    List<Tache> getByProject(String projectId);

    @Query("SELECT * FROM taches")
    List<Tache> getAll();

    // Tâches non synchronisées
    @Query("SELECT * FROM taches WHERE isSynced = 0")
    List<Tache> getUnsynced();

    // Mise à jour du statut (ex: TODO, DONE)
    @Query("UPDATE taches SET status = :status, lastUpdated = :lastUpdated WHERE idTache = :id")
    void updateStatus(String id, String status, Date lastUpdated);

    @Query("SELECT * FROM taches WHERE status = :status")
    List<Tache> getByStatus(String status);

    @Delete
    void delete(Tache tache);
}
