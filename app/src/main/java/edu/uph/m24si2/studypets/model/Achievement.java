package edu.uph.m24si2.studypets.model;

public class Achievement {
    private int id;
    private String title;
    private String description;
    private String icon;
    private boolean unlocked;
    private String unlockedAt;

    public Achievement(int id, String title, String description,
                       String icon, boolean unlocked, String unlockedAt) {
        this.id          = id;
        this.title       = title;
        this.description = description;
        this.icon        = icon;
        this.unlocked    = unlocked;
        this.unlockedAt  = unlockedAt;
    }

    public int     getId()          { return id; }
    public String  getTitle()       { return title; }
    public String  getDescription() { return description; }
    public String  getIcon()        { return icon; }
    public boolean isUnlocked()     { return unlocked; }
    public String  getUnlockedAt()  { return unlockedAt; }
}
