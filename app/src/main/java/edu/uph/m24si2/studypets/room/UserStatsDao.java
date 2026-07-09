package edu.uph.m24si2.studypets.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import edu.uph.m24si2.studypets.model.UserStats;

@Dao
public interface UserStatsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void simpan(UserStats userStats);

    @Update
    void update(UserStats userStats);

    // Ambil data user berdasarkan username
    @Query("SELECT * FROM user_stats WHERE username = :username LIMIT 1")
    UserStats getUser(String username);

    @Query("UPDATE user_stats SET xp = xp + :jumlahXp WHERE username = :username")
    void tambahXp(String username, int jumlahXp);

    @Query("UPDATE user_stats SET coins = coins + :jumlahKoin WHERE username = :username")
    void tambahKoin(String username, int jumlahKoin);

    @Query("UPDATE user_stats SET coins = MAX(0, coins - :jumlahKoin) WHERE username = :username")
    void kurangiKoin(String username, int jumlahKoin);

    @Query("UPDATE user_stats SET totalQuestsCompleted = totalQuestsCompleted + 1 WHERE username = :username")
    void tambahQuestSelesai(String username);

    @Query("DELETE FROM user_stats WHERE username = :username")
    void hapusUser(String username);

    @Query("DELETE FROM user_stats")
    void hapusSemua();
}
