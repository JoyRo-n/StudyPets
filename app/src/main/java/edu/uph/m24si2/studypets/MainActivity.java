package edu.uph.m24si2.studypets;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.switchmaterial.SwitchMaterial;

import edu.uph.m24si2.studypets.database.RoomHelper;
import edu.uph.m24si2.studypets.model.PetData;
import edu.uph.m24si2.studypets.model.UserStats;

// MainActivity = halaman utama / dashboard
public class MainActivity extends AppCompatActivity {

    // db = objek untuk akses database — sama seperti di materi:
    // AppDatabase db = AppDatabase.getDatabase(this)
    RoomHelper db;
    SharedPreferences prefs;

    TextView tvUsername, tvLevel, tvXP, tvCoins;
    ProgressBar pbXP, pbHunger, pbThirst, pbHealth, pbMood;
    ImageView imgPetDisplay;
    TextView tvPetName, tvPetMoodLabel, tvQuestCount;
    Button btnAddQuest, btnFeedPet, btnShop;
    Button btnAchievements, btnStats, btnCalendar, btnLogout, btnRenamePet, btnBelajar;
    Button btnKelolaUser; // Tombol khusus admin
    CardView cardDropMotivation;
    SwitchMaterial switchDarkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ambil instance database — pastikan sudah di-init lewat StudyPetsApp
        db    = RoomHelper.getOrInit(getApplicationContext());
        prefs = getSharedPreferences("studypets_user", MODE_PRIVATE);

        // Set username aktif di RoomHelper — supaya semua query filter by user
        String username = prefs.getString("username", "");
        db.setCurrentUsername(username);

        // Inisialisasi data user baru saat pertama kali login
        if (prefs.getBoolean("is_new_user", false)) {
            String petType = prefs.getString("pet_type", "cat");
            db.initializeUserStats(username);
            db.initializePet(petType, petType.equals("dog") ? "Buddy" : "Mochi");
            db.seedShopItemsForUser(username);
            db.seedAchievementsForUser(username);
            prefs.edit().putBoolean("is_new_user", false).apply();
        }

        inisialisasiView();
        loadData();
        pasangTombol();

        // Animasi entrance — setiap section muncul berurutan dari atas
        animasiEntrance();

        // Minta izin notifikasi di Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }

    // onResume dipanggil setiap kali halaman aktif kembali (misal balik dari ShopActivity)
    @Override
    protected void onResume() {
        super.onResume();
        // Kurangi stats pet dan refresh tampilan
        PetData p = db.getPetStats();
        if (p != null) {
            db.decreasePetStats();
            db.checkAchievements();
        }
        loadData(); // refresh data setelah kembali dari halaman lain
    }

    // Hubungkan variabel Java dengan komponen layout XML
    private void inisialisasiView() {
        tvUsername       = findViewById(R.id.tv_username);
        tvLevel          = findViewById(R.id.tv_level);
        tvXP             = findViewById(R.id.tv_xp);
        tvCoins          = findViewById(R.id.tv_coins);
        pbXP             = findViewById(R.id.pb_xp);
        imgPetDisplay    = findViewById(R.id.img_pet_display);
        tvPetName        = findViewById(R.id.tv_pet_name);
        tvPetMoodLabel   = findViewById(R.id.tv_pet_mood_label);
        pbHunger         = findViewById(R.id.pb_hunger);
        pbThirst         = findViewById(R.id.pb_thirst);
        pbHealth         = findViewById(R.id.pb_health);
        pbMood           = findViewById(R.id.pb_mood);
        tvQuestCount     = findViewById(R.id.tv_quest_count);
        btnAddQuest      = findViewById(R.id.btn_add_quest);
        btnFeedPet       = findViewById(R.id.btn_feed_pet);
        btnShop          = findViewById(R.id.btn_shop);
        btnAchievements  = findViewById(R.id.btn_achievements);
        btnStats         = findViewById(R.id.btn_stats);
        btnCalendar      = findViewById(R.id.btn_calendar);
        btnLogout        = findViewById(R.id.btn_logout);
        btnRenamePet     = findViewById(R.id.btn_rename_pet);
        btnBelajar       = findViewById(R.id.btn_belajar);
        cardDropMotivation = findViewById(R.id.card_drop_motivation);
        switchDarkMode   = findViewById(R.id.switch_dark_mode);

        // Tombol & badge admin — hanya tampil jika user adalah admin
        btnKelolaUser    = findViewById(R.id.btn_kelola_user);
        TextView tvAdminBadge = findViewById(R.id.tv_admin_badge);
        boolean isAdmin  = prefs.getBoolean("is_admin", false);
        btnKelolaUser.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        tvAdminBadge.setVisibility(isAdmin ? View.VISIBLE : View.GONE);

        // Set posisi switch sesuai preferensi tersimpan
        SharedPreferences settings = getSharedPreferences("studypets_settings", MODE_PRIVATE);
        switchDarkMode.setChecked(settings.getBoolean("dark_mode", false));
    }

