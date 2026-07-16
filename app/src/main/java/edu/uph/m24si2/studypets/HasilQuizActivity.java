package edu.uph.m24si2.studypets;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import edu.uph.m24si2.studypets.database.RoomHelper;

// HasilQuizActivity = halaman yang menampilkan nilai setelah quiz selesai
public class HasilQuizActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasil_quiz);

        // Ambil data hasil quiz dari Intent
        String namaMateri  = getIntent().getStringExtra("nama_materi");
        int jumlahBenar    = getIntent().getIntExtra("jumlah_benar", 0);
        int jumlahSoal     = getIntent().getIntExtra("jumlah_soal", 10);

        // Hitung nilai (skala 0-100)
        int nilai = (jumlahBenar * 100) / jumlahSoal;

        // Tentukan reward XP dan koin berdasarkan nilai
        int xpDidapat   = hitungXP(nilai);
        int koinDidapat = hitungKoin(nilai);

        // Tentukan pesan motivasi berdasarkan nilai
        String pesan = getPesanHasil(nilai);
        String emoji = getEmojiHasil(nilai);

        // Tampilkan semua info ke layar
        TextView tvMateri  = findViewById(R.id.tv_hasil_materi);
        TextView tvNilai   = findViewById(R.id.tv_nilai);
        TextView tvBenar   = findViewById(R.id.tv_benar_salah);
        TextView tvPesan   = findViewById(R.id.tv_pesan_hasil);
        TextView tvReward  = findViewById(R.id.tv_reward);
        TextView tvEmoji   = findViewById(R.id.tv_emoji_hasil);

        tvMateri.setText("Materi: " + namaMateri);
        tvNilai.setText(String.valueOf(nilai));
        tvBenar.setText(jumlahBenar + " / " + jumlahSoal + " benar");
        tvPesan.setText(pesan);
        tvEmoji.setText(emoji);
        tvReward.setText("+" + xpDidapat + " XP  +  " + koinDidapat + " 🪙");

        // Berikan reward ke akun user lewat database — pakai RoomHelper
        RoomHelper db = RoomHelper.getOrInit(this);
        db.addXP(xpDidapat);
        db.addCoins(koinDidapat);
        db.checkAchievements();

        // Simpan riwayat belajar untuk dilacak admin
        db.simpanRiwayatBelajar(namaMateri, nilai, jumlahBenar, jumlahSoal);

        // Tombol kembali ke dashboard
        Button btnKeDashboard = findViewById(R.id.btn_ke_dashboard);
        btnKeDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kembali ke MainActivity dan bersihkan semua Activity di atas
                Intent intent = new Intent(HasilQuizActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        // Tombol coba lagi (ulangi quiz materi yang sama)
        Button btnUlang = findViewById(R.id.btn_ulangi_quiz);
        btnUlang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HasilQuizActivity.this, QuizActivity.class);
                intent.putExtra("nama_materi", namaMateri);
                startActivity(intent);
                finish();
            }
        });
    }

    // Hitung XP berdasarkan nilai
    private int hitungXP(int nilai) {
        if (nilai == 100) return 150; // nilai sempurna bonus ekstra
        if (nilai >= 80)  return 100;
        if (nilai >= 60)  return 70;
        if (nilai >= 40)  return 40;
        return 20; // tetap dapat sedikit meski nilai jelek
    }

    // Hitung koin berdasarkan nilai
    private int hitungKoin(int nilai) {
        if (nilai == 100) return 100;
        if (nilai >= 80)  return 70;
        if (nilai >= 60)  return 50;
        if (nilai >= 40)  return 30;
        return 10;
    }

    // Pesan motivasi berdasarkan nilai
    private String getPesanHasil(int nilai) {
        if (nilai == 100) return "SEMPURNA! Kamu luar biasa! 🌟";
        if (nilai >= 80)  return "Bagus sekali! Terus semangat belajar!";
        if (nilai >= 60)  return "Cukup baik! Masih ada ruang untuk berkembang.";
        if (nilai >= 40)  return "Jangan menyerah! Coba pelajari lagi materinya.";
        return "Yuk belajar lebih giat lagi! Kamu pasti bisa!";
    }

    private String getEmojiHasil(int nilai) {
        if (nilai == 100) return "🏆";
        if (nilai >= 80)  return "⭐";
        if (nilai >= 60)  return "😊";
        if (nilai >= 40)  return "😐";
        return "💪";
    }
}
