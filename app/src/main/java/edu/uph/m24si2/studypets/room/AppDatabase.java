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
import edu.uph.m24si2.studypets.model.RiwayatBelajar;
import edu.uph.m24si2.studypets.model.UserStats;
import edu.uph.m24si2.studypets.room.UserEntity;

@Database(
    entities = {
        UserEntity.class,
        UserStats.class,
        PetData.class,
        QuestData.class,
        InventoryItem.class,
        AchievementData.class,
        DailyLoginData.class,
        RiwayatBelajar.class    // tabel riwayat belajar/kuis
    },
    version = 7, // naik karena tambah kolom buktiPath, submittedAt di quest_data
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
    public abstract RiwayatBelajarDao riwayatBelajarDao();

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

            // Seed akun admin jika belum ada
            seedAdminAccount(instance);
        }
        return instance;
    }

    // Buat akun admin default saat pertama kali install
    // Username: admin | Password: admin123
    private static void seedAdminAccount(AppDatabase db) {
        if (db.userDao().getAdmin() == null) {
            UserEntity admin = new UserEntity("admin", "admin123", "cat", true);
            db.userDao().simpanUser(admin);
        }
    }
}
