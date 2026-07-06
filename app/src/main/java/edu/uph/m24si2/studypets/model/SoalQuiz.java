package edu.uph.m24si2.studypets.model;

// SoalQuiz = model untuk menyimpan satu soal quiz
// Tipe soal: "pilihan_ganda" atau "isian"
public class SoalQuiz {

    // Konstanta untuk tipe soal
    public static final String TIPE_PILIHAN_GANDA = "pilihan_ganda";
    public static final String TIPE_ISIAN         = "isian";

    private String pertanyaan;      // teks soal
    private String tipe;            // "pilihan_ganda" atau "isian"
    private String[] pilihanJawaban; // hanya dipakai kalau tipe = pilihan_ganda (4 pilihan: A,B,C,D)
    private String jawabanBenar;    // jawaban yang benar (huruf "A"/"B"/"C"/"D" atau teks isian)

    // Constructor untuk soal PILIHAN GANDA
    public SoalQuiz(String pertanyaan, String[] pilihanJawaban, String jawabanBenar) {
        this.pertanyaan      = pertanyaan;
        this.tipe            = TIPE_PILIHAN_GANDA;
        this.pilihanJawaban  = pilihanJawaban;
        this.jawabanBenar    = jawabanBenar;
    }

    // Constructor untuk soal ISIAN
    public SoalQuiz(String pertanyaan, String jawabanBenar) {
        this.pertanyaan     = pertanyaan;
        this.tipe           = TIPE_ISIAN;
        this.pilihanJawaban = null; // isian tidak punya pilihan
        this.jawabanBenar   = jawabanBenar;
    }

    // Getter (pengambil nilai variabel)
    public String getPertanyaan()       { return pertanyaan; }
    public String getTipe()             { return tipe; }
    public String[] getPilihanJawaban() { return pilihanJawaban; }
    public String getJawabanBenar()     { return jawabanBenar; }
}
