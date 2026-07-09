package edu.uph.m24si2.studypets.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import edu.uph.m24si2.studypets.model.QuestData;

@Dao
public interface QuestDataDao {

    // Simpan quest baru, kembalikan id yang digenerate
    @Insert
    long simpan(QuestData questData);

    // Ambil quest aktif milik user tertentu
    @Query("SELECT * FROM quest_data WHERE username = :username AND status = 'pending' ORDER BY deadline ASC")
    List<QuestData> getQuestAktif(String username);

    // Ambil quest selesai milik user tertentu
    @Query("SELECT * FROM quest_data WHERE username = :username AND status = 'completed' ORDER BY createdAt DESC")
    List<QuestData> getQuestSelesai(String username);

    // Ambil semua quest milik user tertentu (untuk kalender)
    @Query("SELECT * FROM quest_data WHERE username = :username ORDER BY deadline ASC")
    List<QuestData> getSemuaQuest(String username);

    // Tandai quest sebagai selesai
    @Query("UPDATE quest_data SET status = 'completed' WHERE id = :idQuest")
    void selesaikanQuest(int idQuest);

    // Ambil satu quest berdasarkan id
    @Query("SELECT * FROM quest_data WHERE id = :idQuest LIMIT 1")
    QuestData getQuestById(int idQuest);

    // Hapus satu quest
    @Query("DELETE FROM quest_data WHERE id = :idQuest")
    void hapusQuest(int idQuest);

    // Hitung quest selesai per kesulitan milik user tertentu
    @Query("SELECT COUNT(*) FROM quest_data WHERE username = :username AND difficulty = :kesulitan AND status = 'completed'")
    int hitungQuestSelesaiPerKesulitan(String username, String kesulitan);

    // Hitung quest selesai per tanggal milik user tertentu
    @Query("SELECT COUNT(*) FROM quest_data WHERE username = :username AND status = 'completed' AND createdAt LIKE :tanggal || '%'")
    int hitungQuestSelesaiPerTanggal(String username, String tanggal);

    // Hitung quest Hard selesai milik user tertentu (untuk achievement)
    @Query("SELECT COUNT(*) FROM quest_data WHERE username = :username AND difficulty = 'Hard' AND status = 'completed'")
    int hitungQuestHardSelesai(String username);
}
