package edu.uph.m24si2.studypets;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.uph.m24si2.studypets.adapter.UserAdapter;
import edu.uph.m24si2.studypets.room.AppDatabase;

// Pola persis MainActivity dari materi dosen
public class DaftarUserActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    UserAdapter adapter;
    AppDatabase db;
    TextView tvJumlahUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_user);

        // AppDatabase.getDatabase() — pola dosen
        db = AppDatabase.getInstance(this);

        tvJumlahUser = findViewById(R.id.tv_jumlah_user);
        recyclerView = findViewById(R.id.rv_daftar_user);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Adapter dengan lambda delete — pola dosen
        adapter = new UserAdapter(
                db.userDao().getSemuaUser(),
                user -> {
                    db.userDao().hapusUser(user);
                    loadData();
                }
        );
        recyclerView.setAdapter(adapter);

        loadData();

        Button btnBack = findViewById(R.id.btn_back_daftar_user);
        btnBack.setOnClickListener(v -> finish());
    }

    // loadData() — pola persis dosen:
    // adapter.setData(db.mahasiswaDAO().getMahasiswa())
    private void loadData() {
        adapter.setData(db.userDao().getSemuaUser());
        tvJumlahUser.setText(db.userDao().getSemuaUser().size() + " user");
    }
}
