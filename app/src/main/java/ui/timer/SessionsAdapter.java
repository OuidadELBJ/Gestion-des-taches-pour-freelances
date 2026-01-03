package ui.timer;

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

import data.modele.TimeEntry;

public class SessionsAdapter extends RecyclerView.Adapter<SessionsAdapter.VH> {

    private final List<TimeEntry> items = new ArrayList<>();
    private final SimpleDateFormat df = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());

    public void submit(List<TimeEntry> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_session, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        TimeEntry e = items.get(position);

        String line1 = df.format(new Date(e.startMillis)) + " → " + df.format(new Date(e.endMillis));
        h.range.setText(line1);
        h.duration.setText("Durée: " + formatDuration(e.durationMillis));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView range, duration;
        VH(@NonNull View itemView) {
            super(itemView);
            range = itemView.findViewById(R.id.textSessionRange);
            duration = itemView.findViewById(R.id.textSessionDuration);
        }
    }

    private String formatDuration(long ms) {
        long totalSeconds = ms / 1000;
        long h = totalSeconds / 3600;
        long m = (totalSeconds % 3600) / 60;
        long s = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s);
    }
}