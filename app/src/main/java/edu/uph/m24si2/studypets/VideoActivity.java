package edu.uph.m24si2.studypets;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

// VideoActivity = halaman untuk menonton video YouTube via WebView
// WebView = komponen Android untuk menampilkan konten web di dalam aplikasi
public class VideoActivity extends AppCompatActivity {

    private String namaMateri; // materi yang dipilih dari MateriActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        // Ambil data yang dikirim dari MateriActivity
        // getIntent() = ambil Intent yang membuka Activity ini
        // getStringExtra() = ambil data String berdasarkan key
        namaMateri = getIntent().getStringExtra("nama_materi");

        // Tampilkan judul materi
        TextView tvJudul = findViewById(R.id.tv_video_judul);
        tvJudul.setText("📺 " + namaMateri);

        // Setup WebView untuk menampilkan video YouTube
        WebView webView = findViewById(R.id.webview_video);

        // WebViewClient = agar link dibuka di dalam app, tidak di browser luar
        webView.setWebViewClient(new WebViewClient());

        // WebChromeClient = diperlukan agar video bisa diputar (fullscreen support)
        webView.setWebChromeClient(new WebChromeClient());

        // Aktifkan JavaScript — wajib untuk memutar YouTube
        webView.getSettings().setJavaScriptEnabled(true);

        // Muat URL video YouTube sesuai materi yang dipilih
        String urlVideo = getUrlVideo(namaMateri);
        webView.loadUrl(urlVideo);

        // Tombol kembali
        Button btnBack = findViewById(R.id.btn_back_video);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Tombol mulai quiz — setelah selesai menonton
        Button btnMulaiQuiz = findViewById(R.id.btn_mulai_quiz);
        btnMulaiQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Buka QuizActivity dan kirim nama materi
                Intent intent = new Intent(VideoActivity.this, QuizActivity.class);
                intent.putExtra("nama_materi", namaMateri);
                startActivity(intent);
            }
        });
    }

    // Kembalikan URL video YouTube dalam format EMBED agar bisa diputar di WebView
    // Format: https://www.youtube.com/embed/{videoId}
    private String getUrlVideo(String materi) {
        if (materi.equals("Matematika")) {
            return "https://www.youtube.com/embed/fBYCd6B7jEM"; // Aljabar SMP
        } else if (materi.equals("Fisika")) {
            return "https://www.youtube.com/embed/ZM8ECpBuQYE"; // Gerak Lurus
        } else if (materi.equals("Kimia")) {
            return "https://www.youtube.com/embed/Vq6UGq3qMFc"; // Tabel Periodik
        } else if (materi.equals("Biologi")) {
            return "https://www.youtube.com/embed/8XE5aFGEVts"; // Sel
        } else if (materi.equals("Bahasa Indonesia")) {
            return "https://www.youtube.com/embed/sKzqpvgLHQo"; // Teks Eksposisi
        } else if (materi.equals("Bahasa Inggris")) {
            return "https://www.youtube.com/embed/rHTBM_SKuZs"; // Simple Present Tense
        } else if (materi.equals("Sejarah")) {
            return "https://www.youtube.com/embed/SiEb2Tk5sLY"; // Proklamasi
        } else if (materi.equals("Geografi")) {
            return "https://www.youtube.com/embed/q3WMDFY7QAQ"; // Lapisan Bumi
        } else {
            return "https://www.youtube.com/results?search_query=" + materi + "+pelajaran+SMP+SMA";
        }
    }
}
