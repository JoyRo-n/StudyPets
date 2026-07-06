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

    // Ambil semua quest yang masih aktif (status = "pending")
    // ORDER BY deadline = urutkan berdasarkan tanggal deadline paling dekat
    @Query("SELECT * FROM quest_data WHERE status = 'pending' ORDER BY deadline ASC")
    List<QuestData> getQuestAktif();

    // Ambil semua quest yang sudah selesai
    @Query("SELECT * FROM quest_data WHERE status = 'completed' ORDER BY createdAt DESC")
    List<QuestData> getQuestSelesai();

    // Ambil semua quest (untuk tampilan kalender)
    @Query("SELECT * FROM quest_data ORDER BY deadline ASC")
    List<QuestData> getSemuaQuest();

    // Tandai quest sebagai selesai
    @Query("UPDATE quest_data SET status = 'completed' WHERE id = :idQuest")
    void selesaikanQuest(int idQuest);

    // Ambil satu quest berdasarkan id (untuk ambil reward sebelum diselesaikan)
    @Query("SELECT * FROM quest_data WHERE id = :idQuest LIMIT 1")
    QuestData getQuestById(int idQuest);

    // Hapus satu quest
    @Query("DELETE FROM quest_data WHERE id = :idQuest")
    void hapusQuest(int idQuest);

    // Hitung quest selesai berdasarkan tingkat kesulitan (untuk chart)
    @Query("SELECT COUNT(*) FROM quest_data WHERE difficulty = :kesulitan AND status = 'completed'")
    int hitungQuestSelesaiPerKesulitan(String kesulitan);

    // Hitung quest selesai yang dibuat pada tanggal tertentu (untuk bar chart 7 hari)
    @Query("SELECT COUNT(*) FROM quest_data WHERE status = 'completed' AND createdAt LIKE :tanggal || '%'")
    int hitungQuestSelesaiPerTanggal(String tanggal);

    // Hitung quest Hard yang sudah selesai (untuk achievement)
    @Query("SELECT COUNT(*) FROM quest_data WHERE difficulty = 'Hard' AND status = 'completed'")
    int hitungQuestHardSelesai();
}
