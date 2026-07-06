package edu.uph.m24si2.studypets;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import edu.uph.m24si2.studypets.model.SoalQuiz;

// QuizActivity = halaman untuk mengerjakan soal quiz
public class QuizActivity extends AppCompatActivity {

    private String namaMateri;
    private List<SoalQuiz> daftarSoal; // semua soal untuk materi ini
    private int indexSoalSekarang = 0;  // soal ke berapa yang sedang ditampilkan
    private int jumlahBenar = 0;        // hitung berapa jawaban benar

    // Komponen UI
    private TextView tvNomorSoal, tvPertanyaan, tvProgress;
    private LinearLayout layoutPilihanGanda, layoutIsian;
    private RadioGroup rgPilihan;
    private RadioButton rbA, rbB, rbC, rbD;
    private EditText etJawabanIsian;
    private Button btnSelanjutnya;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Ambil nama materi dari Intent
        namaMateri = getIntent().getStringExtra("nama_materi");

        // Inisialisasi komponen UI
        tvNomorSoal        = findViewById(R.id.tv_nomor_soal);
        tvPertanyaan       = findViewById(R.id.tv_pertanyaan);
        tvProgress         = findViewById(R.id.tv_progress_quiz);
        layoutPilihanGanda = findViewById(R.id.layout_pilihan_ganda);
        layoutIsian        = findViewById(R.id.layout_isian);
        rgPilihan          = findViewById(R.id.rg_pilihan);
        rbA                = findViewById(R.id.rb_a);
        rbB                = findViewById(R.id.rb_b);
        rbC                = findViewById(R.id.rb_c);
        rbD                = findViewById(R.id.rb_d);
        etJawabanIsian     = findViewById(R.id.et_jawaban_isian);
        btnSelanjutnya     = findViewById(R.id.btn_selanjutnya);

        // Siapkan soal berdasarkan materi
        daftarSoal = buatSoal(namaMateri);

        // Tampilkan soal pertama
        tampilkanSoal(indexSoalSekarang);

