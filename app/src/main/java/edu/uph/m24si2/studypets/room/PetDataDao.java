package edu.uph.m24si2.studypets.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import edu.uph.m24si2.studypets.model.PetData;

@Dao
public interface PetDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void simpan(PetData petData);

    @Update
    void update(PetData petData);

    // Ambil data pet (id selalu 1)
    @Query("SELECT * FROM pet_data WHERE id = 1 LIMIT 1")
    PetData getPet();

    // Ganti nama pet
    @Query("UPDATE pet_data SET petName = :namaBaru WHERE id = 1")
    void gantiNama(String namaBaru);

    // Tambah nilai kenyang (tidak melebihi 100)
    @Query("UPDATE pet_data SET hunger = MIN(100, hunger + :nilai) WHERE id = 1")
    void tambahHunger(int nilai);

    // Tambah nilai haus
    @Query("UPDATE pet_data SET thirst = MIN(100, thirst + :nilai) WHERE id = 1")
    void tambahThirst(int nilai);

    // Tambah health
    @Query("UPDATE pet_data SET health = MIN(100, health + :nilai) WHERE id = 1")
    void tambahHealth(int nilai);

    // Tambah mood
    @Query("UPDATE pet_data SET mood = MIN(100, mood + :nilai) WHERE id = 1")
    void tambahMood(int nilai);

    // Kurangi stats pet setiap waktu (tidak boleh minus)
    @Query("UPDATE pet_data SET " +
           "hunger = MAX(0, hunger - :kurangiHunger), " +
           "thirst = MAX(0, thirst - :kurangiThirst), " +
           "mood   = MAX(0, mood   - :kurangiMood) " +
           "WHERE id = 1")
    void kurangiStats(int kurangiHunger, int kurangiThirst, int kurangiMood);

    // Update waktu terakhir diberi makan
    @Query("UPDATE pet_data SET lastFedTime = :waktu WHERE id = 1")
    void updateWaktuMakan(String waktu);

    // Hapus semua data
    @Query("DELETE FROM pet_data")
    void hapusSemua();
}
