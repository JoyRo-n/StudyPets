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

// InventoryAdapter — menampilkan item yang sudah dibeli di RecyclerView
public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    public interface OnUseItemListener {
        void onUse(InventoryItem item);
    }

    private Context context;
    private List<InventoryItem> daftarItem;
    private OnUseItemListener useListener;

    public InventoryAdapter(Context context, List<InventoryItem> daftarItem,
                            OnUseItemListener useListener) {
        this.context     = context;
        this.daftarItem  = daftarItem;
        this.useListener = useListener;
    }

    // setData() — pola dari materi dosen
    public void setData(List<InventoryItem> data) {
        this.daftarItem = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_inventory, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        InventoryItem item = daftarItem.get(position);

        holder.tvNama.setText(item.name);
        holder.tvEfek.setText("+" + item.effectValue + " " + getLabelEfek(item.type));
        holder.tvJumlah.setText("x" + item.quantity);

        final InventoryItem itemIni = item;
        holder.btnGunakan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (useListener != null) useListener.onUse(itemIni);
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

    static class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvEfek, tvJumlah;
        Button btnGunakan;

        InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama     = itemView.findViewById(R.id.tv_inventory_item_name);
            tvEfek     = itemView.findViewById(R.id.tv_inventory_item_effect);
            tvJumlah   = itemView.findViewById(R.id.tv_inventory_quantity);
            btnGunakan = itemView.findViewById(R.id.btn_use_item);
        }
    }
}
