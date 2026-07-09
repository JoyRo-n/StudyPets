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

    // Ambil data pet milik user tertentu
    @Query("SELECT * FROM pet_data WHERE username = :username LIMIT 1")
    PetData getPet(String username);

    @Query("UPDATE pet_data SET petName = :namaBaru WHERE username = :username")
    void gantiNama(String username, String namaBaru);

    @Query("UPDATE pet_data SET hunger = MIN(100, hunger + :nilai) WHERE username = :username")
    void tambahHunger(String username, int nilai);

    @Query("UPDATE pet_data SET thirst = MIN(100, thirst + :nilai) WHERE username = :username")
    void tambahThirst(String username, int nilai);

    @Query("UPDATE pet_data SET health = MIN(100, health + :nilai) WHERE username = :username")
    void tambahHealth(String username, int nilai);

    @Query("UPDATE pet_data SET mood = MIN(100, mood + :nilai) WHERE username = :username")
    void tambahMood(String username, int nilai);

    @Query("UPDATE pet_data SET " +
           "hunger = MAX(0, hunger - :kurangiHunger), " +
           "thirst = MAX(0, thirst - :kurangiThirst), " +
           "mood   = MAX(0, mood   - :kurangiMood) " +
           "WHERE username = :username")
    void kurangiStats(String username, int kurangiHunger, int kurangiThirst, int kurangiMood);

    @Query("UPDATE pet_data SET lastFedTime = :waktu WHERE username = :username")
    void updateWaktuMakan(String username, String waktu);

    @Query("DELETE FROM pet_data")
    void hapusSemua();
}
