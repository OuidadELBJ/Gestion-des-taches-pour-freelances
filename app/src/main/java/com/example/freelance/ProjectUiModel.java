package com.example.freelance;

public class ProjectUiModel {

    public final String id;
    public final String name;
    public final String client;
    public final String deadlineText;
    public final String progressText;

    public ProjectUiModel(String id,
                          String name,
                          String client,
                          String deadlineText,
                          String progressText) {
        this.id = id;
        this.name = name;
        this.client = client;
        this.deadlineText = deadlineText;
        this.progressText = progressText;
    }
}