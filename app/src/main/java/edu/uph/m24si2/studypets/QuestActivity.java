package edu.uph.m24si2.studypets;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import edu.uph.m24si2.studypets.adapter.QuestAdapter;
import edu.uph.m24si2.studypets.database.RoomHelper;
import edu.uph.m24si2.studypets.model.Quest;
import edu.uph.m24si2.studypets.model.QuestData;
// QuestActivity = halaman daftar quest
// Pola: db.questDataDao().getQuestAktif() → adapter.setData() → RecyclerView
public class QuestActivity extends AppCompatActivity {

    // Database — sama seperti pola materi dosen
    RoomHelper db;

    RecyclerView rvQuests;
    QuestAdapter adapter;
    List<Quest> daftarQuest;
    TabLayout tabLayout;
    TextView tvKosong;
    String deadlineDipilih = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest);

        db = RoomHelper.getOrInit(this);
        daftarQuest = new ArrayList<>();

        rvQuests  = findViewById(R.id.rv_quests);
        tabLayout = findViewById(R.id.tab_quest);
        tvKosong  = findViewById(R.id.tv_empty_quest);
        FloatingActionButton fabTambah = findViewById(R.id.fab_add_quest);

        rvQuests.setLayoutManager(new LinearLayoutManager(this));
        adapter = new QuestAdapter(this, daftarQuest,
            new QuestAdapter.OnQuestCompleteListener() {
                @Override
                public void onComplete(Quest quest) {
                    tampilkanDialogSelesai(quest);
                }
            },
            new QuestAdapter.OnQuestDeleteListener() {
                @Override
                public void onDelete(Quest quest) {
                    tampilkanDialogHapus(quest);
                }
            }
        );
        rvQuests.setAdapter(adapter);

        loadData(); // pola loadData() sesuai materi dosen

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadData(); // refresh saat pindah tab
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        fabTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Buka TambahQuestActivity — pola Intent dari materi dosen:
                // Intent intent = new Intent(this, TambahActivity.class);
                // startActivity(intent);
                startActivity(new Intent(QuestActivity.this, TambahQuestActivity.class));
            }
        });

        Button btnBack = findViewById(R.id.btn_back_quest);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });
    }

    // Refresh data saat kembali dari TambahQuestActivity
    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    // loadData() — sama persis dengan pola materi dosen
    private void loadData() {
        daftarQuest.clear();

        List<QuestData> hasil;
        String pesanKosong;

        if (tabLayout.getSelectedTabPosition() == 0) {
            hasil       = db.getPendingQuests();
            pesanKosong = "Belum ada quest aktif 📝\nTambah quest baru yuk!";
        } else {
            hasil       = db.getCompletedQuests();
            pesanKosong = "Belum ada quest selesai 🎯";
        }

        if (hasil.isEmpty()) {
            tvKosong.setVisibility(View.VISIBLE);
            tvKosong.setText(pesanKosong);
            rvQuests.setVisibility(View.GONE);
        } else {
            tvKosong.setVisibility(View.GONE);
            rvQuests.setVisibility(View.VISIBLE);
            for (QuestData q : hasil) {
                daftarQuest.add(ubahKeModel(q));
            }
        }

        // adapter.setData() — pola dari materi dosen
        adapter.setData(daftarQuest);
    }

    private void tampilkanDialogSelesai(Quest quest) {
        new AlertDialog.Builder(this)
            .setTitle("🎉 Selesaikan Quest?")
            .setMessage("Quest: " + quest.getTitle() + "\n\nHadiah:\n"
                    + "• " + quest.getXpReward() + " XP ⚡\n"
                    + "• " + quest.getCoinReward() + " Koin 🪙")
            .setPositiveButton("Claim!", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    db.completeQuest(quest.getId()); // panggil DAO melalui RoomHelper
                    Toast.makeText(QuestActivity.this,
                        "🎊 Quest Selesai! +" + quest.getXpReward() + " XP",
                        Toast.LENGTH_SHORT).show();
                    loadData(); // refresh list
                }
            })
            .setNegativeButton("Batal", null)
            .show();
    }

    private void tampilkanDialogHapus(Quest quest) {
        new AlertDialog.Builder(this)
            .setTitle("🗑️ Hapus Quest?")
            .setMessage("Quest \"" + quest.getTitle() + "\" akan dihapus.")
            .setPositiveButton("Hapus", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    // Cancel alarm deadline sebelum hapus
                    DeadlineAlarmHelper.cancelAlarm(QuestActivity.this, quest.getId());
                    // @Delete — pola dari materi dosen
                    db.deleteQuest(quest.getId());
                    Toast.makeText(QuestActivity.this, "Quest dihapus", Toast.LENGTH_SHORT).show();
                    loadData();
                }
            })
            .setNegativeButton("Batal", null)
            .show();
    }

    // Konversi QuestData (Room Entity) ke Quest (plain model untuk adapter)
    private Quest ubahKeModel(QuestData q) {
        return new Quest(q.id, q.title, q.description, q.subject,
                q.difficulty, q.xpReward, q.coinReward, q.status, q.deadline);
    }
}
