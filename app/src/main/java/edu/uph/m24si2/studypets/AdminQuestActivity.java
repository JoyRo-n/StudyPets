package edu.uph.m24si2.studypets;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.uph.m24si2.studypets.database.RoomHelper;
import edu.uph.m24si2.studypets.model.RiwayatBelajar;

public class AdminQuestActivity extends AppCompatActivity {

    Spinner spinnerUser, spinnerMapel, spinnerKesulitan;
    EditText etJudul, etDeskripsi;
    Button btnDeadline, btnBuatQuest, btnBack;
    TextView tvInfoReward, tvNoRiwayat;
    RecyclerView rvRiwayat;

    RoomHelper db;
    String selectedUsername = "";
    String selectedDeadline = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_quest);

        // Cek akses admin
        SharedPreferences prefs = getSharedPreferences("studypets_user", MODE_PRIVATE);
        if (!prefs.getBoolean("is_admin", false)) {
            Toast.makeText(this, "⛔ Akses ditolak!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db = RoomHelper.getOrInit(this);

        spinnerUser      = findViewById(R.id.spinner_user);
        spinnerMapel     = findViewById(R.id.spinner_mapel);
        spinnerKesulitan = findViewById(R.id.spinner_kesulitan);
        etJudul          = findViewById(R.id.et_judul_quest);
        etDeskripsi      = findViewById(R.id.et_deskripsi_quest);
        btnDeadline      = findViewById(R.id.btn_pilih_deadline);
        btnBuatQuest     = findViewById(R.id.btn_buat_quest_admin);
        tvInfoReward     = findViewById(R.id.tv_info_reward);
        tvNoRiwayat      = findViewById(R.id.tv_no_riwayat);
        rvRiwayat        = findViewById(R.id.rv_riwayat_belajar);
        btnBack          = findViewById(R.id.btn_back_admin_quest);

        rvRiwayat.setLayoutManager(new LinearLayoutManager(this));

        setupSpinnerUser();
        setupSpinnerMapel();
        setupSpinnerKesulitan();

        btnBack.setOnClickListener(v -> finish());

        btnDeadline.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, y, m, d) -> {
                selectedDeadline = y + "-" + String.format("%02d", m + 1) + "-" + String.format("%02d", d);
                btnDeadline.setText("📅 " + selectedDeadline);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnBuatQuest.setOnClickListener(v -> buatQuest());
    }

    private void setupSpinnerUser() {
        // Ambil semua username yang punya riwayat belajar
        List<String> usernames = db.getDaftarUsernameAktif();

        // Tambahkan juga semua user yang terdaftar (bukan hanya yang punya riwayat)
        List<String> semuaUser = new ArrayList<>();
        semuaUser.add("-- Pilih User --");

        // Ambil dari database user
        List<edu.uph.m24si2.studypets.room.UserEntity> daftarUser =
                db.getAppDatabase().userDao().getSemuaUser();
        for (edu.uph.m24si2.studypets.room.UserEntity u : daftarUser) {
            if (!u.isAdmin) semuaUser.add(u.username); // jangan tampilkan admin
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, semuaUser);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUser.setAdapter(adapter);

        spinnerUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos == 0) {
                    selectedUsername = "";
                    tvNoRiwayat.setVisibility(View.VISIBLE);
                    rvRiwayat.setVisibility(View.GONE);
                    return;
                }
                selectedUsername = semuaUser.get(pos);
                loadRiwayatBelajar(selectedUsername);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadRiwayatBelajar(String username) {
        List<RiwayatBelajar> riwayat = db.getAppDatabase()
                .riwayatBelajarDao().getRiwayatUser(username);

        if (riwayat.isEmpty()) {
            tvNoRiwayat.setVisibility(View.VISIBLE);
            rvRiwayat.setVisibility(View.GONE);
            return;
        }

        tvNoRiwayat.setVisibility(View.GONE);
        rvRiwayat.setVisibility(View.VISIBLE);

        // Adapter inline untuk riwayat belajar
        rvRiwayat.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = getLayoutInflater().inflate(R.layout.item_riwayat_belajar, parent, false);
                return new RecyclerView.ViewHolder(v) {};
            }
            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int pos) {
                RiwayatBelajar r = riwayat.get(pos);
                TextView tvNilai   = holder.itemView.findViewById(R.id.tv_nilai_riwayat);
                TextView tvTopik   = holder.itemView.findViewById(R.id.tv_topik_riwayat);
                TextView tvDetail  = holder.itemView.findViewById(R.id.tv_detail_riwayat);
                TextView tvTanggal = holder.itemView.findViewById(R.id.tv_tanggal_riwayat);

                tvNilai.setText(String.valueOf(r.nilai));
                tvTopik.setText(r.topik);
                tvDetail.setText(r.benar + "/" + r.totalSoal + " benar");
                // Ambil tanggal saja (tanpa jam)
                tvTanggal.setText(r.tanggal.length() >= 10 ? r.tanggal.substring(0, 10) : r.tanggal);

                // Warna nilai
                int warna;
                if (r.nilai >= 80)      warna = 0xFF4CAF50; // hijau
                else if (r.nilai >= 60) warna = 0xFFFF9800; // oranye
                else                    warna = 0xFFF44336; // merah
                tvNilai.setBackgroundColor(warna);

                // Auto-isi mapel di spinner berdasarkan topik riwayat
                holder.itemView.setOnClickListener(v -> {
                    autoIsiFormDariRiwayat(r);
                });
            }
            @Override public int getItemCount() { return riwayat.size(); }
        });
    }

    // Tap item riwayat → auto isi form quest
    private void autoIsiFormDariRiwayat(RiwayatBelajar r) {
        // Set mapel spinner
        String[] mapelList = {"Matematika", "Fisika", "Kimia", "Biologi",
                "Bahasa Indonesia", "Bahasa Inggris", "Sejarah", "Geografi"};
        for (int i = 0; i < mapelList.length; i++) {
            if (mapelList[i].equals(r.topik)) {
                spinnerMapel.setSelection(i);
                break;
            }
        }

        // Saran judul dan deskripsi
        String saran = r.nilai < 60
                ? "Pelajari ulang materi " + r.topik
                : "Latihan soal lanjutan " + r.topik;
        etJudul.setText(saran);
        etDeskripsi.setText("Berdasarkan hasil kuis " + r.topik +
                " dengan nilai " + r.nilai + "/100. " +
                (r.nilai < 60 ? "Perlu dipelajari ulang." : "Tingkatkan dengan soal lebih sulit."));

        // Saran kesulitan berdasarkan nilai
        if (r.nilai >= 80) spinnerKesulitan.setSelection(2);      // Hard
        else if (r.nilai >= 60) spinnerKesulitan.setSelection(1); // Medium
        else spinnerKesulitan.setSelection(0);                    // Easy

        Toast.makeText(this, "✅ Form diisi otomatis dari riwayat!", Toast.LENGTH_SHORT).show();
    }

    private void setupSpinnerMapel() {
        String[] mapel = {"Matematika", "Fisika", "Kimia", "Biologi",
                "Bahasa Indonesia", "Bahasa Inggris", "Sejarah", "Geografi"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, mapel);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMapel.setAdapter(adapter);
    }

    private void setupSpinnerKesulitan() {
        String[] kesulitan = {"Easy", "Medium", "Hard"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, kesulitan);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKesulitan.setAdapter(adapter);
        spinnerKesulitan.setSelection(1); // default Medium

        spinnerKesulitan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String[] info = {"Easy → 10 XP + 20 🪙", "Medium → 30 XP + 50 🪙", "Hard → 100 XP + 150 🪙"};
                tvInfoReward.setText("💰 Reward: " + info[pos]);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void buatQuest() {
        if (selectedUsername.isEmpty()) {
            Toast.makeText(this, "Pilih user dulu!", Toast.LENGTH_SHORT).show();
            return;
        }

        String judul      = etJudul.getText().toString().trim();
        String deskripsi  = etDeskripsi.getText().toString().trim();
        String mapel      = spinnerMapel.getSelectedItem().toString();
        String kesulitan  = spinnerKesulitan.getSelectedItem().toString();

        if (judul.isEmpty()) {
            Toast.makeText(this, "Isi judul quest!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedDeadline.isEmpty()) {
            Toast.makeText(this, "Pilih deadline!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.addQuestFromAdmin(selectedUsername, judul, deskripsi, mapel, kesulitan, selectedDeadline);

        Toast.makeText(this,
                "✅ Quest berhasil dikirim ke " + selectedUsername + "!",
                Toast.LENGTH_SHORT).show();

        // Reset form
        etJudul.setText("");
        etDeskripsi.setText("");
        selectedDeadline = "";
        btnDeadline.setText("📅 Pilih Tanggal Deadline");
        spinnerKesulitan.setSelection(1);
    }
}
