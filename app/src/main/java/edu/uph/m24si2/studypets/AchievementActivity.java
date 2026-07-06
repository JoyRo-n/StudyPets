package edu.uph.m24si2.studypets;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.uph.m24si2.studypets.adapter.AchievementAdapter;
import edu.uph.m24si2.studypets.database.RoomHelper;
import edu.uph.m24si2.studypets.model.Achievement;
import edu.uph.m24si2.studypets.model.AchievementData;

// AchievementActivity = halaman daftar badge/pencapaian
public class AchievementActivity extends AppCompatActivity {

    RoomHelper db;
    RecyclerView rv;
    AchievementAdapter adapter;
    TextView tvJumlah;
    ProgressBar pbProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);

        db = RoomHelper.getOrInit(this);

        tvJumlah   = findViewById(R.id.tv_ach_count);
        pbProgress = findViewById(R.id.pb_ach_progress);

        rv = findViewById(R.id.rv_achievements);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AchievementAdapter(this, new ArrayList<>());
        rv.setAdapter(adapter);

        // Cek achievement dulu sebelum ditampilkan
        db.checkAchievements();
        loadData();

        Button btnBack = findViewById(R.id.btn_back_ach);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });
    }

    // loadData() — ambil achievement dari db, hitung yang sudah di-unlock
    private void loadData() {
        List<AchievementData> hasil = db.getAllAchievements();

        // Konversi ke plain model Achievement untuk adapter
        List<Achievement> daftarAch = new ArrayList<>();
        int jumlahUnlocked = 0;

        for (AchievementData a : hasil) {
            if (a.unlocked) jumlahUnlocked++;
            daftarAch.add(new Achievement(
                a.id, a.title, a.description, a.icon, a.unlocked, a.unlockedAt
            ));
        }

        int total = daftarAch.size();
        tvJumlah.setText(jumlahUnlocked + "/" + total);
        pbProgress.setMax(total);
        pbProgress.setProgress(jumlahUnlocked);

        // setData() — pola dari materi dosen
        adapter.setData(daftarAch);
    }
}
