package com.example.freelance;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private TextView textProfileName;
    private TextView textProfileRole;

    private MaterialButton buttonEditProfile;
    private View rowAccount, rowNotifications, rowHelp;
    private MaterialButton buttonLogout;
    private ImageView btnBackProfile;

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore fs = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textProfileName = view.findViewById(R.id.textProfileName);
        textProfileRole = view.findViewById(R.id.textProfileRole);

        buttonEditProfile = view.findViewById(R.id.buttonEditProfile);
        rowAccount = view.findViewById(R.id.rowAccount);
        rowNotifications = view.findViewById(R.id.rowNotifications);
        rowHelp = view.findViewById(R.id.rowHelp);
        buttonLogout = view.findViewById(R.id.buttonLogout);
        btnBackProfile = view.findViewById(R.id.btnBackProfile);

        btnBackProfile.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        buttonEditProfile.setOnClickListener(v -> openEditProfileDialog());

        rowAccount.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), AccountActivity.class))
        );

        rowNotifications.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), ui.settings.NotificationsSettingsActivity.class))
        );

        rowHelp.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), HelpActivity.class))
        );

        buttonLogout.setOnClickListener(v -> confirmLogout());

        // ✅ Charger les infos user réelles
        loadUserProfile();
    }

    private void loadUserProfile() {
        FirebaseUser u = auth.getCurrentUser();
        if (u == null) {
            goToLoginClean();
            return;
        }

        fs.collection("users").document(u.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    String nom = doc.getString("nom");
                    String prenom = doc.getString("prenom");
                    String role = doc.getString("role"); // optionnel

                    String fullName = ((prenom == null ? "" : prenom) + " " + (nom == null ? "" : nom)).trim();
                    if (TextUtils.isEmpty(fullName)) fullName = "Utilisateur";

                    textProfileName.setText(fullName);
                    textProfileRole.setText(TextUtils.isEmpty(role) ? "Freelance" : role);
                })
                .addOnFailureListener(e -> {
                    // fallback
                    textProfileName.setText(u.getEmail() != null ? u.getEmail() : "Utilisateur");
                    textProfileRole.setText("Freelance");
                });
    }

    private void openEditProfileDialog() {
        FirebaseUser u = auth.getCurrentUser();
        if (u == null) { goToLoginClean(); return; }

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_profile, null);
        EditText editName = dialogView.findViewById(R.id.editName);
        EditText editRole = dialogView.findViewById(R.id.editRole);

        editName.setText(textProfileName.getText().toString());
        editRole.setText(textProfileRole.getText().toString());

        new AlertDialog.Builder(requireContext(), R.style.AppDialog)
                .setView(dialogView)
                .setNegativeButton("Annuler", (d, w) -> d.dismiss())
                .setPositiveButton("Enregistrer", (d, w) -> {
                    String fullName = editName.getText().toString().trim();
                    String role = editRole.getText().toString().trim();

                    // UI direct
                    if (!TextUtils.isEmpty(fullName)) textProfileName.setText(fullName);
                    if (!TextUtils.isEmpty(role)) textProfileRole.setText(role);

                    // Firestore update
                    // On split fullName -> prenom/nom (simple)
                    String prenom = fullName;
                    String nom = "";
                    int idx = fullName.lastIndexOf(" ");
                    if (idx > 0) {
                        prenom = fullName.substring(0, idx).trim();
                        nom = fullName.substring(idx + 1).trim();
                    }

                    Map<String, Object> up = new HashMap<>();
                    up.put("prenom", prenom);
                    up.put("nom", nom);
                    up.put("role", role);

                    fs.collection("users").document(u.getUid())
                            .update(up);
                })
                .show();
    }

    private void confirmLogout() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Déconnexion")
                .setMessage("Voulez-vous vraiment vous déconnecter ?")
                .setNegativeButton("Annuler", null)
                .setPositiveButton("Se déconnecter", (d, w) -> {
                    FirebaseAuth.getInstance().signOut();

                    java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {
                        com.example.freelance.data.local.AppDatabase
                                .getInstance(requireContext().getApplicationContext())
                                .clearAllTables();

                        requireActivity().runOnUiThread(this::goToLoginClean);
                    });
                })
                .show();
    }

    private void goToLoginClean() {
        Intent i = new Intent(requireContext(), LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        requireActivity().finish();
    }
}