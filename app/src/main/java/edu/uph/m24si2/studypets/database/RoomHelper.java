package edu.uph.m24si2.studypets.database;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.uph.m24si2.studypets.model.AchievementData;
import edu.uph.m24si2.studypets.model.DailyLoginData;
import edu.uph.m24si2.studypets.model.InventoryItem;
import edu.uph.m24si2.studypets.model.PetData;
import edu.uph.m24si2.studypets.model.QuestData;
import edu.uph.m24si2.studypets.model.UserStats;
import edu.uph.m24si2.studypets.room.AppDatabase;

/**
 * RoomHelper — pengganti RealmHelper, sesuai pola materi dosen.
 * Semua operasi database langsung panggil DAO, tidak perlu Thread terpisah
 * karena AppDatabase.allowMainThreadQueries() sudah diaktifkan.
 *
 * Pola pemakaian sama dengan materi:
 *   AppDatabase db = AppDatabase.getDatabase(this);
 *   db.mahasiswaDAO().insertMahasiswa(mahasiswa);
 */
public class RoomHelper {

    private static RoomHelper instance;
    private AppDatabase db; // objek database Room

    // Singleton — satu objek untuk seluruh app
    // Kalau belum di-init (edge case), kembalikan null agar bisa di-handle di luar
    public static synchronized RoomHelper getInstance() {
        return instance;
    }

    // Helper: ambil instance, init otomatis kalau belum ada (butuh Context)
    public static synchronized RoomHelper getOrInit(Context context) {
        if (instance == null) {
            init(context);
        }
        return instance;
    }

    // Inisialisasi — panggil di StudyPetsApp.onCreate()
    public static void init(Context context) {
        if (instance == null) {
            instance = new RoomHelper(context);
        }
    }

    private RoomHelper(Context context) {
        // Ambil instance AppDatabase (singleton)
        db = AppDatabase.getInstance(context);
        // Seed data awal kalau belum ada
        seedShopItems();
        seedAchievements();
    }

    // =====================================================
    //  USER STATS
    // =====================================================

    // Buat data user baru saat register
    public void initializeUserStats(String username) {
        // Hapus data lama dulu (kalau ada)
        db.userStatsDao().hapusSemua();

        // Buat objek UserStats baru
        UserStats u = new UserStats();
        u.id       = 1;
        u.username = username;
        u.level    = 1;
        u.xp       = 0;
        u.coins    = 100;
        u.totalQuestsCompleted = 0;
        u.learningStreak       = 0;

        // Simpan ke database — persis seperti pola dosen
        // db.mahasiswaDAO().insertMahasiswa(mahasiswa)
        db.userStatsDao().simpan(u);
    }

    // Ambil data user dari database
    public UserStats getUserStats() {
        return db.userStatsDao().getUser();
    }

    // Tambah XP, lalu cek level up
    public void addXP(int xp) {
        if (isPetDropMotivation()) {
            xp = Math.max(1, xp / 2); // XP setengah kalau pet lapar/sedih
        }
        db.userStatsDao().tambahXp(xp);
        cekLevelUp();
        checkAchievements();
    }

    // Tambah koin
    public void addCoins(int coins) {
        db.userStatsDao().tambahKoin(coins);
    }

    // Kurangi koin
    public void spendCoins(int coins) {
        db.userStatsDao().kurangiKoin(coins);
    }

    // Tambah hitungan quest selesai
    public void incrementQuestCount() {
        db.userStatsDao().tambahQuestSelesai();
    }

    // Cek apakah XP cukup untuk naik level
    private void cekLevelUp() {
        UserStats u = db.userStatsDao().getUser();
        if (u == null) return;
        int xpDibutuhkan = u.level * 100;
        if (u.xp >= xpDibutuhkan) {
            u.level = u.level + 1;
            u.xp    = u.xp - xpDibutuhkan;
            db.userStatsDao().update(u); // update pakai @Update
        }
    }

    // =====================================================
    //  PET DATA
    // =====================================================

    // Buat data pet baru saat register
    public void initializePet(String petType, String petName) {
        db.petDataDao().hapusSemua();

        PetData p = new PetData();
        p.id          = 1;
        p.petType     = petType;
        p.petName     = petName;
        p.hunger      = 100;
        p.thirst      = 100;
        p.health      = 100;
        p.mood        = 100;
        p.lastFedTime = getCurrentTime();

        db.petDataDao().simpan(p);
    }

