package edu.uph.m24si2.studypets.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uph.m24si2.studypets.R;
import edu.uph.m24si2.studypets.model.InventoryItem;

// ShopAdapter — menampilkan item di shop menggunakan RecyclerView
// Pola dari materi dosen: ada setData() untuk update data
public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {

    // Interface untuk event beli — dikirim ke ShopActivity
    public interface OnBuyItemListener {
        void onBuy(InventoryItem item);
    }

    private Context context;
    private List<InventoryItem> daftarItem;
    private OnBuyItemListener buyListener;

    public ShopAdapter(Context context, List<InventoryItem> daftarItem,
                       OnBuyItemListener buyListener) {
        this.context     = context;
        this.daftarItem  = daftarItem;
        this.buyListener = buyListener;
    }

    // setData() — update data adapter dari Activity
    // Dipanggil di dalam loadData(): adapter.setData(db.inventoryItemDao().getSemuaItem())
    public void setData(List<InventoryItem> data) {
        this.daftarItem = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_shop, parent, false);
        return new ShopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopViewHolder holder, int position) {
        InventoryItem item = daftarItem.get(position);

        holder.tvNama.setText(item.name);
        holder.tvEfek.setText("+" + item.effectValue + " " + getLabelEfek(item.type));
        holder.tvHarga.setText(item.price + " 🪙");

        // Pasang listener tombol Beli
        final InventoryItem itemIni = item;
        holder.btnBeli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buyListener != null) buyListener.onBuy(itemIni);
            }
        });
    }

    @Override
    public int getItemCount() {
        return daftarItem.size();
    }

    private String getLabelEfek(String tipe) {
        if (tipe.equals("food"))   return "Kenyang";
        if (tipe.equals("drink"))  return "Haus";
        if (tipe.equals("health")) return "Health";
        if (tipe.equals("mood"))   return "Mood";
        return "";
    }

    // ViewHolder — menyimpan referensi komponen satu item shop
    static class ShopViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvEfek, tvHarga;
        Button btnBeli;

        ShopViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama  = itemView.findViewById(R.id.tv_shop_item_name);
            tvEfek  = itemView.findViewById(R.id.tv_shop_item_effect);
            tvHarga = itemView.findViewById(R.id.tv_shop_item_price);
            btnBeli = itemView.findViewById(R.id.btn_buy_item);
        }
    }
}
