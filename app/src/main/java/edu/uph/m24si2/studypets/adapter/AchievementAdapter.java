package edu.uph.m24si2.studypets.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uph.m24si2.studypets.R;
import edu.uph.m24si2.studypets.model.Achievement;

// AchievementAdapter — menampilkan daftar achievement di RecyclerView
public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.AchVH> {

    private Context context;
    private List<Achievement> daftarAchievement;

    public AchievementAdapter(Context context, List<Achievement> daftarAchievement) {
        this.context           = context;
        this.daftarAchievement = daftarAchievement;
    }

    // setData() — pola dari materi dosen
    public void setData(List<Achievement> data) {
        this.daftarAchievement = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AchVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_achievement, parent, false);
        return new AchVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AchVH holder, int position) {
        Achievement a = daftarAchievement.get(position);

        holder.tvIkon.setText(a.getIcon());
        holder.tvJudul.setText(a.getTitle());
        holder.tvDeskripsi.setText(a.getDescription());

        if (a.isUnlocked()) {
            holder.tvStatus.setText("✅");
            holder.tvTanggal.setVisibility(View.VISIBLE);
            if (a.getUnlockedAt() != null && a.getUnlockedAt().length() >= 10) {
                holder.tvTanggal.setText("Diraih: " + a.getUnlockedAt().substring(0, 10));
            }
            holder.itemView.setAlpha(1f); // tampil normal
        } else {
            holder.tvStatus.setText("🔒");
            holder.tvTanggal.setVisibility(View.GONE);
            holder.itemView.setAlpha(0.5f); // redup karena belum didapat
        }
    }

    @Override
    public int getItemCount() {
        return daftarAchievement.size();
    }

    static class AchVH extends RecyclerView.ViewHolder {
        TextView tvIkon, tvJudul, tvDeskripsi, tvTanggal, tvStatus;

        AchVH(@NonNull View v) {
            super(v);
            tvIkon      = v.findViewById(R.id.tv_ach_icon);
            tvJudul     = v.findViewById(R.id.tv_ach_title);
            tvDeskripsi = v.findViewById(R.id.tv_ach_desc);
            tvTanggal   = v.findViewById(R.id.tv_ach_date);
            tvStatus    = v.findViewById(R.id.tv_ach_status);
        }
    }
}
