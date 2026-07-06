package edu.uph.m24si2.studypets.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import edu.uph.m24si2.studypets.model.AchievementData;
import edu.uph.m24si2.studypets.model.DailyLoginData;
import edu.uph.m24si2.studypets.model.InventoryItem;
import edu.uph.m24si2.studypets.model.PetData;
import edu.uph.m24si2.studypets.model.QuestData;
import edu.uph.m24si2.studypets.model.UserStats;
import edu.uph.m24si2.studypets.room.UserEntity;

// @Database mendaftarkan semua tabel (Entity) dan versi database
// Kalau ada perubahan struktur tabel, naikkan version
@Database(
    entities = {
        UserEntity.class,       // tabel login/register
        UserStats.class,        // stats user (level, xp, koin)
        PetData.class,          // data pet
        QuestData.class,        // daftar quest
        InventoryItem.class,    // item shop & inventory
        AchievementData.class,  // achievement / badge
        DailyLoginData.class    // streak login harian
    },
    version = 2, // naik dari 1 karena ada tabel baru
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    // Room otomatis buatkan implementasi method ini
    public abstract UserDao          userDao();
    public abstract UserStatsDao     userStatsDao();
    public abstract PetDataDao       petDataDao();
    public abstract QuestDataDao     questDataDao();
    public abstract InventoryItemDao inventoryItemDao();
    public abstract AchievementDataDao achievementDataDao();
    public abstract DailyLoginDao    dailyLoginDao();

    // Singleton — satu instance untuk seluruh app
    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "studypets.db"
            )
            .allowMainThreadQueries()        // sesuai materi dosen — query bisa di UI thread
            .fallbackToDestructiveMigration() // reset database kalau versi berubah
            .build();
        }
        return instance;
    }
}