    // Ganti nama pet
    public void renamePet(String namaBaru) {
        db.petDataDao().gantiNama(namaBaru);
    }

    // Ambil data pet dari database
    public PetData getPetStats() {
        return db.petDataDao().getPet();
    }

    // Beri makan / rawat pet
    public void feedPet(String tipeItem, int nilai) {
        switch (tipeItem) {
            case "food":   db.petDataDao().tambahHunger(nilai); break;
            case "drink":  db.petDataDao().tambahThirst(nilai); break;
            case "health": db.petDataDao().tambahHealth(nilai); break;
            case "mood":   db.petDataDao().tambahMood(nilai);   break;
        }
        db.petDataDao().updateWaktuMakan(getCurrentTime());
        checkAchievements();
    }

    // Kurangi stats pet setiap kali onResume
    public void decreasePetStats() {
        db.petDataDao().kurangiStats(5, 5, 2);
    }

    // Cek apakah pet lapar/sedih (Drop Motivation)
    public boolean isPetDropMotivation() {
        PetData p = db.petDataDao().getPet();
        if (p == null) return false;
        return p.hunger < 20 || p.mood < 25;
    }

    // =====================================================
    //  QUEST DATA
    // =====================================================

    // Tambah quest baru — kembalikan id yang digenerate
    public long addQuest(String judul, String deskripsi, String mapel,
                         String kesulitan, String deadline) {
        int xp, koin;
        switch (kesulitan.toLowerCase()) {
            case "easy": xp = 10;  koin = 20;  break;
            case "hard": xp = 100; koin = 150; break;
            default:     xp = 30;  koin = 50;  break;
        }

        QuestData q = new QuestData();
        q.title       = judul;
        q.description = deskripsi;
        q.subject     = mapel;
        q.difficulty  = kesulitan;
        q.xpReward    = xp;
        q.coinReward  = koin;
        q.status      = "pending";
        q.deadline    = deadline;
        q.createdAt   = getCurrentTime();

        // @Insert kembalikan id — sama persis dengan materi dosen
        // long mahasiswaid = db.mahasiswaDAO().insertMahasiswa(mahasiswa)
        return db.questDataDao().simpan(q);
    }

    // Ambil quest aktif
    public List<QuestData> getPendingQuests() {
        return db.questDataDao().getQuestAktif();
    }

    // Ambil quest selesai
    public List<QuestData> getCompletedQuests() {
        return db.questDataDao().getQuestSelesai();
    }

    // Ambil semua quest (untuk kalender)
    public List<QuestData> getAllQuests() {
        return db.questDataDao().getSemuaQuest();
    }

    // Selesaikan quest — update status dan beri reward
    public void completeQuest(int idQuest) {
        // Ambil data quest dulu untuk ambil reward
        QuestData q = db.questDataDao().getQuestById(idQuest);
        if (q == null) return;

        // Tandai selesai
        db.questDataDao().selesaikanQuest(idQuest);

        // Hitung XP (setengah kalau Drop Motivation)
        int xp = isPetDropMotivation() ? Math.max(1, q.xpReward / 2) : q.xpReward;

        // Beri reward ke user
        UserStats u = db.userStatsDao().getUser();
        if (u != null) {
            u.xp    = u.xp + xp;
            u.coins = u.coins + q.coinReward;
            u.totalQuestsCompleted = u.totalQuestsCompleted + 1;
            db.userStatsDao().update(u);

            // Cek level up setelah xp bertambah
            int dibutuhkan = u.level * 100;
            if (u.xp >= dibutuhkan) {
                u.level = u.level + 1;
                u.xp    = u.xp - dibutuhkan;
                db.userStatsDao().update(u);
            }
        }
        checkAchievements();
    }

    // Hapus quest
    public void deleteQuest(int idQuest) {
        db.questDataDao().hapusQuest(idQuest);
    }

    // =====================================================
    //  INVENTORY / SHOP
    // =====================================================

    // Ambil semua item untuk shop
    public List<InventoryItem> getAllShopItems() {
        return db.inventoryItemDao().getSemuaItem();
    }

    // Ambil item yang sudah dibeli (quantity > 0)
    public List<InventoryItem> getUserInventory() {
        return db.inventoryItemDao().getInventoryUser();
    }

