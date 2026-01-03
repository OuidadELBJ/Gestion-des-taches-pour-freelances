package ui.stub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freelance.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import data.modele.Project;

public class ProjectsStubAdapter extends RecyclerView.Adapter<ProjectsStubAdapter.VH> {

    public interface OnClick { void onClick(Project p); }

    private final List<Project> items;
    private final OnClick onClick;
    private final SimpleDateFormat df = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());

    public ProjectsStubAdapter(List<Project> items, OnClick onClick) {
        this.items = items;
        this.onClick = onClick;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Project p = items.get(position);
        h.t1.setText(p.name);

        String d = (p.deadlineMillis > 0)
                ? df.format(new Date(p.deadlineMillis))
                : "Pas de deadline";

        String status = p.reminderEnabled ? (p.useDefaultOffsets ? "Rappel: défaut" : "Rappel: custom") : "Rappel: OFF";
        h.t2.setText(d + " • " + status);

        h.itemView.setOnClickListener(v -> onClick.onClick(p));
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView t1, t2;
        VH(@NonNull View itemView) {
            super(itemView);
            t1 = itemView.findViewById(android.R.id.text1);
            t2 = itemView.findViewById(android.R.id.text2);
        }
    }
}
