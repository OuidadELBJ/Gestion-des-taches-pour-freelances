package com.example.freelance;

public class TaskUiModel {

    public String id;
    public String title;
    public String deadlineText;
    public String statusText;

    public TaskUiModel(String id, String title, String deadlineText, String statusText) {
        this.id = id;
        this.title = title;
        this.deadlineText = deadlineText;
        this.statusText = statusText;
    }
}