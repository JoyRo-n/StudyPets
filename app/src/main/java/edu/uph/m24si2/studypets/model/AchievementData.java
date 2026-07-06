package edu.uph.m24si2.studypets.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "achievement_data")
public class AchievementData {

    @PrimaryKey
    public int     id         = 0;
    public String  title      = "";
    public String  description= "";
    public String  icon       = "";
    public boolean unlocked   = false;
    public String  unlockedAt = "";

    public AchievementData() {}

    public int     getId()                  { return id; }
    public void    setId(int v)             { this.id = v; }
    public String  getTitle()               { return title; }
    public void    setTitle(String v)       { this.title = v; }
    public String  getDescription()         { return description; }
    public void    setDescription(String v) { this.description = v; }
    public String  getIcon()                { return icon; }
    public void    setIcon(String v)        { this.icon = v; }
    public boolean isUnlocked()             { return unlocked; }
    public void    setUnlocked(boolean v)   { this.unlocked = v; }
    public String  getUnlockedAt()          { return unlockedAt; }
    public void    setUnlockedAt(String v)  { this.unlockedAt = v; }
}
