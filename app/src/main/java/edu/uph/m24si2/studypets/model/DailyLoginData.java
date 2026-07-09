package edu.uph.m24si2.studypets.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;

// Composite primary key: (username, loginDate) — streak per user
@Entity(tableName = "daily_login_data", primaryKeys = {"username", "loginDate"})
public class DailyLoginData {

    @NonNull
    public String  username  = "";
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
