package edu.uph.m24si2.studypets.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Delete;
import androidx.room.Query;

import java.util.List;

// @Dao = Data Access Object
// Kelas ini berisi semua query (perintah) ke database
// Room akan otomatis buatkan implementasinya, kita cukup tulis anotasi
@Dao
public interface UserDao {

    // @Insert = perintah untuk menyimpan data baru ke tabel
    // Mengembalikan id baris yang baru dibuat (long)
    @Insert
    long simpanUser(UserEntity user);

    // @Query = perintah SQL untuk membaca data
    // :username = parameter yang dikirim dari Java
    // Cari user berdasarkan username saja (untuk cek apakah sudah terdaftar)
    @Query("SELECT * FROM tabel_user WHERE username = :username LIMIT 1")
    UserEntity cariUserByUsername(String username);

    // Cari user berdasarkan username DAN password (untuk login)
    @Query("SELECT * FROM tabel_user WHERE username = :username AND password = :password LIMIT 1")
    UserEntity cariUserUntukLogin(String username, String password);

    // Ambil semua user yang terdaftar — untuk ditampilkan di RecyclerView
    // Persis seperti pola materi dosen: db.mahasiswaDAO().getMahasiswa()
    @Query("SELECT * FROM tabel_user ORDER BY id ASC")
    List<UserEntity> getSemuaUser();

    // Hapus satu user berdasarkan objek — pola @Delete dari materi dosen
    @Delete
    void hapusUser(UserEntity user);
}