    // loadData() — pola dari materi dosen:
    // private void loadData() { adapter.setData(db.mahasiswaDAO().getMahasiswa()); }
    private void loadData() {
        // Tampilkan data user
        UserStats u = db.getUserStats();
        if (u != null) {
            int xpDibutuhkan = u.level * 100;
            tvUsername.setText(u.username);
            tvLevel.setText("Level " + u.level);
            tvXP.setText(u.xp + " / " + xpDibutuhkan + " XP");
            tvCoins.setText(u.coins + " 🪙");
            pbXP.setMax(xpDibutuhkan);
            pbXP.setProgress(u.xp);
        }

        // Tampilkan data pet
        PetData p = db.getPetStats();
        if (p != null) {
            imgPetDisplay.setImageResource(
                p.petType.equals("dog") ? R.drawable.pet_dog_new : R.drawable.pet_cat_new
            );
            tvPetName.setText(p.petName + " " + getEmojiMood(p.mood));
            tvPetMoodLabel.setText("Mood: " + getLabelMood(p.mood));
            pbHunger.setProgress(p.hunger);
            pbThirst.setProgress(p.thirst);
            pbHealth.setProgress(p.health);
            pbMood.setProgress(p.mood);
        }

        // Tampilkan jumlah quest
        int aktif   = db.getPendingQuests().size();
        int selesai = db.getCompletedQuests().size();
        tvQuestCount.setText(aktif + " Aktif | " + selesai + " Selesai");

        // Tampilkan atau sembunyikan peringatan Drop Motivation
        if (db.isPetDropMotivation()) {
            cardDropMotivation.setVisibility(View.VISIBLE);
        } else {
            cardDropMotivation.setVisibility(View.GONE);
        }
    }

