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
import edu.uph.m24si2.studypets.model.Quest;

// QuestAdapter — menampilkan daftar quest di RecyclerView
// Pola sesuai materi dosen: ada setData() untuk refresh data
public class QuestAdapter extends RecyclerView.Adapter<QuestAdapter.QuestViewHolder> {

    // Interface untuk event complete dan delete — dikirim ke Activity
    public interface OnQuestCompleteListener {
        void onComplete(Quest quest);
    }

    public interface OnQuestDeleteListener {
        void onDelete(Quest quest);
    }

    private Context context;
    private List<Quest> questList;
    private OnQuestCompleteListener completeListener;
    private OnQuestDeleteListener deleteListener;

    public QuestAdapter(Context context, List<Quest> questList,
                        OnQuestCompleteListener completeListener,
                        OnQuestDeleteListener deleteListener) {
        this.context          = context;
        this.questList        = questList;
        this.completeListener = completeListener;
        this.deleteListener   = deleteListener;
    }

    // setData() — method untuk update data dari luar adapter
    // Pola dari materi dosen: adapter.setData(db.mahasiswaDAO().getMahasiswa())
    public void setData(List<Quest> data) {
        this.questList = data;
        notifyDataSetChanged(); // beritahu RecyclerView ada perubahan data
    }

    @NonNull
    @Override
    public QuestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_quest, parent, false);
        return new QuestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestViewHolder holder, int position) {
        Quest quest = questList.get(position);

        holder.tvTitle.setText(quest.getTitle());
        holder.tvSubject.setText(quest.getSubject());
        holder.tvDeadline.setText("📅 " + quest.getDeadline());
        holder.tvReward.setText("⚡ " + quest.getXpReward() + " XP | 🪙 " + quest.getCoinReward());
        holder.tvDifficulty.setText(quest.getDifficulty());

        // Warna badge kesulitan
        if (quest.getDifficulty().equalsIgnoreCase("easy")) {
            holder.tvDifficulty.setBackgroundResource(R.drawable.bg_badge_easy);
        } else if (quest.getDifficulty().equalsIgnoreCase("hard")) {
            holder.tvDifficulty.setBackgroundResource(R.drawable.bg_badge_hard);
        } else {
            holder.tvDifficulty.setBackgroundResource(R.drawable.bg_badge_medium);
        }

        // Tombol Selesai — hanya tampil kalau quest masih aktif
        if (completeListener != null && quest.getStatus().equals("pending")) {
            holder.btnComplete.setVisibility(View.VISIBLE);
            final Quest q = quest;
            holder.btnComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { completeListener.onComplete(q); }
            });
        } else {
            holder.btnComplete.setVisibility(View.GONE);
        }

        // Tombol Hapus
        if (deleteListener != null) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            final Quest q = quest;
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { deleteListener.onDelete(q); }
            });
        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return questList.size();
    }

    // ViewHolder menyimpan referensi ke komponen layout satu item quest
    static class QuestViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubject, tvDeadline, tvReward, tvDifficulty;
        Button btnComplete, btnDelete;

        QuestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle      = itemView.findViewById(R.id.tv_quest_title);
            tvSubject    = itemView.findViewById(R.id.tv_quest_subject);
            tvDeadline   = itemView.findViewById(R.id.tv_quest_deadline);
            tvReward     = itemView.findViewById(R.id.tv_quest_reward);
            tvDifficulty = itemView.findViewById(R.id.tv_quest_difficulty);
            btnComplete  = itemView.findViewById(R.id.btn_complete_quest);
            btnDelete    = itemView.findViewById(R.id.btn_delete_quest);
        }
    }
}
