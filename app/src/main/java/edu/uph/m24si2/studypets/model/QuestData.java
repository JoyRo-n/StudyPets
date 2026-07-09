package edu.uph.m24si2.studypets.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "quest_data")
public class QuestData {

    @PrimaryKey(autoGenerate = true)
    public int    id          = 0;
    public String username    = ""; // pemilik quest — wajib diisi saat simpan
    public String title       = "";
    public String description = "";
    public String subject     = "";
    public String difficulty  = "Medium";
    public int    xpReward    = 0;
    public int    coinReward  = 0;
    public String status      = "pending";
    public String deadline    = "";
    public String createdAt   = "";

    public QuestData() {}

    public int    getId()               { return id; }
    public void   setId(int v)          { this.id = v; }
    public String getTitle()            { return title; }
    public void   setTitle(String v)    { this.title = v; }
    public String getDescription()          { return description; }
    public void   setDescription(String v)  { this.description = v; }
    public String getSubject()          { return subject; }
    public void   setSubject(String v)  { this.subject = v; }
    public String getDifficulty()           { return difficulty; }
    public void   setDifficulty(String v)   { this.difficulty = v; }
    public int    getXpReward()         { return xpReward; }
    public void   setXpReward(int v)    { this.xpReward = v; }
    public int    getCoinReward()       { return coinReward; }
    public void   setCoinReward(int v)  { this.coinReward = v; }
    public String getStatus()           { return status; }
    public void   setStatus(String v)   { this.status = v; }
    public String getDeadline()         { return deadline; }
    public void   setDeadline(String v) { this.deadline = v; }
    public String getCreatedAt()            { return createdAt; }
    public void   setCreatedAt(String v)    { this.createdAt = v; }
}
