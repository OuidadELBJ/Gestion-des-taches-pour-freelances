package com.example.freelance;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ProjectDetailsPagerAdapter extends FragmentStateAdapter {

    private final String projectId;

    public ProjectDetailsPagerAdapter(@NonNull Fragment fragment, String projectId) {
        super(fragment);
        this.projectId = projectId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return ProjectTasksFragment.newInstance(projectId);
            case 1:
                return ProjectTimeFragment.newInstance(projectId);
            case 2:
                return ProjectPaymentsFragment.newInstance(projectId);
            case 3:
            default:
                return ProjectNotesFragment.newInstance(projectId);
        }
    }

    @Override
    public int getItemCount() {
        return 4;  // 4 onglets
    }
}