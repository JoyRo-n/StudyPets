package edu.uph.m24si2.studypets.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import edu.uph.m24si2.studypets.model.DailyLoginData;

@Dao
public interface DailyLoginDao {

    // Simpan record login hari ini
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void simpan(DailyLoginData data);

    // Ambil data login berdasarkan tanggal
    @Query("SELECT * FROM daily_login_data WHERE loginDate = :tanggal LIMIT 1")
    DailyLoginData getLoginByTanggal(String tanggal);

    // Tandai sudah di-claim hari ini
    @Query("UPDATE daily_login_data SET claimed = 1 WHERE loginDate = :tanggal")
    void tandaiSudahClaim(String tanggal);
}
