package com.example.freelance.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;
import java.util.Date;
import com.example.freelance.data.local.entity.Paiement;

@Dao
public interface PaiementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Paiement paiement);

    @Update
    void update(Paiement paiement);

    @Query("SELECT * FROM paiements WHERE projectId = :projectId ORDER BY date DESC")
    List<Paiement> getByProject(String projectId);

    // Total payé pour un projet
    @Query("SELECT SUM(amount) FROM paiements WHERE projectId = :projectId")
    double getTotalByProject(String projectId);

    @Query("SELECT * FROM paiements WHERE isSynced = 0")
    List<Paiement> getUnsynced();

    @Query("UPDATE paiements SET isSynced = 1, lastUpdated = :lastUpdated WHERE idPaiement = :id")
    void markAsSynced(String id, Date lastUpdated);

    @Delete
    void delete(Paiement paiement);
}
