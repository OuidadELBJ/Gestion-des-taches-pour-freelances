package com.example.freelance.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;
import java.util.Date;

import com.example.freelance.data.local.entity.Projet;

@Dao
public interface ProjetDao {

    // ----------CRUD-----------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Projet projet);

    @Update
    void update(Projet projet);

    @Delete
    void delete(Projet projet);

    // --------- READ ---------
    @Query("SELECT * FROM projets WHERE idProjet = :id")
    Projet getById(String id);

    @Query("SELECT * FROM projets ORDER BY deadline ASC")
    List<Projet> getAll();

    // --------- FILTRES METIER ---------
    @Query("SELECT * FROM projets WHERE isSynced = 0")
    List<Projet> getUnsynced();

    @Query("SELECT * FROM projets ORDER BY COALESCE(lastUpdated, deadline) DESC LIMIT 1")
    Projet getLatest();

    @Query("UPDATE projets SET status = :status WHERE idProjet = :id")
    void updateStatus(String id, String status);

    @Query("SELECT * FROM projets WHERE status = :status")
    List<Projet> getByStatus(String status);
    @Query("SELECT COUNT(*) FROM projets")
    int countAllProjects();
    @Query("UPDATE projets SET hourlyRate = :rate, lastUpdated = :lastUpdated WHERE idProjet = :id")
    void updateHourlyRate(String id, double rate, Date lastUpdated);

    @Query("SELECT * FROM projets WHERE deadline IS NOT NULL ORDER BY deadline ASC")
    List<Projet> getByDeadline();

    @Query("SELECT * FROM projets WHERE deadline < :now AND status != 'DONE'")
    List<Projet> getOverdue(Date now);


    // ✅ NOTES PROJET (Option B)
    @Query("UPDATE projets SET description = :description, lastUpdated = :lastUpdated WHERE idProjet = :id")
    void updateDescription(String id, String description, Date lastUpdated);
}
