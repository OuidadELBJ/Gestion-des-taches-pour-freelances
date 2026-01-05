package com.example.freelance.data.repository; // Vérifie que ce package correspond bien à ton dossier !

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// IMPORTANT : Assure-toi d'importer TA classe Projet (celle du Membre B)
// import com.example.freelance.data.model.Projet;

public class FirestoreRepository {

    private final FirebaseFirestore db;
    private final String COLLECTION_NAME = "projets";

    public FirestoreRepository() {
        db = FirebaseFirestore.getInstance();
    }

    // Interface pour dire "C'est fini" (Succès/Erreur)
    public interface OnComplete {
        void onSuccess();
        void onError(Exception e);
    }

    // Interface pour recevoir les données
    public interface OnDataReceived {
        void onData(List<Object> projets); // Remplace Object par Projet si tu peux
        void onError(Exception e);
    }

    // FONCTION 1 : Ajouter un projet
    public void ajouterProjet(Object projet, OnComplete callback) {
        // Astuce : Firestore convertit automatiquement tes objets Java en JSON
        // si ta classe Projet a des "Getters" (getTitre, getBudget...)

        db.collection(COLLECTION_NAME)
                .add(projet)
                .addOnSuccessListener(documentReference -> callback.onSuccess())
                .addOnFailureListener(callback::onError);
    }

    // FONCTION 2 : Récupérer tous les projets
    public void getTousLesProjets(OnDataReceived callback) {
        db.collection(COLLECTION_NAME)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Object> liste = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Ici, on transforme le JSON du cloud en Objet Java
                            // Remplace 'Object.class' par 'Projet.class'
                            Object p = document.toObject(Object.class);
                            liste.add(p);
                        }
                        callback.onData(liste);
                    } else {
                        callback.onError(task.getException());
                    }
                });
    }
}