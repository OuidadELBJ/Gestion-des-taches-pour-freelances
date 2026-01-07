package com.example.freelance.data.local.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.freelance.data.local.AppDatabase;
import com.example.freelance.data.local.entity.Tache;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DashboardRepository {

    public static class DashboardData {
        public com.example.freelance.data.local.entity.Projet latestProject;
        public int projectsCount;
        public double paymentsThisMonth;
        public long timeThisWeekMillis;
        public int urgentCount;
        public List<Tache> urgentTasks;
    }

    public interface Callback {
        void onResult(DashboardData data);
    }

    private final AppDatabase db;
    private final Executor io = Executors.newSingleThreadExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());

    public DashboardRepository(Context context) {
        db = AppDatabase.getInstance(context.getApplicationContext());
    }

    public void load(Callback cb) {
        io.execute(() -> {
            DashboardData d = new DashboardData();

            // 1) count projets
            d.projectsCount = db.projetDao().countAllProjects();
            // ✅ latest project

            d.latestProject = db.projetDao().getLatest();

            // 2) paiements du mois
            Date[] month = monthRange();
            d.paymentsThisMonth = db.paiementDao().sumPaidInPeriod(month[0], month[1]); // ou sumAllInPeriod

            // 3) urgent tasks (<= demain)
            Date limit = tomorrowEnd();
            d.urgentCount = db.tacheDao().countUrgentTasks(limit);
            d.urgentTasks = db.tacheDao().getUrgentTasks(limit);

            // 4) time this week
            Date[] week = weekRange();
            d.timeThisWeekMillis = db.timeEntryDao().sumDuration(week[0], week[1]);

            main.post(() -> cb.onResult(d));
        });
    }

    private static Date[] monthRange() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        setStartOfDay(c);
        Date start = c.getTime();

        c.add(Calendar.MONTH, 1);
        Date end = c.getTime(); // exclusive
        return new Date[]{start, end};
    }

    private static Date[] weekRange() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        setStartOfDay(c);
        Date start = c.getTime();

        c.add(Calendar.DAY_OF_YEAR, 7);
        Date end = c.getTime();
        return new Date[]{start, end};
    }

    private static Date tomorrowEnd() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, 1);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }

    private static void setStartOfDay(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }
}