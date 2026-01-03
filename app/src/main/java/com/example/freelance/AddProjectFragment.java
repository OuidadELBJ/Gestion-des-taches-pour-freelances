package com.example.freelance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.freelance.databinding.FragmentAddProjectBinding;

public class AddProjectFragment extends Fragment {

    private FragmentAddProjectBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAddProjectBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbarAddProject.setNavigationOnClickListener(v ->
                NavHostFragment.findNavController(this).popBackStack()
        );

        binding.buttonCreateProject.setOnClickListener(v -> {
            String name = binding.editProjectName.getText() != null
                    ? binding.editProjectName.getText().toString().trim()
                    : "";

            if (name.isEmpty()) {
                Toast.makeText(getContext(), "Nom du projet obligatoire", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO: enregistrer dans Room / backend
            Toast.makeText(getContext(), "Projet créé (fake)", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).popBackStack();
        });
    }
}