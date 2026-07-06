package edu.uph.m24si2.studypets;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uph.m24si2.studypets.adapter.InventoryAdapter;
import edu.uph.m24si2.studypets.database.RoomHelper;
import edu.uph.m24si2.studypets.model.InventoryItem;

// InventoryActivity = halaman inventory (item yang sudah dibeli)
public class InventoryActivity extends AppCompatActivity {

    RoomHelper db;
    RecyclerView rvInventory;
    InventoryAdapter adapter;
    TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        db = RoomHelper.getOrInit(this);

        rvInventory = findViewById(R.id.rv_inventory);
        tvEmpty     = findViewById(R.id.tv_empty_inventory);

        rvInventory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InventoryAdapter(this, new java.util.ArrayList<>(),
            new InventoryAdapter.OnUseItemListener() {
                @Override
                public void onUse(InventoryItem item) {
                    tampilkanDialogGunakan(item);
                }
            }
        );
        rvInventory.setAdapter(adapter);

        loadData();

        Button btnBack = findViewById(R.id.btn_back_inventory);
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

    // loadData() — ambil item yang sudah dibeli, update adapter
    private void loadData() {
        List<InventoryItem> hasil = db.getUserInventory();

        if (hasil.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvInventory.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvInventory.setVisibility(View.VISIBLE);
        }

        // setData() — pola dari materi dosen
        adapter.setData(hasil);
    }

    private void tampilkanDialogGunakan(InventoryItem item) {
        String pesan = item.name + "\n\n"
                + "Efek: +" + item.effectValue + " " + getLabelEfek(item.type) + "\n"
                + "Sisa: " + item.quantity + " buah";

        final InventoryItem itemDipilih = item;
        new AlertDialog.Builder(this)
            .setTitle("🍗 Gunakan Item?")
            .setMessage(pesan)
            .setPositiveButton("Gunakan", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    boolean berhasil = db.useItem(itemDipilih.id);
                    if (berhasil) {
                        Toast.makeText(InventoryActivity.this,
                            "✨ Pet diberi " + itemDipilih.name + "!",
                            Toast.LENGTH_SHORT).show();
                        loadData(); // refresh setelah digunakan
                    } else {
                        Toast.makeText(InventoryActivity.this,
                            "Gagal!", Toast.LENGTH_SHORT).show();
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
