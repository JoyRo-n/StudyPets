package edu.uph.m24si2.studypets;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uph.m24si2.studypets.database.RoomHelper;
import edu.uph.m24si2.studypets.model.QuestData;

public class AdminReviewActivity extends AppCompatActivity {

    RecyclerView rvReview;
    TextView tvKosong, tvJumlah;
    RoomHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_review);

        // Cek akses admin
        SharedPreferences prefs = getSharedPreferences("studypets_user", MODE_PRIVATE);
        if (!prefs.getBoolean("is_admin", false)) {
            Toast.makeText(this, "⛔ Akses ditolak!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db       = RoomHelper.getOrInit(this);
        rvReview = findViewById(R.id.rv_review_quest);
        tvKosong = findViewById(R.id.tv_review_kosong);
        tvJumlah = findViewById(R.id.tv_jumlah_review);

        rvReview.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.btn_back_review).setOnClickListener(v -> finish());

        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        List<QuestData> daftar = db.getQuestMenungguReview();
        tvJumlah.setText(daftar.size() + " pending");

        if (daftar.isEmpty()) {
            tvKosong.setVisibility(View.VISIBLE);
            rvReview.setVisibility(View.GONE);
            return;
        }

        tvKosong.setVisibility(View.GONE);
        rvReview.setVisibility(View.VISIBLE);

        rvReview.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = getLayoutInflater().inflate(R.layout.item_review_quest, parent, false);
                return new RecyclerView.ViewHolder(v) {};
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int pos) {
                QuestData q = daftar.get(pos);
                View v = holder.itemView;

                ((TextView) v.findViewById(R.id.tv_review_username)).setText("👤 " + q.username);
                ((TextView) v.findViewById(R.id.tv_review_title)).setText(q.title);
                ((TextView) v.findViewById(R.id.tv_review_detail)).setText(
                        q.subject + "  •  " + q.difficulty + "  •  Deadline: " + q.deadline);
                ((TextView) v.findViewById(R.id.tv_review_reward)).setText(
                        "💰 Reward jika disetujui: " + q.xpReward + " XP + " + q.coinReward + " 🪙");

                // Tampilkan foto bukti
                ImageView imgBukti = v.findViewById(R.id.img_bukti);
                if (q.buktiPath != null && !q.buktiPath.isEmpty()) {
                    try {
                        imgBukti.setImageURI(Uri.parse(q.buktiPath));
                    } catch (Exception e) {
                        imgBukti.setImageResource(android.R.drawable.ic_menu_gallery);
                    }
                } else {
                    imgBukti.setImageResource(android.R.drawable.ic_menu_gallery);
                }

                // Tombol Setujui
                v.findViewById(R.id.btn_approve).setOnClickListener(btn -> {
                    new AlertDialog.Builder(AdminReviewActivity.this)
                            .setTitle("✅ Setujui Bukti?")
                            .setMessage("Quest \"" + q.title + "\" milik " + q.username +
                                    "\n\nReward akan dikirim:\n• " + q.xpReward + " XP\n• " + q.coinReward + " 🪙")
                            .setPositiveButton("Setujui", (d, w) -> {
                                db.approveBukti(q.id);
                                Toast.makeText(AdminReviewActivity.this,
                                        "✅ Disetujui! Reward dikirim ke " + q.username,
                                        Toast.LENGTH_SHORT).show();
                                loadData();
                            })
                            .setNegativeButton("Batal", null)
                            .show();
                });

                // Tombol Tolak
                v.findViewById(R.id.btn_reject).setOnClickListener(btn -> {
                    new AlertDialog.Builder(AdminReviewActivity.this)
                            .setTitle("❌ Tolak Bukti?")
                            .setMessage("Quest \"" + q.title + "\" milik " + q.username +
                                    "\n\nUser harus upload ulang bukti.")
                            .setPositiveButton("Tolak", (d, w) -> {
                                db.rejectBukti(q.id);
                                Toast.makeText(AdminReviewActivity.this,
                                        "❌ Ditolak. " + q.username + " harus upload ulang.",
                                        Toast.LENGTH_SHORT).show();
                                loadData();
                            })
                            .setNegativeButton("Batal", null)
                            .show();
                });
            }

            @Override
            public int getItemCount() { return daftar.size(); }
        });
    }
}
