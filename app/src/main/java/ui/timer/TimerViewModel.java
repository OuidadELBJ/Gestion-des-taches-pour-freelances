package ui.timer;

import android.content.Context;

import com.example.freelance.data.local.entity.TimeEntry;
import com.example.freelance.data.local.repository.TimeEntryRepository;
import com.example.freelance.data.mapper.TimeEntryMapper;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TimerViewModel {

    public interface Callback<T> { void onResult(T value); }

    public static class Totals {
        public final long totalTask;
        public final long totalProject;
        public Totals(long totalTask, long totalProject) {
            this.totalTask = totalTask;
            this.totalProject = totalProject;
        }
    }

    private final TimeEntryRepository repo;

    public TimerViewModel(Context context) {
        repo = new TimeEntryRepository(context.getApplicationContext());
    }

    // ✅ à l'arrêt du service → on insère une session
    public void addSession(String projectId, String taskId, long startMillis, long endMillis) {
        long duration = Math.max(0, endMillis - startMillis);

        TimeEntry e = new TimeEntry(
                UUID.randomUUID().toString(),
                taskId,
                projectId,
                new Date(startMillis),
                new Date(endMillis),
                duration,
                false,        // isRunning
                new Date(),    // lastUpdated
                false,        // isSynced
                false,        // isPaused
                0L,           // pausedAccumulated
                null          // note
        );

        repo.insert(e);
    }

    public void getSessionsByTask(String taskId, Callback<List<data.modele.TimeEntry>> cb) {
        repo.getByTask(taskId, entities -> cb.onResult(TimeEntryMapper.toModelList(entities)));
    }

    public void getTotals(String projectId, String taskId, Callback<Totals> cb) {
        repo.getTotalTask(taskId, totalTask ->
                repo.getTotalProject(projectId, totalProject ->
                        cb.onResult(new Totals(totalTask, totalProject))
                )
        );
    }

    public String format(long ms) {
        long totalSeconds = ms / 1000;
        long h = totalSeconds / 3600;
        long m = (totalSeconds % 3600) / 60;
        long s = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }
}
