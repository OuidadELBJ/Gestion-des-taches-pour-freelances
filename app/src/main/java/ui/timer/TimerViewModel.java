package ui.timer;

import java.util.List;

import data.fake.FakeTimeEntryStore;
import data.modele.TimeEntry;

public class TimerViewModel {

    public void addSession(String projectId, String taskId, long startMillis, long endMillis) {
        FakeTimeEntryStore.get().add(projectId, taskId, startMillis, endMillis);
    }

    public List<TimeEntry> getSessionsByTask(String taskId) {
        return FakeTimeEntryStore.get().listByTask(taskId);
    }

    public long getTotalTask(String taskId) {
        return FakeTimeEntryStore.get().totalTask(taskId);
    }

    public long getTotalProject(String projectId) {
        return FakeTimeEntryStore.get().totalProject(projectId);
    }

    public String format(long ms) {
        long totalSeconds = ms / 1000;
        long h = totalSeconds / 3600;
        long m = (totalSeconds % 3600) / 60;
        long s = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }
}
