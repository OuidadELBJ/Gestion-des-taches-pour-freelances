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

    //--------------CRUD------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Tache tache);

    @Update
    void update(Tache tache);

    @Delete
    void delete(Tache tache);

    // --------- READ ---------
    @Query("SELECT * FROM taches")
    List<Tache> getAll();
    @Query("SELECT * FROM taches " +
            "WHERE deadline IS NOT NULL AND deadline <= :limit AND status != 'DONE' " +
            "ORDER BY deadline ASC LIMIT 2")
    List<com.example.freelance.data.local.entity.Tache> getUrgentTasks(Date limit);

    @Query("SELECT COUNT(*) FROM taches " +
            "WHERE deadline IS NOT NULL AND deadline <= :limit AND status != 'DONE'")
    int countUrgentTasks(Date limit);
    @Query("SELECT * FROM taches WHERE idTache = :id")
    Tache getById(String id);

    // --------- METIER ---------

    // Toutes les tâches d’un projet
    @Query("SELECT * FROM taches WHERE projectId = :projectId")
    List<Tache> getByProject(String projectId);

    // Tâches non synchronisées
    @Query("SELECT * FROM taches WHERE isSynced = 0")
    List<Tache> getUnsynced();

    // Mise à jour du statut (ex: TODO, DONE)
    @Query("UPDATE taches SET status = :status, lastUpdated = :lastUpdated WHERE idTache = :id")
    void updateStatus(String id, String status, Date lastUpdated);

    @Query("SELECT * FROM taches WHERE status = :status")
    List<Tache> getByStatus(String status);

    // Tâches avec deadline (tri)
    @Query("SELECT * FROM taches WHERE deadline IS NOT NULL ORDER BY deadline ASC")
    List<Tache> getByDeadline();

    // Tâches en retard
    @Query("SELECT * FROM taches WHERE deadline < :now AND status != 'DONE'")
    List<Tache> getOverdue(Date now);


}
