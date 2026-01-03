package ui.stub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freelance.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import data.modele.Task;

public class TasksStubAdapter extends RecyclerView.Adapter<TasksStubAdapter.VH> {

    public interface OnEditClick { void onEdit(String taskId); }

    private final List<Task> items = new ArrayList<>();
    private final OnEditClick onEdit;
    private final SimpleDateFormat df = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());

    public TasksStubAdapter(OnEditClick onEdit) {
        this.onEdit = onEdit;
    }

    public void submit(List<Task> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_task_stub, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Task t = items.get(position);

        h.tvTitle.setText(t.title);

        String deadline = t.deadlineMillis > 0 ? df.format(new Date(t.deadlineMillis)) : "Pas de deadline";
        String status = t.reminderEnabled
                ? (t.useDefaultOffsets ? "Rappel tâche: défaut" : "Rappel tâche: custom")
                : "Rappel tâche: OFF";

        h.tvMeta.setText(deadline + " • " + status);

        h.btnEdit.setOnClickListener(v -> onEdit.onEdit(t.id));
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMeta;
        View btnEdit;

        VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTaskTitleStub);
            tvMeta  = itemView.findViewById(R.id.tvTaskMetaStub);
            btnEdit = itemView.findViewById(R.id.btnEditTaskNotif);
        }
    }
}
