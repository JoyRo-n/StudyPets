package edu.uph.m24si2.studypets;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.uph.m24si2.studypets.room.AppDatabase;
import edu.uph.m24si2.studypets.room.UserEntity;

// RegisterActivity = halaman daftar akun baru
// Pola persis TambahActivity dari materi dosen:
//   db = AppDatabase.getDatabase(this)
//   UserEntity user = new UserEntity(...)
//   db.userDao().simpanUser(user)
//   finish()
public class RegisterActivity extends AppCompatActivity {

    EditText etUsername, etPassword, etConfirm;
    TextView tvError, tvSelectedPet;
    Button btnRegister;
    LinearLayout petCat, petDog;
    String selectedPet = "";

    // Pakai AppDatabase langsung — sesuai pola materi dosen
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Ambil instance database — pola materi dosen:
        // AppDatabase db = AppDatabase.getDatabase(this)
        db = AppDatabase.getInstance(this);

        etUsername    = findViewById(R.id.et_reg_username);
        etPassword    = findViewById(R.id.et_reg_password);
        etConfirm     = findViewById(R.id.et_reg_confirm);
        tvError       = findViewById(R.id.tv_reg_error);
        tvSelectedPet = findViewById(R.id.tv_selected_pet);
        btnRegister   = findViewById(R.id.btn_register);
        petCat        = findViewById(R.id.pet_cat);
        petDog        = findViewById(R.id.pet_dog);

        petCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { pilihPet("cat"); }
        });

        petDog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { pilihPet("dog"); }
        });

        // Tombol Daftar — pola dari materi dosen:
        // btnSimpan.setOnClickListener → ambil data → simpan → finish()
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { daftar(); }
        });

        TextView tvGoLogin = findViewById(R.id.tv_go_login);
        tvGoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });
    }

    private void pilihPet(String jenis) {
        selectedPet = jenis;
        petCat.setBackground(getDrawable(R.drawable.bg_pet_card_cat));
        petDog.setBackground(getDrawable(R.drawable.bg_pet_card_dog));
        if (jenis.equals("cat")) {
            petCat.setBackground(getDrawable(R.drawable.bg_pet_card_selected_cat));
            tvSelectedPet.setText("✅ Kucing dipilih!");
        } else {
            petDog.setBackground(getDrawable(R.drawable.bg_pet_card_selected_dog));
            tvSelectedPet.setText("✅ Anjing dipilih!");
        }
        tvError.setVisibility(View.GONE);
    }

    private void daftar() {
        String username   = etUsername.getText().toString().trim();
        String password   = etPassword.getText().toString().trim();
        String konfirmasi = etConfirm.getText().toString().trim();

        // Validasi input
        if (username.isEmpty()) { tampilkanError("Username tidak boleh kosong!"); return; }
        if (username.length() < 3) { tampilkanError("Username minimal 3 karakter!"); return; }
        if (username.contains(" ")) { tampilkanError("Username tidak boleh pakai spasi!"); return; }
        if (password.isEmpty()) { tampilkanError("Password tidak boleh kosong!"); return; }
        if (password.length() < 6) { tampilkanError("Password minimal 6 karakter!"); return; }
        if (!password.equals(konfirmasi)) { tampilkanError("Password tidak cocok!"); return; }
        if (selectedPet.isEmpty()) { tampilkanError("Pilih dulu petmu! 🐾"); return; }

        // Cek apakah username sudah dipakai — pakai DAO langsung
        // Sama seperti: db.mahasiswaDAO().getMahasiswa()
        UserEntity userLama = db.userDao().cariUserByUsername(username);
        if (userLama != null) {
            tampilkanError("Username sudah digunakan! Coba yang lain.");
            return;
        }

        // Simpan user baru ke database — pola materi dosen:
        // UserEntity user = new UserEntity(...)
        // long id = db.userDao().simpanUser(user)
        UserEntity userBaru = new UserEntity(username, password, selectedPet);
        long id = db.userDao().simpanUser(userBaru);

        // Simpan info ke SharedPreferences
        SharedPreferences prefs = getSharedPreferences("studypets_user", MODE_PRIVATE);
        prefs.edit()
            .putString("username", username)
            .putString("pet_type", selectedPet)
            .putBoolean("is_logged_in", true)
            .putBoolean("is_new_user", true)
            .apply();

        Toast.makeText(this,
            "Akun berhasil dibuat! Selamat datang " + username + "! 🎉",
            Toast.LENGTH_LONG).show();

        // Pindah ke MainActivity — pola Intent dari materi dosen
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void tampilkanError(String pesan) {
        tvError.setText(pesan);
        tvError.setVisibility(View.VISIBLE);
    }
}
