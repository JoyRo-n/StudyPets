package edu.uph.m24si2.studypets;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import edu.uph.m24si2.studypets.database.RoomHelper;
/**
 * TambahQuestActivity — halaman untuk menambah quest baru.
 *
 * Pola persis seperti TambahActivity dari materi dosen:
 *   db = AppDatabase.getDatabase(this)
 *   Quest quest = new Quest(...)
 *   db.questDataDao().insertQuest(quest)
 *   finish()
 */
public class TambahQuestActivity extends AppCompatActivity {

    EditText etJudul, etDeskripsi;
    Spinner  spMapel, spKesulitan;
    Button   btnPilihTanggal, btnSimpan;
    TextView tvTanggal;

    RoomHelper db;
    String deadlineDipilih = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_quest);

        // Ambil instance database — persis seperti materi dosen:
        // AppDatabase db = AppDatabase.getDatabase(this)
        db = RoomHelper.getOrInit(this);

        // Hubungkan variabel dengan komponen layout
        etJudul       = findViewById(R.id.et_quest_title);
        etDeskripsi   = findViewById(R.id.et_quest_desc);
        spMapel       = findViewById(R.id.sp_quest_subject);
        spKesulitan   = findViewById(R.id.sp_quest_difficulty);
        btnPilihTanggal = findViewById(R.id.btn_pick_deadline);
        tvTanggal     = findViewById(R.id.tv_deadline_display);
        btnSimpan     = findViewById(R.id.btn_save_quest);

        // Isi spinner mata pelajaran
        String[] mataPelajaran = {"Matematika", "Fisika", "Kimia", "Biologi",
                "Bahasa Indonesia", "Bahasa Inggris", "Sejarah", "Lainnya"};
        spMapel.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, mataPelajaran));

        // Isi spinner kesulitan
        String[] kesulitan = {"Easy", "Medium", "Hard"};
        spKesulitan.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, kesulitan));
        spKesulitan.setSelection(1); // default Medium

        // Tombol pilih tanggal deadline
        btnPilihTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar kalender = Calendar.getInstance();
                DatePickerDialog picker = new DatePickerDialog(
                    TambahQuestActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(android.widget.DatePicker view,
                                              int tahun, int bulan, int hari) {
                            kalender.set(tahun, bulan, hari);
                            deadlineDipilih = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    .format(kalender.getTime());
                            tvTanggal.setText("📅 " + deadlineDipilih);
                        }
                    },
                    kalender.get(Calendar.YEAR),
                    kalender.get(Calendar.MONTH),
                    kalender.get(Calendar.DAY_OF_MONTH)
                );
                picker.getDatePicker().setMinDate(System.currentTimeMillis());
                picker.show();
            }
        });

        // Tombol simpan — persis pola materi dosen:
        // btnSimpan.setOnClickListener → ambil data → simpan ke db → finish()
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String judul     = etJudul.getText().toString().trim();
                String deskripsi = etDeskripsi.getText().toString().trim();
                String mapel     = spMapel.getSelectedItem().toString();
                String tingkat   = spKesulitan.getSelectedItem().toString();

                // Validasi
                if (judul.isEmpty()) {
                    Toast.makeText(TambahQuestActivity.this,
                        "Judul quest harus diisi!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (deadlineDipilih.isEmpty()) {
                    Toast.makeText(TambahQuestActivity.this,
                        "Pilih deadline dulu!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Simpan ke database — pola dari materi dosen:
                // long id = db.mahasiswaDAO().insertMahasiswa(mahasiswa)
                long idBaru = db.addQuest(judul, deskripsi, mapel, tingkat, deadlineDipilih);

                if (idBaru > 0) {
                    // Set alarm notifikasi deadline jam 8 pagi di tanggal deadline
                    DeadlineAlarmHelper.setAlarm(
                        TambahQuestActivity.this,
                        (int) idBaru,
                        judul,
                        deadlineDipilih
                    );

                    Toast.makeText(TambahQuestActivity.this,
                        "✅ Quest berhasil ditambahkan!\n⏰ Pengingat deadline diaktifkan",
                        Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(TambahQuestActivity.this,
                        "Gagal menambah quest!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Tombol batal/kembali
        Button btnBatal = findViewById(R.id.btn_cancel_quest);
        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
