package edu.uph.m24si2.studypets.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// @Entity = beritahu Room bahwa kelas ini adalah sebuah TABEL di database
// tableName = nama tabel di SQLite
@Entity(tableName = "tabel_user")
public class UserEntity {

    // @PrimaryKey = kolom yang unik sebagai identitas baris
    // autoGenerate = id otomatis bertambah (1, 2, 3, ...)
    @PrimaryKey(autoGenerate = true)
    public int id;

    // Kolom username — harus unik, tidak boleh sama antar user
    public String username;

    // Kolom password — disimpan apa adanya (plain text, cocok untuk level belajar)
    public String password;

    // Kolom jenis pet yang dipilih saat register ("cat" atau "dog")
    public String petType;

    // Constructor = cara membuat objek UserEntity baru
    public UserEntity(String username, String password, String petType) {
        this.username = username;
        this.password = password;
        this.petType  = petType;
    }
}
