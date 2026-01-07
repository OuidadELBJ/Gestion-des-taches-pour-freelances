package com.example.freelance.data.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;

import com.example.freelance.data.local.AppDatabase;
import com.example.freelance.data.local.entity.Projet;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import com.google.firebase.auth.FirebaseAuth;

import notifications.ReminderScheduler;

public class FirestoreRepository {

    private static final String TAG = "FirestoreRepository";
    private static final String COLLECTION_USERS = "users";
    private static final String SUBCOLL_PROJETS = "projets";

    private String userProjetsPathOrNull() {
        String uid = FirebaseAuth.getInstance().getUid();
        return (uid == null) ? null : (COLLECTION_USERS + "/" + uid + "/" + SUBCOLL_PROJETS);
    }
    private final FirebaseFirestore fs;

    public FirestoreRepository() {
        fs = FirebaseFirestore.getInstance();
    }

    // ✅ Callback simple
    public interface OnComplete {
        void onSuccess();
        void onError(Exception e);
    }

    /**
     * ✅ PUSH: Room -> Firestore
     * Important: on utilise document(idProjet) pour que l'id soit stable (sinon .add() crée un id random)
     */

    public void deleteProjet(@NonNull String projectId, @NonNull OnComplete cb) {
        String path = userProjetsPathOrNull();
        if (path == null) {
            cb.onError(new IllegalStateException("User not logged in"));
            return;
        }

        fs.collection(path)
                .document(projectId)
                .delete()
                .addOnSuccessListener(v -> cb.onSuccess())
                .addOnFailureListener(cb::onError);
    }
    public void upsertProjet(@NonNull Projet p, @NonNull OnComplete cb) {
        String path = userProjetsPathOrNull();
        if (path == null) {
            cb.onError(new IllegalStateException("User not logged in"));
            return;
        }

        Map<String, Object> data = projetToMap(p);

        fs.collection(path)
                .document(p.getIdProjet())
                .set(data)
                .addOnSuccessListener(v -> cb.onSuccess())
                .addOnFailureListener(cb::onError);
    }

