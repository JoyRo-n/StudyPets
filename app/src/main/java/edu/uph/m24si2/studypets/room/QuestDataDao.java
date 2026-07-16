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

    // Ambil quest aktif milik user tertentu (pending + submitted)
    @Query("SELECT * FROM quest_data WHERE username = :username AND status IN ('pending','submitted') ORDER BY deadline ASC")
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

    // Submit bukti — simpan path foto dan ubah status jadi submitted
    @Query("UPDATE quest_data SET buktiPath = :path, submittedAt = :waktu, status = 'submitted' WHERE id = :idQuest")
    void submitBukti(int idQuest, String path, String waktu);

    // Reject bukti — kembalikan ke pending dan hapus bukti lama
    @Query("UPDATE quest_data SET status = 'pending', buktiPath = '', submittedAt = '' WHERE id = :idQuest")
    void rejectBukti(int idQuest);

    // Ambil semua quest submitted dari semua user — untuk admin review
    @Query("SELECT * FROM quest_data WHERE isFromAdmin = 1 AND status = 'submitted' ORDER BY submittedAt ASC")
    List<QuestData> getQuestMenungguReview();

    // Hitung quest yang menunggu review (untuk badge notif di admin panel)
    @Query("SELECT COUNT(*) FROM quest_data WHERE isFromAdmin = 1 AND status = 'submitted'")
    int hitungMenungguReview();

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