    // Pasang semua listener tombol
    private void pasangTombol() {
        btnAddQuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, QuestActivity.class));
            }
        });

        btnFeedPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, InventoryActivity.class));
            }
        });

        btnShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ShopActivity.class));
            }
        });

        btnAchievements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AchievementActivity.class));
            }
        });

        btnStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StatsActivity.class));
            }
        });

        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CalendarActivity.class));
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tampilkanDialogLogout();
            }
        });

        btnRenamePet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tampilkanDialogGantiNama();
            }
        });

        btnBelajar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MateriActivity.class));
            }
        });

        // Tombol kelola user — hanya untuk admin
        btnKelolaUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DaftarUserActivity.class));
            }
        });

        // Toggle dark mode
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Simpan preferensi
            getSharedPreferences("studypets_settings", MODE_PRIVATE)
                .edit()
                .putBoolean("dark_mode", isChecked)
                .apply();

            // Apply dark/light mode — Activity akan restart otomatis
            AppCompatDelegate.setDefaultNightMode(
                isChecked
                    ? AppCompatDelegate.MODE_NIGHT_YES
                    : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        imgPetDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] pesanPet = {"Hei! Main yuk! 🎉", "Kamu hebat! ⭐",
                        "Aku sayang kamu~ 🐾", "Sudah belajar? 📚", "Minta cemilan! 🍗"};
                int acak = (int) (Math.random() * pesanPet.length);
                Toast.makeText(MainActivity.this, pesanPet[acak], Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void tampilkanDialogLogout() {
        new AlertDialog.Builder(this)
            .setTitle("🚪 Logout")
            .setMessage("Yakin mau logout? Progress tetap tersimpan.")
            .setPositiveButton("Logout", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    prefs.edit().putBoolean("is_logged_in", false).apply();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            })
            .setNegativeButton("Batal", null)
            .show();
    }

    private void tampilkanDialogGantiNama() {
        final EditText etNamaBaru = new EditText(this);
        etNamaBaru.setHint("Nama baru...");
        etNamaBaru.setPadding(40, 30, 40, 30);

        PetData p = db.getPetStats();
        if (p != null) etNamaBaru.setText(p.petName);

        new AlertDialog.Builder(this)
            .setTitle("✏️ Ganti Nama Pet")
            .setView(etNamaBaru)
            .setPositiveButton("Simpan", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    String namaBaru = etNamaBaru.getText().toString().trim();
                    if (namaBaru.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Nama tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    db.renamePet(namaBaru);
                    Toast.makeText(MainActivity.this, "Nama diganti jadi " + namaBaru + "! 🐾", Toast.LENGTH_SHORT).show();
                    loadData(); // refresh tampilan setelah ganti nama
                }
            })
            .setNegativeButton("Batal", null)
            .show();
    }

    // Cek dan tampilkan popup daily login reward
    private void cekDailyLogin() {
        int hariKe = db.checkDailyLogin();
        if (hariKe > 0) {
            new AlertDialog.Builder(this)
                .setTitle("🎁 Daily Login Reward!")
                .setMessage("Hari ke-" + hariKe + "!\n\nKlaim hadiah harianmu?")
                .setPositiveButton("Klaim! 🎉", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface d, int w) {
                        String reward = db.claimDailyLoginReward(hariKe);
                        Toast.makeText(MainActivity.this, "✅ " + reward, Toast.LENGTH_LONG).show();
                        loadData();
                    }
                })
                .setNegativeButton("Nanti", null)
                .show();
        }
    }

    private void animasiEntrance() {
        // Animasi tiap card muncul berurutan (staggered)
        android.view.View[] views = {
            findViewById(R.id.card_drop_motivation),
            // pet card, quest card, button area dianimasikan via ScrollView parent
        };
        // Animasi sederhana: seluruh content area fade+slide dari bawah
        android.view.View content = ((android.widget.ScrollView) findViewById(android.R.id.content)
                .getRootView().findViewWithTag("scroll"));

        // Animasi pada ScrollView child
        android.view.View scrollChild = ((android.widget.ScrollView)
                getWindow().getDecorView().findViewWithTag("main_scroll")) != null
                ? getWindow().getDecorView().findViewWithTag("main_scroll")
                : null;

        // Fallback: animasi langsung pada root
        android.view.View root = getWindow().getDecorView().findViewById(android.R.id.content);
        if (root != null) {
            root.setAlpha(0f);
            root.animate().alpha(1f).setDuration(400).setStartDelay(50).start();
        }
    }

    private String getEmojiMood(int mood) {
        if (mood >= 80) return "😊";
        if (mood >= 60) return "🙂";
        if (mood >= 40) return "😐";
        if (mood >= 20) return "😟";
        return "😢";
    }

    private String getLabelMood(int mood) {
        if (mood >= 80) return "Sangat Senang 😊";
        if (mood >= 60) return "Senang 🙂";
        if (mood >= 40) return "Biasa Saja 😐";
        if (mood >= 20) return "Sedih 😟";
        return "Sangat Sedih 😢";
    }
}
