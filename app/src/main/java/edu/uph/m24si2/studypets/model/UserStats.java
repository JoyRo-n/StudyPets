package edu.uph.m24si2.studypets.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_stats")
public class UserStats {

    @PrimaryKey
    public int    id                    = 1;
    public String username              = "";
    public int    level                 = 1;
    public int    xp                    = 0;
    public int    coins                 = 100;
    public int    totalQuestsCompleted  = 0;
    public int    learningStreak        = 0;

    public UserStats() {}

    public int    getId()                      { return id; }
    public void   setId(int v)                 { this.id = v; }
    public String getUsername()                { return username; }
    public void   setUsername(String v)        { this.username = v; }
    public int    getLevel()                   { return level; }
    public void   setLevel(int v)              { this.level = v; }
    public int    getXp()                      { return xp; }
    public void   setXp(int v)                 { this.xp = v; }
    public int    getCoins()                   { return coins; }
    public void   setCoins(int v)              { this.coins = v; }
    public int    getTotalQuestsCompleted()                { return totalQuestsCompleted; }
    public void   setTotalQuestsCompleted(int v)           { this.totalQuestsCompleted = v; }
    public int    getLearningStreak()          { return learningStreak; }
    public void   setLearningStreak(int v)     { this.learningStreak = v; }
}
