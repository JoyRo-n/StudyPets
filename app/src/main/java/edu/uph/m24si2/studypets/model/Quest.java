package edu.uph.m24si2.studypets.model;

public class Quest {
    private int id;
    private String title;
    private String description;
    private String subject;
    private String difficulty;
    private int xpReward;
    private int coinReward;
    private String status;
    private String deadline;

    public Quest(int id, String title, String description, String subject, String difficulty,
                 int xpReward, int coinReward, String status, String deadline) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.difficulty = difficulty;
        this.xpReward = xpReward;
        this.coinReward = coinReward;
        this.status = status;
        this.deadline = deadline;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getSubject() { return subject; }
    public String getDifficulty() { return difficulty; }
    public int getXpReward() { return xpReward; }
    public int getCoinReward() { return coinReward; }
    public String getStatus() { return status; }
    public String getDeadline() { return deadline; }
}
