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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Projet projet);

    @Update
    void update(Projet projet);

    @Query("SELECT * FROM projets WHERE idProjet = :id")
    Projet getById(String id);

    @Query("SELECT * FROM projets")
    List<Projet> getAll();

    @Query("SELECT * FROM projets WHERE isSynced = 0")
    List<Projet> getUnsynced();

    @Query("UPDATE projets SET status = :status WHERE idProjet = :id")
    void updateStatus(String id, String status);

    @Query("SELECT * FROM projets WHERE status = :status")
    List<Projet> getByStatus(String status);

    @Query("UPDATE projets SET hourlyRate = :rate, lastUpdated = :lastUpdated WHERE idProjet = :id")
    void updateHourlyRate(String id, double rate, Date lastUpdated);

    @Delete
    void delete(Projet projet);
}
