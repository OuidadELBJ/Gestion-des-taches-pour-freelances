package com.example.freelance.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

import com.example.freelance.data.local.entity.Paiement;

@Dao
public interface PaiementDao {

    // -------------------------
    // CRUD
    // -------------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Paiement paiement);
    @Query("SELECT IFNULL(SUM(amount), 0) FROM paiements WHERE paid = 1 AND date BETWEEN :start AND :end")
    double sumPaidInPeriod(Date start, Date end);
    @Query("SELECT IFNULL(SUM(amount), 0) FROM paiements WHERE date BETWEEN :start AND :end")
    double sumAllInPeriod(Date start, Date end);
    @Update
    void update(Paiement paiement);

    @Delete
    void delete(Paiement paiement);

    // -------------------------
    // READ
    // -------------------------
    @Query("SELECT * FROM paiements ORDER BY date DESC")
    List<Paiement> getAll();

    @Query("SELECT * FROM paiements WHERE idPaiement = :id LIMIT 1")
    Paiement getById(String id);

    // -------------------------
    // METIER
    // -------------------------
    @Query("SELECT * FROM paiements WHERE projectId = :projectId ORDER BY date DESC")
    List<Paiement> getByProject(String projectId);

    // ✅ filtre par période (mois/année)
    @Query("SELECT * FROM paiements WHERE projectId = :projectId AND date BETWEEN :start AND :end ORDER BY date DESC")
    List<Paiement> getByProjectBetween(String projectId, Date start, Date end);

    // Paiements payés / non payés
    @Query("SELECT * FROM paiements WHERE paid = :paid ORDER BY date DESC")
    List<Paiement> getByPaidStatus(boolean paid);

    // Paiements en retard
    @Query("SELECT * FROM paiements WHERE paid = 0 AND dueDate < :now ORDER BY dueDate ASC")
    List<Paiement> getOverdue(Date now);

    // Paiements à venir
    @Query("SELECT * FROM paiements WHERE paid = 0 AND dueDate >= :now ORDER BY dueDate ASC")
    List<Paiement> getUpcoming(Date now);

    // ✅ Total payé par projet
    @Query("SELECT SUM(amount) FROM paiements WHERE projectId = :projectId AND paid = 1")
    Double getTotalPaidByProject(String projectId);

    // ✅ Total payé par projet sur période (mois)
    @Query("SELECT SUM(amount) FROM paiements WHERE projectId = :projectId AND paid = 1 AND date BETWEEN :start AND :end")
    Double getTotalPaidByProjectBetween(String projectId, Date start, Date end);

    // Total facturé (payé + non payé)
    @Query("SELECT SUM(amount) FROM paiements WHERE projectId = :projectId")
    Double getTotalByProject(String projectId);

    // -------------------------
    // SYNC
    // -------------------------
    @Query("SELECT * FROM paiements WHERE isSynced = 0")
    List<Paiement> getUnsynced();

    @Query("UPDATE paiements SET isSynced = 1, lastUpdated = :lastUpdated WHERE idPaiement = :id")
    void markAsSynced(String id, Date lastUpdated);
}
