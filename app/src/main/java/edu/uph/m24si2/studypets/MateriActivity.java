package edu.uph.m24si2.studypets;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

// MateriActivity = halaman untuk memilih materi yang ingin dipelajari
public class MateriActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materi);

        // Tombol kembali
        Button btnBack = findViewById(R.id.btn_back_materi);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Setiap tombol materi membuka VideoActivity dengan nama materi yang dipilih
        // Intent dipakai untuk berpindah Activity dan membawa data (putExtra)
        pasangTombolMateri(R.id.btn_materi_matematika, "Matematika");
        pasangTombolMateri(R.id.btn_materi_fisika,     "Fisika");
        pasangTombolMateri(R.id.btn_materi_kimia,      "Kimia");
        pasangTombolMateri(R.id.btn_materi_biologi,    "Biologi");
        pasangTombolMateri(R.id.btn_materi_bind,       "Bahasa Indonesia");
        pasangTombolMateri(R.id.btn_materi_bing,       "Bahasa Inggris");
        pasangTombolMateri(R.id.btn_materi_sejarah,    "Sejarah");
        pasangTombolMateri(R.id.btn_materi_geografi,   "Geografi");
    }

    // Method pembantu agar tidak perlu menulis kode yang sama berulang
    private void pasangTombolMateri(int idTombol, final String namaMateri) {
        Button btn = findViewById(idTombol);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Buka VideoActivity dan kirim nama materi yang dipilih
                Intent intent = new Intent(MateriActivity.this, VideoActivity.class);
                intent.putExtra("nama_materi", namaMateri); // kirim data ke Activity berikutnya
                startActivity(intent);
            }
        });
    }
}
