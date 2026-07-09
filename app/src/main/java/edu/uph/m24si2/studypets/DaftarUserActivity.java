package edu.uph.m24si2.studypets;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.uph.m24si2.studypets.adapter.UserAdapter;
import edu.uph.m24si2.studypets.room.AppDatabase;
import edu.uph.m24si2.studypets.room.UserEntity;

// Activity khusus admin — hanya bisa diakses jika is_admin = true
public class DaftarUserActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    UserAdapter adapter;
    AppDatabase db;
    TextView tvJumlahUser;
    String usernameAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_user);

        // Cek apakah yang membuka activity ini adalah admin
        SharedPreferences prefs = getSharedPreferences("studypets_user", MODE_PRIVATE);
        boolean isAdmin = prefs.getBoolean("is_admin", false);
        usernameAdmin   = prefs.getString("username", "");

        if (!isAdmin) {
            // Bukan admin — tolak akses dan tutup activity
            Toast.makeText(this, "⛔ Akses ditolak! Hanya admin yang bisa membuka halaman ini.",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db = AppDatabase.getInstance(this);

        tvJumlahUser = findViewById(R.id.tv_jumlah_user);
        recyclerView = findViewById(R.id.rv_daftar_user);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Adapter dengan konfirmasi dialog sebelum hapus
        adapter = new UserAdapter(
                db.userDao().getSemuaUser(),
                user -> konfirmasiHapus(user)
        );
        recyclerView.setAdapter(adapter);

        loadData();

        Button btnBack = findViewById(R.id.btn_back_daftar_user);
        btnBack.setOnClickListener(v -> finish());
    }

    // Tampilkan dialog konfirmasi sebelum menghapus user
    private void konfirmasiHapus(UserEntity user) {
        // Admin tidak bisa hapus akun dirinya sendiri
        if (user.username.equals(usernameAdmin)) {
            Toast.makeText(this, "❌ Tidak bisa menghapus akun admin aktif!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Admin tidak bisa hapus akun admin lain
        if (user.isAdmin) {
            Toast.makeText(this, "❌ Tidak bisa menghapus akun admin!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Hapus Akun")
                .setMessage("Yakin ingin menghapus akun \"" + user.username + "\"?\nAksi ini tidak bisa dibatalkan.")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    db.userDao().hapusUser(user);
                    Toast.makeText(this, "✅ Akun \"" + user.username + "\" berhasil dihapus.",
                            Toast.LENGTH_SHORT).show();
                    loadData();
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void loadData() {
        adapter.setData(db.userDao().getSemuaUser());
        tvJumlahUser.setText(db.userDao().getSemuaUser().size() + " user");
    }
}