    /**
     * ✅ PULL: Firestore -> Room (+ linkage notifications)
     * - lit tous les projets Firestore
     * - upsert dans Room
     * - déclenche ReminderScheduler.onProjectUpsert() si nécessaire
     */
    public void syncProjetsToRoom(@NonNull Context context, @NonNull OnComplete cb) {
        String path = userProjetsPathOrNull();
        if (path == null) {
            cb.onError(new IllegalStateException("User not logged in"));
            return;
        }

        Context app = context.getApplicationContext();
        AppDatabase db = AppDatabase.getInstance(app);

        fs.collection(path)
                .get()
                .addOnSuccessListener(snapshot -> {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        try {
                            for (QueryDocumentSnapshot doc : snapshot) {

                                Projet entity = docToProjetEntity(doc);

                                // ✅ upsert Room
                                db.projetDao().insert(entity);

                                // ✅ LINKAGE reminders
                                ReminderScheduler.onProjectUpsert(app, entity);
                            }

                            new Handler(Looper.getMainLooper()).post(cb::onSuccess);

                        } catch (Exception e) {
                            Log.e(TAG, "syncProjetsToRoom failed", e);
                            new Handler(Looper.getMainLooper()).post(() -> cb.onError(e));
                        }
                    });
                })
                .addOnFailureListener(cb::onError);
    }

    // -------------------------
    // Helpers
    // -------------------------

    private Map<String, Object> projetToMap(@NonNull Projet p) {
        Map<String, Object> m = new HashMap<>();

        m.put("idProjet", p.getIdProjet());
        m.put("name", p.getName());
        m.put("description", p.getDescription());
        m.put("clientName", p.getClientName());
        m.put("clientEmail", p.getClientEmail());
        m.put("clientPhone", p.getClientPhone());

        m.put("status", p.getStatus());

        // dates
        m.put("startDate", p.getStartDate());
        m.put("endDate", p.getEndDate());
        m.put("deadline", p.getDeadline());

        // billing
        m.put("billingType", p.getBillingType());
        m.put("budgetAmount", p.getBudgetAmount());
        m.put("rate", p.getRate());
        m.put("estimatedHours", p.getEstimatedHours());
        m.put("estimatedDays", p.getEstimatedDays());
        m.put("estimatedMonths", p.getEstimatedMonths());

        // reminders
        m.put("reminderEnabled", p.isReminderEnabled());
        m.put("useDefaultOffsets", p.isUseDefaultOffsets());
        m.put("customOffsets", p.getCustomOffsets());

        // sync flags (si tu utilises)
        m.put("isSynced", true);
        m.put("lastUpdated", new Date());

        return m;
    }

    /**
     * Convertit un document Firestore -> Projet (Room)
     * ⚠️ ton entity Projet a un constructeur complet, donc on remplit des defaults.
     */
    private Projet docToProjetEntity(QueryDocumentSnapshot doc) {

        Map<String, Object> data = doc.getData();

        // id stable
        String id = doc.getId();

        String name = str(data.get("name"), "");
        String description = str(data.get("description"), null);
        String clientName = str(data.get("clientName"), "");
        String clientEmail = str(data.get("clientEmail"), "");
        String clientPhone = str(data.get("clientPhone"), "");
        String status = str(data.get("status"), "En cours");

        int billingType = intVal(data.get("billingType"), 0);
        double budgetAmount = dbl(data.get("budgetAmount"), 0.0);
        double rate = dbl(data.get("rate"), 0.0);
        double estimatedHours = dbl(data.get("estimatedHours"), 0.0);
        double estimatedDays = dbl(data.get("estimatedDays"), 0.0);
        double estimatedMonths = dbl(data.get("estimatedMonths"), 0.0);

        Date startDate = dateVal(data.get("startDate"));
        Date endDate = dateVal(data.get("endDate"));
        Date deadline = dateVal(data.get("deadline"));

        boolean reminderEnabled = bool(data.get("reminderEnabled"), false);
        boolean useDefaultOffsets = bool(data.get("useDefaultOffsets"), true);
        String customOffsets = str(data.get("customOffsets"), "");

        boolean isSynced = true;
        Date lastUpdated = dateVal(data.get("lastUpdated"));
        if (lastUpdated == null) lastUpdated = new Date();

        // ✅ construit entity (selon TON constructeur)
        return new Projet(
                id,
                name,
                description,
                clientName,
                billingType,
                budgetAmount,
                rate,
                estimatedHours,
                estimatedDays,
                estimatedMonths,
                deadline,
                reminderEnabled,
                useDefaultOffsets,
                customOffsets,
                status,
                isSynced,
                lastUpdated,
                clientEmail,
                clientPhone
        );
    }

    private static String str(Object o, String def) {
        return (o == null) ? def : String.valueOf(o);
    }

    private static boolean bool(Object o, boolean def) {
        if (o == null) return def;
        if (o instanceof Boolean) return (Boolean) o;
        return def;
    }

    private static int intVal(Object o, int def) {
        if (o == null) return def;
        if (o instanceof Number) return ((Number) o).intValue();
        try { return Integer.parseInt(String.valueOf(o)); } catch (Exception e) { return def; }
    }

    private static double dbl(Object o, double def) {
        if (o == null) return def;
        if (o instanceof Number) return ((Number) o).doubleValue();
        try { return Double.parseDouble(String.valueOf(o)); } catch (Exception e) { return def; }
    }

    private static Date dateVal(Object o) {
        if (o == null) return null;
        if (o instanceof Date) return (Date) o;
        if (o instanceof Timestamp) return ((Timestamp) o).toDate();
        if (o instanceof Long) return new Date((Long) o);
        return null;
    }
}