        // Ketika tombol "Selanjutnya" / "Selesai" diklik
        btnSelanjutnya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cekJawabanDanLanjut();
            }
        });
    }

    // Tampilkan soal berdasarkan index
    private void tampilkanSoal(int index) {
        SoalQuiz soal = daftarSoal.get(index);

        // Update progress
        tvNomorSoal.setText("Soal " + (index + 1) + " dari " + daftarSoal.size());
        tvProgress.setText("Benar: " + jumlahBenar);
        tvPertanyaan.setText(soal.getPertanyaan());

        // Reset pilihan sebelumnya
        rgPilihan.clearCheck();
        etJawabanIsian.setText("");

        if (soal.getTipe().equals(SoalQuiz.TIPE_PILIHAN_GANDA)) {
            // Tampilkan layout pilihan ganda, sembunyikan isian
            layoutPilihanGanda.setVisibility(View.VISIBLE);
            layoutIsian.setVisibility(View.GONE);

            // Isi teks setiap pilihan
            String[] pilihan = soal.getPilihanJawaban();
            rbA.setText("A.  " + pilihan[0]);
            rbB.setText("B.  " + pilihan[1]);
            rbC.setText("C.  " + pilihan[2]);
            rbD.setText("D.  " + pilihan[3]);

        } else {
            // Tampilkan layout isian, sembunyikan pilihan ganda
            layoutPilihanGanda.setVisibility(View.GONE);
            layoutIsian.setVisibility(View.VISIBLE);
        }

        // Ganti teks tombol di soal terakhir
        if (index == daftarSoal.size() - 1) {
            btnSelanjutnya.setText("Selesai ✅");
        } else {
            btnSelanjutnya.setText("Selanjutnya →");
        }
    }

    // Cek jawaban user, lalu lanjut ke soal berikutnya atau ke hasil
    private void cekJawabanDanLanjut() {
        SoalQuiz soal = daftarSoal.get(indexSoalSekarang);
        String jawabanUser = "";

        if (soal.getTipe().equals(SoalQuiz.TIPE_PILIHAN_GANDA)) {
            // Ambil pilihan yang dipilih
            int idDipilih = rgPilihan.getCheckedRadioButtonId();
            if (idDipilih == -1) {
                // Belum pilih jawaban
                Toast.makeText(this, "Pilih jawaban dulu!", Toast.LENGTH_SHORT).show();
                return;
            }
            // Tentukan jawaban berdasarkan RadioButton yang dipilih
            if (idDipilih == R.id.rb_a)      jawabanUser = "A";
            else if (idDipilih == R.id.rb_b) jawabanUser = "B";
            else if (idDipilih == R.id.rb_c) jawabanUser = "C";
            else                             jawabanUser = "D";

        } else {
            // Ambil teks isian
            jawabanUser = etJawabanIsian.getText().toString().trim();
            if (jawabanUser.isEmpty()) {
                Toast.makeText(this, "Isi jawaban dulu!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Bandingkan jawaban user dengan jawaban benar (tidak case-sensitive)
        if (jawabanUser.equalsIgnoreCase(soal.getJawabanBenar())) {
            jumlahBenar++;
            Toast.makeText(this, "✅ Benar!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,
                "❌ Salah! Jawaban: " + soal.getJawabanBenar(),
                Toast.LENGTH_SHORT).show();
        }

        // Lanjut ke soal berikutnya atau ke halaman hasil
        indexSoalSekarang++;
        if (indexSoalSekarang < daftarSoal.size()) {
            tampilkanSoal(indexSoalSekarang);
        } else {
            // Semua soal selesai — buka halaman hasil
            Intent intent = new Intent(QuizActivity.this, HasilQuizActivity.class);
            intent.putExtra("nama_materi",   namaMateri);
            intent.putExtra("jumlah_benar",  jumlahBenar);
            intent.putExtra("jumlah_soal",   daftarSoal.size());
            startActivity(intent);
            finish(); // tutup QuizActivity agar tidak bisa back
        }
    }

    // =====================================================
    // DATA SOAL — 10 soal per materi (7 pilihan ganda + 3 isian)
    // =====================================================
    private List<SoalQuiz> buatSoal(String materi) {
        List<SoalQuiz> soal = new ArrayList<>();

        if (materi.equals("Matematika")) {
            soal.add(new SoalQuiz("Berapakah hasil dari 7 × 8?",
                new String[]{"54", "56", "63", "64"}, "B"));
            soal.add(new SoalQuiz("Nilai x dari persamaan 2x + 4 = 10 adalah...",
                new String[]{"2", "3", "4", "5"}, "B"));
            soal.add(new SoalQuiz("Luas segitiga dengan alas 10 cm dan tinggi 6 cm adalah...",
                new String[]{"30 cm²", "60 cm²", "16 cm²", "45 cm²"}, "A"));
            soal.add(new SoalQuiz("Bentuk sederhana dari 12/18 adalah...",
                new String[]{"1/2", "2/3", "3/4", "4/6"}, "B"));
            soal.add(new SoalQuiz("Jika a = 3 dan b = 4, maka a² + b² adalah...",
                new String[]{"25", "14", "49", "7"}, "A"));
            soal.add(new SoalQuiz("Berapakah akar kuadrat dari 144?",
                new String[]{"11", "12", "13", "14"}, "B"));
            soal.add(new SoalQuiz("Persamaan garis y = 2x + 1 memotong sumbu y di titik...",
                new String[]{"(0, 1)", "(1, 0)", "(0, 2)", "(2, 0)"}, "A"));
            soal.add(new SoalQuiz("Hasil dari 15% × 200 adalah...", "30"));
            soal.add(new SoalQuiz("Keliling lingkaran dengan jari-jari 7 cm (π=22/7) adalah... cm", "44"));
            soal.add(new SoalQuiz("Jika 3x = 21, maka x = ...", "7"));

        } else if (materi.equals("Fisika")) {
            soal.add(new SoalQuiz("Satuan kecepatan dalam SI adalah...",
                new String[]{"km/jam", "m/s", "cm/s", "m/jam"}, "B"));
            soal.add(new SoalQuiz("Rumus kecepatan adalah...",
                new String[]{"v = s × t", "v = s / t", "v = t / s", "v = s + t"}, "B"));
            soal.add(new SoalQuiz("Gaya gravitasi bumi adalah sekitar...",
                new String[]{"8 m/s²", "9 m/s²", "10 m/s²", "11 m/s²"}, "C"));
            soal.add(new SoalQuiz("Hukum Newton I menyatakan bahwa benda diam akan...",
                new String[]{"Bergerak jika didorong", "Tetap diam jika tidak ada gaya", "Selalu bergerak", "Berubah bentuk"}, "B"));
            soal.add(new SoalQuiz("Alat untuk mengukur massa adalah...",
                new String[]{"Termometer", "Barometer", "Neraca", "Voltmeter"}, "C"));
            soal.add(new SoalQuiz("Energi yang dimiliki benda bergerak disebut energi...",
                new String[]{"Potensial", "Kinetik", "Panas", "Kimia"}, "B"));
            soal.add(new SoalQuiz("Tekanan = Gaya / ...",
                new String[]{"Massa", "Volume", "Luas", "Panjang"}, "C"));
            soal.add(new SoalQuiz("Satuan gaya dalam SI adalah...", "Newton"));
            soal.add(new SoalQuiz("Sebuah benda menempuh jarak 100 m dalam 20 s. Kecepatannya adalah... m/s", "5"));
            soal.add(new SoalQuiz("Alat pengukur suhu disebut...", "termometer"));

        } else if (materi.equals("Kimia")) {
            soal.add(new SoalQuiz("Lambang unsur oksigen adalah...",
                new String[]{"Os", "O", "Ox", "Ok"}, "B"));
            soal.add(new SoalQuiz("Air memiliki rumus kimia...",
                new String[]{"CO₂", "H₂O", "NaCl", "O₂"}, "B"));
            soal.add(new SoalQuiz("Jumlah proton dalam atom disebut...",
                new String[]{"Nomor massa", "Nomor atom", "Elektron valensi", "Neutron"}, "B"));
            soal.add(new SoalQuiz("Reaksi yang melepas panas disebut reaksi...",
                new String[]{"Endoterm", "Eksoterm", "Redoks", "Oksidasi"}, "B"));
            soal.add(new SoalQuiz("pH larutan basa adalah...",
                new String[]{"< 7", "= 7", "> 7", "= 0"}, "C"));
            soal.add(new SoalQuiz("Lambang unsur natrium adalah...",
                new String[]{"Na", "N", "Nt", "Nm"}, "A"));
            soal.add(new SoalQuiz("Molekul yang terdiri dari dua atom oksigen adalah...",
                new String[]{"O", "O₂", "O₃", "CO₂"}, "B"));
            soal.add(new SoalQuiz("Unsur dengan nomor atom 1 adalah...", "Hidrogen"));
            soal.add(new SoalQuiz("Rumus kimia garam dapur adalah...", "NaCl"));
            soal.add(new SoalQuiz("Bagian terkecil dari unsur yang masih memiliki sifat unsur tersebut disebut...", "atom"));

        } else if (materi.equals("Biologi")) {
            soal.add(new SoalQuiz("Organel sel yang berfungsi sebagai pusat kendali sel adalah...",
                new String[]{"Ribosom", "Mitokondria", "Nukleus", "Vakuola"}, "C"));
            soal.add(new SoalQuiz("Proses tumbuhan membuat makanan sendiri disebut...",
                new String[]{"Respirasi", "Fotosintesis", "Transpirasi", "Fermentasi"}, "B"));
            soal.add(new SoalQuiz("Sel darah merah berfungsi untuk...",
                new String[]{"Melawan penyakit", "Membawa oksigen", "Pembekuan darah", "Mengangkut sari makanan"}, "B"));
            soal.add(new SoalQuiz("Satuan terkecil kehidupan adalah...",
                new String[]{"Jaringan", "Organ", "Sel", "Organisme"}, "C"));
            soal.add(new SoalQuiz("Hewan yang berkembang biak dengan bertelur disebut...",
                new String[]{"Vivipar", "Ovovivipar", "Ovipar", "Fragmentasi"}, "C"));
            soal.add(new SoalQuiz("Bagian sel yang hanya dimiliki tumbuhan, tidak pada hewan adalah...",
                new String[]{"Membran sel", "Dinding sel", "Nukleus", "Mitokondria"}, "B"));
            soal.add(new SoalQuiz("Sistem pencernaan pada manusia dimulai dari...",
                new String[]{"Lambung", "Usus halus", "Mulut", "Kerongkongan"}, "C"));
            soal.add(new SoalQuiz("Proses pernapasan menghasilkan gas...", "CO2"));
            soal.add(new SoalQuiz("Pigmen hijau pada daun disebut...", "klorofil"));
            soal.add(new SoalQuiz("Ilmu yang mempelajari makhluk hidup disebut...", "biologi"));

        } else if (materi.equals("Bahasa Indonesia")) {
            soal.add(new SoalQuiz("Teks yang berisi pendapat disertai argumen disebut teks...",
                new String[]{"Narasi", "Deskripsi", "Eksposisi", "Prosedur"}, "C"));
            soal.add(new SoalQuiz("Ide pokok paragraf biasanya terdapat pada kalimat...",
                new String[]{"Penjelas", "Utama", "Penutup", "Sambungan"}, "B"));
            soal.add(new SoalQuiz("Kalimat yang mengandung satu subjek dan satu predikat disebut kalimat...",
                new String[]{"Majemuk", "Tunggal", "Kompleks", "Pasif"}, "B"));
            soal.add(new SoalQuiz("Penulisan yang benar untuk singkatan 'dan lain-lain' adalah...",
                new String[]{"dll.", "dkk.", "dsb.", "etc."}, "A"));
            soal.add(new SoalQuiz("Teks yang menceritakan urutan kejadian disebut teks...",
                new String[]{"Deskripsi", "Eksposisi", "Argumentasi", "Narasi"}, "D"));
            soal.add(new SoalQuiz("Imbuhan me- pada kata 'menulis' berfungsi sebagai...",
                new String[]{"Awalan", "Akhiran", "Sisipan", "Konfiks"}, "A"));
            soal.add(new SoalQuiz("Kata yang berlawanan makna disebut...",
                new String[]{"Sinonim", "Homonim", "Antonim", "Polisemi"}, "C"));
            soal.add(new SoalQuiz("Penulisan huruf kapital digunakan pada awal...", "kalimat"));
            soal.add(new SoalQuiz("Kata 'berlari' merupakan kata kerja yang mengandung awalan...", "ber"));
            soal.add(new SoalQuiz("Tanda baca yang digunakan di akhir kalimat tanya adalah tanda...", "tanya"));

        } else if (materi.equals("Bahasa Inggris")) {
            soal.add(new SoalQuiz("'She ... a student.' — Kata yang tepat untuk mengisi titik adalah...",
                new String[]{"am", "is", "are", "be"}, "B"));
            soal.add(new SoalQuiz("Kalimat 'I go to school every day' termasuk tense...",
                new String[]{"Past tense", "Present tense", "Future tense", "Perfect tense"}, "B"));
            soal.add(new SoalQuiz("Bentuk past tense dari kata 'go' adalah...",
                new String[]{"goes", "going", "went", "gone"}, "C"));
            soal.add(new SoalQuiz("Arti kata 'beautiful' adalah...",
                new String[]{"Pintar", "Cantik/Indah", "Besar", "Kuat"}, "B"));
            soal.add(new SoalQuiz("'They ... playing football now.' — Kata yang tepat adalah...",
                new String[]{"is", "am", "are", "was"}, "C"));
            soal.add(new SoalQuiz("Kalimat tanya untuk menanyakan waktu adalah...",
                new String[]{"What", "Where", "When", "Why"}, "C"));
            soal.add(new SoalQuiz("Bentuk plural dari 'child' adalah...",
                new String[]{"childs", "children", "childrens", "child"}, "B"));
            soal.add(new SoalQuiz("Terjemahan 'I love studying' adalah...", "Saya suka belajar"));
            soal.add(new SoalQuiz("Kata 'slowly' merupakan jenis kata...", "adverb"));
            soal.add(new SoalQuiz("Arti kata 'book' dalam Bahasa Indonesia adalah...", "buku"));

        } else if (materi.equals("Sejarah")) {
            soal.add(new SoalQuiz("Proklamasi kemerdekaan Indonesia dibacakan pada tanggal...",
                new String[]{"17 Agustus 1944", "17 Agustus 1945", "18 Agustus 1945", "17 Juli 1945"}, "B"));
            soal.add(new SoalQuiz("Siapakah yang membacakan teks proklamasi?",
                new String[]{"Soekarno-Hatta", "Soekarno saja", "Hatta saja", "Soeharto"}, "A"));
            soal.add(new SoalQuiz("Kerajaan Hindu tertua di Indonesia adalah...",
                new String[]{"Majapahit", "Sriwijaya", "Kutai", "Tarumanegara"}, "C"));
            soal.add(new SoalQuiz("Borobudur dibangun pada masa kerajaan...",
                new String[]{"Majapahit", "Mataram Hindu", "Sailendra", "Sriwijaya"}, "C"));
            soal.add(new SoalQuiz("Penjajahan Belanda di Indonesia berlangsung selama kurang lebih...",
                new String[]{"100 tahun", "150 tahun", "250 tahun", "350 tahun"}, "D"));
            soal.add(new SoalQuiz("Organisasi pergerakan nasional pertama Indonesia adalah...",
                new String[]{"Sarekat Islam", "Budi Utomo", "PNI", "PKI"}, "B"));
            soal.add(new SoalQuiz("Sumpah Pemuda dideklarasikan pada tahun...",
                new String[]{"1926", "1927", "1928", "1929"}, "C"));
            soal.add(new SoalQuiz("Ibu kota Indonesia saat proklamasi kemerdekaan adalah...", "Jakarta"));
            soal.add(new SoalQuiz("Presiden pertama Republik Indonesia adalah...", "Soekarno"));
            soal.add(new SoalQuiz("Tahun berakhirnya Perang Dunia II adalah...", "1945"));

        } else { // Geografi
            soal.add(new SoalQuiz("Lapisan bumi paling luar disebut...",
                new String[]{"Mantel", "Inti", "Kerak", "Litosfer"}, "C"));
            soal.add(new SoalQuiz("Garis khatulistiwa disebut juga...",
                new String[]{"Garis Bujur", "Garis Lintang 0°", "Garis Balik Utara", "Meridian"}, "B"));
            soal.add(new SoalQuiz("Indonesia terletak di antara dua samudra yaitu...",
                new String[]{"Atlantik dan Hindia", "Pasifik dan Atlantik", "Hindia dan Pasifik", "Arktik dan Pasifik"}, "C"));
            soal.add(new SoalQuiz("Gunung berapi terbentuk akibat...",
                new String[]{"Erosi", "Abrasi", "Tektonik", "Vulkanisme"}, "D"));
            soal.add(new SoalQuiz("Sungai terpanjang di Indonesia adalah...",
                new String[]{"Musi", "Kapuas", "Mahakam", "Citarum"}, "B"));
            soal.add(new SoalQuiz("Angin yang bertiup dari laut ke darat terjadi pada...",
                new String[]{"Malam hari", "Siang hari", "Pagi hari", "Sore hari"}, "B"));
            soal.add(new SoalQuiz("Peta yang menggambarkan kepadatan penduduk disebut peta...",
                new String[]{"Topografi", "Choropleth", "Isohyet", "Kadaster"}, "B"));
            soal.add(new SoalQuiz("Negara dengan jumlah penduduk terbanyak di dunia adalah...", "China"));
            soal.add(new SoalQuiz("Benua terbesar di dunia adalah...", "Asia"));
            soal.add(new SoalQuiz("Ibu kota Australia adalah...", "Canberra"));
        }

        return soal;
    }
}