    // Beli item — kembalikan true kalau berhasil
    public boolean buyItem(int idItem, int jumlah) {
        InventoryItem item = db.inventoryItemDao().getItemById(idItem);
        UserStats user     = db.userStatsDao().getUser();

        if (item == null || user == null) return false;

        int totalHarga = item.price * jumlah;
        if (user.coins < totalHarga) return false; // koin tidak cukup

        // Kurangi koin dan tambah quantity
        db.userStatsDao().kurangiKoin(totalHarga);
        db.inventoryItemDao().tambahQuantity(idItem, jumlah);

        checkAchievements();
        return true;
    }

    // Gunakan item — feed pet dan kurangi quantity
    public boolean useItem(int idItem) {
        InventoryItem item = db.inventoryItemDao().getItemById(idItem);
        if (item == null || item.quantity <= 0) return false;

        // Kurangi quantity
        db.inventoryItemDao().kurangiQuantity(idItem);

        // Feed pet sesuai tipe item
        feedPet(item.type, item.effectValue);
        return true;
    }

    // =====================================================
    //  STATS / CHART
    // =====================================================

    // Hitung quest selesai per tingkat kesulitan
    public int getQuestCountByDifficulty(String kesulitan) {
        return db.questDataDao().hitungQuestSelesaiPerKesulitan(kesulitan);
    }

