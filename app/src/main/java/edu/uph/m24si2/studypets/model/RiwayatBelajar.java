package edu.uph.m24si2.studypets.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Menyimpan riwayat setiap kali user menyelesaikan kuis
// Admin bisa lihat ini untuk tahu kebutuhan belajar tiap user
@Entity(tableName = "riwayat_belajar")
public class RiwayatBelajar {

    @PrimaryKey(autoGenerate = true)
    public int    id        = 0;
    public String username  = "";   // pemilik riwayat
    public String topik     = "";   // nama materi yang dikuis
    public int    nilai     = 0;    // nilai 0-100
    public int    benar     = 0;    // jumlah jawaban benar
    public int    totalSoal = 0;    // total soal
    public String tanggal   = "";   // kapan dikerjakan

    public RiwayatBelajar() {}
}
