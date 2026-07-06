package edu.uph.m24si2.studypets.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import edu.uph.m24si2.studypets.model.UserStats;

// DAO = Data Access Object
// Semua query untuk tabel user_stats ada di sini
@Dao
public interface UserStatsDao {

    // INSERT OR REPLACE = kalau id sudah ada, ganti datanya
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void simpan(UserStats userStats);

    // Update data yang sudah ada
    @Update
    void update(UserStats userStats);

    // Ambil data user (id selalu 1)
    @Query("SELECT * FROM user_stats WHERE id = 1 LIMIT 1")
    UserStats getUser();

    // Tambah XP
    @Query("UPDATE user_stats SET xp = xp + :jumlahXp WHERE id = 1")
    void tambahXp(int jumlahXp);

    // Tambah koin
    @Query("UPDATE user_stats SET coins = coins + :jumlahKoin WHERE id = 1")
    void tambahKoin(int jumlahKoin);

    // Kurangi koin (tidak boleh minus)
    @Query("UPDATE user_stats SET coins = MAX(0, coins - :jumlahKoin) WHERE id = 1")
    void kurangiKoin(int jumlahKoin);

    // Naik level
    @Query("UPDATE user_stats SET level = level + 1, xp = xp - (level * 100) WHERE id = 1")
    void naikLevel();

    // Tambah jumlah quest selesai
    @Query("UPDATE user_stats SET totalQuestsCompleted = totalQuestsCompleted + 1 WHERE id = 1")
    void tambahQuestSelesai();

    // Hapus semua data (untuk fresh start)
    @Query("DELETE FROM user_stats")
    void hapusSemua();
}
