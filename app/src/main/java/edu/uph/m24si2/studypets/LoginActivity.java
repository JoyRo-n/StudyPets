package edu.uph.m24si2.studypets;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.uph.m24si2.studypets.room.AppDatabase;
import edu.uph.m24si2.studypets.room.UserEntity;
import edu.uph.m24si2.studypets.database.RoomHelper;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    TextView tvError;
    Button btnLogin;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = AppDatabase.getInstance(this);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        tvError    = findViewById(R.id.tv_login_error);
        btnLogin   = findViewById(R.id.btn_login);

        // Animasi header — fade + scale dari atas
        View llHeader = findViewById(R.id.ll_header);
        llHeader.setAlpha(0f);
        llHeader.setTranslationY(-30f);
        llHeader.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                .start();

        // Animasi card login — slide dari bawah
        ScrollView cardLogin = findViewById(R.id.card_login);
        cardLogin.setAlpha(0f);
        cardLogin.setTranslationY(80f);
        cardLogin.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setStartDelay(200)
                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                .start();

        btnLogin.setOnClickListener(v -> {
            // Animasi tekan tombol
            btnLogin.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80)
                    .withEndAction(() -> btnLogin.animate().scaleX(1f).scaleY(1f)
                            .setDuration(80).start()).start();
            login();
        });

        TextView tvGoRegister = findViewById(R.id.tv_go_register);
        tvGoRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void login() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty()) { tampilkanError("Username tidak boleh kosong!"); return; }
        if (password.isEmpty()) { tampilkanError("Password tidak boleh kosong!"); return; }

        UserEntity user = db.userDao().cariUserUntukLogin(username, password);

        if (user == null) {
            tampilkanError("Username atau password salah! ❌");
            // Shake animasi pada field
            etUsername.animate().translationX(-10f).setDuration(50)
                    .withEndAction(() -> etUsername.animate().translationX(10f).setDuration(50)
                            .withEndAction(() -> etUsername.animate().translationX(0f)
                                    .setDuration(50).start()).start()).start();
            return;
        }

        // Login berhasil
        SharedPreferences prefs = getSharedPreferences("studypets_user", MODE_PRIVATE);
        prefs.edit()
            .putBoolean("is_logged_in", true)
            .putString("username", user.username)
            .putString("pet_type", user.petType)
            .putBoolean("is_admin", user.isAdmin) // simpan status admin
            .apply();

        // Set username aktif di RoomHelper supaya quest & data terfilter per user
        RoomHelper.getOrInit(this).setCurrentUsername(user.username);

        Toast.makeText(this, "Halo " + user.username + "! 🎉", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void tampilkanError(String pesan) {
        tvError.setText(pesan);
        tvError.setVisibility(View.VISIBLE);
        tvError.setAlpha(0f);
        tvError.animate().alpha(1f).setDuration(300).start();
    }
}
