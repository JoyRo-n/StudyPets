package edu.uph.m24si2.studypets.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import edu.uph.m24si2.studypets.model.AchievementData;

@Dao
public interface AchievementDataDao {

    // Simpan achievement baru (untuk seeding)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void simpan(AchievementData achievement);

    // Ambil semua achievement, yang sudah di-unlock tampil dulu
    @Query("SELECT * FROM achievement_data ORDER BY unlocked DESC")
    List<AchievementData> getSemuaAchievement();

    // Unlock achievement berdasarkan judul (kalau belum di-unlock)
    @Query("UPDATE achievement_data SET unlocked = 1, unlockedAt = :waktu " +
           "WHERE title = :judul AND unlocked = 0")
    void unlockAchievement(String judul, String waktu);

    // Hitung achievement yang sudah di-unlock
    @Query("SELECT COUNT(*) FROM achievement_data WHERE unlocked = 1")
    int hitungAchievementUnlocked();

    // Cek apakah data achievement sudah ada
    @Query("SELECT COUNT(*) FROM achievement_data")
    int hitungSemuaAchievement();
}
