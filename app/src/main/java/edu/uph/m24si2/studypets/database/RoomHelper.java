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
    private AppDatabase db;
    private String currentUsername = ""; // username yang sedang login

    // Singleton
    public static synchronized RoomHelper getInstance() {
        return instance;
    }

    public static synchronized RoomHelper getOrInit(Context context) {
        if (instance == null) {
            init(context);
        }
        return instance;
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new RoomHelper(context);
        }
    }

    private RoomHelper(Context context) {
        db = AppDatabase.getInstance(context);
        seedShopItems();
        seedAchievements();
    }

    // Set username yang sedang aktif — dipanggil saat login berhasil
    public void setCurrentUsername(String username) {
        this.currentUsername = username;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    // =====================================================
    //  USER STATS
    // =====================================================

    public void initializeUserStats(String username) {
        // Hanya buat kalau belum ada data untuk user ini
        if (db.userStatsDao().getUser(username) != null) return;

        UserStats u = new UserStats();
        u.username = username;
        u.level    = 1;
        u.xp       = 0;
        u.coins    = 100;
        u.totalQuestsCompleted = 0;
        u.learningStreak       = 0;
        db.userStatsDao().simpan(u);
    }

    public UserStats getUserStats() {
        return db.userStatsDao().getUser(currentUsername);
    }

    public void addXP(int xp) {
        if (isPetDropMotivation()) xp = Math.max(1, xp / 2);
        db.userStatsDao().tambahXp(currentUsername, xp);
        cekLevelUp();
        checkAchievements();
    }

    public void addCoins(int coins) {
        db.userStatsDao().tambahKoin(currentUsername, coins);
    }

    public void spendCoins(int coins) {
        db.userStatsDao().kurangiKoin(currentUsername, coins);
    }

    public void incrementQuestCount() {
        db.userStatsDao().tambahQuestSelesai(currentUsername);
    }

    private void cekLevelUp() {
        UserStats u = db.userStatsDao().getUser(currentUsername);
        if (u == null) return;
        int xpDibutuhkan = u.level * 100;
        if (u.xp >= xpDibutuhkan) {
            u.level = u.level + 1;
            u.xp    = u.xp - xpDibutuhkan;
            db.userStatsDao().update(u);
        }
    }

    // =====================================================
    //  PET DATA
    // =====================================================

    public void initializePet(String petType, String petName) {
        // Hanya buat kalau belum ada pet untuk user ini
        if (db.petDataDao().getPet(currentUsername) != null) return;

        PetData p = new PetData();
        p.username    = currentUsername;
        p.petType     = petType;
        p.petName     = petName;
        p.hunger      = 100;
        p.thirst      = 100;
        p.health      = 100;
        p.mood        = 100;
        p.lastFedTime = getCurrentTime();
        db.petDataDao().simpan(p);
    }

    public void renamePet(String namaBaru) {
        db.petDataDao().gantiNama(currentUsername, namaBaru);
    }

    public PetData getPetStats() {
        return db.petDataDao().getPet(currentUsername);
    }

    public void feedPet(String tipeItem, int nilai) {
        switch (tipeItem) {
            case "food":   db.petDataDao().tambahHunger(currentUsername, nilai); break;
            case "drink":  db.petDataDao().tambahThirst(currentUsername, nilai); break;
            case "health": db.petDataDao().tambahHealth(currentUsername, nilai); break;
            case "mood":   db.petDataDao().tambahMood(currentUsername, nilai);   break;
        }
        db.petDataDao().updateWaktuMakan(currentUsername, getCurrentTime());
        checkAchievements();
    }

    public void decreasePetStats() {
        db.petDataDao().kurangiStats(currentUsername, 5, 5, 2);
    }

    public boolean isPetDropMotivation() {
        PetData p = db.petDataDao().getPet(currentUsername);
        if (p == null) return false;
        return p.hunger < 20 || p.mood < 25;
    }

    // =====================================================
    //  QUEST DATA
    // =====================================================

    // Tambah quest baru — masukkan username otomatis
    public long addQuest(String judul, String deskripsi, String mapel,
                         String kesulitan, String deadline) {
        int xp, koin;
        switch (kesulitan.toLowerCase()) {
            case "easy": xp = 10;  koin = 20;  break;
            case "hard": xp = 100; koin = 150; break;
            default:     xp = 30;  koin = 50;  break;
        }

        QuestData q = new QuestData();
        q.username    = currentUsername; // PENTING: isi username pemilik quest
        q.title       = judul;
        q.description = deskripsi;
        q.subject     = mapel;
        q.difficulty  = kesulitan;
        q.xpReward    = xp;
        q.coinReward  = koin;
        q.status      = "pending";
        q.deadline    = deadline;
        q.createdAt   = getCurrentTime();

        return db.questDataDao().simpan(q);
    }

    // Ambil quest aktif milik user yang sedang login
    public List<QuestData> getPendingQuests() {
        return db.questDataDao().getQuestAktif(currentUsername);
    }

    // Ambil quest selesai milik user yang sedang login
    public List<QuestData> getCompletedQuests() {
        return db.questDataDao().getQuestSelesai(currentUsername);
    }

    // Ambil semua quest milik user yang sedang login
    public List<QuestData> getAllQuests() {
        return db.questDataDao().getSemuaQuest(currentUsername);
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

    public List<InventoryItem> getAllShopItems() {
        return db.inventoryItemDao().getSemuaItem(currentUsername);
    }

    public List<InventoryItem> getUserInventory() {
        return db.inventoryItemDao().getInventoryUser(currentUsername);
    }

    public boolean buyItem(int idItem, int jumlah) {
        InventoryItem item = db.inventoryItemDao().getItemById(currentUsername, idItem);
        UserStats user     = db.userStatsDao().getUser(currentUsername);

        if (item == null || user == null) return false;

        int totalHarga = item.price * jumlah;
        if (user.coins < totalHarga) return false;

        db.userStatsDao().kurangiKoin(currentUsername, totalHarga);
        db.inventoryItemDao().tambahQuantity(currentUsername, idItem, jumlah);

        checkAchievements();
        return true;
    }

    public boolean useItem(int idItem) {
        InventoryItem item = db.inventoryItemDao().getItemById(currentUsername, idItem);
        if (item == null || item.quantity <= 0) return false;

        db.inventoryItemDao().kurangiQuantity(currentUsername, idItem);
        feedPet(item.type, item.effectValue);
        return true;
    }

    // =====================================================
    //  STATS / CHART
    // =====================================================

    // Hitung quest selesai per tingkat kesulitan
    public int getQuestCountByDifficulty(String kesulitan) {
        return db.questDataDao().hitungQuestSelesaiPerKesulitan(currentUsername, kesulitan);
    }

    // Hitung quest selesai per hari untuk 7 hari terakhir
    public int[] getQuestPerWeek() {
        int[] hasil = new int[7];
        for (int i = 0; i < 7; i++) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -(6 - i));
            String tanggal = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(cal.getTime());
            hasil[i] = db.questDataDao().hitungQuestSelesaiPerTanggal(currentUsername, tanggal);
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

    public List<AchievementData> getAllAchievements() {
        return db.achievementDataDao().getSemuaAchievement(currentUsername);
    }

    private void unlockAchievement(String judul) {
        db.achievementDataDao().unlockAchievement(currentUsername, judul, getCurrentTime());
    }

    public void checkAchievements() {
        UserStats u = db.userStatsDao().getUser(currentUsername);
        if (u == null) return;

        if (u.totalQuestsCompleted >= 1)  unlockAchievement("🏆 First Quest");
        if (u.totalQuestsCompleted >= 5)  unlockAchievement("📚 Study Starter");
        if (u.totalQuestsCompleted >= 10) unlockAchievement("🔥 On Fire");
        if (u.level >= 5)                 unlockAchievement("⭐ Level 5");
        if (u.level >= 10)                unlockAchievement("🌟 Level 10");
        if (u.coins >= 1000)              unlockAchievement("🪙 Rich Student");

        if (db.questDataDao().hitungQuestHardSelesai(currentUsername) >= 1)
            unlockAchievement("💪 Hard Worker");

        PetData p = db.petDataDao().getPet(currentUsername);
        if (p != null && p.mood >= 100) unlockAchievement("🐾 Pet Master");

        if (db.inventoryItemDao().hitungItemDibeli(currentUsername) > 0)
            unlockAchievement("🛒 Shopper");
    }

    // =====================================================
    //  DAILY LOGIN
    // =====================================================

    public int checkDailyLogin() {
        String hariIni = getTodayDate();

        DailyLoginData dataHariIni = db.dailyLoginDao().getLoginByTanggal(currentUsername, hariIni);

        if (dataHariIni != null) {
            return dataHariIni.claimed ? 0 : dataHariIni.dayNumber;
        }

        String kemarin = getYesterdayDate();
        DailyLoginData dataKemarin = db.dailyLoginDao().getLoginKemarin(currentUsername, kemarin);

        int hariKe = (dataKemarin != null) ? dataKemarin.dayNumber + 1 : 1;

        DailyLoginData baru = new DailyLoginData();
        baru.username  = currentUsername;
        baru.loginDate = hariIni;
        baru.dayNumber = hariKe;
        baru.claimed   = false;
        db.dailyLoginDao().simpan(baru);

        if (hariKe >= 7) unlockAchievement("📅 Weekly Warrior");
        return hariKe;
    }

    public String claimDailyLoginReward(int hariKe) {
        db.dailyLoginDao().tandaiSudahClaim(currentUsername, getTodayDate());

        String reward;
        switch (hariKe) {
            case 1: addCoins(50);  reward = "+50 Gold 🪙"; break;
            case 2: addCoins(100); reward = "+100 Gold 🪙"; break;
            case 3:
                db.inventoryItemDao().tambahQuantityByNama(currentUsername, "🍎 Apple", 3);
                reward = "+3 Rare Food 🍎"; break;
            case 7:
                addCoins(500);
                db.inventoryItemDao().tambahQuantityByNama(currentUsername, "🍗 Chicken", 5);
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
    //  SEED DATA — dipanggil saat user baru pertama login
    // =====================================================

    private void seedShopItems() {
        // Tidak perlu seed global lagi — seed dilakukan per user saat initializeUserStats
    }

    // Seed item shop untuk user baru
    public void seedShopItemsForUser(String username) {
        if (db.inventoryItemDao().hitungItemUser(username) > 0) return;

        insertItem(username, 1,  "🍎 Apple",    "food",   15, 20);
        insertItem(username, 2,  "🍞 Bread",    "food",   20, 30);
        insertItem(username, 3,  "🍗 Chicken",  "food",   30, 50);
        insertItem(username, 4,  "🍕 Pizza",    "food",   40, 80);
        insertItem(username, 5,  "💧 Water",    "drink",  15, 15);
        insertItem(username, 6,  "🥤 Juice",    "drink",  25, 30);
        insertItem(username, 7,  "🥛 Milk",     "drink",  30, 40);
        insertItem(username, 8,  "💊 Vitamin",  "health", 20, 50);
        insertItem(username, 9,  "💉 Medicine", "health", 35, 100);
        insertItem(username, 10, "🧸 Toy",      "mood",   10, 40);
        insertItem(username, 11, "🎮 Game",     "mood",   25, 70);
    }

    private void insertItem(String username, int id, String nama, String tipe, int efek, int harga) {
        InventoryItem item = new InventoryItem();
        item.username    = username;
        item.id          = id;
        item.name        = nama;
        item.type        = tipe;
        item.effectValue = efek;
        item.quantity    = 0;
        item.price       = harga;
        db.inventoryItemDao().simpan(item);
    }

    private void seedAchievements() {
        // Tidak perlu seed global lagi — seed dilakukan per user
    }

    // Seed achievement untuk user baru
    public void seedAchievementsForUser(String username) {
        if (db.achievementDataDao().hitungSemuaAchievement(username) > 0) return;

        insertAch(username, 1,  "🏆 First Quest",    "Selesaikan quest pertama",      "🏆");
        insertAch(username, 2,  "📚 Study Starter",  "Selesaikan 5 quest",            "📚");
        insertAch(username, 3,  "🔥 On Fire",        "Selesaikan 10 quest",           "🔥");
        insertAch(username, 4,  "💪 Hard Worker",    "Selesaikan quest Hard pertama", "💪");
        insertAch(username, 5,  "⭐ Level 5",        "Capai Level 5",                 "⭐");
        insertAch(username, 6,  "🌟 Level 10",       "Capai Level 10",                "🌟");
        insertAch(username, 7,  "🐾 Pet Master",     "Jaga pet hingga mood 100%",     "🐾");
        insertAch(username, 8,  "🛒 Shopper",        "Beli item pertama di shop",     "🛒");
        insertAch(username, 9,  "📅 Weekly Warrior", "Login 7 hari berturut-turut",   "📅");
        insertAch(username, 10, "🪙 Rich Student",   "Kumpulkan 1000 koin",           "🪙");
    }

    private void insertAch(String username, int id, String judul, String deskripsi, String ikon) {
        AchievementData a = new AchievementData();
        a.username    = username;
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
