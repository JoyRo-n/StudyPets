package edu.uph.m24si2.studypets.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import edu.uph.m24si2.studypets.model.RiwayatBelajar;
import edu.uph.m24si2.studypets.model.TopikFrequency;

@Dao
public interface RiwayatBelajarDao {

    @Insert
    void simpan(RiwayatBelajar data);

    // Ambil semua riwayat milik user tertentu, terbaru dulu
    @Query("SELECT * FROM riwayat_belajar WHERE username = :username ORDER BY id DESC")
    List<RiwayatBelajar> getRiwayatUser(String username);

    // Ambil semua riwayat semua user — untuk admin
    @Query("SELECT * FROM riwayat_belajar ORDER BY id DESC")
    List<RiwayatBelajar> getSemuaRiwayat();

    // Ambil daftar username unik yang punya riwayat — untuk admin pilih user
    @Query("SELECT DISTINCT username FROM riwayat_belajar ORDER BY username ASC")
    List<String> getDaftarUsernameAktif();

    // Ambil topik yang paling sering dikerjakan user (untuk saran quest)
    @Query("SELECT topik, COUNT(*) as jumlah FROM riwayat_belajar WHERE username = :username GROUP BY topik ORDER BY jumlah DESC LIMIT 3")
    List<TopikFrequency> getTopikTerseringUser(String username);
}
