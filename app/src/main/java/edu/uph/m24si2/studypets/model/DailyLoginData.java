package edu.uph.m24si2.studypets.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "daily_login_data")
public class DailyLoginData {

    @PrimaryKey
    @NonNull
    public String  loginDate = "";
    public int     dayNumber = 1;
    public boolean claimed   = false;

    public DailyLoginData() {}

    public String  getLoginDate()          { return loginDate; }
    public void    setLoginDate(String v)  { this.loginDate = v; }
    public int     getDayNumber()          { return dayNumber; }
    public void    setDayNumber(int v)     { this.dayNumber = v; }
    public boolean isClaimed()             { return claimed; }
    public void    setClaimed(boolean v)   { this.claimed = v; }
}
