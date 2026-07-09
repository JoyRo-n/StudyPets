package edu.uph.m24si2.studypets.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import edu.uph.m24si2.studypets.model.DailyLoginData;

@Dao
public interface DailyLoginDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void simpan(DailyLoginData data);

    // Ambil data login berdasarkan username dan tanggal
    @Query("SELECT * FROM daily_login_data WHERE username = :username AND loginDate = :tanggal LIMIT 1")
    DailyLoginData getLoginByTanggal(String username, String tanggal);

    // Ambil login hari kemarin milik user tertentu
    @Query("SELECT * FROM daily_login_data WHERE username = :username AND loginDate = :tanggal LIMIT 1")
    DailyLoginData getLoginKemarin(String username, String tanggal);

    @Query("UPDATE daily_login_data SET claimed = 1 WHERE username = :username AND loginDate = :tanggal")
    void tandaiSudahClaim(String username, String tanggal);
}
