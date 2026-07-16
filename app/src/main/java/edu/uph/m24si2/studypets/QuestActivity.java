package edu.uph.m24si2.studypets;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import edu.uph.m24si2.studypets.adapter.QuestAdapter;
import edu.uph.m24si2.studypets.database.RoomHelper;
import edu.uph.m24si2.studypets.model.Quest;
import edu.uph.m24si2.studypets.model.QuestData;

public class QuestActivity extends AppCompatActivity {

    RoomHelper db;

    RecyclerView rvQuests;
    QuestAdapter adapter;
    List<Quest> daftarQuest;
    TabLayout tabLayout;
    TextView tvKosong;
    String deadlineDipilih = "";

    // Quest yang sedang menunggu upload bukti
    Quest questPendingBukti = null;

    // Launcher untuk buka galeri foto
    private final ActivityResultLauncher<String> galleryLauncher =
        registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null && questPendingBukti != null) {
                // Ambil persistent permission supaya bisa dibaca nanti
                getContentResolver().takePersistableUriPermission(
                        uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                db.submitBuktiBelajar(questPendingBukti.getId(), uri.toString());
                Toast.makeText(this,
                        "✅ Bukti berhasil dikirim! Menunggu persetujuan admin.",
                        Toast.LENGTH_LONG).show();
                questPendingBukti = null;
                loadData();
            }
        });

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
            quest -> tampilkanDialogSelesai(quest),
            quest -> tampilkanDialogHapus(quest)
        );
        // Pasang listener submit bukti untuk quest admin
        adapter.setSubmitBuktiListener(quest -> {
            questPendingBukti = quest;
            galleryLauncher.launch("image/*");
        });
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
        String pesanReward = quest.isFromAdmin()
                ? "Hadiah:\n• " + quest.getXpReward() + " XP ⚡\n• " + quest.getCoinReward() + " Koin 🪙"
                : "Quest pribadi tidak memberikan reward.\nQuest ini hanya sebagai pengingat belajar.";

        String judulDialog = quest.isFromAdmin() ? "🎉 Selesaikan Quest Admin?" : "✅ Tandai Selesai?";

        new AlertDialog.Builder(this)
            .setTitle(judulDialog)
            .setMessage("Quest: " + quest.getTitle() + "\n\n" + pesanReward)
            .setPositiveButton("Selesai!", (dialog, which) -> {
                db.completeQuest(quest.getId());
                String pesan = quest.isFromAdmin()
                        ? "🎊 Quest Selesai! +" + quest.getXpReward() + " XP"
                        : "✅ Quest ditandai selesai!";
                Toast.makeText(QuestActivity.this, pesan, Toast.LENGTH_SHORT).show();
                loadData();
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
                q.difficulty, q.xpReward, q.coinReward, q.status, q.deadline, q.isFromAdmin, q.buktiPath);
    }
}
