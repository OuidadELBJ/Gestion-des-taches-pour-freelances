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

    //--------------CRUD---------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Paiement paiement);

    @Update
    void update(Paiement paiement);

    @Delete
    void delete(Paiement paiement);

    // --------- READ ---------
    @Query("SELECT * FROM paiements")
    List<Paiement> getAll();

    @Query("SELECT * FROM paiements WHERE idPaiement = :id")
    Paiement getById(String id);

    // --------- METIER ---------
    @Query("SELECT * FROM paiements WHERE projectId = :projectId ORDER BY date DESC")
    List<Paiement> getByProject(String projectId);

    // Paiements payés / non payés
    @Query("SELECT * FROM paiements WHERE paid = :paid")
    List<Paiement> getByPaidStatus(boolean paid);

    // Paiements en retard
    @Query("SELECT * FROM paiements WHERE paid = 0 AND dueDate < :now")
    List<Paiement> getOverdue(Date now);

    // Paiements à venir
    @Query("SELECT * FROM paiements WHERE paid = 0 AND dueDate >= :now ORDER BY dueDate ASC")
    List<Paiement> getUpcoming(Date now);

    // Total payé par projet
    @Query("SELECT SUM(amount) FROM paiements WHERE projectId = :projectId AND paid = 1")
    Double getTotalPaidByProject(String projectId);

    // Total facturé (payé + non payé)
    @Query("SELECT SUM(amount) FROM paiements WHERE projectId = :projectId")
    double getTotalByProject(String projectId);

    @Query("SELECT * FROM paiements WHERE isSynced = 0")
    List<Paiement> getUnsynced();

    @Query("UPDATE paiements SET isSynced = 1, lastUpdated = :lastUpdated WHERE idPaiement = :id")
    void markAsSynced(String id, Date lastUpdated);

}
