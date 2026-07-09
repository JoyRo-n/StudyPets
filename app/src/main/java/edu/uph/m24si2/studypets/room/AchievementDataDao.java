package edu.uph.m24si2.studypets.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import edu.uph.m24si2.studypets.model.AchievementData;

@Dao
public interface AchievementDataDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void simpan(AchievementData achievement);

    // Ambil semua achievement milik user tertentu
    @Query("SELECT * FROM achievement_data WHERE username = :username ORDER BY unlocked DESC")
    List<AchievementData> getSemuaAchievement(String username);

    @Query("UPDATE achievement_data SET unlocked = 1, unlockedAt = :waktu " +
           "WHERE username = :username AND title = :judul AND unlocked = 0")
    void unlockAchievement(String username, String judul, String waktu);

    @Query("SELECT COUNT(*) FROM achievement_data WHERE username = :username AND unlocked = 1")
    int hitungAchievementUnlocked(String username);

    @Query("SELECT COUNT(*) FROM achievement_data WHERE username = :username")
    int hitungSemuaAchievement(String username);
}
