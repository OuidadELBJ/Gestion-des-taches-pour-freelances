package com.example.freelance;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;

public class ProfileFragment extends Fragment {

    private TextView textProfileName;
    private TextView textProfileRole;

    private MaterialButton buttonEditProfile;
    private View rowAccount, rowNotifications, rowHelp;
    private MaterialButton buttonLogout;
    private ImageView btnBackProfile;

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

        // Back
        btnBackProfile.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        // Modifier -> dialog
        buttonEditProfile.setOnClickListener(v -> openEditProfileDialog());

        // Compte -> page Compte (à créer)
        rowAccount.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), AccountActivity.class))
        );

        // Notifications -> ton écran existant
        rowNotifications.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), ui.settings.NotificationsSettingsActivity.class))
        );

        // Aide & support -> page Aide (à créer)
        rowHelp.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), HelpActivity.class))
        );

        // Logout (pour l’instant simple)
        buttonLogout.setOnClickListener(v ->
                android.widget.Toast.makeText(getContext(), "Déconnexion (TODO)", android.widget.Toast.LENGTH_SHORT).show()
        );
    }

    private void openEditProfileDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_profile, null);

        EditText editName = dialogView.findViewById(R.id.editName);
        EditText editRole = dialogView.findViewById(R.id.editRole);

        editName.setText(textProfileName.getText().toString());
        editRole.setText(textProfileRole.getText().toString());

        AlertDialog dialog = new AlertDialog.Builder(requireContext(), R.style.AppDialog)
                .setView(dialogView)
                .setNegativeButton("Annuler", (d, w) -> d.dismiss())
                .setPositiveButton("Enregistrer", (d, w) -> {
                    String newName = editName.getText().toString().trim();
                    String newRole = editRole.getText().toString().trim();

                    if (!newName.isEmpty()) textProfileName.setText(newName);
                    if (!newRole.isEmpty()) textProfileRole.setText(newRole);
                })
                .create();

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog_round);
        }
    }}