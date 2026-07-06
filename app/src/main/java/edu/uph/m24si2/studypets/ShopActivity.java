package edu.uph.m24si2.studypets;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uph.m24si2.studypets.adapter.ShopAdapter;
import edu.uph.m24si2.studypets.database.RoomHelper;
import edu.uph.m24si2.studypets.model.InventoryItem;
import edu.uph.m24si2.studypets.model.UserStats;

// ShopActivity = halaman toko
// Pola: db.inventoryItemDao().getSemuaItem() → adapter.setData() → RecyclerView
public class ShopActivity extends AppCompatActivity {

    RoomHelper db;
    RecyclerView rvShop;
    ShopAdapter adapter;
    TextView tvCoins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        // Ambil database — pola materi dosen
        db = RoomHelper.getOrInit(this);

        rvShop  = findViewById(R.id.rv_shop);
        tvCoins = findViewById(R.id.tv_shop_coins);

        // Grid 2 kolom
        rvShop.setLayoutManager(new GridLayoutManager(this, 2));

        // Buat adapter dengan list kosong dulu — nanti diisi oleh loadData()
        adapter = new ShopAdapter(this, new java.util.ArrayList<>(),
            new ShopAdapter.OnBuyItemListener() {
                @Override
                public void onBuy(InventoryItem item) {
                    tampilkanDialogBeli(item);
                }
            }
        );
        rvShop.setAdapter(adapter);

        loadData(); // pola loadData() dari materi dosen

        Button btnBack = findViewById(R.id.btn_back_shop);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    // loadData() — ambil data dari db lalu update adapter
    // Sama persis pola: adapter.setData(db.mahasiswaDAO().getMahasiswa())
    private void loadData() {
        List<InventoryItem> daftarItem = db.getAllShopItems();
        adapter.setData(daftarItem);

        // Tampilkan koin
        UserStats u = db.getUserStats();
        if (u != null) {
            tvCoins.setText(u.coins + " 🪙");
        }
    }

    private void tampilkanDialogBeli(InventoryItem item) {
        UserStats u = db.getUserStats();
        if (u == null) return;

        if (u.coins < item.price) {
            Toast.makeText(this,
                "Koin tidak cukup! Kamu punya: " + u.coins + " 🪙",
                Toast.LENGTH_SHORT).show();
            return;
        }

        String pesan = "Item: " + item.name + "\n"
                + "Efek: +" + item.effectValue + " " + getLabelEfek(item.type) + "\n"
                + "Harga: " + item.price + " 🪙\n"
                + "Koin kamu: " + u.coins + " 🪙\n"
                + "Sisa: " + (u.coins - item.price) + " 🪙";

        final InventoryItem itemDipilih = item;
        new AlertDialog.Builder(this)
            .setTitle("🛒 Beli Item?")
            .setMessage(pesan)
            .setPositiveButton("Beli ✅", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    boolean berhasil = db.buyItem(itemDipilih.id, 1);
                    if (berhasil) {
                        Toast.makeText(ShopActivity.this,
                            "✅ " + itemDipilih.name + " berhasil dibeli!",
                            Toast.LENGTH_SHORT).show();
                        loadData(); // refresh setelah beli
                    } else {
                        Toast.makeText(ShopActivity.this, "❌ Gagal!", Toast.LENGTH_SHORT).show();
                    }
                }
            })
            .setNegativeButton("Batal", null)
            .show();
    }

    private String getLabelEfek(String tipe) {
        if (tipe.equals("food"))   return "Kenyang";
        if (tipe.equals("drink"))  return "Haus";
        if (tipe.equals("health")) return "Health";
        if (tipe.equals("mood"))   return "Mood";
        return "";
    }
}
