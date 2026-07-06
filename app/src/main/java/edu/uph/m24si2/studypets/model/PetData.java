package edu.uph.m24si2.studypets.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pet_data")
public class PetData {

    @PrimaryKey
    public int    id          = 1;
    public String petType     = "cat";
    public String petName     = "Mochi";
    public int    hunger      = 100;
    public int    thirst      = 100;
    public int    health      = 100;
    public int    mood        = 100;
    public String lastFedTime = "";

    public PetData() {}

    public int    getId()               { return id; }
    public void   setId(int v)          { this.id = v; }
    public String getPetType()          { return petType; }
    public void   setPetType(String v)  { this.petType = v; }
    public String getPetName()          { return petName; }
    public void   setPetName(String v)  { this.petName = v; }
    public int    getHunger()           { return hunger; }
    public void   setHunger(int v)      { this.hunger = v; }
    public int    getThirst()           { return thirst; }
    public void   setThirst(int v)      { this.thirst = v; }
    public int    getHealth()           { return health; }
    public void   setHealth(int v)      { this.health = v; }
    public int    getMood()             { return mood; }
    public void   setMood(int v)        { this.mood = v; }
    public String getLastFedTime()          { return lastFedTime; }
    public void   setLastFedTime(String v)  { this.lastFedTime = v; }
}
