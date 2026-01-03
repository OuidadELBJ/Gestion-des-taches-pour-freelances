package com.example.freelance;

public class Project {
    public String id;
    public String name;
    public String client;
    public String deadline;
    public String status;   // "En cours", "En retard", "Terminé"
    public double budget;
    public double hourlyRate;
    public String notes;

    public Project(String id, String name, String client,
                   String deadline, String status,
                   double budget, double hourlyRate,
                   String notes) {
        this.id = id;
        this.name = name;
        this.client = client;
        this.deadline = deadline;
        this.status = status;
        this.budget = budget;
        this.hourlyRate = hourlyRate;
        this.notes = notes;
    }
}