    // Hitung quest selesai per hari untuk 7 hari terakhir
    public int[] getQuestPerWeek() {
        int[] hasil = new int[7];
        for (int i = 0; i < 7; i++) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -(6 - i));
            String tanggal = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(cal.getTime());
            hasil[i] = db.questDataDao().hitungQuestSelesaiPerTanggal(tanggal);
        }
        return hasil;
    }

    // Hitung total XP akumulasi
    public int getCurrentTotalXP() {
        UserStats u = db.userStatsDao().getUser();
        if (u == null) return 0;
        return (u.level - 1) * u.level / 2 * 100 + u.xp;
    }

    // =====================================================
    //  ACHIEVEMENT
    // =====================================================

    // Ambil semua achievement
    public List<AchievementData> getAllAchievements() {
        return db.achievementDataDao().getSemuaAchievement();
    }

    // Unlock satu achievement berdasarkan judulnya
    private void unlockAchievement(String judul) {
        db.achievementDataDao().unlockAchievement(judul, getCurrentTime());
    }

    // Cek dan unlock semua achievement yang sudah tercapai
    public void checkAchievements() {
        UserStats u = db.userStatsDao().getUser();
        if (u == null) return;

        if (u.totalQuestsCompleted >= 1)  unlockAchievement("🏆 First Quest");
        if (u.totalQuestsCompleted >= 5)  unlockAchievement("📚 Study Starter");
        if (u.totalQuestsCompleted >= 10) unlockAchievement("🔥 On Fire");
        if (u.level >= 5)                 unlockAchievement("⭐ Level 5");
        if (u.level >= 10)                unlockAchievement("🌟 Level 10");
        if (u.coins >= 1000)              unlockAchievement("🪙 Rich Student");

        // Cek quest Hard selesai
        if (db.questDataDao().hitungQuestHardSelesai() >= 1)
            unlockAchievement("💪 Hard Worker");

        // Cek pet mood 100
        PetData p = db.petDataDao().getPet();
        if (p != null && p.mood >= 100) unlockAchievement("🐾 Pet Master");

        // Cek pernah beli item
        if (db.inventoryItemDao().hitungItemDibeli() > 0)
            unlockAchievement("🛒 Shopper");
    }

    // =====================================================
    //  DAILY LOGIN
    // =====================================================

    // Cek login hari ini — return > 0 berarti belum claim
    public int checkDailyLogin() {
        String hariIni = getTodayDate();

        // Cek apakah sudah ada record hari ini
        DailyLoginData dataHariIni = db.dailyLoginDao().getLoginByTanggal(hariIni);

        if (dataHariIni != null) {
            // Sudah ada — return 0 kalau sudah claim, dayNumber kalau belum
            return dataHariIni.claimed ? 0 : dataHariIni.dayNumber;
        }

        // Belum ada record hari ini — hitung streak
        String kemarin = getYesterdayDate();
        DailyLoginData dataKemarin = db.dailyLoginDao().getLoginByTanggal(kemarin);

        int hariKe = (dataKemarin != null) ? dataKemarin.dayNumber + 1 : 1;

        // Simpan record hari ini
        DailyLoginData baru = new DailyLoginData();
        baru.loginDate = hariIni;
        baru.dayNumber = hariKe;
        baru.claimed   = false;
        db.dailyLoginDao().simpan(baru);

        if (hariKe >= 7) unlockAchievement("📅 Weekly Warrior");
        return hariKe;
    }

    // Claim reward harian
    public String claimDailyLoginReward(int hariKe) {
        db.dailyLoginDao().tandaiSudahClaim(getTodayDate());

        String reward;
        switch (hariKe) {
            case 1: addCoins(50);  reward = "+50 Gold 🪙"; break;
            case 2: addCoins(100); reward = "+100 Gold 🪙"; break;
            case 3:
                db.inventoryItemDao().tambahQuantityByNama("🍎 Apple", 3);
                reward = "+3 Rare Food 🍎"; break;
            case 7:
                addCoins(500);
                db.inventoryItemDao().tambahQuantityByNama("🍗 Chicken", 5);
                reward = "🥚 Mystery Egg!\n+500 Gold + 5 Chicken"; break;
            default:
                int bonus = hariKe * 30;
                addCoins(bonus);
                reward = "+" + bonus + " Gold 🪙 (Hari ke-" + hariKe + ")"; break;
        }
        checkAchievements();
        return reward;
    }

    // =====================================================
    //  SEED DATA (data awal saat app pertama kali dibuka)
    // =====================================================

    // Isi data item shop kalau belum ada — pola mirip dengan insert Mahasiswa di materi
    private void seedShopItems() {
        if (db.inventoryItemDao().hitungSemuaItem() > 0) return; // sudah ada, skip

        // Buat dan simpan setiap item — persis seperti pola dosen:
        // Mahasiswa m = new Mahasiswa("Budi", "0312454");
        // db.mahasiswaDAO().insertMahasiswa(m);
        insertItem(1,  "🍎 Apple",    "food",   15, 20);
        insertItem(2,  "🍞 Bread",    "food",   20, 30);
        insertItem(3,  "🍗 Chicken",  "food",   30, 50);
        insertItem(4,  "🍕 Pizza",    "food",   40, 80);
        insertItem(5,  "💧 Water",    "drink",  15, 15);
        insertItem(6,  "🥤 Juice",    "drink",  25, 30);
        insertItem(7,  "🥛 Milk",     "drink",  30, 40);
        insertItem(8,  "💊 Vitamin",  "health", 20, 50);
        insertItem(9,  "💉 Medicine", "health", 35, 100);
        insertItem(10, "🧸 Toy",      "mood",   10, 40);
        insertItem(11, "🎮 Game",     "mood",   25, 70);
    }

    private void insertItem(int id, String nama, String tipe, int efek, int harga) {
        InventoryItem item = new InventoryItem();
        item.id          = id;
        item.name        = nama;
        item.type        = tipe;
        item.effectValue = efek;
        item.quantity    = 0;
        item.price       = harga;
        db.inventoryItemDao().simpan(item);
    }

    private void seedAchievements() {
        if (db.achievementDataDao().hitungSemuaAchievement() > 0) return; // sudah ada, skip

        insertAch(1,  "🏆 First Quest",    "Selesaikan quest pertama",      "🏆");
        insertAch(2,  "📚 Study Starter",  "Selesaikan 5 quest",            "📚");
        insertAch(3,  "🔥 On Fire",        "Selesaikan 10 quest",           "🔥");
        insertAch(4,  "💪 Hard Worker",    "Selesaikan quest Hard pertama", "💪");
        insertAch(5,  "⭐ Level 5",        "Capai Level 5",                 "⭐");
        insertAch(6,  "🌟 Level 10",       "Capai Level 10",                "🌟");
        insertAch(7,  "🐾 Pet Master",     "Jaga pet hingga mood 100%",     "🐾");
        insertAch(8,  "🛒 Shopper",        "Beli item pertama di shop",     "🛒");
        insertAch(9,  "📅 Weekly Warrior", "Login 7 hari berturut-turut",   "📅");
        insertAch(10, "🪙 Rich Student",   "Kumpulkan 1000 koin",           "🪙");
    }

    private void insertAch(int id, String judul, String deskripsi, String ikon) {
        AchievementData a = new AchievementData();
        a.id          = id;
        a.title       = judul;
        a.description = deskripsi;
        a.icon        = ikon;
        a.unlocked    = false;
        db.achievementDataDao().simpan(a);
    }

    // =====================================================
    //  UTILITY
    // =====================================================

    private String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());
    }

    private String getTodayDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());
    }

    private String getYesterdayDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(cal.getTime());
    }
}
